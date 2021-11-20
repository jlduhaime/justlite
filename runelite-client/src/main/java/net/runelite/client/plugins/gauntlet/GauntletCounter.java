package net.runelite.client.plugins.gauntlet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;

import static net.runelite.client.plugins.gauntlet.GauntletConfig.CounterDisplay.NONE;
import static net.runelite.client.plugins.gauntlet.GauntletConfig.CounterDisplay.ONBOSS;

import net.runelite.client.plugins.gauntlet.overlay.TableAlignment;
import net.runelite.client.plugins.gauntlet.overlay.TableComponent;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

@Singleton
public class GauntletCounter extends Overlay {
    private final GauntletPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    GauntletCounter(final GauntletPlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        final Hunllef hunllef = plugin.getHunllef();

        if (!plugin.fightingBoss() ||
                hunllef == null ||
                plugin.getCountAttacks() == NONE ||
                plugin.getCountAttacks() == ONBOSS) {
            return null;
        }

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Hunllef")
                .color(Color.pink)
                .build());


        Color color = hunllef.getPlayerAttacks() == 1 ? Color.RED : Color.WHITE;
        final String pHits = ColorUtil.prependColorTag(Integer.toString(hunllef.getPlayerAttacks()), color);

        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
        tableComponent.addRow("Hunllef Hits: ", Integer.toString(hunllef.getBossAttacks()));
        tableComponent.addRow("Player Hits Left: ", pHits);
        panelComponent.getChildren().add(tableComponent);
        return panelComponent.render(graphics);
    }
}
