package grondag.sml.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class WeightedBakedModel implements BakedModel {
    private final int totalWeight;
    private final List<WeightedBakedModel.ModelEntry> models;
    private final BakedModel defaultModel;

    public WeightedBakedModel(List<WeightedBakedModel.ModelEntry> list_1) {
        this.models = list_1;
        this.totalWeight = WeightedPicker.getWeightSum(list_1);
        this.defaultModel = ((WeightedBakedModel.ModelEntry)list_1.get(0)).model;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState_1, @Nullable Direction direction_1, Random random_1) {
        return ((WeightedBakedModel.ModelEntry)WeightedPicker.getAt(this.models, Math.abs((int)random_1.nextLong()) % this.totalWeight)).model.getQuads(blockState_1, direction_1, random_1);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepthInGui() {
        return this.defaultModel.hasDepthInGui();
    }

    @Override
    public boolean isBuiltin() {
        return this.defaultModel.isBuiltin();
    }

    @Override
    public Sprite getSprite() {
        return this.defaultModel.getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.defaultModel.getTransformation();
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.defaultModel.getItemPropertyOverrides();
    }

    @Environment(EnvType.CLIENT)
    static class ModelEntry extends WeightedPicker.Entry {
        protected final BakedModel model;

        public ModelEntry(BakedModel bakedModel_1, int int_1) {
            super(int_1);
            this.model = bakedModel_1;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final List<WeightedBakedModel.ModelEntry> models = Lists.newArrayList();

        public WeightedBakedModel.Builder add(@Nullable BakedModel bakedModel_1, int int_1) {
            if (bakedModel_1 != null) {
                this.models.add(new WeightedBakedModel.ModelEntry(bakedModel_1, int_1));
            }

            return this;
        }

        @Nullable
        public BakedModel getFirst() {
            if (this.models.isEmpty()) {
                return null;
            } else {
                return (BakedModel)(this.models.size() == 1 ? ((WeightedBakedModel.ModelEntry)this.models.get(0)).model : new WeightedBakedModel(this.models));
            }
        }
    }
}