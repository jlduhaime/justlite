package net.runelite.client.plugins.gauntlet;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import static net.runelite.api.GraphicID.*;

@Getter(AccessLevel.PACKAGE)
class Missiles {
    private Projectile projectile;
    private int id;
    private BufferedImage image;
    private Color color;

    Missiles(Projectile projectile, SkillIconManager skillIconManager) {
        this.projectile = projectile;
        this.id = projectile.getId();
        this.image = assignedImage(skillIconManager, id);
        this.color = assignedColor(id);
    }

    private Color assignedColor(int id) {
        switch (id) {
            case HUNLLEF_MAGE_ATTACK:
            case HUNLLEF_CORRUPTED_MAGE_ATTACK:
                return Color.CYAN;
            case HUNLLEF_RANGE_ATTACK:
            case HUNLLEF_CORRUPTED_RANGE_ATTACK:
                return Color.GREEN;
            case HUNLLEF_PRAYER_ATTACK:
            case HUNLLEF_CORRUPTED_PRAYER_ATTACK:
                return Color.MAGENTA;
            default:
                return null;
        }
    }

    private BufferedImage assignedImage(SkillIconManager SkillIconManager, int id) {
        switch (id) {
            case HUNLLEF_MAGE_ATTACK:
            case HUNLLEF_CORRUPTED_MAGE_ATTACK:
                return SkillIconManager.getSkillImage(Skill.MAGIC);
            case HUNLLEF_RANGE_ATTACK:
            case HUNLLEF_CORRUPTED_RANGE_ATTACK:
                return SkillIconManager.getSkillImage(Skill.RANGED);
            case HUNLLEF_PRAYER_ATTACK:
            case HUNLLEF_CORRUPTED_PRAYER_ATTACK:
                return SkillIconManager.getSkillImage(Skill.PRAYER);
            default:
                return null;
        }
    }
}
