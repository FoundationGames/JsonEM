package io.github.foundationgames.jsonem.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.foundationgames.jsonem.util.JsonEntityModelUtil;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
    @SuppressWarnings("unchecked")
    @WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", ordinal = 0))
    private <U> CompletableFuture<U> jsonem$loadJsonEntityModels(Supplier<U> supplier, Executor executor, Operation<CompletableFuture<U>> original, PreparableReloadListener.SharedState currentReload) {
        var resourceManager = currentReload.resourceManager();
        Supplier<U> wrappedSupplier = () -> {
            U obj = supplier.get();

            if (obj instanceof EntityModelSet entityModels) {
                var modelParts = new HashMap<>(((EntityModelSetAccess) entityModels).jsonem$getModels());
                JsonEntityModelUtil.loadModels(resourceManager, modelParts);
                return (U) new EntityModelSet(ImmutableMap.copyOf(modelParts));
            } else {
                return obj;
            }
        };

        return original.call(wrappedSupplier, executor);
    }
}
