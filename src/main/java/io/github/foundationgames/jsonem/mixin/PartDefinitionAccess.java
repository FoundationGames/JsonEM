package io.github.foundationgames.jsonem.mixin;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(PartDefinition.class)
public interface PartDefinitionAccess {
    @Accessor("cubes")
    List<CubeDefinition> jsonem$cuboids();

    @Accessor("partPose")
    PartPose jsonem$pose();

    @Accessor("children")
    Map<String, PartDefinition> jsonem$children();

    @Invoker("<init>")
    static PartDefinition create(List<CubeDefinition> cuboids, PartPose pose) {
        throw new AssertionError("mixin broke");
    }
}
