/*
 * Copyright 2024 tim03we, Ovis Development
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
 */

package ovis.futureplots.listener.plot;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;
import lombok.RequiredArgsConstructor;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.components.util.language.manager.LanguageManager;
import ovis.futureplots.components.util.language.TranslationKey;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.Plot;


/**
 * @author Tim tim03we, Ovis Development (2024)
 */
@RequiredArgsConstructor
public class PlayerInteract implements Listener {

    private final FuturePlots plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlotManager plotManager = this.plugin.getPlotManager(player.getLevel());

        if (plotManager != null && !player.hasPermission("plot.admin.interact")) {
            final Block block = event.getBlock();
            final Item item = event.getItem();

            switch (event.getAction()) {
                case PHYSICAL:
                    if (block != null) {
                        final Plot plot = plotManager.getMergedPlot(block.getFloorX(), block.getFloorZ());

                        if (plot != null) {
                            if (!plotManager.hasPermissions(player, plot)) {
                                event.setCancelled(true);
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }
                    break;

                case RIGHT_CLICK_BLOCK:
                case LEFT_CLICK_BLOCK:
                    if (block != null) {
                        final int x = block.getFloorX();
                        final int z = block.getFloorZ();
                        final Plot plot = plotManager.getMergedPlot(x, z);

                        if (plot == null || !plotManager.hasPermissions(player, plot)) {
                            event.setCancelled(true);
                            break;
                        }

                        if (block.canBeActivated()) {
                            if (plot != null) {
                                if (!plotManager.hasPermissions(player, plot)) {
                                    event.setCancelled(true);
                                }

                                if (FuturePlots.getSettings().isHomeProtectEnabled() && plot.getHomePosition() != null && plot.getHomePosition().distance(event.getBlock()) < FuturePlots.getSettings().getHomeProtectDistance()) {
                                    event.setCancelled(true);
                                    LanguageManager language = new LanguageManager(player.getLoginChainData().getLanguageCode());
                                    player.sendMessage(language.message(TranslationKey.TOO_CLOSE_TO_HOME));
                                }
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                    break;

                case RIGHT_CLICK_AIR:
                case LEFT_CLICK_AIR:
                    if (item != null) {
                        if (item.canBeActivated()) {
                            final int x = player.getFloorX();
                            final int z = player.getFloorZ();
                            final Plot plot = plotManager.getMergedPlot(x, z);

                            if (plot != null) {
                                if (!plotManager.hasPermissions(player, plot)) {
                                    event.setCancelled(true);
                                }
                            } else {
                                event.setCancelled(true);
                            }
                        } else if (item instanceof ItemFood) {
                            break;
                        }
                    }
                    break;

                default:
                    // nothing
                    break;
            }
        }
    }


}
