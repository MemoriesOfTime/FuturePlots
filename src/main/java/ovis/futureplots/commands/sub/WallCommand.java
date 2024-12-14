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
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowSimple;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.commands.SubCommand;
import ovis.futureplots.components.util.language.TranslationKey;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.BlockEntry;
import ovis.futureplots.components.util.Plot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static cn.nukkit.form.element.ElementButtonImageData.IMAGE_DATA_TYPE_PATH;
import static cn.nukkit.form.element.ElementButtonImageData.IMAGE_DATA_TYPE_URL;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
public class WallCommand extends SubCommand {

    private final FuturePlots plugin;

    public WallCommand(FuturePlots plugin) {
        super(plugin, "wall");
        this.plugin = plugin;
        this.identify();
        this.playerOnly();
        this.setPermissions("plots.wall");
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

        if(!player.hasPermission("plot.command.admin.wall") && !plot.isOwner(player.getUniqueId())) {
            player.sendMessage(this.translate(player, TranslationKey.NO_PLOT_OWNER));
            return;
        }

        final FormWindowSimple window = new FormWindowSimple(this.translate(player, TranslationKey.WALL_FORM_TITLE), "");

        final Map<ElementButton, BlockEntry> buttons = new HashMap<>();
        for(BlockEntry entry : this.plugin.getWallEntries()) {
            final String text;
            if(entry.isDefault()) {
                text = this.translate(player, TranslationKey.WALL_RESET_TO_DEFAULT_BUTTON);
            } else {
                final boolean hasPerm = entry.getPermission() == null || player.hasPermission(entry.getPermission());
                final String permText = this.translate(player, hasPerm ? TranslationKey.WALL_BUTTON_HAS_PERM : TranslationKey.WALL_BUTTON_NO_PERM);

                text = this.translate(player, TranslationKey.WALL_FORM_BUTTON, entry.getName(), permText);
            }

            final String imageType = entry.getImageType();
            final ElementButtonImageData imageData;
            switch(imageType == null ? "" : imageType.toLowerCase(Locale.ROOT)) {
                case "url" -> imageData = new ElementButtonImageData(IMAGE_DATA_TYPE_URL, entry.getImageData());
                case "path" -> imageData = new ElementButtonImageData(IMAGE_DATA_TYPE_PATH, entry.getImageData());
                default -> imageData = null;
            }

            final ElementButton button = new ElementButton(text);
            if(imageData != null) button.addImage(imageData);

            window.addButton(button);
            buttons.put(button, entry);
        }

        window.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if(!window.wasClosed()) {
                final ElementButton button = window.getResponse().getClickedButton();
                final BlockEntry entry;
                if(button == null || (entry = buttons.get(button)) == null) return;

                if(entry.getPermission() != null && !player.hasPermission(entry.getPermission())) {
                    player.sendMessage(this.translate(player, TranslationKey.WALL_NO_PERMS, entry.getName()));
                    return;
                }

                if(entry.isDefault()) {
                    for(Plot mergedPlot : plotManager.getConnectedPlots(plot))
                        plotManager.changeWall(mergedPlot, plotManager.getLevelSettings().getWallFillingState());
                    player.sendMessage(this.translate(player, TranslationKey.WALL_RESET_TO_DEFAULT_SUCCESS));
                } else {
                    for(Plot mergedPlot : plotManager.getConnectedPlots(plot))
                        plotManager.changeWall(mergedPlot, entry.getBlockState());
                    player.sendMessage(this.translate(player, TranslationKey.WALL_SUCCESS, entry.getName()));
                }
            }
        }));

        player.showFormWindow(window);
        return;
    }

}
