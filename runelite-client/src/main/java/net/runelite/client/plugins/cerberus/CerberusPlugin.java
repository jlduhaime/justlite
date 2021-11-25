package net.runelite.client.plugins.cerberus;


import com.google.common.collect.ComparisonChain;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Cerberus",
        description = "Show what to pray against the summoned souls",
        tags = {"bosses", "combat", "ghosts", "prayer", "pve", "overlay", "souls"}
)
@Singleton
public class CerberusPlugin extends Plugin
{
    @Getter(AccessLevel.PACKAGE)
    private final List<NPC> ghosts = new ArrayList<>();

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CerberusOverlay overlay;

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        ghosts.clear();
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        GameState gameState = event.getGameState();
        if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.HOPPING || gameState == GameState.CONNECTION_LOST)
        {
            ghosts.clear();
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        final NPC npc = event.getNpc();
        CerberusGhost.fromNPC(npc).ifPresent(ghost -> ghosts.add(npc));
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        ghosts.remove(event.getNpc());
    }

    @Subscribe
    void onGameTick(GameTick gameTick)
    {
        if (ghosts.isEmpty())
        {
            return;
        }

        ghosts.sort((a, b) -> ComparisonChain.start()
                // First, sort by the southernmost ghost (e.g with lowest y)
                .compare(a.getLocalLocation().getY(), b.getLocalLocation().getY())
                // Then, sort by the westernmost ghost (e.g with lowest x)
                .compare(a.getLocalLocation().getX(), b.getLocalLocation().getX())
                // This will give use the current wave and order of the ghosts based on
                // what ghost will attack first
                .result());
    }
}
