package net.runelite.client.plugins.fightcave;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Fight Cave",
        description = "Displays current and upcoming wave monsters in the fight caves"
)
@Singleton
@Slf4j
public class FightCavePlugin extends Plugin {
    static final int MAX_WAVE = 63;
    @Getter(AccessLevel.PACKAGE)
    static final List<EnumMap<WaveMonster, Integer>> WAVES = new ArrayList<>();
    private static final Pattern WAVE_PATTERN = Pattern.compile(".*Wave: (\\d+).*");
    private static final int FIGHT_CAVE_REGION = 9551;
    private static final int MAX_MONSTERS_OF_TYPE_PER_WAVE = 2;

    static {
        final WaveMonster[] waveMonsters = WaveMonster.values();

        final EnumMap<WaveMonster, Integer> waveOne = new EnumMap<>(WaveMonster.class);
        waveOne.put(waveMonsters[0], 1);
        WAVES.add(waveOne);
        for (int wave = 1; wave < MAX_WAVE; wave++) {
            final EnumMap<WaveMonster, Integer> prevWave = WAVES.get(wave - 1).clone();
            int maxMonsterOrdinal = -1;

            for (int i = 0; i < waveMonsters.length; i++) {
                final int ordinalMonsterQuantity = prevWave.getOrDefault(waveMonsters[i], 0);
                if (ordinalMonsterQuantity == MAX_MONSTERS_OF_TYPE_PER_WAVE) {
                    maxMonsterOrdinal = i;
                    break;
                }
            }

            if (maxMonsterOrdinal >= 0)
                prevWave.remove(waveMonsters[maxMonsterOrdinal]);

            final int addedMonsterOrdinal = maxMonsterOrdinal >= 0 ? maxMonsterOrdinal + 1 : 0;
            final WaveMonster addedMonster = waveMonsters[addedMonsterOrdinal];
            final int addedMonsterQuantity = prevWave.getOrDefault(addedMonster, 0);

            prevWave.put(addedMonster, addedMonsterQuantity + 1);
            WAVES.add(prevWave);
        }
    }

    @Inject
    private Client client;

    @Inject
    private NPCManager npcManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WaveOverlay waveOverlay;

    @Inject
    private FightCaveOverlay fightCaveOverlay;

    @Inject
    private FightCaveConfig config;

    @Getter(AccessLevel.PACKAGE)
    private Set<FightCaveContainer> fightCaveContainer = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private int currentWave = -1;
    @Getter(AccessLevel.PACKAGE)
    private boolean validRegion;
    @Getter(AccessLevel.PACKAGE)
    private List<Integer> mageTicks = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private List<Integer> rangedTicks = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private List<Integer> meleeTicks = new ArrayList<>();

    static String formatMonsterQuantity(final WaveMonster monster, final int quantity) {
        return String.format("%dx %s", quantity, monster);
    }

    @Getter(AccessLevel.PACKAGE)
    private WaveDisplayMode waveDisplay;
    @Getter(AccessLevel.PACKAGE)
    private boolean tickTimersWidget;
    @Getter(AccessLevel.PACKAGE)
    private FightCaveConfig.FontStyle fontStyle;
    @Getter(AccessLevel.PACKAGE)
    private int textSize;
    @Getter(AccessLevel.PACKAGE)
    private boolean shadows;

    @Provides
    FightCaveConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FightCaveConfig.class);
    }

    @Override
    public void startUp() {
        updateConfig();

        if (client.getGameState() == GameState.LOGGED_IN && regionCheck()) {
            validRegion = true;
            overlayManager.add(waveOverlay);
            overlayManager.add(fightCaveOverlay);
        }
    }

    @Override
    public void shutDown() {
        overlayManager.remove(waveOverlay);
        overlayManager.remove(fightCaveOverlay);
        currentWave = -1;
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("fightcave")) {
            return;
        }

        updateConfig();
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (!validRegion) {
            return;
        }

        final Matcher waveMatcher = WAVE_PATTERN.matcher(event.getMessage());

        if (event.getType() != ChatMessageType.GAMEMESSAGE || !waveMatcher.matches()) {
            return;
        }

        currentWave = Integer.parseInt(waveMatcher.group(1));
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (regionCheck()) {
            validRegion = true;
            overlayManager.add(waveOverlay);
            overlayManager.add(fightCaveOverlay);
        } else {
            validRegion = false;
            overlayManager.remove(fightCaveOverlay);
            overlayManager.remove(fightCaveOverlay);
        }

        fightCaveContainer.clear();
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (!validRegion) {
            return;
        }

        NPC npc = event.getNpc();
        log.debug("NPC SPAWNED: {}", npc.getName());
        switch (npc.getId()) {
            case NpcID.TOKXIL_3121:
            case NpcID.TOKXIL_3122:
            case NpcID.YTMEJKOT:
            case NpcID.YTMEJKOT_3124:
            case NpcID.KETZEK:
            case NpcID.KETZEK_3126:
            case NpcID.TZTOKJAD:
            case NpcID.TZTOKJAD_6506:
                fightCaveContainer.add(new FightCaveContainer(npc));
                break;

        }

    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        if (!validRegion) {
            return;
        }

        NPC npc = event.getNpc();

        switch (npc.getId()) {
            case NpcID.TOKXIL_3121:
            case NpcID.TOKXIL_3122:
            case NpcID.YTMEJKOT:
            case NpcID.YTMEJKOT_3124:
            case NpcID.KETZEK:
            case NpcID.KETZEK_3126:
            case NpcID.TZTOKJAD:
            case NpcID.TZTOKJAD_6506:
                fightCaveContainer.removeIf(c -> c.getNpc() == npc);
                break;
        }
    }

    @Subscribe
    private void onGameTick(GameTick Event) {
        if (!validRegion) {
            log.debug("not in fight caves...");
            return;
        }

        mageTicks.clear();
        rangedTicks.clear();
        meleeTicks.clear();

        for (FightCaveContainer npc : fightCaveContainer) {
            log.debug("found npc: {}", npc.getNpcName());
            log.debug("NPC ID: {}", npc.getNpc().getId());
            log.debug("npc attack speed: {}", npc.getAttackSpeed());
            log.debug("npc ticks until next attack: {}", npc.getTicksUntilAttack());
            if (npc.getTicksUntilAttack() >= 0) {
                npc.setTicksUntilAttack(npc.getTicksUntilAttack() - 1);
            }

            if (npc.getAnimations() == null) {
                log.debug("NO ANIMATIONS FOUND FOR NPC: {}", npc.getNpc().getName());
                return;
            }

            for (int anims : npc.getAnimations()) {
                log.debug("ANIM: {}", anims);
                if (anims == npc.getNpc().getAnimation()) {
                    log.debug("FOUND ANIMATION FOR NPC {}", npc.getNpc().getName());
                    log.debug("ANIMATION: {}", anims);
                    log.debug("TICKS UNTIL NEXT ATTACK: {}", npc.getTicksUntilAttack());
                    if (npc.getTicksUntilAttack() < 1) {
                        log.debug("NPC IS: {}", npc.getNpc().getName());
                        log.debug("ticks until attack = {}", npc.getTicksUntilAttack());
                        npc.setTicksUntilAttack(npc.getAttackSpeed());
                        log.debug("attack speed: {}", npc.getAttackSpeed());
                        log.debug("NOW ticks until attack = {}", npc.getTicksUntilAttack());
                    }

                    switch (anims) {
                        case AnimationID.TZTOK_JAD_RANGE_ATTACK:
                            npc.setAttackStyle(FightCaveContainer.AttackStyle.RANGE);
                            break;
                        case AnimationID.TZTOK_JAD_MAGIC_ATTACK:
                            npc.setAttackStyle(FightCaveContainer.AttackStyle.MAGE);
                            break;
                        case AnimationID.TZTOK_JAD_MELEE_ATTACK:
                            npc.setAttackStyle(FightCaveContainer.AttackStyle.MELEE);
                            break;
                    }
                }
            }

            if (npc.getNpcName().equals("TzTok-Jad")) {
                continue;
            }
            log.debug("NPC ATTACK STYLE FOR {}", npc.getNpc().getName());
            log.debug("{}", npc.getAttackStyle());
            switch (npc.getAttackStyle()) {
                case RANGE:
                    if (npc.getTicksUntilAttack() > 0) {
                        rangedTicks.add(npc.getTicksUntilAttack());
                    }
                    break;
                case MELEE:
                    if (npc.getTicksUntilAttack() > 0) {
                        meleeTicks.add(npc.getTicksUntilAttack());
                    }
                    break;
                case MAGE:
                    if (npc.getTicksUntilAttack() > 0) {
                        mageTicks.add(npc.getTicksUntilAttack());
                    }
                    break;
            }
        }

        Collections.sort(mageTicks);
        log.debug("size of mage ticks: {}", mageTicks.size());
        Collections.sort(rangedTicks);
        log.debug("size of range ticks: {}", rangedTicks.size());
        Collections.sort(meleeTicks);
        log.debug("size of melee ticks: {}", meleeTicks.size());
    }

    private boolean regionCheck() {
        return ArrayUtils.contains(client.getMapRegions(), FIGHT_CAVE_REGION);
    }

    private void updateConfig() {
        this.waveDisplay = config.waveDisplay();
        this.tickTimersWidget = config.tickTimersWidget();
        this.fontStyle = config.fontStyle();
        this.textSize = config.textSize();
        this.shadows = config.shadows();
    }
}
