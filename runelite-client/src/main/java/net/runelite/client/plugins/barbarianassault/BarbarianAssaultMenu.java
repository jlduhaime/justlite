package net.runelite.client.plugins.barbarianassault;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.menus.AbstractComparableEntry;
import net.runelite.client.menus.MenuManager;

class BarbarianAssaultMenu
{
    private final MenuManager menuManager;
    private final BarbarianAssaultPlugin game;
    private final List<AbstractComparableEntry> tracker = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean hornUpdated = false;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean rebuildForced = false;

    @Inject
    BarbarianAssaultMenu(final MenuManager menuManager, final BarbarianAssaultPlugin game)
    {
        this.menuManager = menuManager;
        this.game = game;
    }

    private boolean isHornOptionHidden(String option)
    {
        if (game.isInGame() && game.getRole() != null && game.getRole().getTell(game.getLastCallText()).equalsIgnoreCase(option))
        {
            // This will force the menu to be rebuilt after the correct tell is found
            // medic will be added to the menu if it wasn't there before
            if (!hornUpdated)
            {
                rebuildForced = true;
            }
            hornUpdated = true;
            return false;
        }
        return true;
    }

    void clearHiddenMenus()
    {
        // Clears menus from MenuManager and tracker
        for (Iterator<AbstractComparableEntry> iterator = tracker.iterator(); iterator.hasNext(); )
        {
            menuManager.removeHiddenEntry(iterator.next());
            iterator.remove();
        }
    }

    //TODO add omega egg use on?
    void validateHiddenMenus(Role role)
    {
        clearHiddenMenus();

        HashSet<Menus> hiddenMenus = Sets.newHashSet(Menus.getMenus());
        HashSet<Menus> conditionalMenus = Sets.newHashSet(Menus.getMenus());

        // Any option left in this set will not be hidden
        // Checking each option for the correct role prevents MenuManager from
        // iterating over off role menu entry options that are not possible
        conditionalMenus.removeIf(entry ->
        {
            switch (entry)
            {
                // Attacker role options
                case TELL_BLUE_ATTACKER_HORN:
                case TELL_GREEN_ATTACKER_HORN:
                case TELL_RED_ATTACKER_HORN:
                    return ((role == Role.ATTACKER && isHornOptionHidden(entry.getOption())) || role == null) && game.isRemoveIncorrectCalls();

                case ATTACK_PENANCE_FIGHTER:
                case ATTACK_PENANCE_RANGER:
                case GET_SPIKES_PETRIFIED_MUSHROOM:
                case TAKE_ATTACKER_ITEM_MACHINE:
                    return (role != Role.ATTACKER && role != null) && game.isRemoveUnusedMenus();


                // Defender role Options
                case TELL_MEAT_DEFENDER_HORN:
                case TELL_TOFU_DEFENDER_HORN:
                case TELL_WORMS_DEFENDER_HORN:
                    return ((role == Role.DEFENDER && isHornOptionHidden(entry.getOption())) || role == null) && game.isRemoveIncorrectCalls();

                case BLOCK_PENANCE_CAVE:
                    return ((role != Role.DEFENDER && role != null) && game.isRemoveUnusedMenus())
                            || (role == Role.DEFENDER && game.isRemovePenanceCave());

                case DUNK_LAVA_CRATER:
                case FIX:
                case STOCK_UP_DEFENDER_ITEM_MACHINE:
                case TAKE_DEFENDER_ITEM_MACHINE:
                case TAKE_HAMMER:
                case TAKE_LOGS:
                    return (role != Role.DEFENDER && role != null) && game.isRemoveUnusedMenus();


                // Collector role options
                case TELL_ACCURATE_COLLECTOR_HORN:
                case TELL_AGGRESSIVE_COLLECTOR_HORN:
                case TELL_CONTROLLED_COLLECTOR_HORN:
                case TELL_DEFENSIVE_COLLECTOR_HORN:
                    return ((role == Role.COLLECTOR && isHornOptionHidden(entry.getOption())) || role == null) && game.isRemoveIncorrectCalls();

                case CONVERT_COLLECTOR_CONVERTER:
                case LOAD_EGG_HOPPER:
                case TAKE_BLUE_EGG:
                case TAKE_GREEN_EGG:
                case TAKE_RED_EGG:
                case TAKE_YELLOW_EGG:
                    return (role != Role.COLLECTOR && role != null) && game.isRemoveUnusedMenus();


                // Healer role options
                case TELL_CRACKERS_HEALER_HORN:
                case TELL_TOFU_HEALER_HORN:
                case TELL_WORMS_HEALER_HORN:
                    return ((role == Role.HEALER && isHornOptionHidden(entry.getOption())) || role == null) && game.isRemoveIncorrectCalls();

                case DUNK_POISON_CRATER:
                case STOCK_UP_HEALER_ITEM_MACHINE:
                case TAKE_HEALER_ITEM_MACHINE:
                case TAKE_FROM_HEALER_SPRING:
                case DRINK_FROM_HEALER_SPRING:
                    return (role != Role.HEALER && role != null) && game.isRemoveUnusedMenus();

                case USE_VIAL_GROUND:
                case USE_VIAL_ITEM:
                case USE_VIAL_NPC:
                case USE_VIAL_WIDGET:
                    return role == Role.HEALER && game.isRemoveUnusedMenus();


                // Any role options
                case DROP_HORN:
                case EXAMINE_HORN:
                case USE_HORN:
                    return game.isRemoveIncorrectCalls();

                case MEDIC_HORN:
                    return game.isRemoveIncorrectCalls() && !hornUpdated;

                default:
                    return role != null && game.isRemoveUnusedMenus();
            }
        });

        hiddenMenus.removeAll(conditionalMenus);

        for (Menus entry : hiddenMenus)
        {
            menuManager.addHiddenEntry(entry.getEntry());
            tracker.add(entry.getEntry());
        }
    }

    void enableSwaps()
    {
        if (game.isSwapLadder())
        {
            menuManager.addSwap("climb-down", "ladder", "quick-start", "ladder");
        }
        if (game.isSwapCollectorBag())
        {
            menuManager.addSwap("look-in", "collection bag", "empty", "collection bag");
        }
        if (game.isSwapDestroyEggs())
        {
            menuManager.addSwap("use", "blue egg", "destroy", "blue egg");
            menuManager.addSwap("use", "green egg", "destroy", "green egg");
            menuManager.addSwap("use", "red egg", "destroy", "red egg");
        }
    }

    void disableSwaps(boolean force)
    {
        if (!game.isSwapLadder() || force)
        {
            menuManager.removeSwap("climb-down", "ladder", "quick-start", "ladder");
        }

        if (!game.isSwapCollectorBag() || force)
        {
            menuManager.removeSwap("look-in", "collection bag", "empty", "collection bag");
        }

        if (!game.isSwapDestroyEggs() || force)
        {
            menuManager.removeSwap("use", "blue egg", "destroy", "blue egg");
            menuManager.removeSwap("use", "green egg", "destroy", "green egg");
            menuManager.removeSwap("use", "red egg", "destroy", "red egg");
        }
    }
}