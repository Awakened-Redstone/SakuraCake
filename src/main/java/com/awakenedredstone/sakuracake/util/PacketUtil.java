package com.awakenedredstone.sakuracake.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class PacketUtil {
    public static void tryUpdateBlockEntityAt(List<ServerPlayerEntity> players, World world, BlockPos pos, BlockState state) {
        if (state.hasBlockEntity()) {
            sendBlockEntityUpdatePacket(players, world, pos);
        }
    }

    public static void sendBlockEntityUpdatePacket(List<ServerPlayerEntity> players, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            Packet<?> packet = blockEntity.toUpdatePacket();
            if (packet != null) {
                sendPacketToPlayers(players, packet);
            }
        }
    }

    public static void sendPacketToPlayers(List<ServerPlayerEntity> players, Packet<?> packet) {
        players.forEach(player -> player.networkHandler./*? if fabric {*/sendPacket/*?} else {*//*send*//*?}*/(packet));
    }
}
