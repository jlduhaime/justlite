package net.runelite.client.plugins.zulrah;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.zulrah.overlays.ZulrahCurrentPhaseOverlay;
import net.runelite.client.plugins.zulrah.overlays.ZulrahNextPhaseOverlay;
import net.runelite.client.plugins.zulrah.overlays.ZulrahOverlay;
import net.runelite.client.plugins.zulrah.overlays.ZulrahPrayerOverlay;
import net.runelite.client.plugins.zulrah.patterns.*;
import net.runelite.client.plugins.zulrah.phase.ZulrahPhase;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@PluginDescriptor(
        name = "zulrah Helper"
)
@Slf4j
@Singleton
public class ZulrahPlugin extends Plugin {
    private static final ZulrahPattern[] patterns = new ZulrahPattern[]
            {
                    new ZulrahPatternA(),
                    new ZulrahPatternB(),
                    new ZulrahPatternC(),
                    new ZulrahPatternD()
            };

    @Getter(AccessLevel.PACKAGE)
    private NPC zulrah;

    @Inject
    private Client client;

    @Inject
    private ZulrahConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private Notifier notifier;

    @Inject
    private ZulrahCurrentPhaseOverlay currentPhaseOverlay;

    @Inject
    private ZulrahNextPhaseOverlay nextPhaseOverlay;

    @Inject
    private ZulrahPrayerOverlay zulrahPrayerOverlay;

    @Inject
    private ZulrahOverlay zulrahOverlay;

    private ZulrahInstance instance;

    public ZulrahPlugin() {
    }

    @Provides
    ZulrahConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ZulrahConfig.class);
    }

    @Override
    protected void startUp() {

        overlayManager.add(currentPhaseOverlay);
        overlayManager.add(nextPhaseOverlay);
        overlayManager.add(zulrahPrayerOverlay);
        overlayManager.add(zulrahOverlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(currentPhaseOverlay);
        overlayManager.remove(nextPhaseOverlay);
        overlayManager.remove(zulrahPrayerOverlay);
        overlayManager.remove(zulrahOverlay);
        zulrah = null;
        instance = null;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (zulrah == null) {
            if (instance != null) {
                log.debug("zulrah encounter has ended.");
                instance = null;
            }
            return;
        }

        if (instance == null) {
            instance = new ZulrahInstance(zulrah);
            log.debug("zulrah encounter has started.");
        }

        ZulrahPhase currentPhase = ZulrahPhase.valueOf(zulrah, instance.getStartLocation());

        if (instance.getPhase() == null) {
            instance.setPhase(currentPhase);
        } else if (!instance.getPhase().equals(currentPhase)) {
            ZulrahPhase previousPhase = instance.getPhase();
            instance.setPhase(currentPhase);
            instance.nextStage();

            log.debug("zulrah phase has moved from {} -> {}, stage: {}", previousPhase, currentPhase, instance.getStage());
        }

        ZulrahPattern pattern = instance.getPattern();

        if (pattern == null) {
            int potential = 0;
            ZulrahPattern potentialPattern = null;

            for (ZulrahPattern p : patterns) {
                if (p.stageMatches(instance.getStage(), instance.getPhase())) {
                    potential++;
                    potentialPattern = p;
                }
            }

            if (potential == 1) {
                log.debug("zulrah pattern identified: {}", potentialPattern);

                instance.setPattern(potentialPattern);
            }
        } else if (pattern.canReset(instance.getStage()) && (instance.getPhase() == null || instance.getPhase().equals(pattern.get(0)))) {
            log.debug("zulrah pattern has reset.");

            instance.reset();
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {

    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc != null && npc.getName() != null &&
                npc.getName().toLowerCase().contains("zulrah")) {
            zulrah = npc;
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (npc != null && npc.getName() != null &&
                npc.getName().toLowerCase().contains("zulrah")) {
            zulrah = null;
        }
    }

    public ZulrahInstance getInstance() {
        return instance;
    }
}
