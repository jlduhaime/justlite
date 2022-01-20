package net.runelite.client.plugins.alchemicalhydra;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("betterHydra")
public interface HydraConfig extends Config
{
    @ConfigSection(
            name = "Features",
            position = 0,
            description = "Features"
    )
    String features = "features";

    @ConfigItem(
            keyName = "counting",
            name = "Prayer helper",
            description = "Shows count of switching prayers",
            position = 1,
            section = features
    )
    default boolean counting() { return true; }

    @ConfigItem(
            keyName = "fountain",
            name = "Fountain helper",
            description = "Indicates if hydra is on a fountain",
            position = 2,
            section = features
    )
    default boolean fountain()
    {
        return true;
    }

    @ConfigItem(
            keyName = "stun",
            name = "Stun timer",
            description = "Shows when you can walk in fire phase",
            position = 3,
            section = features
    )
    default boolean stun()
    {
        return false;
    }

    @ConfigSection(
            name = "Colors",
            description = "colours...",
            position = 2
    )
    String colors = "colors";

    @Alpha
    @ConfigItem(
            keyName = "safeCol",
            name = "Safe colour",
            description = "Colour overlay will be when there's >2 attacks left",
            position = 1,
            section = colors
    )
    default Color safeCol()
    {
        return new Color(0, 156, 0, 156);
    }

    @Alpha
    @ConfigItem(
            keyName = "medCol",
            name = "Medium colour",
            description = "Colour overlay will be when a input is coming up",
            position = 2,
            section = colors
    )
    default Color medCol()
    {
        return new Color(200, 156, 0, 156);
    }

    @Alpha
    @ConfigItem(
            keyName = "badCol",
            name = "Bad colour",
            description = "Colour overlay will be when you have to do something NOW",
            position = 3,
            section = colors
    )
    default Color badCol()
    {
        return new Color(156, 0, 0, 156);
    }

    @Alpha
    @ConfigItem(
            keyName = "poisonBorderCol",
            name = "Poison border colour",
            description = "Colour the edges of the area highlighted by poison special will be",
            position = 4,
            section = colors
    )
    default Color poisonBorderCol()
    {
        return new Color(255, 0, 0, 100);
    }

    @Alpha
    @ConfigItem(
            keyName = "poisonCol",
            name = "Poison colour",
            description = "Colour the fill of the area highlighted by poison special will be",
            position = 5,
            section = colors
    )
    default Color poisonCol()
    {
        return new Color(255, 0, 0, 50);
    }

    @Alpha
    @ConfigItem(
            keyName = "fountainColA",
            name = "Fountain colour (not on top)",
            description = "Fountain colour (not on top)",
            position = 6,
            section = colors
    )
    default Color fountainColA()
    {
        return new Color(255, 0, 0, 100);
    }

    @Alpha
    @ConfigItem(
            keyName = "fountainColB",
            name = "Fountain colour (on top)",
            description = "Fountain colour (on top)",
            position = 7,
            section = colors
    )
    default Color fountainColB()
    {
        return new Color(0, 255, 0, 100);
    }
}
