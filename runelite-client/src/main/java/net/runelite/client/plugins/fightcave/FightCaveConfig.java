package net.runelite.client.plugins.fightcave;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("fightcave")
public interface FightCaveConfig extends Config {
    @ConfigSection(
            name = "Main config",
            description = "",
            position = 0
    )
    String mainConfig = "mainConfig";

    @ConfigItem(
            position = 1,
            section = mainConfig,
            name = "Wave display",
            description = "Shows monsters that will spawn on the selected wave(s)",
            keyName = "waveDisplay"
    )
    default WaveDisplayMode waveDisplay() {
        return WaveDisplayMode.BOTH;
    }

    @ConfigItem(
            position = 2,
            keyName = "tickTimersWidget",
            name = "Tick Timers in Prayer",
            description = "Adds an overlay to the prayer interface with the ticks until next attack for that prayer",
            section = mainConfig
    )
    default boolean tickTimersWidget() {
        return true;
    }

    @ConfigSection(
            name = "Text",
            description = "",
            position = 3
    )
    String textSection = "textSection";

    @ConfigItem(
            position = 4,
            keyName = "fontStyle",
            name = "Font Style",
            description = "Plain | Bold | Italics",
            section = textSection
    )
    default FontStyle fontStyle() {
        return FontStyle.BOLD;
    }

    @Range(
            min = 14,
            max = 40
    )
    @ConfigItem(
            position = 5,
            keyName = "textSize",
            name = "Text Size",
            description = "Text Size for Timers",
            section = textSection
    )
    default int textSize() {
        return 32;
    }

    @ConfigItem(
            position = 6,
            keyName = "shadows",
            name = "Shadows",
            description = "Adds shadows to text",
            section = textSection
    )
    default boolean shadows() {
        return false;
    }

    @Getter
    @AllArgsConstructor
    enum FontStyle {
        BOLD("Bold", Font.BOLD),
        ITALIC("Italic", Font.ITALIC),
        PLAIN("Plain", Font.PLAIN);

        private String name;
        private int font;

        @Override
        public String toString() {
            return getName();
        }
    }
}
