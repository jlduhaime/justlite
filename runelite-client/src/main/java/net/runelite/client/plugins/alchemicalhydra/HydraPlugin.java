package net.runelite.client.plugins.alchemicalhydra;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@PluginDescriptor(
        name = "Alchemical Hydra",
        description = "Show what to pray against hydra",
        tags = { "Hydra"},
        enabledByDefault = true
)
@Slf4j
@Singleton
public class HydraPlugin extends Plugin
{
    private static final int[] HYDRA_REGIONS = {
        5279, 5280,
        5535, 5536
    };
    private static final int STUN_LENGTH = 7;
    private static int lastPose = -1;
    private static int rangedProj = 1468;
    private int[] hydras = {NpcID.ALCHEMICAL_HYDRA, NpcID.ALCHEMICAL_HYDRA_8616, NpcID.ALCHEMICAL_HYDRA_8617, NpcID.ALCHEMICAL_HYDRA_8618,
                            NpcID.ALCHEMICAL_HYDRA_8619, NpcID.ALCHEMICAL_HYDRA_8620, NpcID.ALCHEMICAL_HYDRA_8621, NpcID.ALCHEMICAL_HYDRA_8622};

    @Getter(AccessLevel.PACKAGE)
    private Map<LocalPoint, Projectile> poisonProjectiles = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private Hydra hydra;

    @Getter(AccessLevel.PACKAGE)
    private boolean counting;

    @Getter(AccessLevel.PACKAGE)
    private boolean fountain;

    @Getter(AccessLevel.PACKAGE)
    private boolean stun;

    private boolean inHydraInstance;
    private int lastAttackTick;

    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private HydraConfig config;

    @Inject
    private HydraOverlay overlay;

    @Inject
    private HydraSceneOverlay sceneOverlay;

    @Inject
    private OverlayManager overlayManager;

    @Provides
    HydraConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HydraConfig.class);
    }

    @Override
    protected void startUp()
    {
        initConfig();
        inHydraInstance = checkArea();
        lastAttackTick = -1;
        poisonProjectiles.clear();
    }

    @Override
    protected void shutDown()
    {
        inHydraInstance = false;
        hydra = null;
        poisonProjectiles.clear();
        removeOverlays();
        lastAttackTick = -1;
    }

    private void initConfig()
    {
        this.counting = config.counting();
        this.fountain = config.fountain();
        this.stun = config.stun();
        this.overlay.setSafeCol(config.safeCol());
        this.overlay.setMedCol(config.medCol());
        this.overlay.setBadCol(config.badCol());
        this.sceneOverlay.setPoisonBorder(config.poisonBorderCol());
        this.sceneOverlay.setPoisonFill(config.poisonCol());
        this.sceneOverlay.setBadFountain(config.fountainColA());
        this.sceneOverlay.setGoodFountain(config.fountainColB());
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();

        if (!inHydraInstance || hydra == null || actor == client.getLocalPlayer())
        {
            return;
        }

        if (poisonProjectiles.isEmpty())
        {
            return;
        }

        Set<LocalPoint> exPoisonProjectiles = new HashSet<>();
        for (Map.Entry<LocalPoint, Projectile> entry : poisonProjectiles.entrySet())
        {
            if (entry.getValue().getEndCycle() < client.getGameCycle())
            {
                exPoisonProjectiles.add(entry.getKey());
            }
        }
        for (LocalPoint toRemove : exPoisonProjectiles)
        {
            poisonProjectiles.remove(toRemove);
        }
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        if (!inHydraInstance || hydra == null
                || client.getGameCycle() >= event.getProjectile().getStartCycle())
        {
            return;
        }

        Projectile projectile = event.getProjectile();
        int id = projectile.getId();

        if (hydra.getPhase().getSpecProjectileId() != 0 && hydra.getPhase().getSpecProjectileId() == id)
        {
            if (hydra.getAttackCount() == hydra.getNextSpecial())
            {
                // Only add 9 to next special on the first poison projectile (whoops)
                hydra.setNextSpecial(hydra.getNextSpecial() + 9);
            }

            poisonProjectiles.put(event.getPosition(), projectile);
        }
        else if (client.getTickCount() != lastAttackTick
                && (id == Hydra.AttackStyle.MAGIC.getProjectileID() || id == Hydra.AttackStyle.RANGED.getProjectileID()))
        {
            hydra.handleAttack(id);
            lastAttackTick = client.getTickCount();
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (event.getMessage().equals("The chemicals neutralise the Alchemical Hydra's defences!"))
        {
            hydra.setWeakened(true);
        }
        else if (event.getMessage().equals("The Alchemical Hydra temporarily stuns you."))
        {
            if (isStun())
            {
                overlay.setStunTicks(STUN_LENGTH);
            }
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("betterHydra"))
        {
            return;
        }

        switch (event.getKey())
        {
            case "counting":
                this.counting = config.counting();
                break;
            case "fountain":
                this.fountain = config.fountain();
                break;
            case "stun":
                this.stun = config.stun();
                break;
            case "safeCol":
                overlay.setSafeCol(config.safeCol());
                return;
            case "medCol":
                overlay.setMedCol(config.medCol());
                return;
            case "badCol":
                overlay.setBadCol(config.badCol());
                return;
            case "poisonBorderCol":
                sceneOverlay.setPoisonBorder(config.poisonBorderCol());
                break;
            case "poisonCol":
                sceneOverlay.setPoisonFill(config.poisonCol());
                break;
            case "fountainColA":
                sceneOverlay.setBadFountain(config.fountainColA());
                break;
            case "fountainColB":
                sceneOverlay.setGoodFountain(config.fountainColB());
                break;
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (event.getNpc().getId() != NpcID.ALCHEMICAL_HYDRA)
        {
            return;
        }

        hydra = new Hydra(event.getNpc());
        addOverlays();
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        /*
            want to:
            - check what phase
                if different than what's set now, increment it
            npc IDs:
                8615 - default / fresh
                8619 - Phase 2
                8620 - Phase 3
                8621 - Phase 4
                8622 - Dead
         */
        // search for hydra
        for (NPC npcs : client.getNpcs()) {
            // if hydra is found
            if (ArrayUtils.contains(hydras, npcs.getId())) {

                // check to see if a special is happening right now
                HydraPhase phase = hydra.getPhase();

                // if it's a spec, increment the spec timer
                if (npcs.getAnimation() == phase.getSpecAnimationId() && phase.getSpecAnimationId() != 0)
                    hydra.setNextSpecial(hydra.getNextSpecial() + 9);

                // handle flame and lightning specs since animations don't happen for them anymore
                if (hydra.getNextSpecial() - hydra.getAttackCount() < 0) {
                    hydra.setNextSpecial((hydra.getNextSpecial() + 9));
                }

                // check to see if phase is changing
                if (hydra.getPhase().getNpcId() == npcs.getId())
                {
                    // do nothing since phase hasn't changed
                    return;
                }

                // if a new NPC id is found, then we have a phase change
                switch (phase)
                {
                    case ONE:
                        if (npcs.getId() == HydraPhase.TWO.getNpcId())
                            hydra.changePhase(HydraPhase.TWO);
                        return;
                    case TWO:
                        if (npcs.getId() == HydraPhase.THREE.getNpcId())
                            hydra.changePhase(HydraPhase.THREE);
                        return;
                    case THREE:
                        if (npcs.getId() == HydraPhase.FOUR.getNpcId())
                            hydra.changePhase(HydraPhase.FOUR);
                        return;
                    case FOUR:
                        hydra = null;
                        poisonProjectiles.clear();
                        removeOverlays();
                        return;
                    default:
                        log.debug("Tried some weird shit");
                        break;
                }
            }
        }
    }


    @Subscribe
    private void onGameStateChanged(GameStateChanged state)
    {
        if (state.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        inHydraInstance = checkArea();

        if (!inHydraInstance)
        {

            if (hydra != null)
            {
                removeOverlays();
                hydra = null;
            }

            return;
        }

        for (NPC npc : client.getNpcs())
        {
            if (npc.getId() == NpcID.ALCHEMICAL_HYDRA)
            {
                hydra = new Hydra(npc);
                break;
            }
        }

        addOverlays();
    }

    private boolean checkArea()
    {
        return Arrays.equals(client.getMapRegions(), HYDRA_REGIONS) && client.isInInstancedRegion();
    }

    private void addOverlays()
    {
        if (counting || stun)
        {
            overlayManager.add(overlay);
        }

        if (counting || fountain)
        {
            overlayManager.add(sceneOverlay);
        }
    }

    private void removeOverlays()
    {
        overlayManager.remove(overlay);
        overlayManager.remove(sceneOverlay);
    }
}
