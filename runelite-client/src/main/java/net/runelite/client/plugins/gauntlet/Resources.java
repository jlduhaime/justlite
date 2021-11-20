package net.runelite.client.plugins.gauntlet;

import java.awt.image.BufferedImage;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.client.game.SkillIconManager;

@Getter(AccessLevel.PACKAGE)
class Resources {
    private GameObject gameObject;
    private Tile tile;
    private BufferedImage image;

    Resources(GameObject object, Tile tile, SkillIconManager skillIconManager) {
        this.gameObject = object;
        this.tile = tile;
        this.image = assignedImage(skillIconManager, object.getId());
    }

    private BufferedImage assignedImage(SkillIconManager SkillIconManager, int id) {
        switch (id) {
            case ObjectID.CRYSTAL_DEPOSIT:
            case ObjectID.CORRUPT_DEPOSIT:
                return SkillIconManager.getSkillImage(Skill.MINING);
            case ObjectID.PHREN_ROOTS:
            case ObjectID.PHREN_ROOTS_36066:
                return SkillIconManager.getSkillImage(Skill.WOODCUTTING);
            case ObjectID.FISHING_SPOT_36068:
            case ObjectID.FISHING_SPOT_35971:
                return SkillIconManager.getSkillImage(Skill.FISHING);
            case ObjectID.GRYM_ROOT:
            case ObjectID.GRYM_ROOT_36070:
                return SkillIconManager.getSkillImage(Skill.HERBLORE);
            case ObjectID.LINUM_TIRINUM:
            case ObjectID.LINUM_TIRINUM_36072:
                return SkillIconManager.getSkillImage(Skill.FARMING);
            default:
                return null;
        }
    }
}