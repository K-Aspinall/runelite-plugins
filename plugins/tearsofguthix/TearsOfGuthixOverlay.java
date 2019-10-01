/*
 * Copyright (c) 2018, Infinitay <https://github.com/Infinitay>
 * Modified by, K-Aspinall <https://github.com/
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
package net.runelite.client.plugins.tearsofguthix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class TearsOfGuthixOverlay extends Overlay
{
    private static final Color CYAN_ALPHA = new Color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), 100);
    private static final Duration MAX_TIME = Duration.ofSeconds(9);
    private final TearsOfGuthixPlugin plugin;
    private final TearsOfGuthixConfig config;
    private final PanelComponent panelComponent = new PanelComponent();


    @Inject
    private TearsOfGuthixOverlay(TearsOfGuthixPlugin plugin, TearsOfGuthixConfig config)
    {
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        plugin.getStreams().forEach((object, timer) ->
        {
            final Point position = object.getCanvasLocation(100);

            if (position == null)
            {
                return;
            }

            final ProgressPieComponent progressPie = new ProgressPieComponent();
            progressPie.setDiameter(15);
            progressPie.setFill(CYAN_ALPHA);
            progressPie.setBorderColor(Color.CYAN);
            progressPie.setPosition(position);

            final Duration duration = Duration.between(timer, Instant.now());
            progressPie.setProgress(1 - (duration.compareTo(MAX_TIME) < 0
                    ? (double) duration.toMillis() / MAX_TIME.toMillis()
                    : 1));

            progressPie.render(graphics);
        });

        panelComponent.getChildren().clear();
        //panelComponent.setPreferredLocation();
        if(config.showNextSkill())
        {
            String overlayTitle = "Skill to get XP:";

            // Build overlay title
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(overlayTitle)
                    .color(Color.GREEN)
                    .build());

            // Set the size of the overlay (width)
            panelComponent.setPreferredSize(new Dimension(
                    graphics.getFontMetrics().stringWidth(overlayTitle) + 30,
                    0));

            // Add a line on the overlay for world number
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Skill:")
                    .right(plugin.getNextSkill().getName())
                    .build());
        }

        return panelComponent.render(graphics);
    }
}