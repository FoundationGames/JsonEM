package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.Dilation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Dilation.class)
public interface DilationAccess {
    @Accessor("radiusX")
    float jsonem$radiusX();

    @Accessor("radiusY")
    float jsonem$radiusY();

    @Accessor("radiusZ")
    float jsonem$radiusZ();
}
