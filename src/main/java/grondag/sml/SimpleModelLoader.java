package grondag.sml;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

public class SimpleModelLoader implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(Provider::new);
    }
}
