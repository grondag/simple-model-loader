package grondag.sml.json.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import grondag.sml.json.encoding.JsonUnbakedModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OverrideListHelper {
   
    public static ModelItemPropertyOverrideList make(ModelLoader loader, JsonUnbakedModel unbakedModel, Function<Identifier, UnbakedModel> mapFunc, List<ModelItemOverride> overridesIn) {
        ModelItemPropertyOverrideList result = new ModelItemPropertyOverrideList(null, null, null, Collections.emptyList());
        OverrideListExt ext = (OverrideListExt)result;
        List<BakedModel> models = overridesIn.stream().map((modelItemOverride_1) -> {
           UnbakedModel unbakedModel_1 = (UnbakedModel)mapFunc.apply(modelItemOverride_1.getModelId());
           return Objects.equals(unbakedModel_1, unbakedModel) ? null : loader.bake(modelItemOverride_1.getModelId(), net.minecraft.client.render.model.ModelRotation.X0_Y0);
        }).collect(Collectors.toCollection(() -> ext.models()));
        Collections.reverse(models);

        List<ModelItemOverride> overrides = ext.overrides();
        for(int i = overridesIn.size() - 1; i >= 0; --i) {
           overrides.add(overridesIn.get(i));
        }

        return result;
     }
    
    public static interface OverrideListExt {
        List<ModelItemOverride> overrides();
        List<BakedModel> models();
    }
}
