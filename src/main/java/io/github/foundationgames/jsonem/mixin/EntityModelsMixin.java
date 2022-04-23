package io.github.foundationgames.jsonem.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.foundationgames.jsonem.JsonEM;
import io.github.foundationgames.jsonem.util.JsonEntityModelUtil;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.Map;

@Mixin(EntityModels.class)
public class EntityModelsMixin {
    @Inject(method = "getModels", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void jsonem$dumpModels(CallbackInfoReturnable<Map<EntityModelLayer, TexturedModelData>> cir, ImmutableMap.Builder<EntityModelLayer, TexturedModelData> layers) {
        if ("true".equals(JsonEM.CONFIG.values.getProperty("dump_models"))) {
            layers.build().forEach((layer, data) -> {
                try {
                    JsonEntityModelUtil.dump(layer, data);
                } catch (IOException e) {
                    JsonEM.LOG.error(e);
                }
            });
        }
    }
}
