//package grondag.sml.json.encoding;
//
//import com.google.common.collect.Lists;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonDeserializer;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParseException;
//
//import grondag.sml.model.WeightedBakedModel;
//
//import java.lang.reflect.Type;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import javax.annotation.Nullable;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.ModelBakeSettings;
//import net.minecraft.client.render.model.ModelLoader;
//import net.minecraft.client.render.model.UnbakedModel;
//import net.minecraft.client.texture.Sprite;
//import net.minecraft.util.Identifier;
//
//@Environment(EnvType.CLIENT)
//public class WeightedUnbakedModel implements UnbakedModel {
//    private final List<ModelVariant> variants;
//
//    public WeightedUnbakedModel(List<ModelVariant> list_1) {
//        this.variants = list_1;
//    }
//
//    public List<ModelVariant> getVariants() {
//        return this.variants;
//    }
//
//    @Override
//    public boolean equals(Object object_1) {
//        if (this == object_1) {
//            return true;
//        } else if (object_1 instanceof WeightedUnbakedModel) {
//            WeightedUnbakedModel weightedUnbakedModel_1 = (WeightedUnbakedModel)object_1;
//            return this.variants.equals(weightedUnbakedModel_1.variants);
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public int hashCode() {
//        return this.variants.hashCode();
//    }
//
//    @Override
//    public Collection<Identifier> getModelDependencies() {
//        return (Collection<Identifier>)this.getVariants().stream().map(ModelVariant::getLocation).collect(Collectors.toSet());
//    }
//
//    @Override
//    public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> function_1, Set<String> set_1) {
//        return (Collection<Identifier>)this.getVariants().stream().map(ModelVariant::getLocation).distinct().flatMap((identifier_1) -> {
//            return ((UnbakedModel)function_1.apply(identifier_1)).getTextureDependencies(function_1, set_1).stream();
//        }).collect(Collectors.toSet());
//    }
//
//    @Override
//    @Nullable
//    public BakedModel bake(ModelLoader modelLoader_1, Function<Identifier, Sprite> function_1, ModelBakeSettings modelBakeSettings_1) {
//        if (this.getVariants().isEmpty()) {
//            return null;
//        } else {
//            WeightedBakedModel.Builder weightedBakedModel$Builder_1 = new WeightedBakedModel.Builder();
//            Iterator<ModelVariant> var5 = this.getVariants().iterator();
//
//            while(var5.hasNext()) {
//                ModelVariant modelVariant_1 = (ModelVariant)var5.next();
//                BakedModel bakedModel_1 = modelLoader_1.bake(modelVariant_1.getLocation(), modelVariant_1);
//                weightedBakedModel$Builder_1.add(bakedModel_1, modelVariant_1.getWeight());
//            }
//
//            return weightedBakedModel$Builder_1.getFirst();
//        }
//    }
//
//    @Environment(EnvType.CLIENT)
//    public static class Deserializer implements JsonDeserializer<WeightedUnbakedModel> {
//        @Override
//        public WeightedUnbakedModel deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
//            List<ModelVariant> list_1 = Lists.newArrayList();
//            if (jsonElement_1.isJsonArray()) {
//                JsonArray jsonArray_1 = jsonElement_1.getAsJsonArray();
//                if (jsonArray_1.size() == 0) {
//                    throw new JsonParseException("Empty variant array");
//                }
//
//                Iterator<JsonElement> var6 = jsonArray_1.iterator();
//
//                while(var6.hasNext()) {
//                    JsonElement jsonElement_2 = (JsonElement)var6.next();
//                    list_1.add(jsonDeserializationContext_1.deserialize(jsonElement_2, ModelVariant.class));
//                }
//            } else {
//                list_1.add(jsonDeserializationContext_1.deserialize(jsonElement_1, ModelVariant.class));
//            }
//
//            return new WeightedUnbakedModel(list_1);
//        }
//
//    }
//}
