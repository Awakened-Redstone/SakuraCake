package com.awakenedredstone.sakuracake.item;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.entity.SitEntity;
import com.awakenedredstone.sakuracake.integration.Pehkui;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GiantPizzaItem extends Item {
    private SitEntity sit;

    public GiantPizzaItem(Settings settings) {
        super(settings.food(new FoodComponent.Builder().hunger(Integer.MAX_VALUE).saturationModifier(13f).build()));
    }

    public int getMaxUseTime(ItemStack stack) {
        return 160;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new LiteralText("Don't ask why, it was LolTwitchMods' idea!").formatted(Formatting.DARK_GRAY));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (world.isClient()) {
            world.playSound(user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.MASTER, 120f, 0f, false);
            world.playSound(user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.MASTER, 120f, 0f, false);
            world.playSound(user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.MASTER, 120f, 0f, false);
        } else if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.POOF, user.getX(), user.getY(), user.getZ(), 10000, 1, 1, 1,5);
            serverWorld.spawnParticles(ParticleTypes.CLOUD, user.getX(), user.getY(), user.getZ(), 2000, 1, 1, 1,5);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Integer.MAX_VALUE, 255, false, false));
            if (FabricLoader.getInstance().getModContainer("pehkui").isPresent()) {
                new Pehkui().scaleEntityModel(user, 16f);
                if (user instanceof PlayerEntity player) {
                    sit = SakuraCake.SIT_ENTITY_TYPE.create(world);
                    Vec3d pos = new Vec3d(user.getX(), user.getY(), user.getZ());

                    SitEntity.OCCUPIED.put(pos, player.getBlockPos());
                    sit.updatePosition(pos.getX(), pos.getY(), pos.getZ());
                    world.spawnEntity(sit);
                    player.startRiding(sit);
                }
            }
        }
        Executors.newScheduledThreadPool(1).schedule(() -> {
            user.removeStatusEffect(StatusEffects.SLOWNESS);
            if (FabricLoader.getInstance().getModContainer("pehkui").isPresent()) {
                new Pehkui().resetScale(user);
                discardSit();
            }
        }, 13, TimeUnit.SECONDS);
        return super.finishUsing(stack, world, user);
    }

    private void discardSit() {
        sit.discard();
    }
}
