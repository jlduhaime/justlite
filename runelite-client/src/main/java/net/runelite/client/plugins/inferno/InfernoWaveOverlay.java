package net.runelite.client.plugins.inferno;

import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.client.plugins.inferno.displaymodes.InfernoWaveDisplayMode;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

import static net.runelite.client.plugins.inferno.InfernoWaveMappings.addWaveComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class InfernoWaveOverlay extends Overlay {
    private final InfernoPlugin plugin;
    private final PanelComponent panelComponent;

    @Setter(AccessLevel.PACKAGE)
    private Color waveHeaderColor;

    @Setter(AccessLevel.PACKAGE)
    private Color waveTextColor;

    @Setter(AccessLevel.PACKAGE)
    private InfernoWaveDisplayMode displayMode;

    @Inject
    InfernoWaveOverlay(final InfernoPlugin plugin) {
        this.panelComponent = new PanelComponent();
        this.setPosition(OverlayPosition.TOP_RIGHT);
        this.setPriority(OverlayPriority.HIGH);
        this.plugin = plugin;

        panelComponent.setPreferredSize(new Dimension(160, 0));
    }

    public Dimension render(final Graphics2D graphics) {
        panelComponent.getChildren().clear();

        if (displayMode == InfernoWaveDisplayMode.CURRENT || displayMode == InfernoWaveDisplayMode.BOTH) {
            addWaveComponent(
                    plugin,
                    panelComponent,
                    "Current Wave (Wave " + plugin.getCurrentWaveNumber() + ")",
                    plugin.getCurrentWaveNumber(),
                    waveHeaderColor,
                    waveTextColor
            );
        }

        if (displayMode == InfernoWaveDisplayMode.NEXT || displayMode == InfernoWaveDisplayMode.BOTH) {
            addWaveComponent(
                    plugin,
                    panelComponent,
                    "Next Wave (Wave " + plugin.getNextWaveNumber() + ")",
                    plugin.getNextWaveNumber(),
                    waveHeaderColor,
                    waveTextColor
            );
        }

        return panelComponent.render(graphics);
    }
}
