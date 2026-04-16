package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CubeDeformation.class)
public interface CubeDeformationAccess {
    @Accessor("growX")
    float jsonem$dilateX();

    @Accessor("growY")
    float jsonem$dilateY();

    @Accessor("growZ")
    float jsonem$dilateZ();
}
