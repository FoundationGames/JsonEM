package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(CubeDefinition.class)
public interface CubeDefinitionAccess {
    @Accessor("comment")
    String jsonem$name();

    @Accessor("origin")
    Vector3fc jsonem$offset();

    @Accessor("dimensions")
    Vector3fc jsonem$dimensions();

    @Accessor("grow")
    CubeDeformation jsonem$dilation();

    @Accessor("mirror")
    boolean jsonem$mirror();

    @Accessor("texCoord")
    UVPair jsonem$uv();

    @Accessor("texScale")
    UVPair jsonem$uvScale();

    @Accessor("visibleFaces")
    Set<Direction> jsonem$faces();

    @Invoker("<init>")
    static CubeDefinition jsonem$create(@Nullable String name, float textureX, float textureY, float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, CubeDeformation extra, boolean mirror, float textureScaleX, float textureScaleY, Set<Direction> directions) {
        throw new AssertionError("mixin broke");
    }
}
