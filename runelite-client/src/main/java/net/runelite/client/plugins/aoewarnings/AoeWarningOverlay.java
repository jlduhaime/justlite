package net.runelite.client.plugins.aoewarnings;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import static net.runelite.api.GraphicID.ICE_DEMON_ICE_BARRAGE_AOE;
import static net.runelite.api.GraphicID.TEKTON_METEOR_AOE;

@Slf4j
@Singleton
public class AoeWarningOverlay extends Overlay {
    private static final int FILL_START_ALPHA = 25;
    private static final int OUTLINE_START_ALPHA = 255;

    private final Client client;
    private final AoeWarningPlugin plugin;

    @Inject
    public AoeWarningOverlay(final Client client, final AoeWarningPlugin plugin) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        WorldPoint lp = client.getLocalPlayer().getWorldLocation();

        plugin.getLightningTrail().forEach(o ->
                OverlayUtil.drawTiles(graphics, client, o, lp, new Color(0, 150, 200), 2, 150, 50));

        plugin.getAcidTrail().forEach(o ->
                OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, new Color(69, 241, 44), 2, 150, 50));

        plugin.getCrystalSpike().forEach(o ->
                OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, new Color(255, 0, 84), 2, 150, 50));

        plugin.getWintertodtSnowFall().forEach(o ->
                OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, new Color(255, 0, 84), 2, 150, 50));

        Instant now = Instant.now();
        Set<ProjectileContainer> projectiles = plugin.getProjectiles();
        projectiles.forEach(proj ->
        {
            if (proj.getTargetPoint() == null) {
                log.debug("PROJECTILE HAS NO TARGET POINT");
                return;
            }

            Color color;

            if (now.isAfter(proj.getStartTime().plus(Duration.ofMillis(proj.getLifetime())))) {
                log.debug("RIGHT NOW IS AFTER THE LIFETIME OF THE PROJECTILE");
                return;
            }

            if (proj.getProjectile().getId() == ICE_DEMON_ICE_BARRAGE_AOE || proj.getProjectile().getId() == TEKTON_METEOR_AOE) {
                if (client.getVar(Varbits.IN_RAID) == 0) {
                    log.debug("USER IS NOT IN A RAID. CANNOT TRACK ICE DEMON AND TEKTON");
                    return;
                }
            }

            final Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, proj.getTargetPoint(), proj.getAoeProjectileInfo().getAoeSize());

            if (tilePoly == null) {
                log.debug("tile polygon is null");
                return;
            }

            final double progress = (System.currentTimeMillis() - proj.getStartTime().toEpochMilli()) / (double) proj.getLifetime();

            final int tickProgress = proj.getFinalTick() - client.getTickCount();

            int fillAlpha, outlineAlpha;
            if (plugin.isConfigFadeEnabled()) {
                fillAlpha = (int) ((1 - progress) * FILL_START_ALPHA);
                outlineAlpha = (int) ((1 - progress) * OUTLINE_START_ALPHA);
            } else {
                fillAlpha = FILL_START_ALPHA;
                outlineAlpha = OUTLINE_START_ALPHA;
            }
            if (tickProgress == 0) {
                color = Color.RED;
            } else {
                color = Color.WHITE;
            }

            if (fillAlpha < 0) {
                fillAlpha = 0;
            }
            if (outlineAlpha < 0) {
                outlineAlpha = 0;
            }

            if (fillAlpha > 255) {
                fillAlpha = 255;
            }
            if (outlineAlpha > 255) {
                outlineAlpha = 255;
            }

            if (plugin.isConfigOutlineEnabled()) {
                log.debug("CONFIG OUTLINE IS ENABLED");
                graphics.setColor(new Color(setAlphaComponent(plugin.getOverlayColor().getRGB(), outlineAlpha), true));
                graphics.drawPolygon(tilePoly);
            }
            if (plugin.isTickTimers() && tickProgress >= 0) {
                log.debug("IS TICK TIMERS IS TRUE && TICK PROGERSS >= 0");
                OverlayUtil.renderTextLocation(graphics, Integer.toString(tickProgress), plugin.getTextSize(),
                        plugin.getFontStyle(), color, centerPoint(tilePoly.getBounds()), plugin.isShadows(), 0);
            }

            graphics.setColor(new Color(setAlphaComponent(plugin.getOverlayColor().getRGB(), fillAlpha), true));
            graphics.fillPolygon(tilePoly);
            log.debug("Rendering polygon for projectile ID: " + proj.getAoeProjectileInfo().getId());
            OverlayUtil.renderPolygon(graphics, tilePoly, color, new BasicStroke((float) proj.getAoeProjectileInfo().getAoeSize()));
        });

        projectiles.removeIf(proj -> now.isAfter(proj.getStartTime().plus(Duration.ofMillis(proj.getLifetime()))));
        return null;
    }

    public static int setAlphaComponent(int color, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
        return (color & 0x00ffffff) | (alpha << 24);
    }

    private Point centerPoint(Rectangle rect) {
        int x = (int) (rect.getX() + rect.getWidth() / 2);
        int y = (int) (rect.getY() + rect.getHeight() / 2);
        return new Point(x, y);
    }
}