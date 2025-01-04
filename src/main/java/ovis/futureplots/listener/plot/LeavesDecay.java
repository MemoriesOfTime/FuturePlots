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
import cn.nukkit.event.block.LeavesDecayEvent;
import lombok.RequiredArgsConstructor;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.Plot;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
@RequiredArgsConstructor
public class LeavesDecay implements Listener {

    private final FuturePlots plugin;

    @EventHandler
    public void on(LeavesDecayEvent event) {
        final Block block = event.getBlock();
        final PlotManager plotManager = this.plugin.getPlotManager(block.getLevel());
        if(plotManager == null) return;

        final Plot plot = plotManager.getMergedPlot(block.getFloorX(), block.getFloorZ());
        if(plot == null) event.setCancelled(true);
        else if(FuturePlots.getSettings().isHomeProtectEnabled() && plot.getHomePosition() != null && plot.getHomePosition().distance(event.getBlock()) < FuturePlots.getSettings().getHomeProtectDistance())
            event.setCancelled(true);
    }
}
