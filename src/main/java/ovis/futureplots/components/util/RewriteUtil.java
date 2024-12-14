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

package ovis.futureplots.components.util;

import lombok.experimental.UtilityClass;
import ovis.futureplots.FuturePlots;

import java.io.File;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
@UtilityClass
public class RewriteUtil {

    public void rewriteIfOld(FuturePlots plugin, File configFile, String levelName) {
        /*
        final LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        final Yaml yaml = new Yaml(loaderOptions);

        Map<String, Object> map;
        try(final FileInputStream inputStream = new FileInputStream(configFile)) {
            //noinspection unchecked
            map = yaml.loadAs(inputStream, Map.class);
        } catch(IOException e) {
            plugin.getLogger().error("Could not read config file for level " + levelName + "!", e);
            map = Collections.emptyMap();
        }

        final Provider provider = plugin.getProvider();
        provider.createPlotsTable(levelName);

        final List<?> plots = (List<?>) map.get("plots");
        if(plots != null && !plots.isEmpty()) {
            final List<Provider.DatabaseAction> databaseActions = new ArrayList<>();

            for(Object plotObject : plots) {
                final Map<?, ?> plotMap = (Map<?, ?>) plotObject;
                final String owner = (String) plotMap.get("owner");
                final int plotX = ((Number) plotMap.get("x")).intValue();
                final int plotZ = ((Number) plotMap.get("z")).intValue();
                final PlotId plotId = PlotId.of(plotX, plotZ);

                final List<String> helpers = ((List<?>) plotMap.get("helpers")).stream().map(o -> (String) o).toList();
                final List<String> deniedPlayers = ((List<?>) plotMap.get("denied")).stream().map(o -> (String) o).toList();
                final Map<String, Object> config = ((Map<?, ?>) plotMap.get("config")).entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), Map.Entry::getValue));
                final List<?> homePositionList = (List<?>) plotMap.get("home-position");
                final BlockVector3 homePosition = homePositionList == null || homePositionList.size() != 3 ? null : new BlockVector3(
                        ((Number) homePositionList.get(0)).intValue(),
                        ((Number) homePositionList.get(1)).intValue(),
                        ((Number) homePositionList.get(2)).intValue()
                );

                final List<?> mergedDirectionList = (List<?>) plotMap.get("merges");
                final boolean[] mergedDirections = new boolean[mergedDirectionList.size()];
                for(int i = 0; i < mergedDirectionList.size(); i++)
                    mergedDirections[i] = (boolean) mergedDirectionList.get(i);

                databaseActions.add(provider.insertPlot(
                        levelName,
                        plotId,
                        owner == null || owner.isBlank() || owner.equals("null") ? null : UUID.fromString(owner).toString(),
                        helpers,
                        deniedPlayers,
                        config,
                        mergedDirections,
                        homePosition,
                        plotId
                ));
            }

            //provider.executeActions(databaseActions);
        }

        final Map<?, ?> settings = (Map<?, ?>) map.get("Settings");
        if(settings != null && !settings.isEmpty() && configFile.delete()) {
            try {
                final Config config = new Config(configFile, Config.YAML);
                config.setAll(new LinkedHashMap<>(settings.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), Map.Entry::getValue))));
                config.save(configFile);
            } catch(Exception e) {
                plugin.getLogger().error("Could not write new settings into config file for level " + levelName + "!", e);
            }
        }
         */
    }

}
