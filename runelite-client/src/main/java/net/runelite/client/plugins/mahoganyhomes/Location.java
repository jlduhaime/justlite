package net.runelite.client.plugins.mahoganyhomes;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

import javax.annotation.Nullable;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum Location {
    ARDY(ItemID.ARDOUGNE_TELEPORT),
    HOME(ItemID.TELEPORT_TO_HOUSE),
    VARROCK(ItemID.VARROCK_TELEPORT),
    FALADOR(ItemID.FALADOR_TELEPORT);

    private final int itemId;

    private static final Map<Integer, Location> ID_MAP;

    static
    {
        ImmutableMap.Builder<Integer, Location> builder = new ImmutableMap.Builder<>();

        for (Location item : values())
        {
            builder.put(item.getItemId(), item);
        }

        ID_MAP = builder.build();
    }

    @Nullable
    static Location findItem(int itemId)
    {
        return ID_MAP.get(itemId);
    }
}
