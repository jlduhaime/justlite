package net.runelite.client.plugins.fightcave;

import lombok.AllArgsConstructor;

@AllArgsConstructor
enum WaveMonster {
    TZ_KIH("Drainer", 22, 4),
    TZ_KEK("Blob", 45, 4),
    TOK_XIL("Range", 90, 4),
    YT_MEJKOT("Melee", 180, 4),
    KET_ZEK("Mage", 360, 4),
    TZKOK_JAD("Jad", 702, 8);

    private final String name;
    private final int level;
    public final int attackSpeed;

    @Override
    public String toString() {
        return String.format("%s - Level %s", name, level);
    }
}