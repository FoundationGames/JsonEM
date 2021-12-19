package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.TextureDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureDimensions.class)
public interface TextureDimensionsAccess {
    @Accessor("width")
    int jsonem$width();

    @Accessor("height")
    int jsonem$height();
}
