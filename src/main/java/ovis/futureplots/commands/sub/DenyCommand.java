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

package ovis.futureplots.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.commands.SubCommand;
import ovis.futureplots.components.util.language.TranslationKey;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.Plot;
import ovis.futureplots.components.util.Utils;

import java.util.UUID;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
public class DenyCommand extends SubCommand {

    private final FuturePlots plugin;

    public DenyCommand(FuturePlots plugin) {
        super(plugin, "deny");
        this.plugin = plugin;
        this.identify();
        this.playerOnly();
        this.setPermissions("plots.deny", "plots.perm.basic");
        this.addParameter(CommandParameter.newType("player", CommandParamType.TARGET));
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        Player player = (Player) sender;
        final PlotManager plotManager = this.plugin.getPlotManager(player.getLevel());
        final Plot plot;
        if(plotManager == null || (plot = plotManager.getMergedPlot(player.getFloorX(), player.getFloorZ())) == null) {
            player.sendMessage(this.translate(player, TranslationKey.NO_PLOT));
            return false;
        }

        final String targetName = (args.length > 0 ? args[0] : "").trim();
        final UUID targetId = this.plugin.getUniqueIdByName(targetName);
        final boolean isEveryone = targetId != null && targetId.equals(Utils.UUID_EVERYONE);
        final Player target = targetId != null ? player.getServer().getPlayer(targetId).orElse(null) : null;

        if(targetName.equalsIgnoreCase(player.getName()) && !player.hasPermission("plot.command.admin.deny")) {
            player.sendMessage(this.translate(player, TranslationKey.PLAYER_SELF));
            return false;
        }

        if(targetName.trim().isEmpty() || targetId == null) {
            player.sendMessage(this.translate(player, TranslationKey.NO_PLAYER));
            return false;
        }

        if(!player.hasPermission("plot.command.admin.deny") && !plot.isOwner(player.getUniqueId())) {
            player.sendMessage(this.translate(player, TranslationKey.NO_PLOT_OWNER));
            return false;
        }

        if(!plot.denyPlayer(targetId)) {
            player.sendMessage(this.translate(player, TranslationKey.DENY_FAILURE, this.plugin.getCorrectName(targetId)));
            return false;
        }

        plotManager.savePlot(plot);

        final Plot basePlot = plot.getBasePlot();

        if(target != null || isEveryone) {
            if(!isEveryone) {
                final Plot plotOfTarget = plotManager.getMergedPlot(target.getFloorX(), target.getFloorZ());
                if(plotOfTarget != null && (plot.getOriginId().equals(plotOfTarget.getOriginId())) && !target.hasPermission("plot.admin.bypass.deny"))
                    plotManager.teleportPlayerToPlot(target, basePlot, false);
            } else {
                for(Player onlinePlayer : this.plugin.getServer().getOnlinePlayers().values()) {
                    final Plot plotOfTarget = plotManager.getMergedPlot(onlinePlayer.getFloorX(), onlinePlayer.getFloorZ());
                    if(!plot.isOwner(onlinePlayer.getUniqueId()) && !plot.isHelper(onlinePlayer.getUniqueId()) && plotOfTarget != null && (plot.getOriginId().equals(plotOfTarget.getOriginId())) && !onlinePlayer.hasPermission("plot.admin.bypass.deny"))
                        plotManager.teleportPlayerToPlot(onlinePlayer, basePlot, false);
                }
            }
        }

        player.sendMessage(this.translate(player, TranslationKey.DENY_SUCCESS, this.plugin.getCorrectName(targetId)));
        return true;
    }

}
