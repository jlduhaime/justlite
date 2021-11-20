package net.runelite.client.plugins.demonicgorillas;

import lombok.Getter;
import net.runelite.api.Player;

public class PendingGorillaAttack {

    @Getter
    private DemonicGorilla attacker;

    @Getter
    private DemonicGorilla.AttackStyle attackStyle;

    @Getter
    private Player target;

    @Getter
    private int finishesOnTick;

    public PendingGorillaAttack(DemonicGorilla attacker, DemonicGorilla.AttackStyle attackStyle, Player target, int finishesOnTick) {
        this.attacker = attacker;
        this.attackStyle = attackStyle;
        this.target = target;
        this.finishesOnTick = finishesOnTick;
    }
}
