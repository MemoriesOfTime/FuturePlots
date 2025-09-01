/*
 * Copyright 2025 tim03we, Ovis Development
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

package ovis.futureplots.components.provider.economy.client;

import ovis.futureplots.components.provider.economy.client.connection.Connection;
import ovis.futureplots.components.provider.economy.client.connection.LlamaEconomyConnection;
import ovis.futureplots.components.provider.economy.client.enums.ClientType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author  Tim tim03we, Ovis Development (2025)
 */
public class EconomyClient {

    private Connection economy;

    private Map<String, Connection> economyMap = new HashMap<>() {{
       put("llamaeconomy", new LlamaEconomyConnection());
    }};

    public EconomyClient(ClientType type) {
        this.economy = economyMap.get(type.toString());
    }

    public EconomyClient(Connection economyConnection) {
        this.economy = economyConnection;
    }

    public Connection getAPI() {
        return this.economy;
    }
}
