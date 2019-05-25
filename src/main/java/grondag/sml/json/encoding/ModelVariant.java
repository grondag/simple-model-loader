package grondag.sml.json.encoding;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelVariant implements ModelBakeSettings {
    private final Identifier location;
    private final net.minecraft.client.render.model.ModelRotation rotation;
    private final boolean uvLock;
    private final int weight;

    public ModelVariant(Identifier identifier_1, net.minecraft.client.render.model.ModelRotation modelRotation_1, boolean boolean_1, int int_1) {
        this.location = identifier_1;
        this.rotation = modelRotation_1;
        this.uvLock = boolean_1;
        this.weight = int_1;
    }

    public Identifier getLocation() {
        return this.location;
    }

    @Override
    public net.minecraft.client.render.model.ModelRotation getRotation() {
        return this.rotation;
    }

    @Override
    public boolean isUvLocked() {
        return this.uvLock;
    }

    public int getWeight() {
        return this.weight;
    }

    @Override
    public String toString() {
        return "Variant{modelLocation=" + this.location + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
    }

    @Override
    public boolean equals(Object object_1) {
        if (this == object_1) {
            return true;
        } else if (!(object_1 instanceof ModelVariant)) {
            return false;
        } else {
            ModelVariant modelVariant_1 = (ModelVariant)object_1;
            return this.location.equals(modelVariant_1.location) && this.rotation == modelVariant_1.rotation && this.uvLock == modelVariant_1.uvLock && this.weight == modelVariant_1.weight;
        }
    }

    @Override
    public int hashCode() {
        int int_1 = this.location.hashCode();
        int_1 = 31 * int_1 + this.rotation.hashCode();
        int_1 = 31 * int_1 + Boolean.valueOf(this.uvLock).hashCode();
        int_1 = 31 * int_1 + this.weight;
        return int_1;
    }

    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelVariant> {
        @Override
        public ModelVariant deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
            JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
            Identifier identifier_1 = this.deserializeModel(jsonObject_1);
            net.minecraft.client.render.model.ModelRotation modelRotation_1 = this.deserializeRotation(jsonObject_1);
            boolean boolean_1 = this.deserializeUvLock(jsonObject_1);
            int int_1 = this.deserializeWeight(jsonObject_1);
            return new ModelVariant(identifier_1, modelRotation_1, boolean_1, int_1);
        }

        private boolean deserializeUvLock(JsonObject jsonObject_1) {
            return JsonHelper.getBoolean(jsonObject_1, "uvlock", false);
        }

        protected net.minecraft.client.render.model.ModelRotation deserializeRotation(JsonObject jsonObject_1) {
            int int_1 = JsonHelper.getInt(jsonObject_1, "x", 0);
            int int_2 = JsonHelper.getInt(jsonObject_1, "y", 0);
            net.minecraft.client.render.model.ModelRotation modelRotation_1 = net.minecraft.client.render.model.ModelRotation.get(int_1, int_2);
            if (modelRotation_1 == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + int_1 + ", y: " + int_2);
            } else {
                return modelRotation_1;
            }
        }

        protected Identifier deserializeModel(JsonObject jsonObject_1) {
            return new Identifier(JsonHelper.getString(jsonObject_1, "model"));
        }

        protected int deserializeWeight(JsonObject jsonObject_1) {
            int int_1 = JsonHelper.getInt(jsonObject_1, "weight", 1);
            if (int_1 < 1) {
                throw new JsonParseException("Invalid weight " + int_1 + " found, expected integer >= 1");
            } else {
                return int_1;
            }
        }
    }
}
