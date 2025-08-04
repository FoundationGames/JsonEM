package io.github.foundationgames.jsonem.mixin;

import io.github.foundationgames.jsonem.JsonEM;
import io.github.foundationgames.jsonem.util.JsonEntityModelUtil;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Map;

@Mixin(EntityModels.class)
public class EntityModelsMixin {
    @Inject(method = "getModels", at = @At("RETURN"))
    private static void jsonem$dumpModels(CallbackInfoReturnable<Map<EntityModelLayer, TexturedModelData>> cir) {
        if ("true".equals(JsonEM.CONFIG.values.getProperty("dump_models"))) {
            cir.getReturnValue().forEach((layer, data) -> {
                try {
                    JsonEntityModelUtil.dump(layer, data);
                } catch (IOException e) {
                    JsonEM.LOG.error(e);
                }
            });
        }
    }
}
