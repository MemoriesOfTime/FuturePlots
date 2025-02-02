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

package ovis.futureplots.schematic.format;

import cn.nukkit.level.generator.block.state.BlockState;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovis.futureplots.schematic.Schematic;
import ovis.futureplots.schematic.SchematicBlock;
import ovis.futureplots.schematic.SchematicBlockEntity;

import java.util.Map;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SchematicSerializerV3 implements SchematicSerializer {
    public static final SchematicSerializer INSTANCE = new SchematicSerializerV3();

    @Override
    public void serialize(Schematic schematic, BinaryStream binaryStream) {
        binaryStream.putLInt(schematic.getBlockPalette().size());

        for(SchematicBlock block : schematic.getBlockPalette()) {
            binaryStream.putLInt(block.getLayer0().getFullId());
            binaryStream.putLInt(block.getLayer1().getFullId());
        }

        binaryStream.putLInt(schematic.getBlocks().size());
        for(Object2IntMap.Entry<Vector3> entry : schematic.getBlocks().object2IntEntrySet()) {
            final Vector3 blockVector = entry.getKey();
            final int paletteIndex = entry.getIntValue();

            binaryStream.putLInt(blockVector.getFloorX());
            binaryStream.putLInt(blockVector.getFloorY());
            binaryStream.putLInt(blockVector.getFloorZ());

            binaryStream.putLInt(paletteIndex);
        }

        binaryStream.putLInt(schematic.getBlockEntities().size());
        for(Map.Entry<BlockVector3, SchematicBlockEntity> entry : schematic.getBlockEntities().entrySet()) {
            final BlockVector3 blockVector = entry.getKey();
            final SchematicBlockEntity blockEntity = entry.getValue();

            binaryStream.putLInt(blockVector.getX());
            binaryStream.putLInt(blockVector.getY());
            binaryStream.putLInt(blockVector.getZ());

            binaryStream.putString(blockEntity.getType());
            binaryStream.putNbtTag(blockEntity.getCompoundTag());
        }
    }

    @Override
    public void deserialize(Schematic schematic, BinaryStream binaryStream) {
        final int blockPaletteCount = binaryStream.getLInt();
        for(int i = 0; i < blockPaletteCount; i++) {
            final int blockLayer0Id = binaryStream.getLInt();
            final int blockLayer1Id = binaryStream.getLInt();
            schematic.getBlockPalette().add(new SchematicBlock(BlockState.fromFullId(blockLayer0Id), BlockState.fromFullId(blockLayer1Id)));
        }

        final int blockCount = binaryStream.getLInt();
        for(int i = 0; i < blockCount; i++) {
            final int blockX = binaryStream.getLInt();
            final int blockY = binaryStream.getLInt();
            final int blockZ = binaryStream.getLInt();

            final int blockPaletteIndex = binaryStream.getLInt();
            schematic.getBlocks().put(new Vector3(blockX, blockY, blockZ), blockPaletteIndex);
        }

        final int blockEntityCount = binaryStream.getLInt();
        for(int i = 0; i < blockEntityCount; i++) {
            final int blockX = binaryStream.getLInt();
            final int blockY = binaryStream.getLInt();
            final int blockZ = binaryStream.getLInt();

            final String type = binaryStream.getString();
            final CompoundTag compoundTag = binaryStream.getTag();

            schematic.getBlockEntities().put(new BlockVector3(blockX, blockY, blockZ), new SchematicBlockEntity(type, compoundTag));
        }
    }

    @Override
    public int version() {
        return 3;
    }

}
