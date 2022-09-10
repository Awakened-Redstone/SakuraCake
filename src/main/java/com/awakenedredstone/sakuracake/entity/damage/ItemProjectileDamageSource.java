package com.awakenedredstone.sakuracake.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ItemProjectileDamageSource extends EntityDamageSource {
    @Nullable
    private final Entity attacker;

    public ItemProjectileDamageSource(String name, Entity projectile, @Nullable Entity attacker) {
        super(name, projectile);
        this.attacker = attacker;
    }

    @Override
    @Nullable
    public Entity getSource() {
        return this.source;
    }

    @Override
    @Nullable
    public Entity getAttacker() {
        return this.attacker;
    }

    @Override
    public Text getDeathMessage(LivingEntity entity) {
        Text text = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
        ItemStack itemStack = (source instanceof ItemEntity itemEntity) ? itemEntity.getStack() : ItemStack.EMPTY;
        String string = "death.attack." + this.name;
        if (!itemStack.isEmpty()) {
            return Text.translatable(string, entity.getDisplayName(), text, itemStack.toHoverableText());
        }
        return Text.translatable(string, entity.getDisplayName(), text, "Unknown item");
    }
}
