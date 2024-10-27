package com.awakenedredstone.sakuracake.event;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldEvents {
    private static final Map<Integer, WorldEvent> EVENTS = new HashMap<>();

    public static void registerEvent(Identifier id, WorldEvent processor) {
        if (EVENTS.containsKey(id.hashCode())) {
            throw new IllegalStateException("Duplicate event ID");
        }

        EVENTS.put(id.hashCode(), processor);
    }

    public static WorldEvent getEvent(int id) {
        return EVENTS.get(id);
    }

    public static void syncEvent(World world, Identifier id, BlockPos pos, int data) {
        world.syncWorldEvent(id.hashCode(), pos, data);
    }

    @FunctionalInterface
    public interface WorldEvent {
        void process(ClientWorld world, BlockPos pos, int data, Random random);
    }
}
