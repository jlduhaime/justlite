package net.runelite.client.plugins.inventorysetups;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class InventorySetupsItem
{
    @Getter
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final int quantity;
    @Getter
    @Setter
    private boolean fuzzy;
    @Getter
    @Setter
    private InventorySetupsStackCompareID stackCompare;

    public void toggleIsFuzzy()
    {
        fuzzy = !fuzzy;
    }

    public static InventorySetupsItem getDummyItem()
    {
        return new InventorySetupsItem(-1, "", 0, false, InventorySetupsStackCompareID.None);
    }

}