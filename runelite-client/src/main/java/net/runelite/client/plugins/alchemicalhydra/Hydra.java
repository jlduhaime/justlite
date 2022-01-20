package net.runelite.client.plugins.alchemicalhydra;

import java.awt.image.BufferedImage;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.ProjectileID;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.util.ImageUtil;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
@Singleton
class Hydra
{
    @Getter(AccessLevel.PACKAGE)
    @RequiredArgsConstructor
    enum AttackStyle
    {
        MAGIC(ProjectileID.HYDRA_MAGIC, Prayer.PROTECT_FROM_MAGIC, SpriteID.PRAYER_PROTECT_FROM_MAGIC),
        RANGED(ProjectileID.HYDRA_RANGED, Prayer.PROTECT_FROM_MISSILES, SpriteID.PRAYER_PROTECT_FROM_MISSILES);

        private final int projectileID;
        private final Prayer prayer;
        private final int spriteID;

        @Getter(AccessLevel.NONE)
        private BufferedImage image;

        BufferedImage getImage(SpriteManager spriteManager)
        {
            if (image == null)
            {
                BufferedImage tmp = spriteManager.getSprite(spriteID, 0);
                image = tmp == null ? null : ImageUtil.resizeImage(tmp, HydraOverlay.IMGSIZE, HydraOverlay.IMGSIZE);
            }

            return image;
        }
    }

    private final NPC npc;

    private HydraPhase phase = HydraPhase.ONE;

    private int attackCount = 0;
    private int nextSwitch = phase.getAttacksPerSwitch();

    @Setter(AccessLevel.PACKAGE)
    private int nextSpecial = 3;

    private AttackStyle nextAttack = AttackStyle.MAGIC;
    private AttackStyle lastAttack = AttackStyle.MAGIC;

    @Setter(AccessLevel.PACKAGE)
    private boolean weakened = false;

    void changePhase(HydraPhase newPhase)
    {
        phase = newPhase;
        nextSpecial = 3;
        attackCount = 0;
        weakened = false;

        if (newPhase == HydraPhase.FOUR)
        {
            weakened = true;
            switchStyles();
            nextSwitch = phase.getAttacksPerSwitch();
        }
    }

    private void switchStyles()
    {
        nextAttack = lastAttack == Hydra.AttackStyle.MAGIC
                ? Hydra.AttackStyle.RANGED
                : Hydra.AttackStyle.MAGIC;
    }

    void handleAttack(int id)
    {
        if (id != nextAttack.getProjectileID())
        {
            if (id == lastAttack.getProjectileID())
            {
                // If the current attack isn't what was expected and we accidentally counted 1 too much
                return;
            }

            // If the current attack isn't what was expected and we should have switched prayers
            switchStyles();
            nextSwitch = phase.getAttacksPerSwitch() - 1;
        }
        else
        {
            nextSwitch--;
        }

        lastAttack = nextAttack;
        attackCount++;

        if (nextSwitch <= 0)
        {
            switchStyles();
            nextSwitch = phase.getAttacksPerSwitch();
        }
    }

    int getNextSpecialRelative()
    {
        return nextSpecial - attackCount;
    }
}
