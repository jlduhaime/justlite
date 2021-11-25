package net.runelite.client.plugins.cerberus;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public enum CerberusGhost
{
    RANGE(NpcID.SUMMONED_SOUL, Skill.RANGED),
    MAGE(NpcID.SUMMONED_SOUL_5868, Skill.MAGIC),
    MELEE(NpcID.SUMMONED_SOUL_5869, Skill.ATTACK);

    private final int npcId;
    private final Skill type;

    private static final Map<Integer, CerberusGhost> MAP;

    static
    {
        ImmutableMap.Builder<Integer, CerberusGhost> builder = new ImmutableMap.Builder<>();

        for (final CerberusGhost ghost : values())
        {
            builder.put(ghost.getNpcId(), ghost);
        }

        MAP = builder.build();
    }

    /**
     * Try to identify if NPC is ghost
     *
     * @param npc npc
     * @return optional ghost
     */
    public static Optional<CerberusGhost> fromNPC(final NPC npc)
    {
        return npc == null ? Optional.empty() : Optional.ofNullable(MAP.get(npc.getId()));
    }
}
