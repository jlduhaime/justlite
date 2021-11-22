package net.runelite.client.plugins.fightcave;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.gauntlet.overlay.TableAlignment;
import net.runelite.client.plugins.gauntlet.overlay.TableComponent;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@Singleton
public class FightCaveOverlay extends Overlay {

    private final FightCavePlugin plugin;
    private final Client client;
    private final PanelComponent panelComponent = new PanelComponent();
    private static final Color HEADER_COLOR = ColorScheme.BRAND_ORANGE;

    @Inject
    FightCaveOverlay(final Client client, final FightCavePlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        for (FightCaveContainer npc : plugin.getFightCaveContainer()) {
            if (npc.getNpc() == null)
                continue;

            final int ticksLeft = npc.getTicksUntilAttack();
            final FightCaveContainer.AttackStyle attackStyle = npc.getAttackStyle();

            if (ticksLeft <= 0)
                continue;

            final String ticksLeftStr = String.valueOf(ticksLeft);
            final int font = plugin.getFontStyle().getFont();
            final boolean shadows = plugin.isShadows();
            Color color = (ticksLeft <= 1 ? Color.WHITE : attackStyle.getColor());
            final Point canvasPoint = npc.getNpc().getCanvasTextLocation(graphics, Integer.toString(ticksLeft), 0);

            if (npc.getNpcName().equals("TzTok-Jad")) {
                color = (ticksLeft <= 1 || ticksLeft == 8 ? attackStyle.getColor() : Color.WHITE);
                BufferedImage pray = getPrayerImage(npc.getAttackStyle());
                if (pray == null)
                    continue;
                renderImageLocation(graphics, npc.getNpc().getCanvasImageLocation(ImageUtil.resizeImage(pray, 36, 36), 0), pray, 12, 30);
            }

            OverlayUtil.renderTextLocation(graphics, ticksLeftStr, plugin.getTextSize(), font, color, canvasPoint, shadows, 0);
        }

        if (plugin.isTickTimersWidget()) {
            TableComponent tableComponent = new TableComponent();
            if (!plugin.getMageTicks().isEmpty()) {
                log.debug("mage ticks: {}", plugin.getMageTicks().get(0));
                Color color = plugin.getMageTicks().get(0) >= 2 ? Color.WHITE : Color.RED;
                final String mTicks = ColorUtil.prependColorTag(Integer.toString(plugin.getMageTicks().get(0)), color);
                tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
                tableComponent.addRow("Mage ticks: ", mTicks);
            }
            if (!plugin.getRangedTicks().isEmpty()) {
                log.debug("mage ticks: {}", plugin.getRangedTicks().get(0));
                Color color = plugin.getRangedTicks().get(0) >= 2 ? Color.WHITE : Color.RED;
                final String rTicks = ColorUtil.prependColorTag(Integer.toString(plugin.getRangedTicks().get(0)), color);
                tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
                tableComponent.addRow("Range ticks: ", rTicks);
            }
            if (!plugin.getMeleeTicks().isEmpty()) {
                log.debug("mage ticks: {}", plugin.getMeleeTicks().get(0));
                Color color = plugin.getMeleeTicks().get(0) >= 2 ? Color.WHITE : Color.RED;
                final String mTicks = ColorUtil.prependColorTag(Integer.toString(plugin.getMeleeTicks().get(0)), color);
                tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
                tableComponent.addRow("Range ticks: ", mTicks);
            }
            panelComponent.getChildren().add(tableComponent);
        }
        return panelComponent.render(graphics);
    }

    private BufferedImage getPrayerImage(FightCaveContainer.AttackStyle attackStyle) {
        SkillIconManager mgr = new SkillIconManager();
        switch (attackStyle) {
            case MAGE:
                return mgr.getSkillImage(Skill.MAGIC);
            case MELEE:
                return mgr.getSkillImage(Skill.ATTACK);
            case RANGE:
                return mgr.getSkillImage(Skill.RANGED);
        }

        return null;
    }

    private void renderImageLocation(Graphics2D graphics, Point imgLoc, BufferedImage image, int xOffset, int yOffset) {
        int x = imgLoc.getX() + xOffset;
        int y = imgLoc.getY() - yOffset;

        graphics.drawImage(image, x, y, null);
    }
}
