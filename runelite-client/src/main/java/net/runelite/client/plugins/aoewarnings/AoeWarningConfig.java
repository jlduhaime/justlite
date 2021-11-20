package net.runelite.client.plugins.aoewarnings;


import java.awt.Color;
import java.awt.Font;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("aoe")
public interface AoeWarningConfig extends Config {
    @Getter(AccessLevel.PACKAGE)
    @AllArgsConstructor
    enum FontStyle {
        BOLD("Bold", Font.BOLD),
        ITALIC("Italic", Font.ITALIC),
        PLAIN("Plain", Font.PLAIN);

        private String name;
        private int font;

        @Override
        public String toString() {
            return getName();
        }
    }

    @ConfigSection(
            name = "Notify",
            description = "",
            position = -1
    )
    String notifyTitle = "notifyTitle";

    @ConfigItem(
            keyName = "aoeNotifyAll",
            name = "Notify for all AoE warnings",
            description = "Configures whether or not AoE Projectile Warnings should trigger a notification",
            position = 0,
            section = notifyTitle
    )
    default boolean aoeNotifyAll() {
        return false;
    }

    @ConfigSection(
            name = "Overlay",
            description = "",
            position = 1
    )
    String overlayTitle = "overlayTitle";

    @ConfigItem(
            position = 2,
            keyName = "overlayColor",
            name = "Overlay Color",
            description = "Configures the color of the AoE Projectile Warnings overlay",
            section = overlayTitle
    )
    default Color overlayColor() {
        return new Color(0, 150, 200);
    }

    @ConfigItem(
            keyName = "outline",
            name = "Display Outline",
            description = "Configures whether or not AoE Projectile Warnings have an outline",
            section = overlayTitle,
            position = 3
    )
    default boolean isOutlineEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "delay",
            name = "Fade Delay",
            description = "Configures the amount of time in milliseconds that the warning lingers for after the projectile has touched the ground",
            section = overlayTitle,
            position = 4
    )
    default int delay() {
        return 300;
    }

    @ConfigItem(
            keyName = "fade",
            name = "Fade Warnings",
            description = "Configures whether or not AoE Projectile Warnings fade over time",
            section = overlayTitle,
            position = 5
    )
    default boolean isFadeEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "tickTimers",
            name = "Tick Timers",
            description = "Configures whether or not AoE Projectile Warnings has tick timers overlaid as well.",
            section = overlayTitle,
            position = 6
    )
    default boolean tickTimers() {
        return true;
    }

    @ConfigSection(
            position = 7,
            name = "Text",
            description = ""
    )
    String textTitle = "textTitle";

    @ConfigItem(
            position = 8,
            keyName = "fontStyle",
            name = "Font Style",
            description = "Bold/Italics/Plain",
            section = textTitle,
            hidden = true
    )
    default FontStyle fontStyle() {
        return FontStyle.BOLD;
    }

    @Range(
            min = 20,
            max = 40
    )
    @ConfigItem(
            position = 9,
            keyName = "textSize",
            name = "Text Size",
            description = "Text Size for Timers.",
            section = textTitle
    )
    default int textSize() {
        return 32;
    }

    @ConfigItem(
            position = 10,
            keyName = "shadows",
            name = "Shadows",
            description = "Adds Shadows to text.",
            section = textTitle
    )
    default boolean shadows() {
        return true;
    }

    @ConfigSection(
            name = "NPC's",
            description = "",
            position = 11
    )
    String npcTitle = "npcTitle";

    @ConfigSection(
            name = "Lizardman Shamans",
            description = "",
            position = 12
    )
    String lizardmanAoeTitle = "lizardmanaoeTitle";

    @ConfigItem(
            keyName = "lizardmanaoe",
            name = "Lizardman Shamans",
            description = "Configures whether or not AoE Projectile Warnings for Lizardman Shamans is displayed",
            section = lizardmanAoeTitle,
            position = 13
    )
    default boolean isShamansEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "lizardmanaoenotify",
            name = "Lizardman Shamans Notify",
            description = "Configures whether or not AoE Projectile Warnings for Lizardman Shamans should trigger a notification",
            section = lizardmanAoeTitle,
            position = 14
    )
    default boolean isShamansNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Crazy Archaeologist",
            description = "",
            position = 15
    )
    String archeologistAoeTitle = "archaeologistaoeTitle";

    @ConfigItem(
            keyName = "archaeologistaoe",
            name = "Crazy Archaeologist",
            description = "Configures whether or not AoE Projectile Warnings for Archaeologist is displayed",
            section = archeologistAoeTitle,
            position = 16
    )
    default boolean isArchaeologistEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "archaeologistaoenotify",
            name = "Crazy Archaeologist Notify",
            description = "Configures whether or not AoE Projectile Warnings for Crazy Archaeologist should trigger a notification",
            section = archeologistAoeTitle,
            position = 17
    )
    default boolean isArchaeologistNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Ice Demon",
            description = "",
            position = 18
    )
    String iceDemonTitle = "icedemonTitle";

    @ConfigItem(
            keyName = "icedemon",
            name = "Ice Demon",
            description = "Configures whether or not AoE Projectile Warnings for Ice Demon is displayed",
            section = "icedemonTitle",
            position = 19
    )
    default boolean isIceDemonEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "icedemonnotify",
            name = "Ice Demon Notify",
            description = "Configures whether or not AoE Projectile Warnings for Ice Demon should trigger a notification",
            section = "icedemonTitle",
            position = 20
    )
    default boolean isIceDemonNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Vasa",
            description = "",
            position = 21
    )
    String vasaTitle = "vasaTitle";

    @ConfigItem(
            keyName = "vasa",
            name = "Vasa",
            description = "Configures whether or not AoE Projectile Warnings for Vasa is displayed",
            section = "vasaTitle",
            position = 22
    )
    default boolean isVasaEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "vasanotify",
            name = "Vasa Notify",
            description = "Configures whether or not AoE Projectile Warnings for Vasa should trigger a notification",
            section = "vasaTitle",
            position = 23
    )
    default boolean isVasaNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Tekton",
            description = "",
            position = 24
    )
    String tektonTitle = "tektonTitle";

    @ConfigItem(
            keyName = "tekton",
            name = "Tekton",
            description = "Configures whether or not AoE Projectile Warnings for Tekton is displayed",
            section = "tektonTitle",
            position = 25
    )
    default boolean isTektonEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "tektonnotify",
            name = "Tekton Notify",
            description = "Configures whether or not AoE Projectile Warnings for Tekton should trigger a notification",
            section = "tektonTitle",
            position = 26
    )
    default boolean isTektonNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Vorkath",
            description = "",
            position = 27
    )
    String vorkathTitle = "vorkathTitle";

    @ConfigItem(
            keyName = "vorkath",
            name = "Vorkath",
            description = "Configures whether or not AoE Projectile Warnings for Vorkath are displayed",
            section = "vorkathTitle",
            position = 28
    )
    default boolean isVorkathEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "vorkathotify",
            name = "Vorkath Notify",
            description = "Configures whether or not AoE Projectile Warnings for Vorkath should trigger a notification",
            section = "vorkathTitle",
            position = 29
    )
    default boolean isVorkathNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Galvek",
            description = "",
            position = 30
    )
    String galvekTitle = "galvekTitle";

    @ConfigItem(
            keyName = "galvek",
            name = "Galvek",
            description = "Configures whether or not AoE Projectile Warnings for Galvek are displayed",
            section = "galvekTitle",
            position = 31
    )
    default boolean isGalvekEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "galveknotify",
            name = "Galvek Notify",
            description = "Configures whether or not AoE Projectile Warnings for Galvek should trigger a notification",
            section = "galvekTitle",
            position = 32
    )
    default boolean isGalvekNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Gargoyle Boss",
            description = "",
            position = 33
    )
    String gargBossTitle = "gargbossTitle";

    @ConfigItem(
            keyName = "gargboss",
            name = "Gargoyle Boss",
            description = "Configs whether or not AoE Projectile Warnings for Dawn/Dusk are displayed",
            section = "gargbossTitle",
            position = 34
    )
    default boolean isGargBossEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "gargbossnotify",
            name = "Gargoyle Boss Notify",
            description = "Configures whether or not AoE Projectile Warnings for Gargoyle Bosses should trigger a notification",
            section = "gargbossTitle",
            position = 35
    )
    default boolean isGargBossNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Vet'ion",
            description = "",
            position = 36
    )
    String vetionTitle = "vetionTitle";

    @ConfigItem(
            keyName = "vetion",
            name = "Vet'ion",
            description = "Configures whether or not AoE Projectile Warnings for Vet'ion are displayed",
            section = "vetionTitle",
            position = 37
    )
    default boolean isVetionEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "vetionnotify",
            name = "Vet'ion Notify",
            description = "Configures whether or not AoE Projectile Warnings for Vet'ion should trigger a notification",
            section = "vetionTitle",
            position = 38
    )
    default boolean isVetionNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Chaos Fanatic",
            description = "",
            position = 39
    )
    String chaosFanaticTitle = "chaosfanaticTitle";

    @ConfigItem(
            keyName = "chaosfanatic",
            name = "Chaos Fanatic",
            description = "Configures whether or not AoE Projectile Warnings for Chaos Fanatic are displayed",
            section = "chaosfanaticTitle",
            position = 40
    )
    default boolean isChaosFanaticEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "chaosfanaticnotify",
            name = "Chaos Fanatic Notify",
            description = "Configures whether or not AoE Projectile Warnings for Chaos Fanatic should trigger a notification",
            section = "chaosfanaticTitle",
            position = 41
    )
    default boolean isChaosFanaticNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Olm",
            description = "",
            position = 42
    )
    String olmTitle = "olmTitle";

    @ConfigItem(
            keyName = "olm",
            name = "Olm",
            description = "Configures whether or not AoE Projectile Warnings for The Great Olm are displayed",
            section = "olmTitle",
            position = 43
    )
    default boolean isOlmEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "olmnotify",
            name = "Olm Notify",
            description = "Configures whether or not AoE Projectile Warnings for Olm should trigger a notification",
            section = "olmTitle",
            position = 44
    )
    default boolean isOlmNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Bombs",
            description = "",
            position = 45
    )
    String olmBombsTitle = "olmBombsTitle";

    @ConfigItem(
            keyName = "bombDisplay",
            name = "Olm Bombs",
            description = "Display a timer and colour-coded AoE for Olm's crystal-phase bombs.",
            section = "olmBombsTitle",
            position = 46
    )
    default boolean bombDisplay() {
        return true;
    }

    @ConfigItem(
            keyName = "bombDisplaynotify",
            name = "Olm Bombs Notify",
            description = "Configures whether or not AoE Projectile Warnings for Olm Bombs should trigger a notification",
            section = "olmBombsTitle",
            position = 47
    )
    default boolean bombDisplayNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Lightning Trails",
            description = "",
            position = 48
    )
    String olmLightningTitle = "olmlightningTitle";

    @ConfigItem(
            keyName = "lightning",
            name = "Olm Lightning Trails",
            description = "Show Lightning Trails",
            section = "olmlightningTitle",
            position = 49
    )
    default boolean LightningTrail() {
        return true;
    }

    @ConfigItem(
            keyName = "lightningnotify",
            name = "Olm Lightning Trails Notify",
            description = "Configures whether or not AoE Projectile Warnings for Olm Lightning Trails should trigger a notification",
            section = "olmlightningTitle",
            position = 50
    )
    default boolean LightningTrailNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Corporeal Beast",
            description = "",
            position = 51
    )
    String corpTitle = "corpTitle";

    @ConfigItem(
            keyName = "corp",
            name = "Corporeal Beast",
            description = "Configures whether or not AoE Projectile Warnings for the Corporeal Beast are displayed",
            section = corpTitle,
            position = 52
    )
    default boolean isCorpEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "corpnotify",
            name = "Corporeal Beast Notify",
            description = "Configures whether or not AoE Projectile Warnings for Corporeal Beast should trigger a notification",
            section = "corpTitle",
            position = 53
    )
    default boolean isCorpNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Wintertodt",
            description = "",
            position = 54
    )
    String wintertodtTitle = "wintertodtTitle";

    @ConfigItem(
            keyName = "wintertodt",
            name = "Wintertodt Snow Fall",
            description = "Configures whether or not AOE Projectile Warnings for the Wintertodt snow fall are displayed",
            section = "wintertodtTitle",
            position = 55
    )
    default boolean isWintertodtEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "wintertodtnotify",
            name = "Wintertodt Snow Fall Notify",
            description = "Configures whether or not AoE Projectile Warnings for Wintertodt Snow Fall Notify should trigger a notification",
            section = "wintertodtTitle",
            position = 56
    )
    default boolean isWintertodtNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Xarpus",
            description = "",
            position = 57
    )
    String xarpusTitle = "xarpusTitle";

    @ConfigItem(
            keyName = "isXarpusEnabled",
            name = "Xarpus",
            description = "Configures whether or not AOE Projectile Warnings for Xarpus are displayed",
            section = "xarpusTitle",
            position = 58
    )
    default boolean isXarpusEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "isXarpusEnablednotify",
            name = "Xarpus Notify",
            description = "Configures whether or not AoE Projectile Warnings for Xarpus should trigger a notification",
            section = "xarpusTitle",
            position = 59
    )
    default boolean isXarpusNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Addy Drags",
            description = "",
            position = 60
    )
    String addyDragsTitle = "addyDragsTitle";

    @ConfigItem(
            keyName = "addyDrags",
            name = "Addy Drags",
            description = "Show Bad Areas",
            section = "addyDragsTitle",
            position = 61
    )
    default boolean addyDrags() {
        return true;
    }

    @ConfigItem(
            keyName = "addyDragsnotify",
            name = "Addy Drags Notify",
            description = "Configures whether or not AoE Projectile Warnings for Addy Dragons should trigger a notification",
            section = "addyDragsTitle",
            position = 62
    )
    default boolean addyDragsNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Drakes",
            description = "",
            position = 63
    )
    String drakeTitle = "drakeTitle";

    @ConfigItem(
            keyName = "drake",
            name = "Drakes Breath",
            description = "Configures if Drakes Breath tile markers are displayed",
            section = "drakeTitle",
            position = 64
    )
    default boolean isDrakeEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "drakenotify",
            name = "Drakes Breath Notify",
            description = "Configures whether or not AoE Projectile Warnings for Drakes Breath should trigger a notification",
            section = "drakeTitle",
            position = 65
    )
    default boolean isDrakeNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Cerberus",
            description = "",
            position = 66
    )
    String cerberusTitle = "cerberusTitle";

    @ConfigItem(
            keyName = "cerbFire",
            name = "Cerberus Fire",
            description = "Configures if Cerberus fire tile markers are displayed",
            section = "cerberusTitle",
            position = 67
    )
    default boolean isCerbFireEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "cerbFirenotify",
            name = "Cerberus Fire Notify",
            description = "Configures whether or not AoE Projectile Warnings for Cerberus his fire should trigger a notification",
            section = "cerberusTitle",
            position = 68
    )
    default boolean isCerbFireNotifyEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Demonic Gorilla",
            description = "",
            position = 69
    )
    String demonicGorillaTitle = "demonicGorillaTitle";

    @ConfigItem(
            keyName = "demonicGorilla",
            name = "Demonic Gorilla",
            description = "Configures if Demonic Gorilla boulder tile markers are displayed",
            section = "demonicGorillaTitle",
            position = 70
    )
    default boolean isDemonicGorillaEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "demonicGorillaNotify",
            name = "Demonic Gorilla Notify",
            description = "Configures whether or not AoE Projectile Warnings for Demonic Gorilla boulders should trigger a notification",
            section = "demonicGorillaTitle",
            position = 71
    )
    default boolean isDemonicGorillaNotifyEnabled() {
        return false;
    }
}