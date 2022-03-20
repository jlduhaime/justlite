/*
 * Copyright (c) 2018, Cameron <https://github.com/noremac201>
 * Copyright (c) 2018, Jacob M <https://github.com/jacoblairm>
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
package net.runelite.client.plugins.barbarianassault;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("barbarianAssault")
public interface BarbarianAssaultConfig extends Config
{
	@ConfigItem(
		keyName = "showTimer",
		name = "Show call change timer",
		description = "Show time to next call change"
	)
	default boolean showTimer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHealerBars",
		name = "Show health bars for teammates when healer",
		description = "Displays team health for healer"
	)
	default boolean showHealerBars()
	{
		return true;
	}

	@ConfigItem(
		keyName = "waveTimes",
		name = "Show wave and game duration",
		description = "Displays wave and game duration"
	)
	default boolean waveTimes()
	{
		return true;
	}

	// ####################### CUSTOM ######################

	@ConfigItem(
		keyName = "removeIncorrectCalls",
		name = "Remove incorrect calls",
		description = "Removes incorrect 'Tell' menu options from horn"
	)
	default boolean removeIncorrectCalls() { return true; }

	@ConfigItem(
		keyName = "showTotalRewards",
		name = "Summarize total reward points",
		description = "Gives summary of advanced points breakdown in chat log"
	)
	default boolean showTotalRewards() { return true; }

	/*///************///*/
	/*///  Attacker  ///*/
	/*///************///*/

	@ConfigSection(
		name = "Attacker",
		description = "",
		position = 1
	)
	String attackerSection = "attacker";

	@ConfigItem(
			keyName = "highlightArrows",
			name = "Highlight called arrows",
			description = "Highlights arrows called by your teammate",
			position = 0,
			section = attackerSection
	)
	default boolean highlightArrows() { return true; }

	@ConfigItem(
			keyName = "highlightArrowColor",
			name = "Arrow color",
			description = "Configures the color to highlight the called arrows",
			position = 1,
			section = attackerSection
	)
	default Color highlightArrowColor()
	{
		return Color.GREEN;
	}

	/*///************///*/
	/*///  Defender  ///*/
	/*///************///*/

	@ConfigSection(
			name = "Defender",
			description = "",
			position = 2
	)
	String defenderSection = "defender";

	@ConfigItem(
			keyName = "highlightBait",
			name = "Highlight called bait",
			description = "Highlights bait called by your teammate",
			position = 0,
			section = defenderSection
	)
	default boolean highlightBait()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightBaitColor",
			name = "Bait color",
			description = "Configures the color to highlight the called bait",
			position = 1,
			section = defenderSection
	)
	default Color highlightBaitColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "showDefTimer",
			name = "Show defender tick timer",
			description = "Shows the current cycle tick of runners",
			position = 2,
			section = defenderSection
	)
	default boolean showDefTimer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "deprioritizeBait",
			name = "Deprioritize bait",
			description = "Moves 'Take' menu option for all bait below 'Walk Here'",
			position = 3,
			section = defenderSection
	)
	default boolean deprioritizeBait()
	{
		return true;
	}

	@ConfigItem(
			keyName = "removePenanceCave",
			name = "Remove penance cave",
			description = "Removes 'Block' menu option from penance cave",
			position = 4,
			section = defenderSection
	)
	default boolean removePenanceCave()
	{
		return true;
	}

	/*///**********///*/
	/*///  Healer  ///*/
	/*///**********///*/

	@ConfigSection(
			name = "Healer",
			description = "",
			position = 12
	)
	String healerSection = "healerSection";

	@ConfigItem(
			keyName = "highlightPoison",
			name = "Highlight called poison",
			description = "Highlights poison called by your teammate",
			position = 0,
			section = healerSection
	)
	default boolean highlightPoison()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightPoisonColor",
			name = "Poison color",
			description = "Configures the color to highlight the called poison",
			position = 1,
			section = healerSection
	)
	default Color highlightPoisonColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "highlightNotification",
			name = "Highlight incorrect notification",
			description = "Highlights incorrect poison chat notification",
			position = 2,
			section = healerSection
	)
	default boolean highlightNotification()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightNotificationColor",
			name = "Notification color",
			description = "Configures the color to highlight the notification text",
			position = 3,
			section = healerSection
	)
	default Color highlightNotificationColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "showHpCountOverlay",
			name = "Show number of hitpoints healed",
			description = "Displays current number of hitpoints healed",
			position = 4,
			section = healerSection
	)
	default boolean showHpCountOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showTeammateHealthbars",
			name = "Show health bars",
			description = "Displays a health bar where a teammate's remaining health is located",
			position = 5,
			section = healerSection
	)
	default boolean showTeammateHealthbars()
	{
		return true;
	}

	@ConfigItem(
			keyName = "healerCodes",
			name = "Show healer codes",
			description = "Overlay to show healer codes",
			position = 6,
			section = healerSection
	)
	default boolean healerCodes()
	{
		return false;
	}

	@ConfigItem(
			keyName = "healerMenuOption",
			name = "Show healer menu options",
			description = "Shows tick count in healer menu options",
			position = 7,
			section = healerSection
	)
	default boolean healerMenuOption()
	{
		return false;
	}

	@ConfigItem(
			keyName = "shiftOverstock",
			name = "Enable shift overstock",
			description = "Enables overstocking by pressing shift",
			position = 8,
			section = healerSection
	)
	default boolean shiftOverstock()
	{
		return true;
	}

	@ConfigItem(
			keyName = "controlHealer",
			name = "Control Healer",
			description = "Hold ctrl to put last healer clicked on top",
			position = 9,
			section = healerSection
	)
	default boolean controlHealer()
	{
		return true;
	}


	/*///*************///*/
	/*///  Collector  ///*/
	/*///*************///*/

	@ConfigSection(
			name = "Collector",
			description = "",
			position = 13
	)
	String collectorSection = "collectorSection";

	@ConfigItem(
			keyName = "swapCollectorBag",
			name = "Swap empty",
			description = "Enables swapping of 'Look-in' and 'Empty' on the collector's bag",
			position = 0,
			section = collectorSection
	)
	default boolean swapCollectorBag()
	{
		return true;
	}

	@ConfigItem(
			keyName = "swapDestroyEggs",
			name = "Swap destroy",
			description = "Enables swapping of 'Use' and 'Destroy' on collector eggs; this does not affect yellow/omega eggs",
			position = 1,
			section = collectorSection
	)
	default boolean swapDestroyEggs()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightCollectorEggs",
			name = "Highlight collector eggs",
			description = "Highlight called egg colors",
			position = 2,
			section = collectorSection
	)
	default boolean highlightCollectorEggs()
	{
		return true;
	}

	@ConfigItem(
			keyName = "deprioritizeIncorrectEggs",
			name = "Deprioritize incorrect eggs",
			description = "Moves 'Take' menu option for incorrect eggs below 'Walk Here'",
			position = 3,
			section = collectorSection
	)
	default boolean deprioritizeIncorrectEggs()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showEggCountOverlay",
			name = "Show number of eggs collected",
			description = "Displays current number of eggs collected",
			position = 4,
			section = collectorSection
	)
	default boolean showEggCountOverlay()
	{
		return true;
	}
}