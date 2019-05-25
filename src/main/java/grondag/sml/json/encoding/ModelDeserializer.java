package grondag.sml.json.encoding;

import javax.annotation.Nullable;

import grondag.sml.model.SimpleModel;
import grondag.sml.model.SimpleUnbakedModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ModelDeserializer {
    public static @Nullable UnbakedModel deserialize(Identifier resourceId, ModelProviderContext context, ResourceManager resourceManager) {
        if(resourceId.getPath().equals("block/quartz_block")) {
            return temp();
        } else {
            return null;
        }
    }
    
    private static UnbakedModel temp() {
        return new SimpleUnbakedModel(mb -> {
            Sprite sprite = mb.getSprite("minecraft:block/quartz_block_side");
            mb.box(mb.finder().emissive(0, true).disableAo(0, true).disableDiffuse(0, true).find(),
                    -1, sprite, 
                    0, 0, 0, 1, 1, 1);
            return new SimpleModel(mb.builder.build(), null, sprite, ModelHelper.MODEL_TRANSFORM_BLOCK, null);
        });
    }
}
