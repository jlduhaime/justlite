package net.runelite.client.plugins.alchemicalhydra;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.AnimationID;
import net.runelite.api.NpcID;
import net.runelite.api.ProjectileID;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
enum HydraPhase
{
    ONE(NpcID.ALCHEMICAL_HYDRA,3, AnimationID.HYDRA_1_1, AnimationID.HYDRA_1_2, ProjectileID.HYDRA_POISON, 0, SpriteID.BIG_ASS_GUTHIX_SPELL, new WorldPoint(1371, 10263, 0)),
    TWO(NpcID.ALCHEMICAL_HYDRA_8619,3, AnimationID.HYDRA_2_1, AnimationID.HYDRA_2_2, 0, AnimationID.HYDRA_LIGHTNING, SpriteID.BIG_SPEC_TRANSFER, new WorldPoint(1371, 10272, 0)),
    THREE(NpcID.ALCHEMICAL_HYDRA_8620,3, AnimationID.HYDRA_3_1, AnimationID.HYDRA_3_2, 0, AnimationID.HYDRA_FIRE, SpriteID.BIG_SUPERHEAT, new WorldPoint(1362, 10272, 0)),
    FOUR(NpcID.ALCHEMICAL_HYDRA_8621, 1, AnimationID.HYDRA_4_1, AnimationID.HYDRA_4_2, ProjectileID.HYDRA_POISON, 0, SpriteID.BIG_ASS_GUTHIX_SPELL, null);

    private final int npcId;
    private final int attacksPerSwitch;
    private final int deathAnim1;
    private final int deathAnim2;
    private final int specProjectileId;
    private final int specAnimationId;

    @Getter(AccessLevel.NONE)
    private final int specImageID;
    private final WorldPoint fountain;

    private BufferedImage specImage;

    BufferedImage getSpecImage(SpriteManager spriteManager)
    {
        if (specImage == null)
        {
            BufferedImage tmp = spriteManager.getSprite(specImageID, 0);
            specImage = tmp == null ? null : ImageUtil.resizeImage(tmp, HydraOverlay.IMGSIZE, HydraOverlay.IMGSIZE);
        }

        return specImage;
    }
}
