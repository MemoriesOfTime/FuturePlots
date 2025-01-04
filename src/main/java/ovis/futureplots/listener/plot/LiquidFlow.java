/*
 * Copyright 2022 KCodeYT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified 2024 by tim03we, Ovis Development
 */

package ovis.futureplots.listener.plot;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.LiquidFlowEvent;
import lombok.RequiredArgsConstructor;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.Plot;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
@RequiredArgsConstructor
public class LiquidFlow implements Listener {

    private final FuturePlots plugin;

    @EventHandler
    public void on(LiquidFlowEvent event) {
        final Block blockSource = event.getSource();
        final PlotManager plotManager = this.plugin.getPlotManager(blockSource.getLevel());
        if(plotManager != null) {
            final Block blockTo = event.getTo();
            final Plot plotFrom = plotManager.getMergedPlot(blockSource.getFloorX(), blockSource.getFloorZ());
            final Plot plotTo = plotManager.getMergedPlot(blockTo.getFloorX(), blockTo.getFloorZ());

            if(plotFrom != null && plotTo == null) event.setCancelled(true);
            if(plotTo != null && plotFrom == null) event.setCancelled(true);

            if(plotFrom != null && FuturePlots.getSettings().isHomeProtectEnabled() && plotFrom.getHomePosition() != null && plotFrom.getHomePosition().distance(event.getBlock()) < FuturePlots.getSettings().getHomeProtectDistance())
                event.setCancelled(true);
            if(plotTo != null && FuturePlots.getSettings().isHomeProtectEnabled() && plotTo.getHomePosition() != null && plotTo.getHomePosition().distance(event.getBlock()) < FuturePlots.getSettings().getHomeProtectDistance())
                event.setCancelled(true);
        }
    }
}
