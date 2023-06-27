package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(ModelCuboidData.class)
public interface ModelCuboidDataAccess {
    @Accessor("name")
    String jsonem$name();

    @Accessor("offset")
    Vector3f jsonem$offset();

    @Accessor("dimensions")
    Vector3f jsonem$dimensions();

    @Accessor("extraSize")
    Dilation jsonem$dilation();

    @Accessor("mirror")
    boolean jsonem$mirror();

    @Accessor("textureUV")
    Vector2f jsonem$uv();

    @Accessor("textureScale")
    Vector2f jsonem$uvScale();

    @Accessor("directions")
    Set<Direction> jsonem$faces();

    @Invoker("<init>")
    static ModelCuboidData jsonem$create(@Nullable String name, float textureX, float textureY, float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, Dilation extra, boolean mirror, float textureScaleX, float textureScaleY, Set<Direction> directions) {
        throw new AssertionError("mixin broke");
    }
}
