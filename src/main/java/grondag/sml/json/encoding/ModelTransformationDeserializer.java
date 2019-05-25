package grondag.sml.json.encoding;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;

@Environment(EnvType.CLIENT)
public class ModelTransformationDeserializer implements JsonDeserializer<ModelTransformation> {
    @Override
    public ModelTransformation deserialize(JsonElement jsonElement_1, java.lang.reflect.Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
        JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
        Transformation transformation_1 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "thirdperson_righthand");
        Transformation transformation_2 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "thirdperson_lefthand");
        if (transformation_2 == Transformation.NONE) {
            transformation_2 = transformation_1;
        }

        Transformation transformation_3 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "firstperson_righthand");
        Transformation transformation_4 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "firstperson_lefthand");
        if (transformation_4 == Transformation.NONE) {
            transformation_4 = transformation_3;
        }

        Transformation transformation_5 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "head");
        Transformation transformation_6 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "gui");
        Transformation transformation_7 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "ground");
        Transformation transformation_8 = this.parseModelTransformation(jsonDeserializationContext_1, jsonObject_1, "fixed");
        return new ModelTransformation(transformation_2, transformation_1, transformation_4, transformation_3, transformation_5, transformation_6, transformation_7, transformation_8);
    }

    private Transformation parseModelTransformation(JsonDeserializationContext jsonDeserializationContext_1, JsonObject jsonObject_1, String string_1) {
        return jsonObject_1.has(string_1) ? (Transformation)jsonDeserializationContext_1.deserialize(jsonObject_1.get(string_1), Transformation.class) : Transformation.NONE;
    }
}