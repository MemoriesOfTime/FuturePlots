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

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.generator.block.state.BlockState;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
@Getter
@Setter
public class PlotLevelSettings {

    private int dimension = Level.DIMENSION_OVERWORLD;
    private int plotBiome = EnumBiome.PLAINS.id;
    private int roadBiome = EnumBiome.PLAINS.id;
    private int groundHeight = 64;
    private int wallHeight = 64;
    private int plotSize = 35;
    private int roadSize = 7;

    private int claimPrice = 100;
    private int clearPrice = 100;
    private int resetPrice = 100;
    private int mergePrice = 100;

    private int firstLayerBlockHash = new BlockState(Block.BEDROCK, 0).getFullId();
    private int middleLayerBlockHash = new BlockState(Block.DIRT, 0).getFullId();
    private int lastLayerBlockHash = new BlockState(Block.GRASS, 0).getFullId();
    private int wallFillingBlockHash = new BlockState(Block.STONE, 0).getFullId();
    private int wallPlotBlockHash = new BlockState(Block.STONE_SLAB, 0).getFullId();
    private int claimPlotBlockHash = new BlockState(Block.STONE_SLAB, BlockSlabStone.BRICK).getFullId();
    private int roadBlockHash = new BlockState(Block.PLANKS, BlockPlanks.OAK).getFullId();
    private int roadFillingBlockHash = new BlockState(Block.DIRT, 0).getFullId();

    public BlockState getFirstLayerState() {
        return BlockState.fromFullId(this.firstLayerBlockHash);
    }

    public BlockState getMiddleLayerState() {
        return BlockState.fromFullId(middleLayerBlockHash);
    }

    public BlockState getLastLayerState() {
        return BlockState.fromFullId(lastLayerBlockHash);
    }

    public BlockState getWallFillingState() {
        return BlockState.fromFullId(wallFillingBlockHash);
    }

    public BlockState getWallPlotState() {
        return BlockState.fromFullId(wallPlotBlockHash);
    }

    public BlockState getClaimPlotState() {
        return BlockState.fromFullId(claimPlotBlockHash);
    }

    public BlockState getRoadState() {
        return BlockState.fromFullId(roadBlockHash);
    }

    public BlockState getRoadFillingState() {
        return BlockState.fromFullId(roadFillingBlockHash);
    }

    public int getTotalSize() {
        return this.plotSize + this.roadSize;
    }

    public void fromMap(Map<String, Object> map) {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Class<?> type = field.getType();
                if (field.getType() == Integer.class || type == int.class) {
                    Number number = (Number) map.get(field.getName());
                    field.set(this, number.intValue());
                } else if (type == Short.class || type == short.class) {
                    Number number = (Number) map.get(field.getName());
                    field.set(this, number.shortValue());
                } else if (type == Byte.class || type == byte.class) {
                    Number number = (Number) map.get(field.getName());
                    field.set(this, number.byteValue());
                } else if (type == Double.class || type == double.class) {
                    Number number = (Number) map.get(field.getName());
                    field.set(this, number.doubleValue());
                } else if (type == Float.class || type == float.class) {
                    Number number = (Number) map.get(field.getName());
                    field.set(this, number.floatValue());
                } else if (type == Long.class || type == long.class) {
                    Number number = (Number) map.get(field.getName());
                    field.set(this, number.longValue());
                } else {
                    field.set(this, map.get(field.getName()));
                }
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(this));
            } catch (IllegalAccessException ignored) {
            }
        }
        return map;
    }
}
