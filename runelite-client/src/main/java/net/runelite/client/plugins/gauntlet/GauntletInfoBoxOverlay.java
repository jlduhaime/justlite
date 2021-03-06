package net.runelite.client.plugins.gauntlet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class GauntletInfoBoxOverlay extends Overlay {
    private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);
    private final Client client;
    private final GauntletPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private final SpriteManager spriteManager;

    @Inject
    GauntletInfoBoxOverlay(final Client client, final GauntletPlugin plugin, final SpriteManager spriteManager) {
        this.client = client;
        this.plugin = plugin;
        this.spriteManager = spriteManager;
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        if (!plugin.isHighlightPrayerInfobox() || !plugin.fightingBoss() || plugin.getHunllef() == null) {
            return null;
        }

        final Hunllef hunllef = plugin.getHunllef();
        final Hunllef.BossAttackPhase phase = hunllef.getCurrentPhase();
        Prayer prayer = phase.getPrayer();

        if (plugin.getHunllef() == null || !plugin.startedGauntlet()) {
            return null;
        }
        if (prayer == null) {
            return null;
        }

        InfoBoxComponent prayComponent = new InfoBoxComponent();
        BufferedImage prayImg = scaleImg(getPrayerImage(prayer));
        prayComponent.setImage(prayImg);
        prayComponent.setColor(Color.WHITE);
        prayComponent.setBackgroundColor(client.isPrayerActive(prayer)
                ? ComponentConstants.STANDARD_BACKGROUND_COLOR
                : NOT_ACTIVATED_BACKGROUND_COLOR);
        prayComponent.setPreferredSize(new Dimension(40, 40));
        panelComponent.getChildren().add(prayComponent);
        panelComponent.setPreferredSize(new Dimension(40, 40));
        panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
        return panelComponent.render(graphics);
    }

    private BufferedImage getPrayerImage(Prayer prayer) {
        switch (prayer) {
            case PROTECT_FROM_MAGIC:
                return spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0);
            case PROTECT_FROM_MELEE:
                return spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0);
            case PROTECT_FROM_MISSILES:
                return spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0);
        }
        return null;
    }

    private static BufferedImage scaleImg(final BufferedImage img) {
        if (img == null) {
            return null;
        }
        final double width = img.getWidth(null);
        final double height = img.getHeight(null);
        final double size = 36; // Limit size to 2 as that is minimum size not causing breakage
        final double scalex = size / width;
        final double scaley = size / height;
        final double scale = Math.min(scalex, scaley);
        final int newWidth = (int) (width * scale);
        final int newHeight = (int) (height * scale);
        final BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics g = scaledImage.createGraphics();
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return scaledImage;
    }
}
