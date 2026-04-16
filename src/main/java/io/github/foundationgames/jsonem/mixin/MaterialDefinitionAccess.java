package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.geom.builders.MaterialDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MaterialDefinition.class)
public interface MaterialDefinitionAccess {
    @Accessor("xTexSize")
    int jsonem$uSize();

    @Accessor("yTexSize")
    int jsonem$vSize();
}
