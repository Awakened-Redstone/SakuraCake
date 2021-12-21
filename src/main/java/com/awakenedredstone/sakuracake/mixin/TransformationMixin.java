package com.awakenedredstone.sakuracake.mixin;

import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Transformation.Deserializer.class)
public class TransformationMixin {

    @ModifyArgs(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/Transformation;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3f;clamp(FF)V", ordinal = 1))
    private void removeScaleCap(Args args) {
        args.setAll(-1024.0f, 1024.0f);
    }


}
