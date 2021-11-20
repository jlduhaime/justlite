package net.runelite.client.plugins.olmhelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.Color;
import java.awt.Font;

@ConfigGroup("CoX")
public interface OlmHelperConfig extends Config {
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

    @ConfigSection(
            name = "Olm Config",
            description = "Settings for Olm fight",
            position = 0
    )
    String olmSettings = "olmSetting";

    @ConfigItem(
            position = 1,
            keyName = "prayAgainstOlm",
            name = "Olm Show Prayer",
            description = "Shows what prayer to use against Olm",
            section = olmSettings
    )
    default boolean prayAgainstOlm() {
        return true;
    }

    @Range(
            min = 40,
            max = 100
    )
    @ConfigItem(
            position = 2,
            keyName = "prayAgainstOlmSize",
            name = "Olm prayer size",
            description = "Change the size of the Olm infobox",
            section = olmSettings
    )
    default int prayAgainstOlmSize() {
        return 40;
    }

    @ConfigItem(
            position = 3,
            keyName = "timers",
            name = "Olm show burn/acid timers",
            description = "Shows tick timers for burns / acids",
            section = olmSettings
    )
    default boolean timers() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "tpOverlay",
            name = "Show teleport overlays",
            description = "Shows overlays for targeted players",
            section = olmSettings
    )
    default boolean tpOverlay() {
        return true;
    }

    @ConfigItem(
            position = 5,
            keyName = "olmTick",
            name = "Olm Tick Counter",
            description = "Show tick counter on Olm",
            section = olmSettings
    )
    default boolean olmTick() {
        return true;
    }

    @ConfigSection(
            name = "Colors",
            description = "",
            position = 6
    )
    String colorSettings = "Colors";

    @ConfigItem(
            position = 7,
            keyName = "burnColor",
            name = "Burn Victim Color",
            description = "Changes the tile color for burn victim",
            section = colorSettings
    )
    default Color burnColor() {
        return new Color(255, 100, 0);
    }

    @ConfigItem(
            position = 8,
            keyName = "acidColor",
            name = "Acid Victim Color",
            description = "Changes the tile color for acid victim",
            section = colorSettings
    )
    default Color acidColor() {
        return new Color(69, 241, 44);
    }

    @ConfigItem(
            position = 9,
            keyName = "tpColor",
            name = "Teleport Target Color",
            description = "Changes the tile color for the teleport target",
            section = colorSettings
    )
    default Color tpColor() {
        return new Color(193, 255, 245);
    }

    @ConfigSection(
            name = "Text",
            description = "",
            position = 10
    )
    String textSettings = "Text";

    @ConfigItem(
            position = 11,
            keyName = "fontStyle",
            name = "Font Style",
            description = "Bold/Italics/Plain",
            section = textSettings
    )
    default FontStyle fontStyle() {
        return FontStyle.BOLD;
    }

    @ConfigItem(
            position = 12,
            keyName = "textSize",
            name = "Text Size",
            description = "Text size for timers",
            section = textSettings
    )
    default int textSize() {
        return 14;
    }

    @ConfigItem(
            position = 13,
            keyName = "shadows",
            name = "Shadows",
            description = "Adds shadows to text",
            section = textSettings
    )
    default boolean shadows() {
        return true;
    }
}
