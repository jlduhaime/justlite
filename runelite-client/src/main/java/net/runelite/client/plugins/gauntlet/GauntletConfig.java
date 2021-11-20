package net.runelite.client.plugins.gauntlet;

import java.awt.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Gauntlet")

public interface GauntletConfig extends Config {
    @Getter
    @AllArgsConstructor
    enum CounterDisplay {
        ONBOSS("On Boss"),
        INFOBOX("Info Box"),
        BOTH("Both"),
        NONE("None");

        private String name;

        @Override
        public String toString() {
            return getName();
        }
    }

    @ConfigSection(
            name = "resources",
            description = "",
            position = 0
    )
    String resourceSettings = "resourceSettings";

    @ConfigItem(
            position = 1,
            keyName = "highlightResources",
            name = "Highlight Resources (Outline)",
            description = "Highlights all the resources in each room with an outline.",
            section = resourceSettings
    )
    default boolean highlightResources() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "highlightResourcesColor",
            name = "Highlight Color",
            description = "Highlights all the resources in each room with this color.",
            section = resourceSettings
    )
    default Color highlightResourcesColor() {
        return Color.YELLOW;
    }

    @ConfigItem(
            position = 3,
            keyName = "highlightResourcesIcons",
            name = "Highlight Resources (Icon)",
            description = "Highlights all the icons in each room with an icon.",
            section = resourceSettings
    )
    default boolean highlightResourcesIcons() {
        return false;
    }

    @Range(
            min = 1,
            max = 50
    )
    @ConfigItem(
            position = 4,
            keyName = "resourceIconSize",
            name = "Resource Icon Size",
            description = " change the size of resource icons.",
            section = resourceSettings
    )
    default int resourceIconSize() {
        return 20;
    }

    @ConfigSection(
            name = "boss",
            description = "",
            position = 5
    )
    String bossSettings = "bossSettings";

    @ConfigItem(
            position = 6,
            keyName = "countAttacks",
            name = "Count Attacks Display",
            description = "Count the attacks until the Hunllef switches their attack style and prayer.",
            section = bossSettings
    )
    default CounterDisplay countAttacks() {
        return CounterDisplay.NONE;
    }

    @ConfigItem(
            position = 7,
            keyName = "highlightWidget",
            name = "Highlight Prayer (Prayer Tab)",
            description = "Highlights the correct prayer to use in your prayer book.",
            section = bossSettings
    )
    default boolean highlightWidget() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "highlightPrayerInfobox",
            name = "Highlight Prayer (InfoBox)",
            description = "Highlights the correct prayer to use in an Infobox.",
            section = bossSettings
    )
    default boolean highlightPrayerInfobox() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "flashOnWrongAttack",
            name = "Flash screen on Wrong Attack",
            description = "This will flash your screen if you attack with the wrong stlye.",
            section = bossSettings
    )
    default boolean flashOnWrongAttack() {
        return false;
    }

    @ConfigItem(
            position = 10,
            keyName = "uniquePrayerAudio",
            name = "Prayer Audio Warning",
            description = "Plays a unique sound whenever the boss is about to shut down your prayer.",
            section = bossSettings
    )
    default boolean uniquePrayerAudio() {
        return false;
    }

    @ConfigItem(
            position = 11,
            keyName = "uniquePrayerVisual",
            name = "Prayer Attack (Icon)",
            description = "Prayer attacks will have a unique overlay visual.",
            section = bossSettings
    )
    default boolean uniquePrayerVisual() {
        return false;
    }

    @ConfigItem(
            position = 12,
            keyName = "uniqueAttackVisual",
            name = "Magic & Range Attack (Icon)",
            description = "Magic and Range attacks will have a unique overlay visual.",
            section = bossSettings
    )
    default boolean uniqueAttackVisual() {
        return false;
    }

    @ConfigItem(
            position = 13,
            keyName = "attackVisualOutline",
            name = "Hunllefs' attacks (Outline)",
            description = "Outline the Hunllefs' attacks.",
            section = bossSettings
    )
    default boolean attackVisualOutline() {
        return false;
    }

    @ConfigItem(
            position = 14,
            keyName = "overlayBoss",
            name = "Outline Hunllef (Color)",
            description = "Overlay Hunllef while you are on the wrong prayer with an color denoting it's current attack style.",
            section = bossSettings
    )
    default boolean overlayBoss() {
        return false;
    }


    @ConfigItem(
            position = 15,
            keyName = "overlayBossPrayer",
            name = "Hunllef Overlay (Icons)",
            description = "Overlay the Hunllef with an icon denoting it's current attack style.",
            section = bossSettings
    )
    default boolean overlayBossPrayer() {
        return false;
    }

    @ConfigItem(
            position = 16,
            keyName = "overlayTornadoes",
            name = "Show Tornado Decay",
            description = "Display the amount of ticks left until the tornadoes decay.",
            section = bossSettings
    )
    default boolean overlayTornadoes() {
        return false;
    }

    @Range(
            min = 1,
            max = 50
    )
    @ConfigItem(
            position = 17,
            keyName = "projectileIconSize",
            name = "Hunllef Projectile Icon Size",
            description = " change the size of Projectile icons.",
            section = bossSettings
    )
    default int projectileIconSize() {
        return 20;
    }

    @ConfigSection(
            name = "Timer",
            description = "",
            position = 18
    )
    String timerSettings = "timer";

    @ConfigItem(
            position = 19,
            keyName = "displayTimerWidget",
            name = "Show Gauntlet timer overlay",
            description = "Display a timer widget that tracks your gauntlet progress.",
            section = timerSettings
    )
    default boolean displayTimerWidget() {
        return false;
    }

    @ConfigItem(
            position = 20,
            keyName = "displayTimerChat",
            name = "Show Gauntlet timer chat message",
            description = "Display a chat message that tracks your gauntlet progress.",
            section = timerSettings
    )
    default boolean displayTimerChat() {
        return false;
    }

    @ConfigItem(
            position = 21,
            keyName = "displayResources",
            name = "Show raw resources gathered",
            description = "Displays how much of each resource you have gathered.",
            section = resourceSettings
    )
    default boolean displayGatheredResources() {
        return false;
    }
}
