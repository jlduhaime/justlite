package net.runelite.client.plugins.zulrah.overlays;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.zulrah.ImagePanelComponent;
import net.runelite.client.plugins.zulrah.ZulrahInstance;
import net.runelite.client.plugins.zulrah.ZulrahPlugin;
import net.runelite.client.plugins.zulrah.phase.ZulrahPhase;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

@Singleton
@Slf4j
public class ZulrahPrayerOverlay extends Overlay {
    private final Client client;
    private final ZulrahPlugin plugin;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    ZulrahPrayerOverlay(final @Nullable Client client, final ZulrahPlugin plugin) {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setPriority(OverlayPriority.MED);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        ZulrahInstance instance = plugin.getInstance();

        if (instance == null) {
            return null;
        }

        ZulrahPhase currentPhase = instance.getPhase();
        if (currentPhase == null) {
            return null;
        }

        Prayer prayer = currentPhase.isJad() ? null : currentPhase.getPrayer();
        if (prayer == null) {
            return null;
        }

        //BufferedImage prayerImage = ZulrahImageManager.getProtectionPrayerBufferedImage(prayer);
        BufferedImage prayerImage;
        switch (prayer) {
            case PROTECT_FROM_MAGIC:
                ;
                prayerImage = iconManager.getSkillImage(Skill.MAGIC);
                break;
            case PROTECT_FROM_MISSILES:
                prayerImage = iconManager.getSkillImage(Skill.RANGED);
                break;
            default:
                prayerImage = null;
        }
        ImagePanelComponent imagePanelComponent = new ImagePanelComponent();
        imagePanelComponent.setTitle((!client.isPrayerActive(prayer)) ? "Switch!" : "Prayer");
        imagePanelComponent.setImage(prayerImage);
        return imagePanelComponent.render(graphics);
    }
}

