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
import net.minecraft.util.math.Vec3f;

import java.util.HashMap;
import java.util.Optional;

public class JsonEMCodecs {
    public static final Codec<TextureDimensions> TEXTURE_DIMENSIONS = RecordCodecBuilder.create((instance) ->
        instance.group(
                Codec.INT.fieldOf("width").forGetter(obj -> ((TextureDimensionsAccess) obj).jsonem$width()),
                Codec.INT.fieldOf("height").forGetter(obj -> ((TextureDimensionsAccess) obj).jsonem$height())
        ).apply(instance, TextureDimensions::new)
    );

    public static final Codec<ModelTransform> MODEL_TRANSFORM = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Vec3f.CODEC.optionalFieldOf("origin", Vec3f.ZERO).forGetter(obj -> new Vec3f(obj.pivotX, obj.pivotY, obj.pivotZ)),
                    Vec3f.CODEC.optionalFieldOf("rotation", Vec3f.ZERO).forGetter(obj -> new Vec3f(obj.pitch, obj.yaw, obj.roll))
            ).apply(instance, (origin, rot) -> ModelTransform.of(origin.getX(), origin.getY(), origin.getZ(), rot.getX(), rot.getY(), rot.getZ()))
    );

    public static final Codec<Dilation> DILATION = Vec3f.CODEC.xmap(
            vec -> new Dilation(vec.getX(), vec.getY(), vec.getZ()),
            dil -> new Vec3f(
                    ((DilationAccess) dil).jsonem$radiusX(),
                    ((DilationAccess) dil).jsonem$radiusY(),
                    ((DilationAccess) dil).jsonem$radiusZ())
    );

    public static final Codec<Vector2f> VECTOR2F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
            Util.toArray(vec, 2).map((arr) -> new Vector2fComparable(arr.get(0), arr.get(1))),
            (vec) -> ImmutableList.of(vec.getX(), vec.getY())
    );

    private static ModelCuboidData createCuboidData(Optional<String> name, Vec3f offset, Vec3f dimensions, Dilation dilation, boolean mirror, Vector2f uv, Vector2f uvSize) {
        return ModelCuboidDataAccess.jsonem$create(name.orElse(null), uv.getX(), uv.getY(), offset.getX(), offset.getY(), offset.getZ(), dimensions.getX(), dimensions.getY(), dimensions.getZ(), dilation, mirror, uvSize.getX(), uvSize.getY());
    }

    private static final Vector2f DEFAULT_UV_SCALE = new Vector2fComparable(1.0f, 1.0f);

    public static final Codec<ModelCuboidData> MODEL_CUBOID_DATA = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.STRING.optionalFieldOf("name").forGetter(obj -> Optional.ofNullable(((ModelCuboidDataAccess) (Object) obj).jsonem$name())),
                    Vec3f.CODEC.fieldOf("offset").forGetter(obj -> ((ModelCuboidDataAccess)(Object)obj).jsonem$offset()),
                    Vec3f.CODEC.fieldOf("dimensions").forGetter(obj -> ((ModelCuboidDataAccess)(Object)obj).jsonem$dimensions()),
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
