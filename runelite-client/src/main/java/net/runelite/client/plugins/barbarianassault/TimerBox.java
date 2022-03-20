package net.runelite.client.plugins.barbarianassault;

import java.awt.Color;
import java.awt.image.BufferedImage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

@EqualsAndHashCode(callSuper = true)
@Data
public class TimerBox extends InfoBox
{
    private int count;

    private boolean inSync = true;

    private boolean tooltipEnabled = false;

    TimerBox(BufferedImage image, Plugin plugin, int count)
    {
        super(image, plugin);
        this.count = count;
    }

    @Override
    public String getText()
    {
        if (count == -1)
        {
            return "";
        }
        return Integer.toString(getCount());
    }

    @Override
    public Color getTextColor()
    {
        if (inSync)
        {
            return Color.WHITE;
        }
        else
        {
            return Color.RED;
        }
    }

    @Override
    public String getTooltip()
    {
        if (!tooltipEnabled)
        {
            return "";
        }
        else if (inSync)
        {
            return "<col=00FF00>Valid";
        }
        else
        {
            return "<col=FF0000>Invalid";
        }
    }
}