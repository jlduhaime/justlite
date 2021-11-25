package net.runelite.client.plugins.inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum InventorySetupsFilteringModeID
{
    // All items
    ALL(0),

    // Only Inventory
    INVENTORY(1),

    // Only Equipment
    EQUIPMENT(2),

    // Only Additional Filtered Items
    ADDITIONAL_FILTERED_ITEMS(3);

    private final int type;

    private static final List<InventorySetupsFilteringModeID> VALUES;

    static
    {
        VALUES = new ArrayList<>();
        Collections.addAll(VALUES, InventorySetupsFilteringModeID.values());
    }

    InventorySetupsFilteringModeID(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    public static List<InventorySetupsFilteringModeID> getValues()
    {
        return VALUES;
    }
}
