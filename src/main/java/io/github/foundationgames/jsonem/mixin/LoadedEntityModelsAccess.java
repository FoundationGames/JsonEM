package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LoadedEntityModels.class)
public interface LoadedEntityModelsAccess {
    @Accessor("modelParts")
    Map<EntityModelLayer, TexturedModelData> jsonem$getModelParts();
}
