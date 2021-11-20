package net.runelite.client.plugins.zulrah.overlays;

import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.zulrah.ImagePanelComponent;
import net.runelite.client.plugins.zulrah.ZulrahInstance;
import net.runelite.client.plugins.zulrah.ZulrahPlugin;
import net.runelite.client.plugins.zulrah.phase.ZulrahPhase;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
public class ZulrahCurrentPhaseOverlay extends Overlay {
    private final ZulrahPlugin plugin;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    ZulrahCurrentPhaseOverlay(final ZulrahPlugin plugin) {
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

        ZulrahPhase currentPhase = instance.getPhase();
        if (currentPhase == null) {
            return null;
        }

        String pattern = instance.getPattern() != null ? instance.getPattern().toString() : "Unknown";
        String title = currentPhase.isJad() ? "JAD PHASE" : pattern;
        Color backgroundColor = currentPhase.getColor();
        BufferedImage zulrahImage;

        switch (currentPhase.getType()) {
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
        imagePanelComponent.setTitle(title);
        imagePanelComponent.setBackgroundColor(backgroundColor);
        imagePanelComponent.setImage(zulrahImage);
        return imagePanelComponent.render(graphics);
    }
}