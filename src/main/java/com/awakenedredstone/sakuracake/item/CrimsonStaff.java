package com.awakenedredstone.sakuracake.item;

import com.awakenedredstone.sakuracake.util.Explosion;
import com.awakenedredstone.sakuracake.util.Raycasting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CrimsonStaff extends Item {
    public CrimsonStaff(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            if (getPullProgress(this.getMaxUseTime(stack) - remainingUseTicks) < 0.8f) {
                return;
            }
            HitResult raycast = new Raycasting().raycast(world, player, 127, false);
            if (raycast.getType() == HitResult.Type.MISS) {
                return;
            }
            Vec3d target = raycast.getPos();
//            world.createExplosion(player, target.x, target.y, target.z, 1000, Explosion.DestructionType.BREAK);
            Explosion.createExplosion(player, world, new BlockPos(target), 30f);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public static float getPullProgress(int useTicks) {
        float time = (float)useTicks / 20.0f;
        if ((time = (time * time + time * 2.0f) / 3.0f) > 1.0f) {
            time = 1.0f;
        }
        return time;
    }
}
