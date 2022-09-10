package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.events.ItemPickupEvent;
import com.awakenedredstone.sakuracake.item.food.GiantPizzaItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "onPlayerCollision",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;sendPickup(Lnet/minecraft/entity/Entity;I)V",
                    shift = At.Shift.AFTER),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/entity/ItemEntity;pickupDelay:I"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;triggerItemPickedUpByEntityCriteria(Lnet/minecraft/entity/ItemEntity;)V")))
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        ItemPickupEvent.EVENT.invoker().onItemPickup(player, (ItemEntity) (Object) this, this.getStack().copy());
    }

    @Inject(method = "<init>*", at = @At(value = "RETURN"))
    private void init(CallbackInfo ci) {
        if (this.getStack().getItem() instanceof GiantPizzaItem) this.ignoreCameraFrustum = true;
    }
}
