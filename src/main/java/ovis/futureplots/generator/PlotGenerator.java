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

package ovis.futureplots.generator;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.block.state.BlockState;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.schematic.Schematic;
import ovis.futureplots.components.util.Allowed;
import ovis.futureplots.components.util.LevelUtils;
import ovis.futureplots.components.util.PlotLevelSettings;
import ovis.futureplots.components.util.ShapeType;
import ovis.futureplots.components.util.async.TaskExecutor;

import java.util.*;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
public class PlotGenerator extends Generator {

    private ChunkManager chunkManager;

    private NukkitRandom random;

    public static final Allowed<ShapeType> GENERATE_ALLOWED = new Allowed<>(ShapeType.values());
    public static final Allowed<ShapeType> REGENERATE_ALLOWED = new Allowed<>(ShapeType.WALL, ShapeType.ROAD);
    private final FuturePlots plugin;
    private Level level;

    public PlotGenerator() {
        this(Collections.emptyMap());
    }

    public PlotGenerator(Map<String, Object> options) {
        this.plugin = FuturePlots.INSTANCE;
    }

    @Override
    public int getId() {
        return 101;
    }

    @Override
    public DimensionData getDimensionData() {
        final PlotManager plotManager;
        if (this.level != null && (plotManager = this.plugin.getPlotManager(this.level)) != null)
            return DimensionEnum.getDataFromId(plotManager.getLevelSettings().getDimension());
        return DimensionEnum.OVERWORLD.getDimensionData();
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.chunkManager = level;
        this.random = random;
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(-3, 65, -3);
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    public void populateStructure(int chunkX, int chunkZ) {

    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        if (this.level == null) {
            this.level = this.chunkManager.getChunk(chunkX, chunkZ).getProvider().getLevel();
        }

        final PlotManager plotManager = FuturePlots.INSTANCE.getPlotManager(this.level);
        if (plotManager == null) return;

        BaseFullChunk chunk = this.chunkManager.getChunk(chunkX, chunkZ);

        final ShapeType[] shapes = plotManager.getShapes(chunk.getX() << 4, chunk.getZ() << 4);

        preGenerateChunk(plotManager, chunk, shapes, GENERATE_ALLOWED, true, null, null, null, null);
        final Schematic schematic = plotManager.getPlotSchematic().getSchematic();
        if (schematic != null)
            placeChunkSchematic(plotManager, schematic, chunk, shapes, GENERATE_ALLOWED, null, null, null, null);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {

    }

    public void regenerateChunk(PlotManager plotManager, BaseFullChunk IChunk) {
        final ShapeType[] shapes = plotManager.getShapes(IChunk.getX() << 4, IChunk.getZ() << 4);

        final List<Entity> toClose0 = new ArrayList<>();
        final List<BlockEntity> toClose1 = new ArrayList<>();

        for (Entity entity : new ArrayList<>(IChunk.getEntities().values())) {
            if (entity instanceof Player) continue;

            if (REGENERATE_ALLOWED.isDisallowed(shapes[((entity.getFloorZ() & 15) << 4) | (entity.getFloorX() & 15)]))
                continue;

            toClose0.add(entity);
        }

        for (BlockEntity blockEntity : new ArrayList<>(IChunk.getBlockEntities().values())) {
            if (REGENERATE_ALLOWED.isDisallowed(shapes[((blockEntity.getFloorZ() & 15) << 4) | (blockEntity.getFloorX() & 15)]))
                continue;

            toClose1.add(blockEntity);
        }

        TaskExecutor.execute(() -> {
            for (Entity entity : toClose0) entity.close();
            for (BlockEntity blockEntity : toClose1) blockEntity.close();
        });

        preGenerateChunk(plotManager, IChunk, shapes, REGENERATE_ALLOWED, false, null, null, null, null);

        final Schematic schematic = plotManager.getPlotSchematic().getSchematic();
        if (schematic != null)
            placeChunkSchematic(plotManager, schematic, IChunk, shapes, REGENERATE_ALLOWED, null, null, null, null);

        final Level level = IChunk.getProvider().getLevel();
        level.getChunkPlayers(IChunk.getX(), IChunk.getZ()).values().forEach(player -> level.requestChunk(IChunk.getX(), IChunk.getZ(), player));
    }

    public void regenerateChunkWithin(PlotManager plotManager, BaseFullChunk IChunk, int minX, int minZ, int maxX, int maxZ) {
        final ShapeType[] shapes = plotManager.getShapes(IChunk.getX() << 4, IChunk.getZ() << 4);

        final List<Entity> toClose0 = new ArrayList<>();
        final List<BlockEntity> toClose1 = new ArrayList<>();

        for (Entity entity : new ArrayList<>(IChunk.getEntities().values())) {
            if (entity instanceof Player) continue;
            if (entity.getFloorX() < minX || entity.getFloorX() > maxX || entity.getFloorZ() < minZ || entity.getFloorZ() > maxZ)
                continue;

            if (REGENERATE_ALLOWED.isDisallowed(shapes[((entity.getFloorZ() & 15) << 4) | (entity.getFloorX() & 15)]))
                continue;

            toClose0.add(entity);
        }

        for (BlockEntity blockEntity : new ArrayList<>(IChunk.getBlockEntities().values())) {
            if (blockEntity.getFloorX() < minX || blockEntity.getFloorX() > maxX || blockEntity.getFloorZ() < minZ || blockEntity.getFloorZ() > maxZ)
                continue;
            if (REGENERATE_ALLOWED.isDisallowed(shapes[((blockEntity.getFloorZ() & 15) << 4) | (blockEntity.getFloorX() & 15)]))
                continue;

            toClose1.add(blockEntity);
        }

        TaskExecutor.execute(() -> {
            for (Entity entity : toClose0) entity.close();
            for (BlockEntity blockEntity : toClose1) blockEntity.close();
        });

        this.preGenerateChunk(plotManager, IChunk, shapes, REGENERATE_ALLOWED, false, minX, minZ, maxX, maxZ);

        final Schematic schematic = plotManager.getPlotSchematic().getSchematic();
        if (schematic != null)
            this.placeChunkSchematic(plotManager, schematic, IChunk, shapes, REGENERATE_ALLOWED, minX, minZ, maxX, maxZ);
    }

    public boolean isDefaultBlockStateAt(PlotManager plotManager, ShapeType[] shapes, Vector3 blockVector, BlockState blockState) {
        final PlotLevelSettings levelSettings = plotManager.getLevelSettings();
        final int xBlock = blockVector.getFloorX() & 15;
        final int yBlock = blockVector.getFloorY();
        final int zBlock = blockVector.getFloorZ() & 15;
        final ShapeType shapeType = shapes[(zBlock << 4) | xBlock];

        final int dimension = plotManager.getLevelSettings().getDimension();
        final int chunkMinY = LevelUtils.getChunkMinY(dimension);

        if (yBlock == chunkMinY)
            return levelSettings.getFirstLayerState().equals(blockState);
        else if (yBlock < levelSettings.getGroundHeight() + chunkMinY)
            return switch (shapeType) {
                case PLOT -> levelSettings.getMiddleLayerState().equals(blockState);
                case WALL -> levelSettings.getWallFillingState().equals(blockState);
                case ROAD -> levelSettings.getRoadFillingState().equals(blockState);
            };
        else if (yBlock == levelSettings.getGroundHeight() + chunkMinY)
            return switch (shapeType) {
                case PLOT -> levelSettings.getLastLayerState().equals(blockState);
                case WALL -> levelSettings.getWallFillingState().equals(blockState);
                case ROAD -> levelSettings.getRoadState().equals(blockState);
            };
        else if (yBlock == levelSettings.getGroundHeight() + 1 + chunkMinY)
            if (shapeType == ShapeType.WALL)
                return levelSettings.getWallPlotState().equals(blockState) || levelSettings.getClaimPlotState().equals(blockState);

        return BlockState.AIR.equals(blockState);
    }

    public static void placeChunkSchematic(PlotManager plotManager, Schematic schematic, BaseFullChunk IChunk,
                                           ShapeType[] shapes, Allowed<ShapeType> allowedShapes, Integer minX, Integer minZ, Integer maxX, Integer maxZ) {
        final Set<Vector3> handledVectors = new HashSet<>();
        final int fullX = IChunk.getX() << 4;
        final int fullZ = IChunk.getZ() << 4;

        for (int blockX = fullX; blockX < fullX + 16; blockX++) {
            if (minX != null && (blockX < minX || blockX > maxX)) continue;
            for (int blockZ = fullZ; blockZ < fullZ + 16; blockZ++) {
                if (minZ != null && (blockZ < minZ || blockZ > maxZ)) continue;

                final Vector3 plotArea = getPlotAreaStart(plotManager, blockX, blockZ);
                if (handledVectors.contains(plotArea)) continue;

                handledVectors.add(plotArea);
                schematic.buildInChunk(plotArea, IChunk, shapes, allowedShapes, minX, minZ, maxX, maxZ);
            }
        }
    }

    public static void preGenerateChunk(PlotManager plotManager, BaseFullChunk IChunk, ShapeType[] shapes,
                                        Allowed<ShapeType> allowedShapes, boolean ignoreAir, Integer minX, Integer minZ, Integer maxX, Integer maxZ) {
        final PlotLevelSettings levelSettings = plotManager.getLevelSettings();

        final BlockState firstLayerState = levelSettings.getFirstLayerState();
        final BlockState middleLayerState = levelSettings.getMiddleLayerState();
        final BlockState wallFillingState = levelSettings.getWallFillingState();
        final BlockState roadFillingState = levelSettings.getRoadFillingState();
        final BlockState lastLayerState = levelSettings.getLastLayerState();
        final BlockState wallPlotState = levelSettings.getWallPlotState();
        final BlockState roadState = levelSettings.getRoadState();

        final int dimension = plotManager.getLevelSettings().getDimension();
        final int chunkMinY = LevelUtils.getChunkMinY(dimension);
        final int chunkMaxY = LevelUtils.getChunkMaxY(dimension);

        for (int xBlock = 0; xBlock < 16; ++xBlock) {
            if (minX != null && (xBlock + (IChunk.getX() << 4) < minX || xBlock + (IChunk.getX() << 4) > maxX))
                continue;
            for (int zBlock = 0; zBlock < 16; ++zBlock) {
                if (minZ != null && (zBlock + (IChunk.getZ() << 4) < minZ || zBlock + (IChunk.getZ() << 4) > maxZ))
                    continue;

                final ShapeType shapeType = shapes[(zBlock << 4) | xBlock];
                if (allowedShapes.isDisallowed(shapeType)) continue;

                if (shapeType == ShapeType.PLOT) {
                    for (int i = chunkMinY; i <= chunkMaxY; i++) {
                        IChunk.setBiomeId(xBlock, i, zBlock, levelSettings.getPlotBiome());
                    }
                } else {
                    for (int i = chunkMinY; i <= chunkMaxY; i++) {
                        IChunk.setBiomeId(xBlock, i, zBlock, levelSettings.getRoadBiome());
                    }
                }

                if (!ignoreAir) for (int yBlock = chunkMinY; yBlock <= chunkMaxY; ++yBlock)
                    IChunk.setBlockIdAt(xBlock, yBlock, zBlock, 1, BlockID.AIR);

                IChunk.setBlockFullIdAt(xBlock, chunkMinY, zBlock, firstLayerState.getFullId());

                final BlockState currentState;
                if (shapeType == ShapeType.PLOT) currentState = middleLayerState;
                else if (shapeType == ShapeType.WALL) currentState = wallFillingState;
                else currentState = roadFillingState;

                for (int yBlock = 1; yBlock < levelSettings.getGroundHeight(); ++yBlock)
                    IChunk.setBlockFullIdAt(xBlock, yBlock + chunkMinY, zBlock, currentState.getFullId());

                if (shapeType == ShapeType.PLOT) {
                    IChunk.setBlockFullIdAt(xBlock, levelSettings.getGroundHeight() + chunkMinY, zBlock, lastLayerState.getFullId());
                    if (!ignoreAir)
                        for (int yBlock = levelSettings.getGroundHeight() + 1; yBlock <= chunkMaxY; ++yBlock)
                            IChunk.setBlockFullIdAt(xBlock, yBlock, zBlock, BlockState.AIR.getFullId());
                } else {
                    if (shapeType == ShapeType.WALL) {
                        IChunk.setBlockFullIdAt(xBlock, levelSettings.getWallHeight() + 1 + chunkMinY, zBlock, wallPlotState.getFullId());
                        IChunk.setBlockFullIdAt(xBlock, levelSettings.getWallHeight() + chunkMinY, zBlock, wallFillingState.getFullId());
                        if (!ignoreAir)
                            for (int yBlock = levelSettings.getWallHeight() + 2; yBlock <= chunkMaxY; ++yBlock)
                                IChunk.setBlockFullIdAt(xBlock, yBlock, zBlock, BlockState.AIR.getFullId());
                    } else {
                        IChunk.setBlockFullIdAt(xBlock, levelSettings.getGroundHeight() + chunkMinY, zBlock, roadState.getFullId());
                        if (!ignoreAir)
                            for (int yBlock = levelSettings.getGroundHeight() + 1; yBlock <= chunkMaxY; ++yBlock)
                                IChunk.setBlockFullIdAt(xBlock, yBlock, zBlock, BlockState.AIR.getFullId());
                    }
                    /* Old way without wallheight
                    if (shapeType == ShapeType.WALL) {
                        IChunk.setBlockState(xBlock, levelSettings.getGroundHeight() + 1 + chunkMinY, zBlock, wallPlotState);
                        IChunk.setBlockState(xBlock, levelSettings.getGroundHeight() + chunkMinY, zBlock, wallFillingState);
                        if (!ignoreAir)
                            for (int yBlock = levelSettings.getGroundHeight() + 2; yBlock <= chunkMaxY; ++yBlock)
                                IChunk.setBlockState(xBlock, yBlock, zBlock, BlockAir.STATE);
                    } else {
                        IChunk.setBlockState(xBlock, levelSettings.getGroundHeight() + chunkMinY, zBlock, roadState);
                        if (!ignoreAir)
                            for (int yBlock = levelSettings.getGroundHeight() + 1; yBlock <= chunkMaxY; ++yBlock)
                                IChunk.setBlockState(xBlock, yBlock, zBlock, BlockAir.STATE);
                    }
                     */
                }
            }
        }
    }

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return "Plot";
    }

    public static Vector3 getPlotAreaStart(PlotManager plotManager, int x, int z) {
        final PlotLevelSettings levelSettings = plotManager.getLevelSettings();
        final int totalSize = levelSettings.getTotalSize();
        final int xPart = x / totalSize;
        final int zPart = z / totalSize;
        return new Vector3(
                (x < 0 ? xPart - 1 : xPart) * totalSize,
                LevelUtils.getChunkMinY(levelSettings.getDimension()),
                (z < 0 ? zPart - 1 : zPart) * totalSize
        );
    }

    public static Vector3 getPlotAreaEnd(PlotManager plotManager, int x, int z) {
        final PlotLevelSettings levelSettings = plotManager.getLevelSettings();
        final int totalSize = levelSettings.getTotalSize();
        final int xPart = x / totalSize;
        final int zPart = z / totalSize;
        return new Vector3(
                (x < 0 ? xPart : xPart + 1) * totalSize,
                LevelUtils.getChunkMaxY(levelSettings.getDimension()),
                (z < 0 ? zPart : zPart + 1) * totalSize
        );
    }

    public static Vector3[] getPlotArea(PlotManager plotManager, int x, int z) {
        return new Vector3[]{getPlotAreaStart(plotManager, x, z), getPlotAreaEnd(plotManager, x, z)};
    }

}
