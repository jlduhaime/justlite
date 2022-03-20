package net.runelite.client.menus;

import javax.annotation.Nonnull;
import joptsimple.internal.Strings;
import lombok.EqualsAndHashCode;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode(callSuper = true)
public class BaseComparableEntry extends AbstractComparableEntry
{
    /**
     * If two entries are both suppose to be left click,
     * the entry with the higher priority will be selected.
     * This only effects left click priority entries.
     */

    public BaseComparableEntry(String option, String target, int id, int type, boolean strictOption, boolean strictTarget)
    {
        super.option = option.trim().toLowerCase();
        super.target = Text.standardize(target);
        super.id = id;
        super.type = type;
        super.strictOption = strictOption;
        super.strictTarget = strictTarget;
    }

    public boolean matches(@Nonnull MenuEntry entry)
    {
        String opt = entry.getOption();

        if (strictOption && !StringUtils.equalsIgnoreCase(opt, option) || !strictOption && !StringUtils.containsIgnoreCase(opt, option))
        {
            return false;
        }

        if (strictTarget || !Strings.isNullOrEmpty(target))
        {
            String tgt = Text.standardize(entry.getTarget());

            if (strictTarget && !tgt.equals(target) || !strictTarget && !tgt.contains(target))
            {
                return false;
            }
        }

        if (id != -1)
        {
            int id = entry.getIdentifier();

            if (this.id != id)
            {
                return false;
            }
        }

//        if (type != -1)
//        {
//            int type = entry.getOpcode();
//
//            return this.type == type;
//        }

        return true;
    }
}