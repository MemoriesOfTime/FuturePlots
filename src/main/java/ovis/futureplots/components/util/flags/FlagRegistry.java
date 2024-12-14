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

package ovis.futureplots.components.util.flags;

import java.util.*;

/**
 * @author Tim tim03we, Ovis Development (2024)
 */
public class FlagRegistry {

    private static final Map<String, Flag> REGISTERED_FLAGS = new HashMap<>();

    static {
        registerFlag(new Flag("break", null, FlagType.BLOCK_TYPE_LIST));
        registerFlag(new Flag("place", null, FlagType.BLOCK_TYPE_LIST));
        registerFlag(new Flag("use", null, FlagType.BLOCK_TYPE_LIST));

        registerFlag(new Flag("pvp", false, FlagType.BOOLEAN));
        registerFlag(new Flag("pve", false, FlagType.BOOLEAN));
    }

    public static Set<Flag> getFlags() {
        return new HashSet<>(REGISTERED_FLAGS.values());
    }

    public static void registerFlag(Flag flag) {
        REGISTERED_FLAGS.put(flag.getSaveName().toLowerCase(), flag);
    }

    public static Flag getFlagByName(String name) {
        return REGISTERED_FLAGS.get(name.toLowerCase());
    }

    public static boolean isDefaultValue(String saveName, Object value) {
        return getFlagByName(saveName).getDefaultValue() == value;
    }
}
