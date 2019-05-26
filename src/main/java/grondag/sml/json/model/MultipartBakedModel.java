//package grondag.sml.json.model;
//
//import com.google.common.collect.Lists;
//import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
//import java.util.BitSet;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.function.Predicate;
//import javax.annotation.Nullable;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.BakedQuad;
//import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
//import net.minecraft.client.render.model.json.ModelTransformation;
//import net.minecraft.client.texture.Sprite;
//import net.minecraft.util.SystemUtil;
//import net.minecraft.util.math.Direction;
//import org.apache.commons.lang3.tuple.Pair;
//
////TODO: patch MultipartBakedModel
//@Environment(EnvType.CLIENT)
//public class MultipartBakedModel implements BakedModel {
//   private final List<Pair<Predicate<BlockState>, BakedModel>> components;
//   protected final boolean ambientOcclusion;
//   protected final boolean depthGui;
//   protected final Sprite sprite;
//   protected final ModelTransformation transformations;
//   protected final ModelItemPropertyOverrideList itemPropertyOverrides;
//   private final Map<BlockState, BitSet> field_5431 = new Object2ObjectOpenCustomHashMap<>(SystemUtil.identityHashStrategy());
//
//   public MultipartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> list_1) {
//      this.components = list_1;
//      BakedModel bakedModel_1 = list_1.iterator().next().getRight();
//      this.ambientOcclusion = bakedModel_1.useAmbientOcclusion();
//      this.depthGui = bakedModel_1.hasDepthInGui();
//      this.sprite = bakedModel_1.getSprite();
//      this.transformations = bakedModel_1.getTransformation();
//      this.itemPropertyOverrides = bakedModel_1.getItemPropertyOverrides();
//   }
//
//   @Override
//public List<BakedQuad> getQuads(@Nullable BlockState blockState_1, @Nullable Direction direction_1, Random random_1) {
//      if (blockState_1 == null) {
//         return Collections.emptyList();
//      } else {
//         BitSet bitSet_1 = (BitSet)this.field_5431.get(blockState_1);
//         if (bitSet_1 == null) {
//            bitSet_1 = new BitSet();
//
//            for(int int_1 = 0; int_1 < this.components.size(); ++int_1) {
//               Pair<Predicate<BlockState>, BakedModel> pair_1 = this.components.get(int_1);
//               if ((pair_1.getLeft()).test(blockState_1)) {
//                  bitSet_1.set(int_1);
//               }
//            }
//
//            this.field_5431.put(blockState_1, bitSet_1);
//         }
//
//         List<BakedQuad> list_1 = Lists.newArrayList();
//         long long_1 = random_1.nextLong();
//
//         for(int int_2 = 0; int_2 < bitSet_1.length(); ++int_2) {
//            if (bitSet_1.get(int_2)) {
//               list_1.addAll(((BakedModel)(components.get(int_2)).getRight()).getQuads(blockState_1, direction_1, new Random(long_1)));
//            }
//         }
//
//         return list_1;
//      }
//   }
//
//   @Override
//public boolean useAmbientOcclusion() {
//      return this.ambientOcclusion;
//   }
//
//   @Override
//public boolean hasDepthInGui() {
//      return this.depthGui;
//   }
//
//   @Override
//public boolean isBuiltin() {
//      return false;
//   }
//
//   @Override
//public Sprite getSprite() {
//      return this.sprite;
//   }
//
//   @Override
//public ModelTransformation getTransformation() {
//      return this.transformations;
//   }
//
//   @Override
//public ModelItemPropertyOverrideList getItemPropertyOverrides() {
//      return this.itemPropertyOverrides;
//   }
//
//   @Environment(EnvType.CLIENT)
//   public static class Builder {
//      private final List<Pair<Predicate<BlockState>, BakedModel>> components = Lists.newArrayList();
//
//      public void addComponent(Predicate<BlockState> predicate_1, BakedModel bakedModel_1) {
//         this.components.add(Pair.of(predicate_1, bakedModel_1));
//      }
//
//      public BakedModel build() {
//         return new MultipartBakedModel(this.components);
//      }
//   }
//}
