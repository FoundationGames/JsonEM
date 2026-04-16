package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LayerDefinition.class)
public interface LayerDefinitionAccess {
    @Accessor("mesh")
    MeshDefinition jsonem$root();

    @Accessor("material")
    MaterialDefinition jsonem$texture();

    @Invoker("<init>")
    static LayerDefinition create(MeshDefinition data, MaterialDefinition texture) {
        throw new AssertionError("mixin broke");
    }
}
