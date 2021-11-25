package net.runelite.client.plugins.inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum InventorySetupsStackCompareID
{
    // Don't highlight at all
    None(0),

    // Only highlight if stacks are equal
    Standard(1),

    // Only highlight if stack is less than what is in the setup
    Less_Than(2),

    // Only highlight if stack is greater than what is in the setup
    Greater_Than(3);

    private final int type;

    private static final List<InventorySetupsStackCompareID> VALUES;

    static
    {
        VALUES = new ArrayList<>();
        Collections.addAll(VALUES, InventorySetupsStackCompareID.values());
    }

    InventorySetupsStackCompareID(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    public static List<InventorySetupsStackCompareID> getValues()
    {
        return VALUES;
    }

    public static String getStringFromValue(final InventorySetupsStackCompareID stackCompare)
    {
        if (stackCompare == null)
        {
            return "";
        }

        switch (stackCompare)
        {
            case None:
                return "";
            case Standard:
                return "!=";
            case Less_Than:
                return "<";
            case Greater_Than:
                return ">";
        }

        return "";
    }

}
