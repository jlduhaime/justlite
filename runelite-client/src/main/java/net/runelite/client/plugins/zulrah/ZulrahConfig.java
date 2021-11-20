package net.runelite.client.plugins.zulrah;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("zulrah")

public interface ZulrahConfig extends Config {
    @ConfigItem(
            keyName = "alerts",
            name = "Alerts Enabled",
            description = "Configures whether client alerts are enabled for Zulrah"
    )
    default boolean alerts() {
        return true;
    }
}
