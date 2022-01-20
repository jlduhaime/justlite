package net.runelite.client.plugins.alchemicalhydra;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.Collection;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.api.Client;
import static net.runelite.api.Perspective.getCanvasTileAreaPoly;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@Singleton
class HydraSceneOverlay extends Overlay
{
    @Setter(AccessLevel.PACKAGE)
    private Color poisonBorder;

    @Setter(AccessLevel.PACKAGE)
    private Color poisonFill;

    @Setter(AccessLevel.PACKAGE)
    private Color goodFountain;

    @Setter(AccessLevel.PACKAGE)
    private Color badFountain;

    private final HydraPlugin plugin;
    private final Client client;

    @Inject
    public HydraSceneOverlay(final Client client, final HydraPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Hydra hydra = plugin.getHydra();
        final Map<LocalPoint, Projectile> poisonProjectiles = plugin.getPoisonProjectiles();

        if (plugin.isCounting() && !poisonProjectiles.isEmpty())
        {
            drawPoisonArea(graphics, poisonProjectiles);
        }

        if (plugin.isFountain() && hydra.getPhase().getFountain() != null)
        {
            drawFountain(graphics, hydra);
        }

        return null;
    }

    private void drawPoisonArea(Graphics2D graphics, Map<LocalPoint, Projectile> poisonProjectiles)
    {
        Area poisonTiles = new Area();

        for (Map.Entry<LocalPoint, Projectile> entry : poisonProjectiles.entrySet())
        {
            if (entry.getValue().getEndCycle() < client.getGameCycle())
            {
                continue;
            }

            LocalPoint point = entry.getKey();
            Polygon poly = getCanvasTileAreaPoly(client, point, 3);

            if (poly != null)
            {
                poisonTiles.add(new Area(poly));
            }
        }

        graphics.setPaintMode();
        graphics.setColor(poisonBorder);
        graphics.draw(poisonTiles);
        graphics.setColor(poisonFill);
        graphics.fill(poisonTiles);
    }

    private void drawFountain(Graphics2D graphics, Hydra hydra)
    {
        Collection<WorldPoint> fountainWorldPoint = WorldPoint.toLocalInstance(client, hydra.getPhase().getFountain()); // thanks
        if (fountainWorldPoint.size() > 1) // for
        {
            return;
        }

        WorldPoint wp = null;
        for (WorldPoint p : fountainWorldPoint) // this
        {
            wp = p;
        }

        LocalPoint fountainPoint = wp == null ? null : LocalPoint.fromWorld(client, wp); // trash

        if (fountainPoint == null || hydra.isWeakened()) // I
        {
            return;
        }

        final Polygon poly = getCanvasTileAreaPoly(client, fountainPoint, 3); // don't

        if (poly == null)
        {
            return;
        }

        Color color; // like

        if (hydra.getNpc().getWorldArea().intersectsWith(new WorldArea(wp, 1, 1)))    // coords
        {                                                                                            // WHICH FUCKING RETARD DID X, Y, dX, dY, Z???? IT'S XYZdXdY REEEEEEEEEE
            color = goodFountain;
        }
        else
        {
            color = badFountain;
        }

        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(3));
        graphics.draw(poly);
    }
}

