package net.runelite.client.plugins.zulrah;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.client.ui.overlay.RenderableEntity;

public class TextComponent implements RenderableEntity {
    @Setter(AccessLevel.PACKAGE)
    private String text;

    @Setter(AccessLevel.PACKAGE)
    private Point position = new Point();

    @Setter(AccessLevel.PACKAGE)
    private Color color = Color.WHITE;

    @Override
    public Dimension render(Graphics2D graphics) {
        // Draw shadow
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, position.x + 1, position.y + 1);

        // Draw actual text
        graphics.setColor(color);
        graphics.drawString(text, position.x, position.y);

        final FontMetrics fontMetrics = graphics.getFontMetrics();
        return new Dimension(fontMetrics.stringWidth(text), fontMetrics.getHeight());
    }
}