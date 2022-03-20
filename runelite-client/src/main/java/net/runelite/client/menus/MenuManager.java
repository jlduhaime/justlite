/*
 * Copyright (c) 2017, Robin <robin.weymans@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.menus;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.PlayerMenuOptionsChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import static net.runelite.client.menus.ComparableEntries.newBaseComparableEntry;

@Singleton
@Slf4j
public class MenuManager
{
	/*
	 * The index needs to be between 4 and 7,
	 */
	private static final int IDX_LOWER = 4;
	private static final int IDX_UPPER = 8;

	private final Client client;

	//Maps the indexes that are being used to the menu option.
	private final Map<Integer, String> playerMenuIndexMap = new HashMap<>();
	//Used to manage custom non-player menu options
	private final Multimap<Integer, WidgetMenuOption> managedMenuOptions = LinkedHashMultimap.create();
	private final Set<AbstractComparableEntry> priorityEntries = new HashSet<>();
	private final Map<MenuEntry, AbstractComparableEntry> currentPriorityEntries = new LinkedHashMap<>();
	private final Set<AbstractComparableEntry> hiddenEntries = new HashSet<>();
	private final Map<AbstractComparableEntry, AbstractComparableEntry> swaps = new HashMap<>();


	@Inject
	private MenuManager(Client client, EventBus eventBus)
	{
		this.client = client;
		eventBus.register(this);
	}

	public void removeHiddenEntry(String option, String target)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		AbstractComparableEntry entry = newBaseComparableEntry(option, target);

		hiddenEntries.removeIf(entry::equals);
	}

	public void removeHiddenEntry(String option)
	{
		option = option.trim().toLowerCase();

		AbstractComparableEntry entry = newBaseComparableEntry(option, "", false);

		hiddenEntries.removeIf(entry::equals);
	}

	public void removeHiddenEntry(AbstractComparableEntry entry)
	{
		hiddenEntries.remove(entry);
	}

	public void addHiddenEntry(String option, String target)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		AbstractComparableEntry entry = newBaseComparableEntry(option, target);

		hiddenEntries.add(entry);
	}

	public void addHiddenEntry(String option)
	{
		option = option.trim().toLowerCase();

		AbstractComparableEntry entry = newBaseComparableEntry(option, "", false);

		hiddenEntries.add(entry);
	}

	/**
	 * Adds to the map of swaps. Strict options, not strict target but target1=target2
	 */
	public void addSwap(String option, String target, String option2)
	{
		addSwap(option, target, option2, target, true, false);
	}

	public void removeSwap(String option, String target, String option2)
	{
		removeSwap(option, target, option2, target, true, false);
	}

	/**
	 * Adds to the map of swaps.
	 */
	private void addSwap(String option, String target, String option2, String target2, boolean strictOption, boolean strictTarget)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		option2 = option2.trim().toLowerCase();
		target2 = Text.standardize(target2);

		AbstractComparableEntry swapFrom = newBaseComparableEntry(option, target, -1, -1, strictOption, strictTarget);
		AbstractComparableEntry swapTo = newBaseComparableEntry(option2, target2, -1, -1, strictOption, strictTarget);

		if (swapTo.equals(swapFrom))
		{
			log.warn("You shouldn't try swapping an entry for itself");
			return;
		}

		swaps.put(swapFrom, swapTo);
	}


	private void removeSwap(String option, String target, String option2, String target2, boolean strictOption, boolean strictTarget)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		option2 = option2.trim().toLowerCase();
		target2 = Text.standardize(target2);

		AbstractComparableEntry swapFrom = newBaseComparableEntry(option, target, -1, -1, strictOption, strictTarget);
		AbstractComparableEntry swapTo = newBaseComparableEntry(option2, target2, -1, -1, strictOption, strictTarget);

		removeSwap(swapFrom, swapTo);
	}

	/**
	 * Adds to the map of swaps. - Strict option + target
	 */
	public void addSwap(String option, String target, String option2, String target2)
	{
		addSwap(option, target, option2, target2, false, false);
	}

	public void removeSwap(String option, String target, String option2, String target2)
	{
		removeSwap(option, target, option2, target2, false, false);
	}

	/**
	 * Adds to the map of swaps - Pre-baked entry
	 */
	public void addSwap(AbstractComparableEntry swapFrom, AbstractComparableEntry swapTo)
	{
		if (swapTo.equals(swapFrom))
		{
			log.warn("You shouldn't try swapping an entry for itself");
			return;
		}

		swaps.put(swapFrom, swapTo);
	}

	/**
	 * Adds to the map of swaps - Non-strict option/target, but with opcode & id
	 * ID's of -1 are ignored in matches()!
	 */
	public void addSwap(String option, String target, int id, int type, String option2, String target2, int id2, int type2)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		option2 = option2.trim().toLowerCase();
		target2 = Text.standardize(target2);

		AbstractComparableEntry swapFrom = newBaseComparableEntry(option, target, id, type, false, false);
		AbstractComparableEntry swapTo = newBaseComparableEntry(option2, target2, id2, type2, false, false);

		if (swapTo.equals(swapFrom))
		{
			log.warn("You shouldn't try swapping an entry for itself");
			return;
		}

		swaps.put(swapFrom, swapTo);
	}

	public void removeSwap(String option, String target, int id, int type, String option2, String target2, int id2, int type2)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		option2 = option2.trim().toLowerCase();
		target2 = Text.standardize(target2);

		AbstractComparableEntry swapFrom = newBaseComparableEntry(option, target, id, type, false, false);
		AbstractComparableEntry swapTo = newBaseComparableEntry(option2, target2, id2, type2, false, false);

		swaps.entrySet().removeIf(e -> e.getKey().equals(swapFrom) && e.getValue().equals(swapTo));
	}

	public void removeSwap(AbstractComparableEntry swapFrom, AbstractComparableEntry swapTo)
	{
		swaps.entrySet().removeIf(e -> e.getKey().equals(swapFrom) && e.getValue().equals(swapTo));
	}

	/**
	 * Removes all swaps with target
	 */
	public void removeSwaps(String... fromTarget)
	{
		for (String target : fromTarget)
		{
			final String s = Text.standardize(target);
			swaps.keySet().removeIf(e -> e.getTarget() != null && e.getTarget().equals(s));
			priorityEntries.removeIf(e -> e.getTarget() != null && e.getTarget().equals(s));
			hiddenEntries.removeIf(e -> e.getTarget() != null && e.getTarget().equals(s));
		}
	}

	public void addHiddenEntry(String option, String target, boolean strictOption, boolean strictTarget)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		AbstractComparableEntry entry = newBaseComparableEntry(option, target, -1, -1, strictOption, strictTarget);

		hiddenEntries.add(entry);
	}

	public void addHiddenEntry(AbstractComparableEntry entry)
	{
		hiddenEntries.add(entry);
	}

	public void removeHiddenEntry(String option, String target, boolean strictOption, boolean strictTarget)
	{
		option = option.trim().toLowerCase();
		target = Text.standardize(target);

		AbstractComparableEntry entry = newBaseComparableEntry(option, target, -1, -1, strictOption, strictTarget);

		hiddenEntries.remove(entry);
	}

	/**
	 * Adds a CustomMenuOption to the list of managed menu options.
	 *
	 * @param customMenuOption The custom menu to add
	 * @param callback callback to be called when the menu is clicked
	 */
	public void addManagedCustomMenu(WidgetMenuOption customMenuOption, Consumer<MenuEntry> callback)
	{
		managedMenuOptions.put(customMenuOption.getWidgetId(), customMenuOption);
		customMenuOption.callback = callback;
	}

	/**
	 * Removes a CustomMenuOption from the list of managed menu options.
	 *
	 * @param customMenuOption The custom menu to add
	 */
	public void removeManagedCustomMenu(WidgetMenuOption customMenuOption)
	{
		managedMenuOptions.remove(customMenuOption.getWidgetId(), customMenuOption);
	}

	private static boolean menuContainsCustomMenu(MenuEntry[] menuEntries, WidgetMenuOption customMenuOption)
	{
		for (MenuEntry menuEntry : menuEntries)
		{
			String option = menuEntry.getOption();
			String target = menuEntry.getTarget();

			if (option.equals(customMenuOption.getMenuOption()) && target.equals(customMenuOption.getMenuTarget()))
			{
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (client.getSpellSelected() || event.getType() != MenuAction.CC_OP.getId())
		{
			return;
		}

		int widgetId = event.getActionParam1();
		Collection<WidgetMenuOption> options = managedMenuOptions.get(widgetId);
		if (options.isEmpty())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		// Menu entries are sorted with higher-index entries appearing toward the top of the minimenu, so insert older
		// managed menu entries at higher indices and work backward for newer entries so newly-added entries appear at
		// the bottom
		int insertIdx = -1;
		for (WidgetMenuOption currentMenu : options)
		{
			// Exit if we've inserted the managed menu entries already
			if (menuContainsCustomMenu(menuEntries, currentMenu))
			{
				return;
			}

			client.createMenuEntry(insertIdx--)
				.setOption(currentMenu.getMenuOption())
				.setTarget(currentMenu.getMenuTarget())
				.setType(MenuAction.RUNELITE)
				.setParam1(widgetId)
				.onClick(currentMenu.callback);
		}
	}

	public void addPlayerMenuItem(String menuText)
	{
		Preconditions.checkNotNull(menuText);

		int playerMenuIndex = findEmptyPlayerMenuIndex();
		if (playerMenuIndex == IDX_UPPER)
		{
			return; // no more slots
		}

		addPlayerMenuItem(playerMenuIndex, menuText);
	}

	public void removePlayerMenuItem(String menuText)
	{
		Preconditions.checkNotNull(menuText);
		for (Map.Entry<Integer, String> entry : playerMenuIndexMap.entrySet())
		{
			if (entry.getValue().equalsIgnoreCase(menuText))
			{
				removePlayerMenuItem(entry.getKey());
				break;
			}
		}
	}

	@Subscribe
	public void onPlayerMenuOptionsChanged(PlayerMenuOptionsChanged event)
	{
		int idx = event.getIndex();

		String menuText = playerMenuIndexMap.get(idx);
		if (menuText == null)
		{
			return; // not our menu
		}

		// find new index for this option
		int newIdx = findEmptyPlayerMenuIndex();
		if (newIdx == IDX_UPPER)
		{
			log.debug("Client has updated player menu index {} where option {} was, and there are no more free slots available", idx, menuText);
			return;
		}

		log.debug("Client has updated player menu index {} where option {} was, moving to index {}", idx, menuText, newIdx);

		playerMenuIndexMap.remove(idx);
		addPlayerMenuItem(newIdx, menuText);
	}

	private void addPlayerMenuItem(int playerOptionIndex, String menuText)
	{
		client.getPlayerOptions()[playerOptionIndex] = menuText;
		client.getPlayerOptionsPriorities()[playerOptionIndex] = true;
		client.getPlayerMenuTypes()[playerOptionIndex] = MenuAction.RUNELITE_PLAYER.getId();

		playerMenuIndexMap.put(playerOptionIndex, menuText);
	}

	private void removePlayerMenuItem(int playerOptionIndex)
	{
		client.getPlayerOptions()[playerOptionIndex] = null;
		playerMenuIndexMap.remove(playerOptionIndex);
	}

	/**
	 * Find the next empty player menu slot index
	 */
	private int findEmptyPlayerMenuIndex()
	{
		int index = IDX_LOWER;

		String[] playerOptions = client.getPlayerOptions();
		while (index < IDX_UPPER && playerOptions[index] != null)
		{
			index++;
		}

		return index;
	}
}