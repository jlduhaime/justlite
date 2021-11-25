/*
 * Copyright (c) 2018, Kamiel
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
package net.runelite.client.plugins.raidpointsoverlay;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

public class RaidsPointsOverlay extends OverlayPanel
{
	private static final DecimalFormat POINTS_FORMAT = new DecimalFormat("#,###");
	private static final DecimalFormat POINTS_PERCENT_FORMAT = new DecimalFormat(" (##0.00%)");
	private static final DecimalFormat UNIQUE_FORMAT = new DecimalFormat("#0.00%");

	private Client client;
	private RaidPointsOverlayPlugin plugin;
	private RaidsPointsConfig config;
	private TooltipManager tooltipManager;

	@Inject
	private RaidsPointsOverlay(Client client,
							   RaidPointsOverlayPlugin plugin,
							   RaidsPointsConfig config,
							   TooltipManager tooltipManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.tooltipManager = tooltipManager;
		setPosition(OverlayPosition.TOP_RIGHT);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInRaidChambers())
		{
			return null;
		}

		int totalPoints = client.getVar(Varbits.TOTAL_POINTS);
		int personalPoints = client.getVar(Varbits.PERSONAL_POINTS);
		int partySize = client.getVar(Varbits.RAID_PARTY_SIZE);
		FontMetrics metrics = graphics.getFontMetrics();

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Total:")
			.right(POINTS_FORMAT.format(totalPoints))
			.build());

		String personalPointsPercent = "";
		if (config.raidsPointsPercent() && partySize > 1)
		{
			personalPointsPercent += totalPoints > 0 ?
				POINTS_PERCENT_FORMAT.format((double) personalPoints / totalPoints) :
				POINTS_PERCENT_FORMAT.format(0);
		}
		String personalPointsString = POINTS_FORMAT.format(personalPoints) + personalPointsPercent;

		panelComponent.getChildren().add(LineComponent.builder()
			.left(client.getLocalPlayer().getName() + ":")
			.right(personalPointsString)
			.build());

		if (config.raidsTimer())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Time:")
				.right(plugin.getTime())
				.build());
		}

		if (partySize > 1 && config.showTeamSize())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Party size:")
				.right(String.valueOf(partySize))
				.build());
		}

		if (config.raidsUniqueChance() != UniqueConfigOptions.OFF)
		{
			// 0.675 is rate at which the droprate switches to other roll and doesn't go up for a single drop per wiki
			double personalUniqueChance = Math.min((double) personalPoints / 867500, 0.657);
			String personalUniqueChanceStr = UNIQUE_FORMAT.format(personalUniqueChance);

			double totalUniqueChance = Math.min((double) totalPoints / 867500, 0.657);
			String totalUniqueChanceStr = UNIQUE_FORMAT.format(totalUniqueChance);

			String uniqueChance;
			if (config.raidsUniqueChance() == UniqueConfigOptions.BOTH)
			{
				uniqueChance = personalUniqueChanceStr;

				if(partySize > 1)
				{
					uniqueChance += " (" + totalUniqueChanceStr + ")";
				}
			}
			else if (config.raidsUniqueChance() == UniqueConfigOptions.PERSONAL_CHANCE)
			{
				uniqueChance = personalUniqueChanceStr;
			}
			else
			{
				uniqueChance = totalUniqueChanceStr;
			}

			panelComponent.getChildren().add(LineComponent.builder()
				.left("Unique:")
				.right(uniqueChance)
				.build());
		}

		final Rectangle bounds = this.getBounds();
		if (bounds.getX() > 0)
		{
			final Point mousePosition = client.getMouseCanvasPosition();

			if (bounds.contains(mousePosition.getX(), mousePosition.getY()))
			{
				String tooltip = plugin.getTooltip();

				if (tooltip != null)
				{
					tooltipManager.add(new Tooltip(tooltip));
				}
			}
		}
		return super.render(graphics);
	}
}
