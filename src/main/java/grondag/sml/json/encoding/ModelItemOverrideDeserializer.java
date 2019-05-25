package grondag.sml.json.encoding;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelItemOverrideDeserializer implements JsonDeserializer<ModelItemOverride> {

    @Override
    public ModelItemOverride deserialize(JsonElement jsonElement_1, Type type_1, JsonDeserializationContext jsonDeserializationContext_1) throws JsonParseException {
        JsonObject jsonObject_1 = jsonElement_1.getAsJsonObject();
        Identifier identifier_1 = new Identifier(JsonHelper.getString(jsonObject_1, "model"));
        Map<Identifier, Float> map_1 = this.deserializeMinPropertyValues(jsonObject_1);
        return new ModelItemOverride(identifier_1, map_1);
    }

    protected Map<Identifier, Float> deserializeMinPropertyValues(JsonObject jsonObject_1) {
        Map<Identifier, Float> map_1 = Maps.newLinkedHashMap();
        JsonObject jsonObject_2 = JsonHelper.getObject(jsonObject_1, "predicate");
        Iterator<Entry<String, JsonElement>> var4 = jsonObject_2.entrySet().iterator();

        while(var4.hasNext()) {
            Entry<String, JsonElement> map$Entry_1 = var4.next();
            map_1.put(new Identifier((String)map$Entry_1.getKey()), JsonHelper.asFloat((JsonElement)map$Entry_1.getValue(), (String)map$Entry_1.getKey()));
        }

        return map_1;
    }

}
