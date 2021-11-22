package net.runelite.client.plugins.fightcave;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import net.runelite.api.*;

import java.awt.*;

@Getter(AccessLevel.PACKAGE)
class FightCaveContainer {
    private NPC npc;
    private String npcName;
    private int npcIndex;
    private int npcSize;
    private int priority;
    private ImmutableSet<Integer> animations;
    @Setter(AccessLevel.PACKAGE)
    private int ticksUntilAttack;
    @Setter(AccessLevel.PACKAGE)
    private Actor npcInteracting;
    @Setter(AccessLevel.PACKAGE)
    private AttackStyle attackStyle;
    private int attackSpeed;

    FightCaveContainer(NPC npc) {
        this.npc = npc;
        this.npcName = npc.getName();
        this.npcIndex = npc.getIndex();
        this.npcInteracting = npc.getInteracting();
        this.attackStyle = AttackStyle.UNKNOWN;
        this.ticksUntilAttack = -1;
        final NPCComposition composition = npc.getTransformedComposition();

        BossMonsters monster = BossMonsters.of(npc.getId());

        if (monster == null) {
            throw new IllegalStateException();
        }

        this.animations = monster.animations;
        this.attackStyle = monster.attackStyle;
        this.priority = monster.priority;
        this.attackSpeed = monster.attackSpeed;
        if (composition != null) {
            this.npcSize = composition.getSize();
        }
    }

    @RequiredArgsConstructor
    enum BossMonsters {
        TOK_XIL1(NpcID.TOKXIL_3121, AttackStyle.RANGE, ImmutableSet.of(AnimationID.TOK_XIL_RANGE_ATTACK, AnimationID.TOK_XIL_MELEE_ATTACK), 1, 4),
        TOK_XIL2(NpcID.TOKXIL_3122, AttackStyle.RANGE, ImmutableSet.of(AnimationID.TOK_XIL_RANGE_ATTACK, AnimationID.TOK_XIL_MELEE_ATTACK), 1, 4),
        KETZEK1(NpcID.KETZEK, AttackStyle.MAGE, ImmutableSet.of(AnimationID.KET_ZEK_MAGE_ATTACK, AnimationID.KET_ZEK_MELEE_ATTACK), 0, 4),
        KETZEK2(NpcID.KETZEK_3126, AttackStyle.MAGE, ImmutableSet.of(AnimationID.KET_ZEK_MAGE_ATTACK, AnimationID.KET_ZEK_MELEE_ATTACK), 0, 4),
        YTMEJKOT1(NpcID.YTMEJKOT, AttackStyle.MELEE, ImmutableSet.of(AnimationID.MEJ_KOT_HEAL_ATTACK, AnimationID.MEJ_KOT_MELEE_ATTACK), 2, 4),
        YTMEJKOT2(NpcID.YTMEJKOT_3124, AttackStyle.MELEE, ImmutableSet.of(AnimationID.MEJ_KOT_HEAL_ATTACK, AnimationID.MEJ_KOT_MELEE_ATTACK), 2, 4),
        TZTOKJAD1(NpcID.TZTOKJAD, AttackStyle.UNKNOWN, ImmutableSet.of(AnimationID.TZTOK_JAD_MAGIC_ATTACK, AnimationID.TZTOK_JAD_RANGE_ATTACK, AnimationID.TZTOK_JAD_MELEE_ATTACK), 0, 8),
        TZTOKJAD2(NpcID.TZTOKJAD_6506, AttackStyle.UNKNOWN, ImmutableSet.of(AnimationID.TZTOK_JAD_MAGIC_ATTACK, AnimationID.TZTOK_JAD_RANGE_ATTACK, AnimationID.TZTOK_JAD_MELEE_ATTACK), 0, 8);

        private static final ImmutableMap<Integer, BossMonsters> idMap;

        static {
            ImmutableMap.Builder<Integer, BossMonsters> builder = ImmutableMap.builder();

            for (BossMonsters monster : values()) {
                builder.put(monster.npcID, monster);
            }

            idMap = builder.build();
        }

        private final int npcID;
        private final AttackStyle attackStyle;
        private final ImmutableSet<Integer> animations;
        private final int priority;
        private final int attackSpeed;

        static BossMonsters of(int npcID) {
            return idMap.get(npcID);
        }
    }

    @Getter(AccessLevel.PACKAGE)
    @AllArgsConstructor
    enum AttackStyle {
        MAGE("Mage", Color.CYAN, Prayer.PROTECT_FROM_MAGIC),
        RANGE("Range", Color.GREEN, Prayer.PROTECT_FROM_MISSILES),
        MELEE("Melee", Color.RED, Prayer.PROTECT_FROM_MELEE),
        UNKNOWN("Unkown", Color.WHITE, null);

        private String name;
        private Color color;
        private Prayer prayer;
    }

}
