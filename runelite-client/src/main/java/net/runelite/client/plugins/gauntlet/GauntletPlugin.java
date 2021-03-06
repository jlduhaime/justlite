package net.runelite.client.plugins.gauntlet;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;

import java.awt.Color;
import java.util.*;
import javax.annotation.Nullable;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import static net.runelite.api.GraphicID.*;
import static net.runelite.client.plugins.gauntlet.Hunllef.BossAttack.LIGHTNING;
import static net.runelite.client.plugins.gauntlet.Hunllef.BossAttack.MAGIC;
import static net.runelite.client.plugins.gauntlet.Hunllef.BossAttack.PRAYER;
import static net.runelite.client.plugins.gauntlet.Hunllef.BossAttack.RANGE;

import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(
        name = "Gauntlet",
        description = "All-in-one plugin for the Gauntlet.",
        tags = {"Gauntlet"},
        enabledByDefault = false
)
@Getter(AccessLevel.PACKAGE)
public class GauntletPlugin extends Plugin {
    private static final int BOW_ATTACK = 426;
    private static final int STAFF_ATTACK = 1167;
    private static final int LIGHTNING_ANIMATION = 8418;
    private static final Set<Integer> TORNADO_NPC_IDS = ImmutableSet.of(9025, 9039);
    private static final Set<Integer> MELEE_ANIMATIONS = ImmutableSet.of(395, 401, 400, 401, 386, 390, 422, 423, 401, 428, 440);
    private static final Set<Integer> PLAYER_ANIMATIONS = ImmutableSet.of(395, 401, 400, 401, 386, 390, 422, 423, 401, 428, 440, 426, 1167);
    private static final Set<Integer> HUNLLEF_MAGE_PROJECTILES = ImmutableSet.of(HUNLLEF_MAGE_ATTACK, HUNLLEF_CORRUPTED_MAGE_ATTACK);
    private static final Set<Integer> HUNLLEF_RANGE_PROJECTILES = ImmutableSet.of(HUNLLEF_RANGE_ATTACK, HUNLLEF_CORRUPTED_RANGE_ATTACK);
    private static final Set<Integer> HUNLLEF_PRAYER_PROJECTILES = ImmutableSet.of(HUNLLEF_PRAYER_ATTACK, HUNLLEF_CORRUPTED_PRAYER_ATTACK);
    private static final Set<Integer> HUNLLEF_PROJECTILES = ImmutableSet.of(HUNLLEF_PRAYER_ATTACK, HUNLLEF_CORRUPTED_PRAYER_ATTACK,
            HUNLLEF_RANGE_ATTACK, HUNLLEF_CORRUPTED_RANGE_ATTACK, HUNLLEF_MAGE_ATTACK, HUNLLEF_CORRUPTED_MAGE_ATTACK
    );
    private static final Set<Integer> HUNLLEF_NPC_IDS = ImmutableSet.of(NpcID.CRYSTALLINE_HUNLLEF, NpcID.CRYSTALLINE_HUNLLEF_9022, NpcID.CRYSTALLINE_HUNLLEF_9023,
            NpcID.CRYSTALLINE_HUNLLEF_9024, NpcID.CORRUPTED_HUNLLEF, NpcID.CORRUPTED_HUNLLEF_9036, NpcID.CORRUPTED_HUNLLEF_9037, NpcID.CORRUPTED_HUNLLEF_9038
    );
    private static final Set<Integer> RESOURCES = ImmutableSet.of(ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT, ObjectID.PHREN_ROOTS,
            ObjectID.PHREN_ROOTS_36066, ObjectID.FISHING_SPOT_36068, ObjectID.FISHING_SPOT_35971, ObjectID.GRYM_ROOT, ObjectID.GRYM_ROOT_36070,
            ObjectID.LINUM_TIRINUM, ObjectID.LINUM_TIRINUM_36072
    );
    private static final int GATHERING_HERB = 0;
    private static final int GATHERING_CLOTH = 1;

    @Inject
    @Getter(AccessLevel.NONE)
    private Client client;

    @Inject
    @Getter(AccessLevel.NONE)
    private ClientThread clientThread;

    @Inject
    @Getter(AccessLevel.NONE)
    private OverlayManager overlayManager;

    @Inject
    @Getter(AccessLevel.NONE)
    private GauntletOverlay overlay;

    @Inject
    @Getter(AccessLevel.NONE)
    private GauntletInfoBoxOverlay infoboxoverlay;

    @Inject
    @Getter(AccessLevel.NONE)
    private GauntletConfig config;

    @Inject
    @Getter(AccessLevel.NONE)
    private EventBus eventBus;

    @Inject
    @Getter(AccessLevel.NONE)
    private GauntletTimer timer;

    @Inject
    @Getter(AccessLevel.NONE)
    private SkillIconManager skillIconManager;

    @Inject
    @Getter(AccessLevel.NONE)
    private GauntletCounter GauntletCounter;

    @Setter(AccessLevel.PACKAGE)
    @Nullable
    private Hunllef hunllef;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private ItemManager itemManager;

    private boolean attackVisualOutline;
    private boolean completeStartup = false;
    private boolean displayTimerChat;
    private boolean displayTimerWidget;
    @Setter(AccessLevel.PACKAGE)
    private boolean flash;
    private boolean flashOnWrongAttack;
    private boolean highlightPrayerInfobox;
    private boolean highlightResources;
    private boolean highlightResourcesIcons;
    private boolean highlightWidget;
    private boolean overlayBoss;
    private boolean overlayBossPrayer;
    private boolean overlayTornadoes;
    private boolean timerVisible = true;
    private boolean uniqueAttackVisual;
    private boolean uniquePrayerAudio;
    private boolean uniquePrayerVisual;
    private Color highlightResourcesColor;
    private final Map<String, Integer> items = new HashMap<>();
    private final Set<Missiles> projectiles = new HashSet<>();
    private final Set<Resources> resources = new HashSet<>();

    private GauntletConfig.CounterDisplay countAttacks;
    private int resourceIconSize;
    private Set<Tornado> tornadoes = new HashSet<>();
    private int projectileIconSize;
    private boolean displayResources;
    private Counter oreCounter;
    private Counter woodCounter;
    private Counter clothCounter;
    private Counter fishCounter;
    private Counter herbCounter;
    private int oresGathered;
    private int woodGathered;
    private int clothGathered;
    private int fishGathered;
    private int herbGathered;
    private int currentFarmingAction = -1;
    private boolean countersVisible = false;
    private int miningXp = 0;
    private int farmingXp = 0;
    private int woodcuttingXp = 0;
    private int fishingXp = 0;
    private boolean inGauntlet = false;

    @Provides
    GauntletConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(GauntletConfig.class);
    }

    @Override
    protected void startUp() {
        updateConfig();
        initializeCounters();
        overlayManager.add(overlay);
        overlayManager.add(infoboxoverlay);
        overlayManager.add(GauntletCounter);
        timerVisible = this.displayTimerWidget;
        timer.resetStates();
        if (timerVisible) {
            overlayManager.add(timer);
        }
        if (client.getGameState() != GameState.STARTING && client.getGameState() != GameState.UNKNOWN) {
            completeStartup = false;
            clientThread.invoke(() ->
            {
                timer.initStates();
                completeStartup = true;
            });
        } else {
            completeStartup = true;
        }
    }

    private void addCounters() {
        if (!countersVisible) {
            infoBoxManager.addInfoBox(oreCounter);
            infoBoxManager.addInfoBox(woodCounter);
            infoBoxManager.addInfoBox(clothCounter);
            infoBoxManager.addInfoBox(fishCounter);
            infoBoxManager.addInfoBox(herbCounter);
            countersVisible = true;
        }
    }

    private void initializeCounters() {
        resetGatheringCounters();
        oreCounter = new Counter(itemManager.getImage(ItemID.CORRUPTED_ORE), this, 0);
        woodCounter = new Counter(itemManager.getImage(ItemID.PHREN_BARK_23878), this, 0);
        clothCounter = new Counter(itemManager.getImage(ItemID.LINUM_TIRINUM_23876), this, 0);
        fishCounter = new Counter(itemManager.getImage(ItemID.RAW_PADDLEFISH), this, 0);
        herbCounter = new Counter(itemManager.getImage(ItemID.GRYM_LEAF_23875), this, 0);
    }

    private void resetGatheringCounters() {
        oresGathered = 0;
        fishGathered = 0;
        woodGathered = 0;
        clothGathered = 0;
        herbGathered = 0;
        if (oreCounter != null) updateCounters();
    }

    private void updateCounters() {
        oreCounter.setCount(oresGathered);

        woodCounter.setCount(woodGathered);
        clothCounter.setCount(clothGathered);
        fishCounter.setCount(fishGathered);
        herbCounter.setCount(herbGathered);
    }

    @Override
    protected void shutDown() {
        timer.resetStates();
        if (timerVisible) {
            overlayManager.remove(timer);
            timerVisible = false;
        }
        overlayManager.remove(overlay);
        overlayManager.remove(infoboxoverlay);
        overlayManager.remove(GauntletCounter);
        removeCounters();
        resetGatheringCounters();
        resources.clear();
        projectiles.clear();
        tornadoes.clear();
        setHunllef(null);
    }

    private void removeCounters() {
        infoBoxManager.removeInfoBox(oreCounter);
        infoBoxManager.removeInfoBox(woodCounter);
        infoBoxManager.removeInfoBox(clothCounter);
        infoBoxManager.removeInfoBox(fishCounter);
        infoBoxManager.removeInfoBox(herbCounter);
        countersVisible = false;
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        if (menuOptionClicked.getMenuTarget().toUpperCase().contains("LINUM")) {
            currentFarmingAction = GATHERING_CLOTH;
        }
        if (menuOptionClicked.getMenuTarget().toUpperCase().contains("GRYM")) {
            currentFarmingAction = GATHERING_HERB;
        }
    }

    @Subscribe
    private void onNpcLootReceived(NpcLootReceived npcLootReceived) {
        fishGathered += (int) npcLootReceived.getItems().stream().filter(item -> item.getId() == ItemID.RAW_PADDLEFISH).count();
        herbGathered += (int) npcLootReceived.getItems().stream().filter(item -> item.getId() == ItemID.GRYM_LEAF || item.getId() == ItemID.GRYM_LEAF_23875).count();
        updateCounters();
    }


    @Subscribe
    private void onStatChanged(StatChanged event) {
        switch (event.getSkill()) {
            case MINING:
                if (miningXp != event.getXp()) {
                    oresGathered++;
                    miningXp = event.getXp();
                }
                break;
            case FISHING:
                if (fishingXp != event.getXp()) {
                    fishGathered++;
                    fishingXp = event.getXp();
                }
                break;
            case WOODCUTTING:
                if (woodcuttingXp != event.getXp()) {
                    woodGathered++;
                    woodcuttingXp = event.getXp();
                }
                break;
            case FARMING:
                if (farmingXp != event.getXp()) {
                    if (currentFarmingAction == GATHERING_HERB) {
                        herbGathered++;
                        farmingXp = event.getXp();
                    } else if (currentFarmingAction == GATHERING_CLOTH) {
                        clothGathered++;
                        farmingXp = event.getXp();
                    }
                }
                break;
        }
        updateCounters();
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        if (hunllef == null) {
            return;
        }

        final Actor actor = event.getActor();

        // This section handles the player counter.
        if (actor instanceof Player && fightingBoss()) {
            final Player player = (Player) actor;
            final int anim = player.getAnimation();

            if (player.getName() == null || client.getLocalPlayer() == null || !player.getName().equals(client.getLocalPlayer().getName()) || anim == -1 || !PLAYER_ANIMATIONS.contains(anim)) {
                return;
            }

            NPCComposition comp = hunllef.getNpc().getComposition();

            if (comp == null || comp.getOverheadIcon() == null) {
                return;
            }

            final HeadIcon prayer = comp.getOverheadIcon();

            switch (prayer) {
                case MELEE:
                    if (MELEE_ANIMATIONS.contains(anim)) {
                        setFlash(true);
                        return;
                    }
                    hunllef.updatePlayerAttack();
                    break;
                case RANGED:
                    if (BOW_ATTACK == anim) {
                        setFlash(true);
                        return;
                    }
                    hunllef.updatePlayerAttack();
                    break;
                case MAGIC:
                    if (STAFF_ATTACK == anim) {
                        setFlash(true);
                        return;
                    }
                    hunllef.updatePlayerAttack();
                    break;
            }
        }

        // This section handles the boss attack counter if they perform a lightning attack.
        if (actor instanceof NPC) {
            final NPC npc = (NPC) actor;

            if (npc.getAnimation() == LIGHTNING_ANIMATION) {
                hunllef.updateAttack(LIGHTNING);
            }
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("Gauntlet")) {
            return;
        }

        updateConfig();

        if (event.getKey().equals("displayTimerWidget")) {
            if (this.displayTimerWidget && !timerVisible) {
                overlayManager.add(timer);
                timerVisible = true;
            } else if (!this.displayTimerWidget && timerVisible) {
                overlayManager.remove(timer);
                timerVisible = false;
            }
        }

        if (event.getKey().equals("displayResources")) {
            if (this.displayResources && this.startedGauntlet()) {
                addCounters();
            } else {
                removeCounters();
            }
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        final GameObject obj = event.getGameObject();
        if (RESOURCES.contains(obj.getId())) {
            resources.removeIf(object -> object.getGameObject() == obj);
        }
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        final GameObject obj = event.getGameObject();
        if (RESOURCES.contains(obj.getId())) {
            resources.add(new Resources(obj, event.getTile(), skillIconManager));
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            resources.clear();
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        // This handles the timer based on player health.
        if (this.completeStartup) {
            timer.checkStates(false);
        }
        if (!tornadoes.isEmpty()) {
            tornadoes.forEach(Tornado::updateTimeLeft);
        }
        if (hunllef != null) {
            if (hunllef.getTicksUntilAttack() > 0) {
                hunllef.setTicksUntilAttack(hunllef.getTicksUntilAttack() - 1);
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        final NPC npc = event.getNpc();
        if (HUNLLEF_NPC_IDS.contains(npc.getId())) {
            setHunllef(null);
            resetGatheringCounters();
        } else if (TORNADO_NPC_IDS.contains(npc.getId())) {
            tornadoes.removeIf(tornado -> tornado.getNpc() == npc);
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        final NPC npc = event.getNpc();
        if (HUNLLEF_NPC_IDS.contains(npc.getId())) {
            setHunllef(new Hunllef(npc, skillIconManager));
        } else if (TORNADO_NPC_IDS.contains(npc.getId())) {
            tornadoes.add(new Tornado(npc));
        }
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        if (hunllef == null) {
            return;
        }

        final Projectile proj = event.getProjectile();
        if (!projectileHasSpawnedAlready(projectiles, proj)) {
            if (HUNLLEF_PROJECTILES.contains(proj.getId())) {
                projectiles.add(new Missiles(proj, skillIconManager));
                if (HUNLLEF_MAGE_PROJECTILES.contains(proj.getId())) {
                    hunllef.updateAttack(MAGIC);
                } else if (HUNLLEF_PRAYER_PROJECTILES.contains(proj.getId())) {
                    hunllef.updateAttack(PRAYER);
                    if (this.uniquePrayerAudio) {
                        client.playSoundEffect(SoundEffectID.MAGIC_SPLASH_BOING);
                    }
                } else if (HUNLLEF_RANGE_PROJECTILES.contains(proj.getId())) {
                    hunllef.updateAttack(RANGE);
                }
            }
        }
    }

    private boolean projectileHasSpawnedAlready(Set<Missiles> projectiles, Projectile projectile) {
        Iterator it = projectiles.iterator();
        while (it.hasNext()) {
            Missiles tmp = (Missiles) it.next();
            return tmp.getProjectile().equals(projectile);
        }

        return false;
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        if (client.getVar(Varbits.GAUNTLET_ENTERED) == 1 && !inGauntlet) {
            resetGatheringCounters();
            inGauntlet = true;
        }
        if (this.completeStartup) {
            timer.checkStates(true);
        }
        if (startedGauntlet() && displayResources) {
            addCounters();
        } else {
            removeCounters();
            inGauntlet = false;
        }
    }

    boolean fightingBoss() {
        return client.getVar(Varbits.GAUNTLET_FINAL_ROOM_ENTERED) == 1;
    }

    boolean startedGauntlet() {
        return client.getVar(Varbits.GAUNTLET_ENTERED) == 1;
    }

    private void updateConfig() {
        this.highlightResources = config.highlightResources();
        this.highlightResourcesColor = config.highlightResourcesColor();
        this.highlightResourcesIcons = config.highlightResourcesIcons();
        this.flashOnWrongAttack = config.flashOnWrongAttack();
        this.highlightWidget = config.highlightWidget();
        this.resourceIconSize = config.resourceIconSize();
        this.projectileIconSize = config.projectileIconSize();
        this.countAttacks = config.countAttacks();
        this.uniquePrayerAudio = config.uniquePrayerAudio();
        this.uniquePrayerVisual = config.uniquePrayerVisual();
        this.uniqueAttackVisual = config.uniqueAttackVisual();
        this.overlayBoss = config.overlayBoss();
        this.overlayBossPrayer = config.overlayBossPrayer();
        this.overlayTornadoes = config.overlayTornadoes();
        this.displayTimerWidget = config.displayTimerWidget();
        this.displayTimerChat = config.displayTimerChat();
        this.attackVisualOutline = config.attackVisualOutline();
        this.highlightPrayerInfobox = config.highlightPrayerInfobox();
        this.displayResources = config.displayGatheredResources();
    }
}