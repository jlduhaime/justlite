package net.runelite.client.plugins.aoewarnings;

import net.runelite.api.Projectile;

import static net.runelite.api.GraphicID.*;

import java.util.HashMap;
import java.util.Map;

public enum AoeProjectileInfo {
    LIZARDMAN_SHAMAN_AOE_PROJ(LIZARDMAN_SHAMAN_AOE, 5),
    CRAZY_ARCHAEOLOGIST_AOE_PROJ(CRAZY_ARCHAEOLOGIST_AOE, 3),
    ICE_DEMON_RANGED_AOE_PROJ(ICE_DEMON_RANGED_AOE, 3),

    /**
     * When you don't have pray range on ice demon does an ice barrage
     */
    ICE_DEMON_ICE_BARRAGE_AOE_PROJ(ICE_DEMON_ICE_BARRAGE_AOE, 3),

    /**
     * The AOE when vasa first starts
     */
    VASA_AWAKEN_AOE_PROJ(VASA_AWAKEN_AOE, 3),
    VASA_RANGED_AOE_PROJ(VASA_RANGED_AOE, 3),
    TEKTON_METEOR_AOE_PROJ(TEKTON_METEOR_AOE, 3),

    /**
     * The AOEs of Vorkath
     */
    VORKATH_BOMB_PROJ(VORKATH_BOMB_AOE, 3),
    VORKATH_POISON_POOL_PROJ(VORKATH_POISON_POOL_AOE, 1),
    VORKATH_SPAWN_PROJ(VORKATH_SPAWN_AOE, 1), //extra tick because hard to see otherwise
    VORKATH_TICK_FIRE_PROJ(VORKATH_TICK_FIRE_AOE, 1),

    /**
     * the AOEs of Galvek
     */
    GALVEK_MINE_PROJ(GALVEK_MINE, 3),
    GALVEK_BOMB_PROJ(GALVEK_BOMB, 3),

    /**
     * the AOEs of Grotesque Guardians
     */
    DAWN_FREEZE_PROJ(DAWN_FREEZE, 3),
    DUSK_CEILING_PROJ(DUSK_CEILING, 3),

    /**
     * the AOE of Vet'ion
     */
    VETION_LIGHTNING_PROJ(VETION_LIGHTNING, 1),

    /**
     * the AOE of Chaos Fanatic
     */
    CHAOS_FANATIC_PROJ(CHAOS_FANATIC_AOE, 1),

    /**
     * the AOE of the Corporeal Beast
     */
    CORPOREAL_BEAST_PROJ(CORPOREAL_BEAST_AOE, 1),
    CORPOREAL_BEAST_DARK_CORE_PROJ(CORPOREAL_BEAST_DARK_CORE_AOE, 3),

    /**
     * the AOEs of The Great Olm
     */
    OLM_FALLING_CRYSTAL_PROJ(OLM_FALLING_CRYSTAL, 3),
    OLM_BURNING_PROJ(OLM_BURNING, 1),
    OLM_FALLING_CRYSTAL_TRAIL_PROJ(OLM_FALLING_CRYSTAL_TRAIL, 1),
    OLM_ACID_TRAIL_PROJ(OLM_ACID_TRAIL, 1),
    OLM_FIRE_LINE_PROJ(OLM_FIRE_LINE, 1),

    /**
     * the AOE of the Wintertodt snow that falls
     */
    WINTERTODT_SNOW_FALL_PROJ(WINTERTODT_SNOW_FALL_AOE, 3),

    /**
     * AOE of Xarpus throwing poison
     */
    XARPUS_POISON_AOE_PROJ(XARPUS_ACID, 1),

    /**
     * Aoe of Addy Drags
     */
    ADDY_DRAG_POISON_PROJ(ADDY_DRAG_POISON, 1),

    /**
     * the Breath of the Drake
     */
    DRAKE_BREATH_PROJ(DRAKE_BREATH, 1),

    /**
     * Cerbs fire
     */
    CERB_FIRE_PROJ(CERB_FIRE, 2),

    /**
     * Demonic gorilla
     */
    DEMONIC_GORILLA_BOULDER_PROJ(DEMONIC_GORILLA_BOULDER, 1),

    /**
     * Marble gargoyle (Superior Gargoyle)
     */
    MARBLE_GARGOYLE_AOE_PROJ(MARBLE_GARGOYLE_AOE, 1);

    private static final Map<Integer, AoeProjectileInfo> map = new HashMap<>();

    static {
        for (AoeProjectileInfo aoe : values()) {
            map.put(aoe.id, aoe);
        }
    }

    /**
     * The id of the projectile to trigger this AoE warning
     */
    private final int id;
    /**
     * How long the indicator should last for this AoE warning This might
     * need to be a bit longer than the projectile actually takes to land as
     * there is a fade effect on the warning
     */
    private final int aoeSize;

    AoeProjectileInfo(int id, int aoeSize) {
        this.id = id;
        this.aoeSize = aoeSize;
    }

    public static AoeProjectileInfo getById(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

    public int getAoeSize() {
        return aoeSize;
    }
}
