package grondag.sml;

import grondag.sml.json.encoding.ModelDeserializer;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class Provider implements ModelResourceProvider {
    
    private final ResourceManager resourceManager;
    
    public Provider(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    
    @Override
    public UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        return ModelDeserializer.deserialize(resourceId, context, resourceManager);
    }

}