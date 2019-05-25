package grondag.sml.json.encoding;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelElementTexture {
    public float[] uvs;
    public final int rotation;

    public ModelElementTexture(@Nullable float[] floats_1, int int_1) {
        this.uvs = floats_1;
        this.rotation = int_1;
    }

    public float getU(int int_1) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        } else {
            int int_2 = this.getRotatedUVIndex(int_1);
            return this.uvs[int_2 != 0 && int_2 != 1 ? 2 : 0];
        }
    }

    public float getV(int int_1) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        } else {
            int int_2 = this.getRotatedUVIndex(int_1);
            return this.uvs[int_2 != 0 && int_2 != 3 ? 3 : 1];
        }
    }

    private int getRotatedUVIndex(int int_1) {
        return (int_1 + this.rotation / 90) % 4;
    }

    public int method_3414(int int_1) {
        return (int_1 + 4 - this.rotation / 90) % 4;
    }

    public void setUvs(float[] floats_1) {
        if (this.uvs == null) {
            this.uvs = floats_1;
        }

    }

    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelElementTexture> {
        protected Deserializer() {
        }

        @Override
        public ModelElementTexture deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
            JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
            float[] floats_1 = this.deserializeUVs(jsonObject_1);
            int int_1 = this.deserializeRotation(jsonObject_1);
            return new ModelElementTexture(floats_1, int_1);
        }

        protected int deserializeRotation(JsonObject jsonObject_1) {
            int int_1 = JsonHelper.getInt(jsonObject_1, "rotation", 0);
            if (int_1 >= 0 && int_1 % 90 == 0 && int_1 / 90 <= 3) {
                return int_1;
            } else {
                throw new JsonParseException("Invalid rotation " + int_1 + " found, only 0/90/180/270 allowed");
            }
        }

        @Nullable
        private float[] deserializeUVs(JsonObject jsonObject_1) {
            if (!jsonObject_1.has("uv")) {
                return null;
            } else {
                JsonArray jsonArray_1 = JsonHelper.getArray(jsonObject_1, "uv");
                if (jsonArray_1.size() != 4) {
                    throw new JsonParseException("Expected 4 uv values, found: " + jsonArray_1.size());
                } else {
                    float[] floats_1 = new float[4];

                    for(int int_1 = 0; int_1 < floats_1.length; ++int_1) {
                        floats_1[int_1] = JsonHelper.asFloat(jsonArray_1.get(int_1), "uv[" + int_1 + "]");
                    }

                    return floats_1;
                }
            }
        }
    }
}

