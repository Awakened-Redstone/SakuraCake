package com.awakenedredstone.sakuracake.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class Raycasting {
    public HitResult raycast(World world, Entity entity, double maxDistance, boolean includeFluids) {
        Vec3d start = entity.getCameraPosVec(1);
        Vec3d direction = entity.getRotationVec(1);
        Vec3d end = start.add(direction.multiply(maxDistance));
        HitResult result = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.VISUAL, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
        double reach = maxDistance;
        reach *= reach;

        if (result != null) {
            reach = result.getPos().squaredDistanceTo(start);
        }

        Box box = entity.getBoundingBox().stretch(direction.multiply(maxDistance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, start, end, box, entity2 -> !entity2.isSpectator() && entity2.collides(), reach);
        if (entityHitResult != null) {
            Vec3d vec3d4 = entityHitResult.getPos();
            double distance = start.squaredDistanceTo(vec3d4);
            if (distance < reach || result == null) {
                result = entityHitResult;
            }
        }
        return result;
    }
}
