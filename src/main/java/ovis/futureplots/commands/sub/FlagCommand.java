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
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.registry.Registries;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.commands.SubCommand;
import ovis.futureplots.components.util.flags.Flag;
import ovis.futureplots.components.util.flags.FlagRegistry;
import ovis.futureplots.components.util.flags.FlagType;
import ovis.futureplots.components.util.language.TranslationKey;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.Plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
public class FlagCommand extends SubCommand {

    private final FuturePlots plugin;

    public FlagCommand(FuturePlots plugin) {
        super(plugin, "flag");
        this.plugin = plugin;
        this.identify();
        this.playerOnly();
        this.setPermissions("plots.flag", "plots.perm.basic");

        String[] flags = FlagRegistry.getFlags().stream().map(Flag::getSaveName).toList().toArray(new String[0]);
        this.addSubParameter("_set",
                new CommandParameter[]{
                        CommandParameter.newEnum("set", new String[]{"set"}),
                        CommandParameter.newEnum("flag", false, new CommandEnum("flag", flags)),
                        CommandParameter.newType("value", CommandParamType.STRING)
                }
        );
        this.addSubParameter("_add",
                new CommandParameter[]{
                        CommandParameter.newEnum("add", new String[]{"add"}),
                        CommandParameter.newEnum("flag", false, new CommandEnum("flag", flags)),
                        CommandParameter.newType("value", CommandParamType.STRING)
                }
        );
        this.addSubParameter("_remove",
                new CommandParameter[]{
                        CommandParameter.newEnum("remove", new String[]{"remove"}),
                        CommandParameter.newEnum("flag", false, new CommandEnum("flag", flags)),
                        CommandParameter.newType("value", true, CommandParamType.STRING)
                }
        );
        this.addSubParameter("_list",
                new CommandParameter[]{
                        CommandParameter.newEnum("list", new String[]{"list"})

                }
        );
        this.addSubParameter("_info",
                new CommandParameter[]{
                        CommandParameter.newEnum("flag", new String[]{"flag"}),
                        CommandParameter.newEnum("info", new String[]{"info"}),
                        CommandParameter.newEnum("flag", false, new CommandEnum("flag", flags))

                }
        );
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        Player player = (Player) sender;
        final PlotManager plotManager = this.plugin.getPlotManager(player.getLevel());
        final Plot plot;
        if(plotManager == null || (plot = plotManager.getMergedPlot(player.getFloorX(), player.getFloorZ())) == null) {
            player.sendMessage(this.translate(player, TranslationKey.NO_PLOT));
            return;
        }
        final String parameter = args.length > 0 ? args[0] : "";
        final String flagName = args.length > 1 ? args[1] : null;
        final String value = args.length > 2 ? args[2] : null;
        switch (parameter) {
            case "set" -> {
                Flag flag = FlagRegistry.getFlagByName(flagName);
                if(flag == null) {
                    player.sendMessage("§cFlag existiert nicht.");
                    return;
                }
                Object set = flag.update(plot, value);
                player.sendMessage("§aFlag changed to: " + set);
                break;
            }
            case "add" -> {
                Flag flag = FlagRegistry.getFlagByName(flagName);
                if(flag == null) {
                    player.sendMessage("§cFlag existiert nicht.");
                    return;
                }
                if(flag.getType() != FlagType.BLOCK_TYPE_LIST) {
                    player.sendMessage("Diese Flag unterstützt den §cadd §fParameter nicht.");
                    return;
                }
                if(value == null) {
                    player.sendMessage("Gebe einen Block an.");
                    return;
                }
                String blockName = value.toLowerCase();
                final Block block = Registries.BLOCK.get(blockName);
                if(block == null) {
                    player.sendMessage("Dieser Block existiert nicht.");
                    return;
                }
                final BlockState blockState = block.getBlockState();
                player.sendMessage("Block: " + blockState.getIdentifier());
                List<String> plotFlags = plot.getFlagValue(flagName) == null ? new ArrayList<>() : (List<String>) plot.getFlagValue(flagName);
                if(plotFlags.contains(blockName)) {
                    player.sendMessage("§cDieser Block existiert bereits.");
                    return;
                }
                plotFlags.add(blockName);
                plot.setFlagValue(flagName, plotFlags);
                player.sendMessage("§aDie Flags für das Plot wurden geupdated.");
            }
            case "remove" -> {
                Flag flag = FlagRegistry.getFlagByName(flagName);
                if(flag == null) {
                    player.sendMessage("§cFlag existiert nicht.");
                    return;
                }
                if(args.length < 1) {
                    return;
                }
                if(args.length > 2) { // Remove Flag Value

                } else { // Remove Flag
                    plot.removeFlag(flagName);
                }
                break;
            }
            case "list" -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (Flag flag : FlagRegistry.getFlags()) {
                    stringBuilder.append(flag.getSaveName()).append(", ");
                }
                String flags = stringBuilder.substring(0, stringBuilder.length() - 2);
                player.sendMessage("Flags:");
                player.sendMessage(flags);
                break;
            }
            case "info" -> {

                break;
            }
            default -> {
                player.sendMessage("§cUngültiger Parameter.");
            }
        }
    }

}
