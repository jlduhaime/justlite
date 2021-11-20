package net.runelite.client.plugins.olmhelper;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GraphicID;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.ProjectileID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileSpawned;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Olm Helper",
        description = "Useful tools for OLM fight",
        tags = {"CoX", "Olm"}
)

@Slf4j
@Singleton
@Getter(AccessLevel.PACKAGE)
public class OlmHelperPlugin extends Plugin {
    private static final int ANIMATION_ID_G1 = 430;
    private static final Pattern TP_REGEX = Pattern.compile("You have been paired with <col=ff0000>(.*)</col>! The magical power will enact soon...");

    @Inject
    @Getter(AccessLevel.NONE)
    private Client client;

    @Inject
    @Getter(AccessLevel.NONE)
    private ChatMessageManager chatMessageManager;

    @Inject
    @Getter(AccessLevel.NONE)
    private OlmHelperOverlay olmHelperOverlay;

    @Inject
    @Getter(AccessLevel.NONE)
    private OlmHelperConfig config;

    @Inject
    @Getter(AccessLevel.NONE)
    private OverlayManager overlayManager;

    @Inject
    @Getter(AccessLevel.NONE)
    private EventBus eventBus;

    private boolean handCripple;
    private boolean runOlm;
    private NPC hand;
    private NPC Olm_NPC;
    private List<WorldPoint> Olm_Heal = new ArrayList<>();
    private List<WorldPoint> Olm_TP = new ArrayList<>();
    private Set<Victim> victims = new HashSet<>();
    private Actor acidTarget;
    private int crippleTimer = 45;
    private int teleportTicks = 10;
    private int OlmPhase = 0;
    private int Olm_TicksUntilAction = -1;
    private int Olm_ActionCycle = -1;
    private int Olm_NextSpec = -1;
    private Map<NPC, NPCComposition> npcContainer = new HashMap<>();
    @Setter(AccessLevel.PACKAGE)
    private PrayAgainst prayAgainstOlm;
    private long lastPrayTime;
    private int sleepcount = 0;
    private boolean tpOverlay;
    private boolean olmTick;
    private boolean timers;
    private boolean configPrayAgainstOlm;
    private int prayAgainstSize;
    private Color burnColor;
    private Color acidColor;
    private Color tpColor;
    private OlmHelperConfig.FontStyle fontStyle;
    private int textSize;
    private boolean shadows;

    @Provides
    OlmHelperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(OlmHelperConfig.class);
    }

    @Override
    protected void startUp() {
        updateConfig();
        overlayManager.add(olmHelperOverlay);
        handCripple = false;
        hand = null;
        Olm_TP.clear();
        prayAgainstOlm = null;
        victims.clear();
        crippleTimer = 45;
        teleportTicks = 10;
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(olmHelperOverlay);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("Olm"))
            updateConfig();
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (!inRaid()) return;

        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            final Matcher tpMatcher = TP_REGEX.matcher(event.getMessage());
            if (tpMatcher.matches()) {
                for (Player player : client.getPlayers()) {
                    final String rawPlayerName = player.getName();

                    if (rawPlayerName != null) {
                        final String fixedPlayerName = Text.sanitize(rawPlayerName);
                        if (fixedPlayerName.equals(Text.sanitize(tpMatcher.group(1)))) {
                            victims.add(new Victim(player, Victim.Type.TELEPORT));
                        }
                    }
                }
            }

            switch (Text.standardize(event.getMessageNode().getValue())) {
                case "the great olm rises with the power of acid.":
                case "the great olm rises with the power of crystal.":
                case "the great olm rises with the power of flame.":
                case "the great olm is giving its all. this is its final stand.":
                    if (!runOlm) {
                        Olm_ActionCycle = -1;
                        Olm_TicksUntilAction = 4;
                    } else {
                        Olm_ActionCycle = -1;
                        Olm_TicksUntilAction = 3;
                    }

                    OlmPhase = 0;
                    runOlm = true;
                    crippleTimer = 45;
                    Olm_NextSpec = -1;
                    break;
                case "the great olm fires a sphere of aggression your way. your prayers have been sapped.":
                case "the great olm fires a sphere of aggression your way.":
                    prayAgainstOlm = PrayAgainst.MELEE;
                    lastPrayTime = System.currentTimeMillis();
                    break;
                case "the great olm fires a sphere of magical power your way. your prayers have been sapped.":
                case "the great olm fires a sphere of magical power your way.":
                    prayAgainstOlm = PrayAgainst.MAGIC;
                    lastPrayTime = System.currentTimeMillis();
                    break;
                case "the great olm fires a sphere of accuracy and dexterity your way. your prayers have been sapped.":
                case "the great olm fires a sphere of accuracy and dexterity your way.":
                    prayAgainstOlm = PrayAgainst.RANGED;
                    lastPrayTime = System.currentTimeMillis();
                    break;
                case "the great olm's left claw clenches to protect itself temporarily.":
                    handCripple = true;
            }
        }
    }

    @Subscribe
    private void onProjectileSpawned(ProjectileSpawned event) {
        if (!inRaid()) return;

        final Projectile projectile = event.getProjectile();
        switch (projectile.getId()) {
            case ProjectileID.OLM_MAGE_ATTACK:
                prayAgainstOlm = PrayAgainst.MAGIC;
                lastPrayTime = System.currentTimeMillis();
                break;
            case ProjectileID.OLM_RANGE_ATTACK:
                prayAgainstOlm = PrayAgainst.RANGED;
                lastPrayTime = System.currentTimeMillis();
                break;
            case ProjectileID.OLM_ACID_TRAIL:
                acidTarget = (Player) projectile.getNext();
                break;
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        if (!inRaid()) return;
        if (!(event.getActor() instanceof Player)) return;

        final Player player = (Player) event.getActor();

        if (player.getAnimation() == GraphicID.OLM_BURN) {
            int add = 0;
            for (Victim victim : victims) {
                if (victim.getPlayer().getNext().equals(player.getName()))
                    add++;
            }

            if (add == 0)
                victims.add(new Victim(player, Victim.Type.BURN));
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (!inRaid()) return;

        final NPC npc = event.getNpc();

        switch (npc.getId()) {
            case NpcID.GREAT_OLM_LEFT_CLAW:
            case NpcID.GREAT_OLM_LEFT_CLAW_7555:
                hand = npc;
                break;
            case NpcID.GREAT_OLM:
                Olm_NPC = npc;
        }
    }

    private void onNpcDespawned(NpcDespawned event) {
        if (!inRaid()) return;

        final NPC npc = event.getNpc();

        switch (npc.getId()) {
            case NpcID.GREAT_OLM_RIGHT_CLAW_7553:
            case NpcID.GREAT_OLM_RIGHT_CLAW:
                handCripple = false;
                break;
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!inRaid()) {
            OlmPhase = 0;
            sleepcount = 0;
            Olm_Heal.clear();
            npcContainer.clear();
            victims.clear();
            Olm_NPC = null;
            hand = null;
            prayAgainstOlm = null;
            runOlm = false;
            return;
        }

        handleVictims();

        if (handCripple) {
            crippleTimer--;
            if (crippleTimer <= 0) {
                handCripple = false;
                crippleTimer = 45;
            }
        }

        if (runOlm)
            handleOlm();
    }

    private void handleVictims() {
        if (victims.size() > 0) {
            victims.forEach(Victim::updateTicks);
            victims.removeIf(victim -> victim.getTicks() <= 0);
        }
    }

    private void handleOlm() {
        Olm_Heal.clear();
        Olm_TP.clear();
        client.clearHintArrow();
        sleepcount--;
        if (Olm_TicksUntilAction == 1) {
            if (Olm_ActionCycle == 1) {
                Olm_ActionCycle = 4;
                Olm_TicksUntilAction = 4;
                if (Olm_NextSpec == 1) {
                    if (OlmPhase == 1) {
                        Olm_NextSpec = 4; // 4 = heal 3= cry 2 = lightn 1 = swap
                    } else {
                        Olm_NextSpec = 3;
                    }
                } else {
                    Olm_NextSpec--;
                }
            } else {
                if (Olm_ActionCycle != -1) {
                    Olm_ActionCycle--;
                }
                Olm_TicksUntilAction = 4;
            }
        } else {
            Olm_TicksUntilAction--;
        }

        for (GraphicsObject o : client.getGraphicsObjects()) {
            if (sleepcount <= 0) {
                if (o.getId() == 1338) {
                    Olm_TicksUntilAction = 1;
                    Olm_NextSpec = 2;
                    Olm_ActionCycle = 4; //spec=1 null=3
                    sleepcount = 5;
                }
                if (o.getId() == 1356) {
                    Olm_TicksUntilAction = 4;
                    Olm_NextSpec = 1;
                    Olm_ActionCycle = 4; //spec=1 null=3
                    sleepcount = 50;
                }
            }
            if (o.getId() == GraphicID.OLM_TELEPORT) {
                Olm_TP.add(WorldPoint.fromLocal(client, o.getLocation()));
            }
            if (o.getId() == GraphicID.OLM_HEAL) {
                Olm_Heal.add(WorldPoint.fromLocal(client, o.getLocation()));
            }
            if (!Olm_TP.isEmpty()) {
                teleportTicks--;
                if (teleportTicks <= 0) {
                    client.clearHintArrow();
                    teleportTicks = 10;
                }
            }
        }
    }

    boolean inRaid() {
        return client.getVar(Varbits.IN_RAID) == 1;
    }

    private void updateConfig() {
        this.configPrayAgainstOlm = config.prayAgainstOlm();
        this.timers = config.timers();
        this.tpOverlay = config.tpOverlay();
        this.olmTick = config.olmTick();
        this.burnColor = config.burnColor();
        this.acidColor = config.acidColor();
        this.tpColor = config.tpColor();
        this.fontStyle = config.fontStyle();
        this.textSize = config.textSize();
        this.shadows = config.shadows();
        this.prayAgainstSize = config.prayAgainstOlmSize();
    }
}
