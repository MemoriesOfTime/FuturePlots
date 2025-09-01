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

package ovis.futureplots.components.provider.data.client.clientdetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author  Tim tim03we, Ovis Development (2024)
 */
@Getter
@RequiredArgsConstructor
public class MySQLDetails extends ClientDetails {

    private final String host, port, user, password, database;
}
