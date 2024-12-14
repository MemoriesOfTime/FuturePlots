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
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowSimple;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.commands.SubCommand;
import ovis.futureplots.components.util.language.TranslationKey;
import ovis.futureplots.manager.PlotManager;

import java.util.Map;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
public class TeleportCommand extends SubCommand {

    private final FuturePlots plugin;

    public TeleportCommand(FuturePlots plugin) {
        super(plugin, "teleport");
        this.plugin = plugin;
        this.identify();
        this.playerOnly();
        this.setPermissions("plots.teleport");
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        Player player = (Player) sender;
        final Map<String, PlotManager> plotManagers = this.plugin.getPlotManagerMap();
        final FormWindowSimple window = new FormWindowSimple(this.translate(player, TranslationKey.TELEPORT_FORM_TITLE), "");

        for(String levelName : plotManagers.keySet())
            window.addButton(new ElementButton(levelName));

        window.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if(!window.wasClosed()) {
                final PlotManager plotManager = plotManagers.get(window.getResponse().getClickedButton().getText());
                if(plotManager == null) return;

                player.teleport(plotManager.getLevel().getSpawnLocation());
                player.sendMessage(this.translate(player, TranslationKey.TELEPORT_SUCCESS, plotManager.getLevel().getFolderPath()));
            }
        }));

        player.showFormWindow(window);
        return;
    }

}
