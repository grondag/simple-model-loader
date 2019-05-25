package grondag.sml.json.encoding;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class MultipartModelComponent {
   private final MultipartModelSelector selector;
   private final WeightedUnbakedModel model;

   public MultipartModelComponent(MultipartModelSelector multipartModelSelector_1, WeightedUnbakedModel weightedUnbakedModel_1) {
      if (multipartModelSelector_1 == null) {
         throw new IllegalArgumentException("Missing condition for selector");
      } else if (weightedUnbakedModel_1 == null) {
         throw new IllegalArgumentException("Missing variant for selector");
      } else {
         this.selector = multipartModelSelector_1;
         this.model = weightedUnbakedModel_1;
      }
   }

   public WeightedUnbakedModel getModel() {
      return this.model;
   }

   public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory_1) {
      return this.selector.getPredicate(stateFactory_1);
   }

   @Override
public boolean equals(Object object_1) {
      return this == object_1;
   }

   @Override
public int hashCode() {
      return System.identityHashCode(this);
   }

   @Environment(EnvType.CLIENT)
   public static class Deserializer implements JsonDeserializer<MultipartModelComponent> {
      @Override
    public MultipartModelComponent deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
         JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
         return new MultipartModelComponent(this.deserializeSelectorOrDefault(jsonObject_1), (WeightedUnbakedModel)jsonDeserializationContext_1.deserialize(jsonObject_1.get("apply"), WeightedUnbakedModel.class));
      }

      private MultipartModelSelector deserializeSelectorOrDefault(JsonObject jsonObject_1) {
         return jsonObject_1.has("when") ? deserializeSelector(JsonHelper.getObject(jsonObject_1, "when")) : MultipartModelSelector.TRUE;
      }

      @VisibleForTesting
      static MultipartModelSelector deserializeSelector(JsonObject jsonObject_1) {
         Set<Entry<String, JsonElement>> set_1 = jsonObject_1.entrySet();
         if (set_1.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if (set_1.size() == 1) {
            List<MultipartModelSelector> list_2;
            if (jsonObject_1.has("OR")) {
               list_2 = Streams.stream(JsonHelper.getArray(jsonObject_1, "OR")).map((jsonElement_1) -> {
                  return deserializeSelector(jsonElement_1.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrMultipartModelSelector(list_2);
            } else if (jsonObject_1.has("AND")) {
               list_2 = Streams.stream(JsonHelper.getArray(jsonObject_1, "AND")).map((jsonElement_1) -> {
                  return deserializeSelector(jsonElement_1.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndMultipartModelSelector(list_2);
            } else {
               return createStatePropertySelector(set_1.iterator().next());
            }
         } else {
            return new AndMultipartModelSelector(set_1.stream().map(MultipartModelComponent.Deserializer::createStatePropertySelector).collect(Collectors.toList()));
         }
      }

      private static MultipartModelSelector createStatePropertySelector(Entry<String, JsonElement> map$Entry_1) {
         return new SimpleMultipartModelSelector((String)map$Entry_1.getKey(), ((JsonElement)map$Entry_1.getValue()).getAsString());
      }
   }
}

