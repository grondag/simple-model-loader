package grondag.sml.json.encoding;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import grondag.sml.json.model.MultipartUnbakedModel;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelVariantMap {
   private final Map<String, WeightedUnbakedModel> variantMap = Maps.newLinkedHashMap();
   private MultipartUnbakedModel multipartModel;

   public static ModelVariantMap deserialize(ModelVariantMap.DeserializationContext modelVariantMap$DeserializationContext_1, Reader reader_1) {
      return (ModelVariantMap)JsonHelper.deserialize(modelVariantMap$DeserializationContext_1.gson, reader_1, ModelVariantMap.class);
   }

   public ModelVariantMap(Map<String, WeightedUnbakedModel> map_1, MultipartUnbakedModel multipartUnbakedModel_1) {
      this.multipartModel = multipartUnbakedModel_1;
      this.variantMap.putAll(map_1);
   }

   public ModelVariantMap(List<ModelVariantMap> list_1) {
      ModelVariantMap modelVariantMap_1 = null;

      ModelVariantMap modelVariantMap_2;
      for(Iterator<ModelVariantMap> var3 = list_1.iterator(); var3.hasNext(); this.variantMap.putAll(modelVariantMap_2.variantMap)) {
         modelVariantMap_2 = (ModelVariantMap)var3.next();
         if (modelVariantMap_2.hasMultipartModel()) {
            this.variantMap.clear();
            modelVariantMap_1 = modelVariantMap_2;
         }
      }

      if (modelVariantMap_1 != null) {
         this.multipartModel = modelVariantMap_1.multipartModel;
      }

   }

   @Override
public boolean equals(Object object_1) {
      if (this == object_1) {
         return true;
      } else {
         if (object_1 instanceof ModelVariantMap) {
            ModelVariantMap modelVariantMap_1 = (ModelVariantMap)object_1;
            if (this.variantMap.equals(modelVariantMap_1.variantMap)) {
               return this.hasMultipartModel() ? this.multipartModel.equals(modelVariantMap_1.multipartModel) : !modelVariantMap_1.hasMultipartModel();
            }
         }

         return false;
      }
   }

   @Override
public int hashCode() {
      return 31 * this.variantMap.hashCode() + (this.hasMultipartModel() ? this.multipartModel.hashCode() : 0);
   }

   public Map<String, WeightedUnbakedModel> getVariantMap() {
      return this.variantMap;
   }

   public boolean hasMultipartModel() {
      return this.multipartModel != null;
   }

   public MultipartUnbakedModel getMultipartModel() {
      return this.multipartModel;
   }

   @Environment(EnvType.CLIENT)
   public static class Deserializer implements JsonDeserializer<ModelVariantMap> {
      @Override
    public ModelVariantMap deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
         JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
         Map<String, WeightedUnbakedModel> map_1 = this.deserializeVariants(jsonDeserializationContext_1, jsonObject_1);
         MultipartUnbakedModel multipartUnbakedModel_1 = this.deserializeMultipart(jsonDeserializationContext_1, jsonObject_1);
         if (!map_1.isEmpty() || multipartUnbakedModel_1 != null && !multipartUnbakedModel_1.getModels().isEmpty()) {
            return new ModelVariantMap(map_1, multipartUnbakedModel_1);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, WeightedUnbakedModel> deserializeVariants(JsonDeserializationContext jsonDeserializationContext_1, JsonObject jsonObject_1) {
         Map<String, WeightedUnbakedModel> map_1 = Maps.newHashMap();
         if (jsonObject_1.has("variants")) {
            JsonObject jsonObject_2 = JsonHelper.getObject(jsonObject_1, "variants");
            Iterator<Entry<String, JsonElement>> var5 = jsonObject_2.entrySet().iterator();

            while(var5.hasNext()) {
               Entry<String, JsonElement> map$Entry_1 = var5.next();
               map_1.put(map$Entry_1.getKey(), jsonDeserializationContext_1.deserialize((JsonElement)map$Entry_1.getValue(), WeightedUnbakedModel.class));
            }
         }

         return map_1;
      }

      @Nullable
      protected MultipartUnbakedModel deserializeMultipart(JsonDeserializationContext jsonDeserializationContext_1, JsonObject jsonObject_1) {
         if (!jsonObject_1.has("multipart")) {
            return null;
         } else {
            JsonArray jsonArray_1 = JsonHelper.getArray(jsonObject_1, "multipart");
            return (MultipartUnbakedModel)jsonDeserializationContext_1.deserialize(jsonArray_1, MultipartUnbakedModel.class);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static final class DeserializationContext {
      protected final Gson gson = (new GsonBuilder()).registerTypeAdapter(ModelVariantMap.class, new ModelVariantMap.Deserializer()).registerTypeAdapter(ModelVariant.class, new ModelVariant.Deserializer()).registerTypeAdapter(WeightedUnbakedModel.class, new WeightedUnbakedModel.Deserializer()).registerTypeAdapter(MultipartUnbakedModel.class, new MultipartUnbakedModel.Deserializer(this)).registerTypeAdapter(MultipartModelComponent.class, new MultipartModelComponent.Deserializer()).create();
      private StateFactory<Block, BlockState> stateFactory;

      public StateFactory<Block, BlockState> getStateFactory() {
         return this.stateFactory;
      }

      public void setStateFactory(StateFactory<Block, BlockState> stateFactory_1) {
         this.stateFactory = stateFactory_1;
      }
   }
}
