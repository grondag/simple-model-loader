package grondag.sml.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


import grondag.sml.json.model.OverrideListHelper.OverrideListExt;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;

@Mixin(ModelItemPropertyOverrideList.class)
public class MixinModelItemPropertyOverrideList implements OverrideListExt {
    @Shadow private List<ModelItemOverride> overrides;
    @Shadow private List<BakedModel> models;
    
    @Override
    public List<ModelItemOverride> overrides() {
        return overrides;
    }

    @Override
    public List<BakedModel> models() {
        return models;
    }
}
