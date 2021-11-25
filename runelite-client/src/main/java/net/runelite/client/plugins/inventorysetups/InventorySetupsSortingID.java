package net.runelite.client.plugins.inventorysetups;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventorySetupsSortingID
{
    DEFAULT("Default", 0),
    ALPHABETICAL("Alphabetical", 1);

    @Override
    public String toString()
    {
        return name;
    }

    private final String name;
    private final int identifier;
}
