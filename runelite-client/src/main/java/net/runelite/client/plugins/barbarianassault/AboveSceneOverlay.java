package net.runelite.client.plugins.barbarianassault;

import com.google.common.collect.ImmutableMap;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;

import java.awt.*;
import java.util.Map;

import static net.runelite.client.plugins.barbarianassault.Role.COLLECTOR;
import static net.runelite.client.plugins.barbarianassault.Role.HEALER;

public class AboveSceneOverlay extends Overlay {

    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int HEALTH_BAR_WIDTH = 115;
    private static final int CENTER_OFFSET = Perspective.LOCAL_HALF_TILE_SIZE / 8;
    private static final int EGG_DIAMETER = Perspective.LOCAL_HALF_TILE_SIZE / 4;
    private static final Color HEALTH_BAR_COLOR = new Color(225, 35, 0, 125);
    private static final ImmutableMap<WidgetInfo, Point> TEAMMATES = ImmutableMap.of(
            WidgetInfo.BA_HEAL_TEAMMATE1, new Point(28, 2),
            WidgetInfo.BA_HEAL_TEAMMATE2, new Point(26, 2),
            WidgetInfo.BA_HEAL_TEAMMATE3, new Point(26, 2),
            WidgetInfo.BA_HEAL_TEAMMATE4, new Point(25, 2));

    private final Client client;
    private final BarbarianAssaultPlugin game;

    @Inject
    private AboveSceneOverlay(final Client client, final BarbarianAssaultPlugin game)
    {
        super(game);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.game = game;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!game.isInGame() || game.getRole() == null || game.isUsingGloryHorn())
        {
            return null;
        }

        switch (game.getRole())
        {

            case HEALER:
                if (game.isShowTeammateHealthbars())
                {
                    renderHealthBars(graphics);
                }
                if (game.isHealerCodes())
                {
                    renderHealerCodes(graphics);
                }
                break;


            case COLLECTOR:
                if (game.isHighlightCollectorEggs())
                {
                    renderEggs(graphics);
                }
                break;
        }
        return null;
    }

    private void renderHealthBars(Graphics2D graphics)
    {
        for (Map.Entry<WidgetInfo, Point> teammate : TEAMMATES.entrySet())
        {
            Widget widget = client.getWidget(teammate.getKey());
            if (widget == null)
            {
                continue;
            }

            // This will give us two elements, the first will be current health, and the second will be max health
            String[] teammateHealth = widget.getText().split(" / ");

            graphics.setColor(HEALTH_BAR_COLOR);
            graphics.fillRect((widget.getCanvasLocation().getX() - teammate.getValue().getX()),
                    (widget.getCanvasLocation().getY() - teammate.getValue().getY()),
                    getBarWidth(Integer.parseInt(teammateHealth[1]), Integer.parseInt(teammateHealth[0])),
                    HEALTH_BAR_HEIGHT);
        }
    }

    private int getBarWidth(int base, int current)
    {
        final double ratio = (double) current / base;

        if (ratio >= 1)
        {
            return HEALTH_BAR_WIDTH;
        }

        return (int) Math.round(ratio * HEALTH_BAR_WIDTH);
    }

    private void renderHealerCodes(Graphics2D graphics)
    {
        for (Healer healer : game.getHealers().values())
        {
            Color color = Color.GREEN;
            int timeLeft = 0;

            if (game.getWave() != null)
            {
                timeLeft = healer.getLastFoodTime() - (int) game.getWave().getWaveTimer().getElapsedTime();
            }

            timeLeft = timeLeft < 1 ? 0 : timeLeft;

            if (timeLeft > 0)
            {
                color = Color.RED;
            }

            String text = String.format("%d  %d", healer.getFoodRemaining(), timeLeft);

            OverlayUtil.renderActorOverlay(graphics, healer.getNpc(), text, color);
        }
    }

    private void renderEggs(Graphics2D graphics)
    {
        final Color color = graphics.getColor();
        final Stroke originalStroke = graphics.getStroke();
        String listen = game.getLastListenText();
        if (listen != null && !listen.equals("- - -"))
        {
            graphics.setStroke(new BasicStroke(2));
            //TODO Render quantity text as well
            //TODO add config options for overlay colors
            switch (listen)
            {
                case "Red eggs":
                    graphics.setColor(new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 150));
                    game.getRedEggs().forEach((point, quantity) -> drawCircle(graphics, LocalPoint.fromWorld(client, point)));
                    break;
                case "Green eggs":
                    graphics.setColor(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 150));
                    game.getGreenEggs().forEach((point, quantity) -> drawCircle(graphics, LocalPoint.fromWorld(client, point)));
                    break;
                case "Blue eggs":
                    graphics.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 150));
                    game.getBlueEggs().forEach((point, quantity) -> drawCircle(graphics, LocalPoint.fromWorld(client, point)));
                    break;
            }
        }
        graphics.setColor(new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 150));
        game.getYellowEggs().forEach((point, quantity) -> drawCircle(graphics, LocalPoint.fromWorld(client, point)));
        graphics.setColor(color);
        graphics.setStroke(originalStroke);
    }

    private void drawCircle(Graphics2D graphics, LocalPoint point)
    {
        if (point == null)
        {
            return;
        }

        Point canvasPoint = Perspective.localToCanvas(client, point, 0);
        if (canvasPoint == null)
        {
            return;
        }

        //TODO rendering a model would be better / more accurate
        graphics.fillOval(canvasPoint.getX() - CENTER_OFFSET, canvasPoint.getY() - CENTER_OFFSET, EGG_DIAMETER, EGG_DIAMETER);
    }
}