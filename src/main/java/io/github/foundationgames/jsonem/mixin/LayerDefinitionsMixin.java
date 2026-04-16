package io.github.foundationgames.jsonem.mixin;

import io.github.foundationgames.jsonem.JsonEM;
import io.github.foundationgames.jsonem.util.JsonEntityModelUtil;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Map;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {
    @Inject(method = "createRoots", at = @At("RETURN"))
    private static void jsonem$dumpModels(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
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
