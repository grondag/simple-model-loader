package grondag.sml.json.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import grondag.sml.json.encoding.ModelVariantMap;
import grondag.sml.json.encoding.ModelVariantMap.DeserializationContext;
import grondag.sml.json.encoding.MultipartModelComponent;
import grondag.sml.json.encoding.WeightedUnbakedModel;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MultipartUnbakedModel implements UnbakedModel {
    private final StateFactory<Block, BlockState> stateFactory;
    private final List<MultipartModelComponent> components;

    public MultipartUnbakedModel(StateFactory<Block, BlockState> stateFactory_1, List<MultipartModelComponent> list_1) {
        this.stateFactory = stateFactory_1;
        this.components = list_1;
    }

    public List<MultipartModelComponent> getComponents() {
        return this.components;
    }

    public Set<WeightedUnbakedModel> getModels() {
        Set<WeightedUnbakedModel> set_1 = Sets.newHashSet();
        Iterator<MultipartModelComponent> var2 = this.components.iterator();

        while(var2.hasNext()) {
            MultipartModelComponent multipartModelComponent_1 = (MultipartModelComponent)var2.next();
            set_1.add(multipartModelComponent_1.getModel());
        }

        return set_1;
    }

    @Override
    public boolean equals(Object object_1) {
        if (this == object_1) {
            return true;
        } else if (!(object_1 instanceof MultipartUnbakedModel)) {
            return false;
        } else {
            MultipartUnbakedModel multipartUnbakedModel_1 = (MultipartUnbakedModel)object_1;
            return Objects.equals(this.stateFactory, multipartUnbakedModel_1.stateFactory) && Objects.equals(this.components, multipartUnbakedModel_1.components);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{this.stateFactory, this.components});
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return getComponents().stream().flatMap((multipartModelComponent_1) -> {
            return multipartModelComponent_1.getModel().getModelDependencies().stream();
        }).collect(Collectors.toSet());
    }

    @Override
    public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> function_1, Set<String> set_1) {
        return getComponents().stream().flatMap((multipartModelComponent_1) -> {
            return multipartModelComponent_1.getModel().getTextureDependencies(function_1, set_1).stream();
        }).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public BakedModel bake(ModelLoader modelLoader_1, Function<Identifier, Sprite> function_1, ModelBakeSettings modelBakeSettings_1) {
        MultipartBakedModel.Builder multipartBakedModel$Builder_1 = new MultipartBakedModel.Builder();
        Iterator<MultipartModelComponent> var5 = this.getComponents().iterator();

        while(var5.hasNext()) {
            MultipartModelComponent multipartModelComponent_1 = (MultipartModelComponent)var5.next();
            BakedModel bakedModel_1 = multipartModelComponent_1.getModel().bake(modelLoader_1, function_1, modelBakeSettings_1);
            if (bakedModel_1 != null) {
                multipartBakedModel$Builder_1.addComponent(multipartModelComponent_1.getPredicate(this.stateFactory), bakedModel_1);
            }
        }

        return multipartBakedModel$Builder_1.build();
    }

    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<MultipartUnbakedModel> {
        private final ModelVariantMap.DeserializationContext context;

        public Deserializer(DeserializationContext deserializationContext) {
            this.context = deserializationContext;
        }

        @Override
        public MultipartUnbakedModel deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
            return new MultipartUnbakedModel(this.context.getStateFactory(), this.deserializeComponents(jsonDeserializationContext_1, jsonElement_1.getAsJsonArray()));
        }

        private List<MultipartModelComponent> deserializeComponents(JsonDeserializationContext jsonDeserializationContext_1, JsonArray jsonArray_1) {
            List<MultipartModelComponent> list_1 = Lists.newArrayList();
            Iterator<JsonElement> var4 = jsonArray_1.iterator();

            while(var4.hasNext()) {
                JsonElement jsonElement_1 = (JsonElement)var4.next();
                list_1.add(jsonDeserializationContext_1.deserialize(jsonElement_1, MultipartModelComponent.class));
            }

            return list_1;
        }
    }
}
