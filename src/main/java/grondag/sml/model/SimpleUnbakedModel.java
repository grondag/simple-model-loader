package grondag.sml.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class SimpleUnbakedModel implements UnbakedModel {
    Function<ModelBuilder, BakedModel> baker;
    
    public SimpleUnbakedModel(Function<ModelBuilder, BakedModel> baker) {
        this.baker = baker;
    }
    
    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> var1, Set<String> var2) {
        return ImmutableList.of(new Identifier("minecraft:block/quartz_block_side"));
        //return Collections.emptyList();
    }

    @Override
    public BakedModel bake(ModelLoader var1, Function<Identifier, Sprite> var2, ModelBakeSettings var3) {
        return baker.apply(ModelBuilder.instance());
    }
}