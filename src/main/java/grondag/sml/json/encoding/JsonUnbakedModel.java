package grondag.sml.json.encoding;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import grondag.sml.json.model.BasicBakedModel;
import grondag.sml.json.model.ModelLoaderExt;
import grondag.sml.json.model.OverrideListHelper;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class JsonUnbakedModel implements UnbakedModel {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
   @VisibleForTesting
   static final Gson GSON = (new GsonBuilder())
       .registerTypeAdapter(JsonUnbakedModel.class, new JsonUnbakedModel.Deserializer())
       .registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
       .registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
       .registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
       .registerTypeAdapter(Transformation.class, new TransformationDeserializer())
       .registerTypeAdapter(ModelTransformation.class, new ModelTransformationDeserializer())
       .registerTypeAdapter(ModelItemOverride.class, new ModelItemOverrideDeserializer()).create();
   private final List<ModelElement> elements;
   private final boolean depthInGui;
   private final boolean ambientOcclusion;
   private final ModelTransformation transformations;
   private final List<ModelItemOverride> overrides;
   public String id = "";
   @VisibleForTesting
   protected final Map<String, String> textureMap;
   @Nullable
   protected JsonUnbakedModel parent;
   @Nullable
   protected Identifier parentId;

   public static JsonUnbakedModel deserialize(Reader reader_1) {
      return (JsonUnbakedModel)JsonHelper.deserialize(GSON, reader_1, JsonUnbakedModel.class);
   }

   public static JsonUnbakedModel deserialize(String string_1) {
      return deserialize((Reader)(new StringReader(string_1)));
   }

   public JsonUnbakedModel(@Nullable Identifier identifier_1, List<ModelElement> list_1, Map<String, String> map_1, boolean boolean_1, boolean boolean_2, ModelTransformation modelTransformation_1, List<ModelItemOverride> list_2) {
      this.elements = list_1;
      this.ambientOcclusion = boolean_1;
      this.depthInGui = boolean_2;
      this.textureMap = map_1;
      this.parentId = identifier_1;
      this.transformations = modelTransformation_1;
      this.overrides = list_2;
   }

   public List<ModelElement> getElements() {
      return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
   }

   public boolean useAmbientOcclusion() {
      return this.parent != null ? this.parent.useAmbientOcclusion() : this.ambientOcclusion;
   }

   public boolean hasDepthInGui() {
      return this.depthInGui;
   }

   public List<ModelItemOverride> getOverrides() {
      return this.overrides;
   }

   private ModelItemPropertyOverrideList compileOverrides(ModelLoader modelLoader_1, JsonUnbakedModel jsonUnbakedModel_1) {
      return this.overrides.isEmpty() ? ModelItemPropertyOverrideList.EMPTY : OverrideListHelper.make(modelLoader_1, jsonUnbakedModel_1, modelLoader_1::getOrLoadModel, this.overrides);
   }

   @Override
public Collection<Identifier> getModelDependencies() {
      Set<Identifier> set_1 = Sets.newHashSet();
      Iterator<ModelItemOverride> var2 = this.overrides.iterator();

      while(var2.hasNext()) {
         ModelItemOverride modelItemOverride_1 = (ModelItemOverride)var2.next();
         set_1.add(modelItemOverride_1.getModelId());
      }

      if (this.parentId != null) {
         set_1.add(this.parentId);
      }

      return set_1;
   }

   @Override
public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> function_1, Set<String> set_1) {
      Set<UnbakedModel> set_2 = Sets.newLinkedHashSet();

      for(JsonUnbakedModel jsonUnbakedModel_1 = this; jsonUnbakedModel_1.parentId != null && jsonUnbakedModel_1.parent == null; jsonUnbakedModel_1 = jsonUnbakedModel_1.parent) {
         set_2.add(jsonUnbakedModel_1);
         UnbakedModel unbakedModel_1 = (UnbakedModel)function_1.apply(jsonUnbakedModel_1.parentId);
         if (unbakedModel_1 == null) {
            LOGGER.warn("No parent '{}' while loading model '{}'", this.parentId, jsonUnbakedModel_1);
         }

         if (set_2.contains(unbakedModel_1)) {
            LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", jsonUnbakedModel_1, set_2.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentId);
            unbakedModel_1 = null;
         }

         if (unbakedModel_1 == null) {
            jsonUnbakedModel_1.parentId = ModelLoader.MISSING;
            unbakedModel_1 = (UnbakedModel)function_1.apply(jsonUnbakedModel_1.parentId);
         }

         if (!(unbakedModel_1 instanceof JsonUnbakedModel)) {
            throw new IllegalStateException("BlockModel parent has to be a block model.");
         }

         jsonUnbakedModel_1.parent = (JsonUnbakedModel)unbakedModel_1;
      }

      Set<Identifier> set_3 = Sets.newHashSet(new Identifier[]{new Identifier(this.resolveTexture("particle"))});
      Iterator<ModelElement> var6 = this.getElements().iterator();

      while(var6.hasNext()) {
         ModelElement modelElement_1 = (ModelElement)var6.next();

         String string_1;
         for(Iterator<ModelElementFace> var8 = modelElement_1.faces.values().iterator(); var8.hasNext(); set_3.add(new Identifier(string_1))) {
            ModelElementFace modelElementFace_1 = (ModelElementFace)var8.next();
            string_1 = this.resolveTexture(modelElementFace_1.textureId);
            if (Objects.equals(string_1, MissingSprite.getMissingSpriteId().toString())) {
               set_1.add(String.format("%s in %s", modelElementFace_1.textureId, this.id));
            }
         }
      }

      this.overrides.forEach((modelItemOverride_1) -> {
         UnbakedModel unbakedModel_1 = (UnbakedModel)function_1.apply(modelItemOverride_1.getModelId());
         if (!Objects.equals(unbakedModel_1, this)) {
            set_3.addAll(unbakedModel_1.getTextureDependencies(function_1, set_1));
         }
      });
      if (this.getRootModel() == ModelLoaderExt.GENERATION_MARKER) {
         ItemModelGenerator.LAYERS.forEach((string_1x) -> {
            set_3.add(new Identifier(this.resolveTexture(string_1x)));
         });
      }

      return set_3;
   }

   @Override
public BakedModel bake(ModelLoader modelLoader_1, Function<Identifier, Sprite> function_1, ModelBakeSettings modelBakeSettings_1) {
      return this.bake(modelLoader_1, this, function_1, modelBakeSettings_1);
   }

   public BakedModel bake(ModelLoader modelLoader, JsonUnbakedModel unbakedModel, Function<Identifier, Sprite> spriteFunc, ModelBakeSettings bakeProps) {
      Sprite particleSprite = (Sprite)spriteFunc.apply(new Identifier(this.resolveTexture("particle")));
      if (this.getRootModel() == ModelLoaderExt.BLOCK_ENTITY_MARKER) {
         return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(modelLoader, unbakedModel), particleSprite);
      } else {
         BasicBakedModel.Builder builder = (new BasicBakedModel.Builder(this, this.compileOverrides(modelLoader, unbakedModel))).setParticle(particleSprite);
         Iterator<ModelElement> elements = this.getElements().iterator();

         while(elements.hasNext()) {
            ModelElement element = elements.next();
            Iterator<Direction> faces = element.faces.keySet().iterator();

            while(faces.hasNext()) {
               Direction face = faces.next();
               ModelElementFace elementFace = element.faces.get(face);
               Sprite sprite = spriteFunc.apply(new Identifier(this.resolveTexture(elementFace.textureId)));
               if (elementFace.cullFace == null) {
                  builder.addQuad(null, element, elementFace, sprite, face, bakeProps);
               } else {
                  builder.addQuad(bakeProps.getRotation().apply(elementFace.cullFace), element, elementFace, sprite, face, bakeProps);
               }
            }
         }

         return builder.build();
      }
   }

//   private static BakedQuad createQuad(ModelElement modelElement_1, ModelElementFace modelElementFace_1, Sprite sprite_1, Direction direction_1, ModelBakeSettings modelBakeSettings_1) {
//      return QUAD_FACTORY.bake(modelElement_1.from, modelElement_1.to, modelElementFace_1, sprite_1, direction_1, modelBakeSettings_1, modelElement_1.rotation, modelElement_1.shade);
//   }

   public boolean textureExists(String string_1) {
      return !MissingSprite.getMissingSpriteId().toString().equals(this.resolveTexture(string_1));
   }

   public String resolveTexture(String string_1) {
      if (!this.isTextureReference(string_1)) {
         string_1 = '#' + string_1;
      }

      return this.resolveTexture(string_1, new JsonUnbakedModel.TextureResolutionContext(this));
   }

   private String resolveTexture(String string_1, JsonUnbakedModel.TextureResolutionContext jsonUnbakedModel$TextureResolutionContext_1) {
      if (this.isTextureReference(string_1)) {
         if (this == jsonUnbakedModel$TextureResolutionContext_1.current) {
            LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", string_1, this.id);
            return MissingSprite.getMissingSpriteId().toString();
         } else {
            String string_2 = (String)this.textureMap.get(string_1.substring(1));
            if (string_2 == null && this.parent != null) {
               string_2 = this.parent.resolveTexture(string_1, jsonUnbakedModel$TextureResolutionContext_1);
            }

            jsonUnbakedModel$TextureResolutionContext_1.current = this;
            if (string_2 != null && this.isTextureReference(string_2)) {
               string_2 = jsonUnbakedModel$TextureResolutionContext_1.root.resolveTexture(string_2, jsonUnbakedModel$TextureResolutionContext_1);
            }

            return string_2 != null && !this.isTextureReference(string_2) ? string_2 : MissingSprite.getMissingSpriteId().toString();
         }
      } else {
         return string_1;
      }
   }

   private boolean isTextureReference(String string_1) {
      return string_1.charAt(0) == '#';
   }

   public JsonUnbakedModel getRootModel() {
      return this.parent == null ? this : this.parent.getRootModel();
   }

   public ModelTransformation getTransformations() {
      Transformation transformation_1 = this.getTransformation(ModelTransformation.Type.THIRD_PERSON_LEFT_HAND);
      Transformation transformation_2 = this.getTransformation(ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND);
      Transformation transformation_3 = this.getTransformation(ModelTransformation.Type.FIRST_PERSON_LEFT_HAND);
      Transformation transformation_4 = this.getTransformation(ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND);
      Transformation transformation_5 = this.getTransformation(ModelTransformation.Type.HEAD);
      Transformation transformation_6 = this.getTransformation(ModelTransformation.Type.GUI);
      Transformation transformation_7 = this.getTransformation(ModelTransformation.Type.GROUND);
      Transformation transformation_8 = this.getTransformation(ModelTransformation.Type.FIXED);
      return new ModelTransformation(transformation_1, transformation_2, transformation_3, transformation_4, transformation_5, transformation_6, transformation_7, transformation_8);
   }

   private Transformation getTransformation(ModelTransformation.Type modelTransformation$Type_1) {
      return this.parent != null && !this.transformations.isTransformationDefined(modelTransformation$Type_1) ? this.parent.getTransformation(modelTransformation$Type_1) : this.transformations.getTransformation(modelTransformation$Type_1);
   }

   @Override
public String toString() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public static class Deserializer implements JsonDeserializer<JsonUnbakedModel> {
      @Override
    public JsonUnbakedModel deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
         JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
         List<ModelElement> list_1 = this.deserializeElements(jsonDeserializationContext_1, jsonObject_1);
         String string_1 = this.deserializeParent(jsonObject_1);
         Map<String, String> map_1 = this.deserializeTextures(jsonObject_1);
         boolean boolean_1 = this.deserializeAmbientOcclusion(jsonObject_1);
         ModelTransformation modelTransformation_1 = ModelTransformation.NONE;
         if (jsonObject_1.has("display")) {
            JsonObject jsonObject_2 = JsonHelper.getObject(jsonObject_1, "display");
            modelTransformation_1 = (ModelTransformation)jsonDeserializationContext_1.deserialize(jsonObject_2, ModelTransformation.class);
         }

         List<ModelItemOverride> list_2 = this.deserializeOverrides(jsonDeserializationContext_1, jsonObject_1);
         Identifier identifier_1 = string_1.isEmpty() ? null : new Identifier(string_1);
         return new JsonUnbakedModel(identifier_1, list_1, map_1, boolean_1, true, modelTransformation_1, list_2);
      }

      protected List<ModelItemOverride> deserializeOverrides(JsonDeserializationContext jsonDeserializationContext_1, JsonObject jsonObject_1) {
         List<ModelItemOverride> list_1 = Lists.newArrayList();
         if (jsonObject_1.has("overrides")) {
            JsonArray jsonArray_1 = JsonHelper.getArray(jsonObject_1, "overrides");
            Iterator<JsonElement> var5 = jsonArray_1.iterator();

            while(var5.hasNext()) {
               JsonElement jsonElement_1 = (JsonElement)var5.next();
               list_1.add(jsonDeserializationContext_1.deserialize(jsonElement_1, ModelItemOverride.class));
            }
         }

         return list_1;
      }

      private Map<String, String> deserializeTextures(JsonObject jsonObject_1) {
         Map<String, String> map_1 = Maps.newHashMap();
         if (jsonObject_1.has("textures")) {
            JsonObject jsonObject_2 = jsonObject_1.getAsJsonObject("textures");
            Iterator<Entry<String, JsonElement>> var4 = jsonObject_2.entrySet().iterator();

            while(var4.hasNext()) {
               Entry<String, JsonElement> map$Entry_1 = var4.next();
               map_1.put(map$Entry_1.getKey(), ((JsonElement)map$Entry_1.getValue()).getAsString());
            }
         }

         return map_1;
      }

      private String deserializeParent(JsonObject jsonObject_1) {
         return JsonHelper.getString(jsonObject_1, "parent", "");
      }

      protected boolean deserializeAmbientOcclusion(JsonObject jsonObject_1) {
         return JsonHelper.getBoolean(jsonObject_1, "ambientocclusion", true);
      }

      protected List<ModelElement> deserializeElements(JsonDeserializationContext jsonDeserializationContext_1, JsonObject jsonObject_1) {
         List<ModelElement> list_1 = Lists.newArrayList();
         if (jsonObject_1.has("elements")) {
            Iterator<JsonElement> var4 = JsonHelper.getArray(jsonObject_1, "elements").iterator();

            while(var4.hasNext()) {
               JsonElement jsonElement_1 = (JsonElement)var4.next();
               list_1.add(jsonDeserializationContext_1.deserialize(jsonElement_1, ModelElement.class));
            }
         }

         return list_1;
      }
   }

   @Environment(EnvType.CLIENT)
   static final class TextureResolutionContext {
      public final JsonUnbakedModel root;
      public JsonUnbakedModel current;

      private TextureResolutionContext(JsonUnbakedModel jsonUnbakedModel_1) {
         this.root = jsonUnbakedModel_1;
      }

      // $FF: synthetic method
      TextureResolutionContext(JsonUnbakedModel jsonUnbakedModel_1, Object jsonUnbakedModel$1_1) {
         this(jsonUnbakedModel_1);
      }
   }
}

