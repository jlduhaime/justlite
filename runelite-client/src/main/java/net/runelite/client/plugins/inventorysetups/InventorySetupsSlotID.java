package net.runelite.client.plugins.inventorysetups;

import net.runelite.api.InventoryID;

public enum InventorySetupsSlotID
{

    INVENTORY(0),

    EQUIPMENT(1),

    RUNE_POUCH(2),

    BOLT_POUCH(3),

    SPELL_BOOK(4),

    ADDITIONAL_ITEMS(5);

    private final int id;

    InventorySetupsSlotID(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static InventorySetupsSlotID fromInventoryID(final InventoryID inventoryId)
    {
        if (inventoryId == null)
        {
            return null;
        }

        switch (inventoryId)
        {
            case INVENTORY:
                return INVENTORY;
            case EQUIPMENT:
                return EQUIPMENT;
        }

        return null;
    }

}