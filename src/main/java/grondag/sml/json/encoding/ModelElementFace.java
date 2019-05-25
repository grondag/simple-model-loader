package grondag.sml.json.encoding;

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
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ModelElementFace {
    public final Direction cullFace;
    public final int tintIndex;
    public final String textureId;
    public final ModelElementTexture textureData;

    public ModelElementFace(@Nullable Direction direction_1, int int_1, String string_1, ModelElementTexture modelElementTexture_1) {
        this.cullFace = direction_1;
        this.tintIndex = int_1;
        this.textureId = string_1;
        this.textureData = modelElementTexture_1;
    }

    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelElementFace> {
        protected Deserializer() {
        }

        @Override
        public ModelElementFace deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
            JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
            Direction direction_1 = this.deserializeCullFace(jsonObject_1);
            int int_1 = this.deserializeTintIndex(jsonObject_1);
            String string_1 = this.deserializeTexture(jsonObject_1);
            ModelElementTexture modelElementTexture_1 = (ModelElementTexture)jsonDeserializationContext_1.deserialize(jsonObject_1, ModelElementTexture.class);
            return new ModelElementFace(direction_1, int_1, string_1, modelElementTexture_1);
        }

        protected int deserializeTintIndex(JsonObject jsonObject_1) {
            return JsonHelper.getInt(jsonObject_1, "tintindex", -1);
        }

        private String deserializeTexture(JsonObject jsonObject_1) {
            return JsonHelper.getString(jsonObject_1, "texture");
        }

        @Nullable
        private Direction deserializeCullFace(JsonObject jsonObject_1) {
            String string_1 = JsonHelper.getString(jsonObject_1, "cullface", "");
            return Direction.byName(string_1);
        }
    }
}

