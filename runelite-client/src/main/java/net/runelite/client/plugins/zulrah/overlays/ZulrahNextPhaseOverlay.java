package net.runelite.client.plugins.zulrah.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;

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
public class ZulrahNextPhaseOverlay extends Overlay {
    private final ZulrahPlugin plugin;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    ZulrahNextPhaseOverlay(final ZulrahPlugin plugin) {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setPriority(OverlayPriority.HIGH);
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        ZulrahInstance instance = plugin.getInstance();

        if (instance == null) {
            return null;
        }

        ZulrahPhase nextPhase = instance.getNextPhase();
        if (nextPhase == null) {
            return null;
        }

        Color backgroundColor = nextPhase.getColor();
        BufferedImage zulrahImage;

        switch (nextPhase.getType()) {
            case RANGE:
                zulrahImage = iconManager.getSkillImage(Skill.RANGED);
                break;
            case MAGIC:
                zulrahImage = iconManager.getSkillImage(Skill.MAGIC);
                break;
            case MELEE:
                zulrahImage = iconManager.getSkillImage(Skill.ATTACK);
                break;
            default:
                zulrahImage = null;
        }

        ImagePanelComponent imagePanelComponent = new ImagePanelComponent();
        imagePanelComponent.setTitle("Next");
        imagePanelComponent.setBackgroundColor(backgroundColor);
        imagePanelComponent.setImage(zulrahImage);
        return imagePanelComponent.render(graphics);
    }
}
