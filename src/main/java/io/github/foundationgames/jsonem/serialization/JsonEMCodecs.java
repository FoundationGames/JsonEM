package io.github.foundationgames.jsonem.serialization;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.foundationgames.jsonem.mixin.DilationAccess;
import io.github.foundationgames.jsonem.mixin.ModelCuboidDataAccess;
import io.github.foundationgames.jsonem.mixin.ModelPartDataAccess;
import io.github.foundationgames.jsonem.mixin.TextureDimensionsAccess;
import io.github.foundationgames.jsonem.mixin.TexturedModelDataAccess;
import io.github.foundationgames.jsonem.util.Vector2fComparable;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TextureDimensions;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class JsonEMCodecs {
    private static final Set<Direction> ALL_DIRECTIONS = EnumSet.allOf(Direction.class);

    public static final Codec<Vector2f> VECTOR2F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
            Util.decodeFixedLengthList(vec, 2).map((arr) -> new Vector2fComparable(arr.get(0), arr.get(1))),
            (vec) -> ImmutableList.of(vec.getX(), vec.getY())
    );

    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
            Util.decodeFixedLengthList(vec, 3).map(coords -> new Vector3f(coords.get(0), coords.get(1), coords.get(2))),
            (vec) -> ImmutableList.of(vec.x, vec.y, vec.z)
    );

    public static final Codec<TextureDimensions> TEXTURE_DIMENSIONS = RecordCodecBuilder.create((instance) ->
        instance.group(
                Codec.INT.fieldOf("width").forGetter(obj -> ((TextureDimensionsAccess) obj).jsonem$width()),
                Codec.INT.fieldOf("height").forGetter(obj -> ((TextureDimensionsAccess) obj).jsonem$height())
        ).apply(instance, TextureDimensions::new)
    );

    public static final Codec<ModelTransform> MODEL_TRANSFORM = RecordCodecBuilder.create((instance) ->
            instance.group(
                    VECTOR3F.optionalFieldOf("origin", new Vector3f()).forGetter(obj -> new Vector3f(obj.pivotX, obj.pivotY, obj.pivotZ)),
                    VECTOR3F.optionalFieldOf("rotation", new Vector3f()).forGetter(obj -> new Vector3f(obj.pitch, obj.yaw, obj.roll))
            ).apply(instance, (origin, rot) -> ModelTransform.of(origin.x, origin.y, origin.z, rot.x, rot.y, rot.z))
    );

    public static final Codec<Dilation> DILATION = VECTOR3F.xmap(
            vec -> new Dilation(vec.x, vec.y, vec.z),
            dil -> new Vector3f(
                    ((DilationAccess) dil).jsonem$radiusX(),
                    ((DilationAccess) dil).jsonem$radiusY(),
                    ((DilationAccess) dil).jsonem$radiusZ())
    );

    private static ModelCuboidData createCuboidData(Optional<String> name, Vector3f offset, Vector3f dimensions, Dilation dilation, boolean mirror, Vector2f uv, Vector2f uvSize) {
        return ModelCuboidDataAccess.jsonem$create(name.orElse(null), uv.getX(), uv.getY(), offset.x, offset.y, offset.z, dimensions.x, dimensions.y, dimensions.z, dilation, mirror, uvSize.getX(), uvSize.getY(), ALL_DIRECTIONS);
    }

    private static final Vector2f DEFAULT_UV_SCALE = new Vector2fComparable(1.0f, 1.0f);

    public static final Codec<ModelCuboidData> MODEL_CUBOID_DATA = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.STRING.optionalFieldOf("name").forGetter(obj -> Optional.ofNullable(((ModelCuboidDataAccess) (Object) obj).jsonem$name())),
                    VECTOR3F.fieldOf("offset").forGetter(obj -> ((ModelCuboidDataAccess)(Object)obj).jsonem$offset()),
                    VECTOR3F.fieldOf("dimensions").forGetter(obj -> ((ModelCuboidDataAccess)(Object)obj).jsonem$dimensions()),
                    DILATION.optionalFieldOf("dilation", Dilation.NONE).forGetter(obj -> ((ModelCuboidDataAccess)(Object)obj).jsonem$dilation()),
                    Codec.BOOL.optionalFieldOf("mirror", false).forGetter(obj -> ((ModelCuboidDataAccess)(Object)obj).jsonem$mirror()),
                    VECTOR2F.fieldOf("uv").forGetter(obj -> ((ModelCuboidDataAccess)(Object)obj).jsonem$uv()),
                    VECTOR2F.optionalFieldOf("uv_scale", DEFAULT_UV_SCALE).forGetter(obj -> Vector2fComparable.of(((ModelCuboidDataAccess)(Object)obj).jsonem$uvScale()))
            ).apply(instance, JsonEMCodecs::createCuboidData)
    );

    private static Codec<ModelPartData> createPartDataCodec() {
        return RecordCodecBuilder.create((instance) ->
                instance.group(
                        MODEL_TRANSFORM.optionalFieldOf("transform", ModelTransform.NONE).forGetter(obj -> ((ModelPartDataAccess) obj).jsonem$transform()),
                        Codec.list(MODEL_CUBOID_DATA).fieldOf("cuboids").forGetter(obj -> ((ModelPartDataAccess) obj).jsonem$cuboids()),
                        LazyTypeUnboundedMapCodec.of(Codec.STRING, JsonEMCodecs::createPartDataCodec).optionalFieldOf("children", new HashMap<>()).forGetter(obj -> ((ModelPartDataAccess) obj).jsonem$children())
                ).apply(instance, (transform, cuboids, children) -> {
                    var data = ModelPartDataAccess.create(cuboids, transform);
                    ((ModelPartDataAccess) data).jsonem$children().putAll(children);
                    return data;
                })
        );
    }

    public static final Codec<ModelPartData> MODEL_PART_DATA = createPartDataCodec();

    public static final Codec<TexturedModelData> TEXTURED_MODEL_DATA = RecordCodecBuilder.create((instance) ->
            instance.group(
                    TEXTURE_DIMENSIONS.fieldOf("texture").forGetter(obj -> ((TexturedModelDataAccess) obj).jsonem$texture()),
                    Codec.unboundedMap(Codec.STRING, MODEL_PART_DATA).fieldOf("bones").forGetter(obj -> ((ModelPartDataAccess) ((TexturedModelDataAccess) obj).jsonem$root().getRoot()).jsonem$children())
            ).apply(instance, (texture, bones) -> {
                var data = new ModelData();
                ((ModelPartDataAccess) data.getRoot()).jsonem$children().putAll(bones);
                return TexturedModelDataAccess.create(data, texture);
            })
    );
}
