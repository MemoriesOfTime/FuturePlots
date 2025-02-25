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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
@Getter
@Setter
@AllArgsConstructor
public class ChunkVector {

    private int x;
    private int z;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChunkVector && ((ChunkVector) obj).x == this.x && ((ChunkVector) obj).z == this.z;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.z + ")";
    }

}
