package net.runelite.client.plugins.cerberus;


import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

@Singleton
public class CerberusOverlay extends Overlay
{
    private final CerberusPlugin plugin;
    private final SkillIconManager iconManager;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    CerberusOverlay(final CerberusPlugin plugin, final SkillIconManager iconManager)
    {
        this.plugin = plugin;
        this.iconManager = iconManager;
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getGhosts().isEmpty())
        {
            return null;
        }

        panelComponent.getChildren().clear();

        // Ghosts are already sorted
        plugin.getGhosts().stream()
                // Iterate only through the correct amount of ghosts
                .limit(CerberusGhost.values().length)
                .forEach(npc -> CerberusGhost
                        .fromNPC(npc)
                        .ifPresent(ghost -> panelComponent
                                .getChildren()
                                .add(new ImageComponent(iconManager.getSkillImage(ghost.getType())))));


        return panelComponent.render(graphics);
    }
}
