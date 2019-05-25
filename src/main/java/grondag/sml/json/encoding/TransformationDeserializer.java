package grondag.sml.json.encoding;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class TransformationDeserializer implements JsonDeserializer<Transformation> {
    private static final Vector3f DEFAULT_ROATATION = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

    @Override
    public Transformation deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
        JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
        Vector3f vector3f_1 = this.parseVector3f(jsonObject_1, "rotation", DEFAULT_ROATATION);
        Vector3f vector3f_2 = this.parseVector3f(jsonObject_1, "translation", DEFAULT_TRANSLATION);
        vector3f_2.scale(0.0625F);
        vector3f_2.clamp(-5.0F, 5.0F);
        Vector3f vector3f_3 = this.parseVector3f(jsonObject_1, "scale", DEFAULT_SCALE);
        vector3f_3.clamp(-4.0F, 4.0F);
        return new Transformation(vector3f_1, vector3f_2, vector3f_3);
    }

    private Vector3f parseVector3f(JsonObject jsonObject_1, String string_1, Vector3f vector3f_1) {
        if (!jsonObject_1.has(string_1)) {
            return vector3f_1;
        } else {
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