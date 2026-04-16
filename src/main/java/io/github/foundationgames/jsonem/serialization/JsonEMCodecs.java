package io.github.foundationgames.jsonem.serialization;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.foundationgames.jsonem.mixin.CubeDefinitionAccess;
import io.github.foundationgames.jsonem.mixin.CubeDeformationAccess;
import io.github.foundationgames.jsonem.mixin.LayerDefinitionAccess;
import io.github.foundationgames.jsonem.mixin.MaterialDefinitionAccess;
import io.github.foundationgames.jsonem.mixin.PartDefinitionAccess;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JsonEMCodecs {
    private static final Set<Direction> ALL_DIRECTIONS = EnumSet.allOf(Direction.class);

    public static final Codec<UVPair> UV_PAIR = Codec.FLOAT.listOf().comapFlatMap((vec) ->
            Util.fixedSize(vec, 2).map((arr) -> new UVPair(arr.get(0), arr.get(1))),
            (vec) -> ImmutableList.of(vec.u(), vec.v())
    );

    public static final Codec<Vector3fc> VECTOR3F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
                    Util.fixedSize(vec, 3).map(coords -> new Vector3f(coords.get(0), coords.get(1), coords.get(2))),
            (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z())
    );

    public static final Codec<MaterialDefinition> MATERIAL_DEFINITION = RecordCodecBuilder.create((instance) ->
        instance.group(
                Codec.INT.fieldOf("width").forGetter(obj -> ((MaterialDefinitionAccess) obj).jsonem$uSize()),
                Codec.INT.fieldOf("height").forGetter(obj -> ((MaterialDefinitionAccess) obj).jsonem$vSize())
        ).apply(instance, MaterialDefinition::new)
    );

    public static final Codec<PartPose> PART_POSE = RecordCodecBuilder.create((instance) ->
            instance.group(
                    VECTOR3F.optionalFieldOf("origin", new Vector3f(0)).forGetter(obj -> new Vector3f(obj.x(), obj.y(), obj.z())),
                    VECTOR3F.optionalFieldOf("rotation", new Vector3f(0)).forGetter(obj -> new Vector3f(obj.x(), obj.y(), obj.z()))
            ).apply(instance, (origin, rot) -> PartPose.offsetAndRotation(origin.x(), origin.y(), origin.z(), rot.x(), rot.y(), rot.z()))
    );

    public static final Codec<CubeDeformation> CUBE_DEFORMATION = VECTOR3F.xmap(
            vec -> new CubeDeformation(vec.x(), vec.y(), vec.z()),
            dil -> new Vector3f(
                    ((CubeDeformationAccess) dil).jsonem$dilateX(),
                    ((CubeDeformationAccess) dil).jsonem$dilateY(),
                    ((CubeDeformationAccess) dil).jsonem$dilateZ())
    );

    private static CubeDefinition createCubeDefinition(Optional<String> name, Vector3fc offset, Vector3fc dimensions, CubeDeformation dilation, boolean mirror, UVPair uv, UVPair uvSize, Optional<List<Direction>> faces) {
        return CubeDefinitionAccess.jsonem$create(name.orElse(null),
                uv.u(), uv.v(),
                offset.x(), offset.y(), offset.z(),
                dimensions.x(), dimensions.y(), dimensions.z(),
                dilation, mirror,
                uvSize.u(), uvSize.v(),
                faces.map(Set::copyOf).orElse(ALL_DIRECTIONS));
    }

    // If the set has all faces, return empty
    private static Optional<List<Direction>> optionalFaceList(Set<Direction> faces) {
        for (Direction direction : Direction.values()) {
            if (!faces.contains(direction)) {
                return Optional.of(List.copyOf(faces));
            }
        }

        return Optional.empty();
    }

    private static final UVPair DEFAULT_UV_SCALE = new UVPair(1.0f, 1.0f);

    public static final Codec<CubeDefinition> CUBE_DEFINITION = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.STRING.optionalFieldOf("name").forGetter(obj -> Optional.ofNullable(((CubeDefinitionAccess) (Object) obj).jsonem$name())),
                    VECTOR3F.fieldOf("offset").forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).jsonem$offset()),
                    VECTOR3F.fieldOf("dimensions").forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).jsonem$dimensions()),
                    CUBE_DEFORMATION.optionalFieldOf("dilation", CubeDeformation.NONE).forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).jsonem$dilation()),
                    Codec.BOOL.optionalFieldOf("mirror", false).forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).jsonem$mirror()),
                    UV_PAIR.fieldOf("uv").forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).jsonem$uv()),
                    UV_PAIR.optionalFieldOf("uv_scale", DEFAULT_UV_SCALE).forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).jsonem$uvScale()),
                    Codec.list(Direction.CODEC).optionalFieldOf("faces").forGetter(obj -> optionalFaceList(((CubeDefinitionAccess)(Object)obj).jsonem$faces()))
            ).apply(instance, JsonEMCodecs::createCubeDefinition)
    );

    public static final Codec<PartDefinition> PART_DEFINITION = Codec.recursive("JsonEM Model Part Definition", self ->
        RecordCodecBuilder.create(i -> i.group(
            PART_POSE.optionalFieldOf("transform", PartPose.ZERO).forGetter(obj -> ((PartDefinitionAccess) obj).jsonem$pose()),
            Codec.list(CUBE_DEFINITION).fieldOf("cuboids").forGetter(obj -> ((PartDefinitionAccess) obj).jsonem$cuboids()),
            Codec.unboundedMap(Codec.STRING, self).optionalFieldOf("children", new HashMap<>()).forGetter(obj -> ((PartDefinitionAccess) obj).jsonem$children())
        ).apply(i, (transform, cuboids, children) -> {
            var data = PartDefinitionAccess.create(cuboids, transform);
            ((PartDefinitionAccess) data).jsonem$children().putAll(children);
            return data;
        })));

    public static final Codec<LayerDefinition> LAYER_DEFINITION = RecordCodecBuilder.create((instance) ->
            instance.group(
                    MATERIAL_DEFINITION.fieldOf("texture").forGetter(obj -> ((LayerDefinitionAccess) obj).jsonem$texture()),
                    Codec.unboundedMap(Codec.STRING, PART_DEFINITION).fieldOf("bones").forGetter(obj -> ((PartDefinitionAccess) ((LayerDefinitionAccess) obj).jsonem$root().getRoot()).jsonem$children())
            ).apply(instance, (texture, bones) -> {
                var data = new MeshDefinition();
                ((PartDefinitionAccess) data.getRoot()).jsonem$children().putAll(bones);
                return LayerDefinitionAccess.create(data, texture);
            })
    );
}
