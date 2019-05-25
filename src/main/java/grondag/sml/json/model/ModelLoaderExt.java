package grondag.sml.json.model;

import grondag.sml.json.encoding.JsonUnbakedModel;
import net.minecraft.util.SystemUtil;

public class ModelLoaderExt {
    public static final JsonUnbakedModel GENERATION_MARKER = (JsonUnbakedModel)SystemUtil.consume(JsonUnbakedModel.deserialize("{}"), (jsonUnbakedModel_1) -> {
        jsonUnbakedModel_1.id = "generation marker";
     });
    
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = (JsonUnbakedModel)SystemUtil.consume(JsonUnbakedModel.deserialize("{}"), (jsonUnbakedModel_1) -> {
        jsonUnbakedModel_1.id = "block entity marker";
     });
}
