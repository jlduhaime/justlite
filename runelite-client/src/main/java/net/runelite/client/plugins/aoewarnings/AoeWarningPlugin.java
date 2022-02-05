package net.runelite.client.plugins.aoewarnings;

import com.google.inject.Provides;

import java.awt.Color;
import java.time.Instant;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import static net.runelite.api.GraphicID.OLM_LIGHTNING;

@PluginDescriptor(
        name = "AoE Warnings",
        description = "Shows the final destination for AoE Attack projectiles",
        tags = {"bosses", "combat", "pve", "overlay"},
        enabledByDefault = false
)
@Singleton
@Slf4j
public class AoeWarningPlugin extends Plugin {
    @Getter(AccessLevel.PACKAGE)
    private final Set<CrystalBomb> bombs = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<ProjectileContainer> projectiles = new HashSet<>();

    @Inject
    public AoeWarningConfig config;

    @Inject
    private Notifier notifier;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AoeWarningOverlay coreOverlay;

    @Inject
    private BombOverlay bombOverlay;

    @Inject
    private Client client;

    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> lightningTrail = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private List<GameObject> acidTrail = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private List<GameObject> crystalSpike = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private List<GameObject> wintertodtSnowFall = new ArrayList<>();

    // Config values
    private boolean aoeNotifyAll;
    @Getter(AccessLevel.PACKAGE)
    private Color overlayColor;
    @Getter(AccessLevel.PACKAGE)
    private boolean configOutlineEnabled;
    private int delay;
    @Getter(AccessLevel.PACKAGE)
    private boolean configFadeEnabled;
    @Getter(AccessLevel.PACKAGE)
    private boolean tickTimers;
    @Getter(AccessLevel.PACKAGE)
    private int fontStyle;
    @Getter(AccessLevel.PACKAGE)
    private int textSize;
    @Getter(AccessLevel.PACKAGE)
    private boolean shadows;
    private boolean configShamansEnabled;
    private boolean configShamansNotifyEnabled;
    private boolean configArchaeologistEnabled;
    private boolean configArchaeologistNotifyEnabled;
    private boolean configIceDemonEnabled;
    private boolean configIceDemonNotifyEnabled;
    private boolean configVasaEnabled;
    private boolean configVasaNotifyEnabled;
    private boolean configTektonEnabled;
    private boolean configTektonNotifyEnabled;
    private boolean configVorkathEnabled;
    private boolean configVorkathNotifyEnabled;
    private boolean configGalvekEnabled;
    private boolean configGalvekNotifyEnabled;
    private boolean configGargBossEnabled;
    private boolean configGargBossNotifyEnabled;
    private boolean configVetionEnabled;
    private boolean configVetionNotifyEnabled;
    private boolean configChaosFanaticEnabled;
    private boolean configChaosFanaticNotifyEnabled;
    private boolean configOlmEnabled;
    private boolean configOlmNotifyEnabled;
    @Getter(AccessLevel.PACKAGE)
    private boolean configbombDisplay;
    private boolean configbombDisplayNotifyEnabled;
    private boolean configLightningTrail;
    private boolean configLightningTrailNotifyEnabled;
    private boolean configCorpEnabled;
    private boolean configCorpNotifyEnabled;
    private boolean configWintertodtEnabled;
    private boolean configWintertodtNotifyEnabled;
    private boolean configXarpusEnabled;
    private boolean configXarpusNotifyEnabled;
    private boolean configaddyDrags;
    private boolean configaddyDragsNotifyEnabled;
    private boolean configDrakeEnabled;
    private boolean configDrakeNotifyEnabled;
    private boolean configCerbFireEnabled;
    private boolean configCerbFireNotifyEnabled;
    private boolean configDemonicGorillaEnabled;
    private boolean configDemonicGorillaNotifyEnabled;

    @Provides
    AoeWarningConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AoeWarningConfig.class);
    }

    @Override
    protected void startUp() {
        updateConfig();
        overlayManager.add(coreOverlay);
        overlayManager.add(bombOverlay);
        reset();
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(coreOverlay);
        overlayManager.remove(bombOverlay);
        reset();
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("aoe")) {
            return;
        }

        updateConfig();
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        final Projectile projectile = event.getProjectile();

        if (AoeProjectileInfo.getById(projectile.getId()) == null) {
            return;
        }

        if (!projectileHasSpawnedAlready(projectiles, projectile)) {
            log.debug("PROJECTILE HAS NOT BEEN LOGGED ALREADY");
            final int id = projectile.getId();
            final int lifetime = this.delay + (projectile.getRemainingCycles() * 20);
            int ticksRemaining = projectile.getRemainingCycles() / 30;
            if (!isTickTimersEnabledForProjectileID(id)) {
                ticksRemaining = 0;
            }
            final int tickCycle = client.getTickCount() + ticksRemaining;
            if (isConfigEnabledForProjectileId(id, false)) {
                log.debug("ADDING PROJECTILE {}", projectile.getId());
                projectiles.add(new ProjectileContainer(projectile, Instant.now(), lifetime, tickCycle));
            }
        }

        if (projectiles.isEmpty()) {
            return;
        }

        projectiles.forEach(proj ->
        {
            if (proj.getProjectile() == projectile) {
                proj.setTargetPoint(event.getPosition());
            }
        });
    }

    private boolean projectileHasSpawnedAlready(Set<ProjectileContainer> projectiles, Projectile projectile) {
        Iterator it = projectiles.iterator();
        while (it.hasNext()) {
            ProjectileContainer tmp = (ProjectileContainer) it.next();
            return tmp.getProjectile().equals(projectile);
        }

        return false;
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        final GameObject gameObject = event.getGameObject();

        switch (gameObject.getId()) {
            case ObjectID.CRYSTAL_BOMB:
                bombs.add(new CrystalBomb(gameObject, client.getTickCount()));

                if (this.aoeNotifyAll || this.configbombDisplayNotifyEnabled) {
                    notifier.notify("Bomb!");
                }
                break;
            case ObjectID.ACID_POOL:
                acidTrail.add(gameObject);
                break;
            case ObjectID.SMALL_CRYSTALS:
                crystalSpike.add(gameObject);
                break;
            case NullObjectID.NULL_26690:
                if (this.configWintertodtEnabled) {
                    wintertodtSnowFall.add(gameObject);

                    if (this.aoeNotifyAll || this.configWintertodtNotifyEnabled) {
                        notifier.notify("Snow Fall!");
                    }
                }
                break;
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        final GameObject gameObject = event.getGameObject();

        switch (gameObject.getId()) {
            case ObjectID.CRYSTAL_BOMB:
                bombs.removeIf(o -> o.getGameObject() == gameObject);
                break;
            case ObjectID.ACID_POOL:
                acidTrail.remove(gameObject);
                break;
            case ObjectID.SMALL_CRYSTALS:
                crystalSpike.remove(gameObject);
                break;
            case NullObjectID.NULL_26690:
                wintertodtSnowFall.remove(gameObject);
                break;
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            return;
        }
        reset();
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        lightningTrail.clear();

        if (this.configLightningTrail) {
            client.getGraphicsObjects().forEach(o ->
            {
                if (o.getId() == OLM_LIGHTNING) {
                    lightningTrail.add(WorldPoint.fromLocal(client, o.getLocation()));

                    if (this.aoeNotifyAll || this.configLightningTrailNotifyEnabled) {
                        notifier.notify("Lightning!");
                    }
                }
            });
        }

        bombs.forEach(CrystalBomb::bombClockUpdate);
    }

    private boolean isTickTimersEnabledForProjectileID(int projectileId) {
        AoeProjectileInfo projectileInfo = AoeProjectileInfo.getById(projectileId);

        if (projectileInfo == null) {
            return false;
        }

        switch (projectileInfo) {
            case VASA_RANGED_AOE_PROJ:
            case VORKATH_POISON_POOL_PROJ:
            case VORKATH_SPAWN_PROJ:
            case VORKATH_TICK_FIRE_PROJ:
            case OLM_BURNING_PROJ:
            case OLM_FALLING_CRYSTAL_TRAIL_PROJ:
            case OLM_ACID_TRAIL_PROJ:
            case OLM_FIRE_LINE_PROJ:
                return false;
        }

        return true;
    }

    private boolean isConfigEnabledForProjectileId(int projectileId, boolean notify) {
        AoeProjectileInfo projectileInfo = AoeProjectileInfo.getById(projectileId);
        if (projectileInfo == null) {
            return false;
        }

        if (notify && this.aoeNotifyAll) {
            return true;
        }

        switch (projectileInfo) {
            case LIZARDMAN_SHAMAN_AOE_PROJ:
                return notify ? this.configShamansNotifyEnabled : this.configShamansEnabled;
            case CRAZY_ARCHAEOLOGIST_AOE_PROJ:
                return notify ? this.configArchaeologistNotifyEnabled : this.configArchaeologistEnabled;
            case ICE_DEMON_RANGED_AOE_PROJ:
            case ICE_DEMON_ICE_BARRAGE_AOE_PROJ:
                return notify ? this.configIceDemonNotifyEnabled : this.configIceDemonEnabled;
            case VASA_AWAKEN_AOE_PROJ:
            case VASA_RANGED_AOE_PROJ:
                return notify ? this.configVasaNotifyEnabled : this.configVasaEnabled;
            case TEKTON_METEOR_AOE_PROJ:
                return notify ? this.configTektonNotifyEnabled : this.configTektonEnabled;
            case VORKATH_BOMB_PROJ:
            case VORKATH_POISON_POOL_PROJ:
            case VORKATH_SPAWN_PROJ:
            case VORKATH_TICK_FIRE_PROJ:
                return notify ? this.configVorkathNotifyEnabled : this.configVorkathEnabled;
            case VETION_LIGHTNING_PROJ:
                return notify ? this.configVetionNotifyEnabled : this.configVetionEnabled;
            case CHAOS_FANATIC_PROJ:
                return notify ? this.configChaosFanaticNotifyEnabled : this.configChaosFanaticEnabled;
            case GALVEK_BOMB_PROJ:
            case GALVEK_MINE_PROJ:
                return notify ? this.configGalvekNotifyEnabled : this.configGalvekEnabled;
            case DAWN_FREEZE_PROJ:
            case DUSK_CEILING_PROJ:
                return notify ? this.configGargBossNotifyEnabled : this.configGargBossEnabled;
            case OLM_FALLING_CRYSTAL_PROJ:
            case OLM_BURNING_PROJ:
            case OLM_FALLING_CRYSTAL_TRAIL_PROJ:
            case OLM_ACID_TRAIL_PROJ:
            case OLM_FIRE_LINE_PROJ:
                return notify ? this.configOlmNotifyEnabled : this.configOlmEnabled;
            case CORPOREAL_BEAST_PROJ:
            case CORPOREAL_BEAST_DARK_CORE_PROJ:
                return notify ? this.configCorpNotifyEnabled : this.configCorpEnabled;
            case XARPUS_POISON_AOE_PROJ:
                return notify ? this.configXarpusNotifyEnabled : this.configXarpusEnabled;
            case ADDY_DRAG_POISON_PROJ:
                return notify ? this.configaddyDragsNotifyEnabled : this.configaddyDrags;
            case DRAKE_BREATH_PROJ:
                return notify ? this.configDrakeNotifyEnabled : this.configDrakeEnabled;
            case CERB_FIRE_PROJ:
                return notify ? this.configCerbFireNotifyEnabled : this.configCerbFireEnabled;
            case DEMONIC_GORILLA_BOULDER_PROJ:
                return notify ? this.configDemonicGorillaNotifyEnabled : this.configDemonicGorillaEnabled;
        }

        return false;
    }

    private void updateConfig() {
        this.aoeNotifyAll = config.aoeNotifyAll();
        this.overlayColor = config.overlayColor();
        this.configOutlineEnabled = config.isOutlineEnabled();
        this.delay = config.delay();
        this.configFadeEnabled = config.isFadeEnabled();
        this.tickTimers = config.tickTimers();
        this.fontStyle = config.fontStyle().getFont();
        this.textSize = config.textSize();
        this.shadows = config.shadows();
        this.configShamansEnabled = config.isShamansEnabled();
        this.configShamansNotifyEnabled = config.isShamansNotifyEnabled();
        this.configArchaeologistEnabled = config.isArchaeologistEnabled();
        this.configArchaeologistNotifyEnabled = config.isArchaeologistNotifyEnabled();
        this.configIceDemonEnabled = config.isIceDemonEnabled();
        this.configIceDemonNotifyEnabled = config.isIceDemonNotifyEnabled();
        this.configVasaEnabled = config.isVasaEnabled();
        this.configVasaNotifyEnabled = config.isVasaNotifyEnabled();
        this.configTektonEnabled = config.isTektonEnabled();
        this.configTektonNotifyEnabled = config.isTektonNotifyEnabled();
        this.configVorkathEnabled = config.isVorkathEnabled();
        this.configVorkathNotifyEnabled = config.isVorkathNotifyEnabled();
        this.configGalvekEnabled = config.isGalvekEnabled();
        this.configGalvekNotifyEnabled = config.isGalvekNotifyEnabled();
        this.configGargBossEnabled = config.isGargBossEnabled();
        this.configGargBossNotifyEnabled = config.isGargBossNotifyEnabled();
        this.configVetionEnabled = config.isVetionEnabled();
        this.configVetionNotifyEnabled = config.isVetionNotifyEnabled();
        this.configChaosFanaticEnabled = config.isChaosFanaticEnabled();
        this.configChaosFanaticNotifyEnabled = config.isChaosFanaticNotifyEnabled();
        this.configOlmEnabled = config.isOlmEnabled();
        this.configOlmNotifyEnabled = config.isOlmNotifyEnabled();
        this.configbombDisplay = config.bombDisplay();
        this.configbombDisplayNotifyEnabled = config.bombDisplayNotifyEnabled();
        this.configLightningTrail = config.LightningTrail();
        this.configLightningTrailNotifyEnabled = config.LightningTrailNotifyEnabled();
        this.configCorpEnabled = config.isCorpEnabled();
        this.configCorpNotifyEnabled = config.isCorpNotifyEnabled();
        this.configWintertodtEnabled = config.isWintertodtEnabled();
        this.configWintertodtNotifyEnabled = config.isWintertodtNotifyEnabled();
        this.configXarpusEnabled = config.isXarpusEnabled();
        this.configXarpusNotifyEnabled = config.isXarpusNotifyEnabled();
        this.configaddyDrags = config.addyDrags();
        this.configaddyDragsNotifyEnabled = config.addyDragsNotifyEnabled();
        this.configDrakeEnabled = config.isDrakeEnabled();
        this.configDrakeNotifyEnabled = config.isDrakeNotifyEnabled();
        this.configCerbFireEnabled = config.isCerbFireEnabled();
        this.configCerbFireNotifyEnabled = config.isCerbFireNotifyEnabled();
        this.configDemonicGorillaEnabled = config.isDemonicGorillaEnabled();
        this.configDemonicGorillaNotifyEnabled = config.isDemonicGorillaNotifyEnabled();
    }

    private void reset() {
        lightningTrail.clear();
        acidTrail.clear();
        crystalSpike.clear();
        wintertodtSnowFall.clear();
        bombs.clear();
        projectiles.clear();
    }
}
