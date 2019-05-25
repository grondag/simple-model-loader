package grondag.sml.json.encoding;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ModelElement {
   public final Vector3f from;
   public final Vector3f to;
   public final Map<Direction, ModelElementFace> faces;
   public final ModelRotation rotation;
   public final boolean shade;

   public ModelElement(Vector3f vector3f_1, Vector3f vector3f_2, Map<Direction, ModelElementFace> map_1, @Nullable ModelRotation modelRotation_1, boolean boolean_1) {
      this.from = vector3f_1;
      this.to = vector3f_2;
      this.faces = map_1;
      this.rotation = modelRotation_1;
      this.shade = boolean_1;
      this.method_3402();
   }

   private void method_3402() {
      Iterator<Entry<Direction, ModelElementFace>> var1 = this.faces.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<Direction, ModelElementFace> map$Entry_1 = var1.next();
         float[] floats_1 = this.method_3401(map$Entry_1.getKey());
         map$Entry_1.getValue().textureData.setUvs(floats_1);
      }

   }

   private float[] method_3401(Direction direction_1) {
      switch(direction_1) {
      case DOWN:
         return new float[]{this.from.x(), 16.0F - this.to.z(), this.to.x(), 16.0F - this.from.z()};
      case UP:
         return new float[]{this.from.x(), this.from.z(), this.to.x(), this.to.z()};
      case NORTH:
      default:
         return new float[]{16.0F - this.to.x(), 16.0F - this.to.y(), 16.0F - this.from.x(), 16.0F - this.from.y()};
      case SOUTH:
         return new float[]{this.from.x(), 16.0F - this.to.y(), this.to.x(), 16.0F - this.from.y()};
      case WEST:
         return new float[]{this.from.z(), 16.0F - this.to.y(), this.to.z(), 16.0F - this.from.y()};
      case EAST:
         return new float[]{16.0F - this.to.z(), 16.0F - this.to.y(), 16.0F - this.from.z(), 16.0F - this.from.y()};
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Deserializer implements JsonDeserializer<ModelElement> {
      protected Deserializer() {
      }

      @Override
    public ModelElement deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
         JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
         Vector3f vector3f_1 = this.deserializeFrom(jsonObject_1);
         Vector3f vector3f_2 = this.deserializeTo(jsonObject_1);
         ModelRotation modelRotation_1 = this.deserializeRotation(jsonObject_1);
         Map<Direction, ModelElementFace> map_1 = this.deserializeFacesValidating(jsonDeserializationContext_1, jsonObject_1);
         if (jsonObject_1.has("shade") && !JsonHelper.hasBoolean(jsonObject_1, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean boolean_1 = JsonHelper.getBoolean(jsonObject_1, "shade", true);
            return new ModelElement(vector3f_1, vector3f_2, map_1, modelRotation_1, boolean_1);
         }
      }

      @Nullable
      private ModelRotation deserializeRotation(JsonObject jsonObject_1) {
         ModelRotation modelRotation_1 = null;
         if (jsonObject_1.has("rotation")) {
            JsonObject jsonObject_2 = JsonHelper.getObject(jsonObject_1, "rotation");
            Vector3f vector3f_1 = this.deserializeVec3f(jsonObject_2, "origin");
            vector3f_1.scale(0.0625F);
            Direction.Axis direction$Axis_1 = this.deserializeAxis(jsonObject_2);
            float float_1 = this.deserializeRotationAngle(jsonObject_2);
            boolean boolean_1 = JsonHelper.getBoolean(jsonObject_2, "rescale", false);
            modelRotation_1 = new ModelRotation(vector3f_1, direction$Axis_1, float_1, boolean_1);
         }

         return modelRotation_1;
      }

      private float deserializeRotationAngle(JsonObject jsonObject_1) {
         float float_1 = JsonHelper.getFloat(jsonObject_1, "angle");
         if (float_1 != 0.0F && MathHelper.abs(float_1) != 22.5F && MathHelper.abs(float_1) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + float_1 + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return float_1;
         }
      }

      private Direction.Axis deserializeAxis(JsonObject jsonObject_1) {
         String string_1 = JsonHelper.getString(jsonObject_1, "axis");
         Direction.Axis direction$Axis_1 = Direction.Axis.fromName(string_1.toLowerCase(Locale.ROOT));
         if (direction$Axis_1 == null) {
            throw new JsonParseException("Invalid rotation axis: " + string_1);
         } else {
            return direction$Axis_1;
         }
      }

      private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext jsonDeserializationContext_1, JsonObject jsonObject_1) {
         Map<Direction, ModelElementFace> map_1 = this.deserializeFaces(jsonDeserializationContext_1, jsonObject_1);
         if (map_1.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map_1;
         }
      }

      private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext jsonDeserializationContext_1, JsonObject jsonObject_1) {
         Map<Direction, ModelElementFace> map_1 = Maps.newEnumMap(Direction.class);
         JsonObject jsonObject_2 = JsonHelper.getObject(jsonObject_1, "faces");
         Iterator<Entry<String, JsonElement>> var5 = jsonObject_2.entrySet().iterator();

         while(var5.hasNext()) {
            Entry<String, JsonElement> map$Entry_1 = (Entry<String, JsonElement>)var5.next();
            Direction direction_1 = this.getDirection(map$Entry_1.getKey());
            map_1.put(direction_1, jsonDeserializationContext_1.deserialize(map$Entry_1.getValue(), ModelElementFace.class));
         }

         return map_1;
      }

      private Direction getDirection(String string_1) {
         Direction direction_1 = Direction.byName(string_1);
         if (direction_1 == null) {
            throw new JsonParseException("Unknown facing: " + string_1);
         } else {
            return direction_1;
         }
      }

      private Vector3f deserializeTo(JsonObject jsonObject_1) {
         Vector3f vector3f_1 = this.deserializeVec3f(jsonObject_1, "to");
         if (vector3f_1.x() >= -16.0F && vector3f_1.y() >= -16.0F && vector3f_1.z() >= -16.0F && vector3f_1.x() <= 32.0F && vector3f_1.y() <= 32.0F && vector3f_1.z() <= 32.0F) {
            return vector3f_1;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f_1);
         }
      }

      private Vector3f deserializeFrom(JsonObject jsonObject_1) {
         Vector3f vector3f_1 = this.deserializeVec3f(jsonObject_1, "from");
         if (vector3f_1.x() >= -16.0F && vector3f_1.y() >= -16.0F && vector3f_1.z() >= -16.0F && vector3f_1.x() <= 32.0F && vector3f_1.y() <= 32.0F && vector3f_1.z() <= 32.0F) {
            return vector3f_1;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f_1);
         }
      }

      private Vector3f deserializeVec3f(JsonObject jsonObject_1, String string_1) {
         JsonArray jsonArray_1 = JsonHelper.getArray(jsonObject_1, string_1);
         if (jsonArray_1.size() != 3) {
            throw new JsonParseException("Expected 3 " + string_1 + " values, found: " + jsonArray_1.size());
         } else {
            float[] floats_1 = new float[3];

            for(int int_1 = 0; int_1 < floats_1.length; ++int_1) {
               floats_1[int_1] = JsonHelper.asFloat(jsonArray_1.get(int_1), string_1 + "[" + int_1 + "]");
            }

            return new Vector3f(floats_1[0], floats_1[1], floats_1[2]);
         }
      }
   }
}
