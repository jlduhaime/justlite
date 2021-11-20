package net.runelite.client.plugins.olmhelper;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Set;

@Singleton
public class OlmHelperOverlay extends Overlay {

    private static final Set<Integer> GAP = ImmutableSet.of(34, 33, 26, 25, 18, 17, 10, 9, 2, 1);
    private final Client client;
    private final OlmHelperPlugin plugin;

    @Inject
    private OlmHelperOverlay(final Client client, final OlmHelperPlugin olmPlugin) {
        this.client = client;
        this.plugin = olmPlugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (WorldPoint point : plugin.getOlm_Heal()) {
            drawTile(graphics, point, plugin.getTpColor(), 2, 150);
        }

        for (WorldPoint point : plugin.getOlm_TP()) {
            client.setHintArrow(point);
            drawTile(graphics, point, plugin.getTpColor(), 2, 150);
        }

        if (plugin.inRaid()) {
            if (plugin.isHandCripple()) {
                int tick = plugin.getCrippleTimer();
                NPC olmHand = plugin.getHand();
                final String tickStr = String.valueOf(tick);
                Point canvasPoint = olmHand.getCanvasTextLocation(graphics, tickStr, 50);
                renderTextLocation(graphics, tickStr, plugin.getTextSize(), plugin.getFontStyle().getFont(), Color.GRAY, canvasPoint);
            }

            if (plugin.isTimers()) {
                if (plugin.getVictims().size() > 0) {
                    plugin.getVictims().forEach(victim -> {
                        final int ticksLeft = victim.getTicks();
                        String ticksLeftStr = String.valueOf(ticksLeft);
                        Color tickColor;
                        switch (victim.getType()) {
                            case ACID:
                                if (ticksLeft > 0) {
                                    if (ticksLeft > 1)
                                        tickColor = new Color(69, 241, 44, 255);
                                    else
                                        tickColor = new Color(255, 255, 255, 255);
                                    Point canvasPoint = victim.getPlayer().getCanvasTextLocation(graphics, ticksLeftStr, 0);
                                    renderTextLocation(graphics, ticksLeftStr, plugin.getTextSize(), plugin.getFontStyle().getFont(), tickColor, canvasPoint);
                                }
                                break;
                            case BURN:
                                if (ticksLeft > 0) {
                                    if (GAP.contains(ticksLeft)) {
                                        tickColor = new Color(255, 0, 0, 255);
                                        ticksLeftStr = "GAP";
                                    } else
                                        tickColor = new Color(255, 255, 255, 255);
                                    Point canvasPoint = victim.getPlayer().getCanvasTextLocation(graphics, ticksLeftStr, 0);
                                    renderTextLocation(graphics, ticksLeftStr, plugin.getTextSize(), plugin.getFontStyle().getFont(), tickColor, canvasPoint);
                                }
                                break;
                            case TELEPORT:
                                if (plugin.isTpOverlay()) {
                                    if (ticksLeft > 0) {
                                        if (ticksLeft > 1)
                                            tickColor = new Color(193, 255, 245, 255);
                                        else
                                            tickColor = new Color(255, 255, 255, 255);
                                        Point canvasPoint = victim.getPlayer().getCanvasTextLocation(graphics, ticksLeftStr, 0);
                                        renderTextLocation(graphics, ticksLeftStr, plugin.getTextSize(), plugin.getFontStyle().getFont(), tickColor, canvasPoint);
                                    }
                                    renderActorOverlay(graphics, victim.getPlayer(), new Color(193, 255, 245, 255));
                                }
                                break;
                        }
                    });
                }
            }
            if (plugin.isRunOlm()) {
                NPC boss = plugin.getOlm_NPC();

                if (plugin.isOlmTick()) {
                    if (boss != null) {
                        final int tick = plugin.getOlm_TicksUntilAction();
                        final int cycle = plugin.getOlm_ActionCycle();
                        final int spec = plugin.getOlm_NextSpec();
                        final String tickStr = String.valueOf(tick);
                        String cycleStr = "?";
                        switch (cycle) {
                            case 1:
                                switch (spec) {
                                    case 1:
                                        cycleStr = "Portals";
                                        break;
                                    case 2:
                                        cycleStr = "lightning";
                                        break;
                                    case 3:
                                        cycleStr = "Crystals";
                                        break;
                                    case 4:
                                        cycleStr = "Heal";
                                        break;
                                    case -1:
                                        cycleStr = "???";
                                        break;
                                }
                                break;
                            case 2:
                                cycleStr = "auto 1";
                                break;
                            case 3:
                                cycleStr = "Null";
                                break;
                            case 4:
                                cycleStr = "auto 2";
                                break;
                            case -1:
                                cycleStr = "???";
                                break;
                        }
                        final String combinedStr = cycleStr + ":" + tickStr;
                        Point canvasPoint = boss.getCanvasTextLocation(graphics, combinedStr, 130);
                        renderTextLocation(graphics, combinedStr, plugin.getTextSize(), plugin.getFontStyle().getFont(), Color.WHITE, canvasPoint);
                    }
                }
            }
        }

        return null;
    }

    private void renderActorOverlay(Graphics2D graphics, Player actor, Color color) {
        final int size = 1;
        final LocalPoint lp = actor.getLocalLocation();
        final Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
        if (tilePoly != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(tilePoly);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
            graphics.fill(tilePoly);
        }
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint) {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null) {
            final Point canvasCenterPoint = new Point(
                    canvasPoint.getX(),
                    canvasPoint.getY()
            );
            final Point canvasCenterPoint_Shadow = new Point(
                    canvasPoint.getX() + 1,
                    canvasCenterPoint.getY() + 1
            );
            if (plugin.isShadows()) {
                OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_Shadow, txtString, Color.BLACK);
            }
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    private void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) >= 32) return;

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) return;

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) return;

        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        graphics.fill(poly);
    }
}
