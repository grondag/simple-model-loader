package grondag.sml.json.model;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

@Environment(EnvType.CLIENT)
public class BasicBakedModel implements BakedModel, FabricBakedModel {
    protected static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();
    
    protected final Mesh mesh;
    protected WeakReference<List<BakedQuad>[]> quadLists = null;
    protected final boolean usesAo;
    protected final boolean depthInGui;
    protected final Sprite particleSprite;
    protected final ModelTransformation transformation;
    protected final ModelItemPropertyOverrideList itemPropertyOverrides;

    public BasicBakedModel(Mesh mesh, boolean usesAo, boolean depthInGui, Sprite particleSprite, ModelTransformation transformation, ModelItemPropertyOverrideList ttemPropertyOverrides) {
        this.mesh = mesh;
        this.usesAo = usesAo;
        this.depthInGui = depthInGui;
        this.particleSprite = particleSprite;
        this.transformation = transformation;
        this.itemPropertyOverrides = ttemPropertyOverrides;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random rand) {
        List<BakedQuad>[] lists = quadLists == null ? null : quadLists.get();
        if(lists == null) {
            lists = ModelHelper.toQuadLists(this.mesh);
            quadLists = new WeakReference<>(lists);
        }
        List<BakedQuad> result = lists[face == null ? 6 : face.getId()];
        return result == null ? ImmutableList.of() : result;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.usesAo;
    }

    @Override
    public boolean hasDepthInGui() {
        return this.depthInGui;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return this.particleSprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.transformation;
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.itemPropertyOverrides;
    }
    
    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(ExtendedBlockView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
//        final MeshTransformer transform = transformerFactory == null ? null : transformerFactory.get().prepare(blockView, state, pos, randomSupplier);
//        if(transform != null) {
//            context.pushTransform(transform);
//        }
        if(mesh != null) {
            context.meshConsumer().accept(mesh);
        }
//        if(dynamicRender != null) {
//            dynamicRender.render(blockView, state, pos, randomSupplier, context);
//        }
//        if(transform != null) {
//            context.popTransform();
//        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
//        final MeshTransformer transform = transformerFactory == null ? null : transformerFactory.get().prepare(stack, randomSupplier);
//        if(transform != null) {
//            context.pushTransform(transform);
//        }
        if(mesh != null) {
            context.meshConsumer().accept(mesh);
        }
//        if(dynamicRender != null) {
//            dynamicRender.render(null, null, null, randomSupplier, context);
//        }
//        if(transform != null) {
//            context.popTransform();
//        }
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final MeshBuilder meshBuilder;
        private final MaterialFinder finder;
        private final QuadEmitter emitter;
        private final ModelItemPropertyOverrideList itemPropertyOverrides;
        private final boolean usesAo;
        private Sprite particleTexture;
        private final boolean depthInGui;
        private final ModelTransformation transformation;

        public Builder(JsonUnbakedModel unbakedModel, ModelItemPropertyOverrideList itemPropertyOverrides) {
            this(unbakedModel.useAmbientOcclusion(), unbakedModel.hasDepthInGui(), unbakedModel.getTransformations(), itemPropertyOverrides);
        }

        //      public Builder(BlockState blockState, BakedModel bakedModel, Sprite particleSprite, Random random, long seed) {
        //         this(bakedModel.useAmbientOcclusion(), bakedModel.hasDepthInGui(), bakedModel.getTransformation(), bakedModel.getItemPropertyOverrides());
        //         this.particleTexture = bakedModel.getSprite();
        //         for(int i = 0; i < 7; i++) {
        //            Direction face = ModelHelper.faceFromIndex(i);
        //            random.setSeed(seed);
        //            Iterator<BakedQuad> quads = bakedModel.getQuads(blockState, face, random).iterator();
        //
        //            while(quads.hasNext()) {
        //               BakedQuad quad = quads.next();
        //               this.addQuad(face, new RetexturedBakedQuad(quad, particleSprite));
        //            }
        //         }
        //      }

        private Builder(boolean usesAo, boolean depthInGui, ModelTransformation transformation, ModelItemPropertyOverrideList itemPropertyOverrides) {
            this.meshBuilder = RENDERER.meshBuilder();
            this.finder = RENDERER.materialFinder();
            this.emitter = meshBuilder.getEmitter();
            this.itemPropertyOverrides = itemPropertyOverrides;
            this.usesAo = usesAo;
            this.depthInGui = depthInGui;
            this.transformation = transformation;
        }

        //      public BasicBakedModel.Builder addQuad(Direction direction_1, BakedQuad bakedQuad_1) {
        //         this.faceQuads.get(direction_1).add(bakedQuad_1);
        //         return this;
        //      }

        //      public BasicBakedModel.Builder addQuad(BakedQuad bakedQuad_1) {
        //         this.quads.add(bakedQuad_1);
        //         return this;
        //      }

        public BasicBakedModel.Builder setParticle(Sprite sprite) {
            this.particleTexture = sprite;
            return this;
        }

        public BakedModel build() {
            if (this.particleTexture == null) {
                throw new RuntimeException("Missing particle!");
            } else {
                return new BasicBakedModel(meshBuilder.build(), usesAo, depthInGui, particleTexture, transformation, itemPropertyOverrides);
            }
        }

        private static final BakedQuadFactory QUADFACTORY = new BakedQuadFactory();
        private static final BakedQuadFactoryExt QUADFACTORY_EXT = (BakedQuadFactoryExt)QUADFACTORY;
        
        /**
         * Intent here is to duplicate vanilla baking exactly.  Code is adapted from BakedQuadFactory.
         */
        public void addQuad(Direction cullFace, ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction face, ModelBakeSettings bakeProps) {
            RenderMaterial mat = finder.clear().disableDiffuse(0, !element.shade).find();
            final QuadEmitter emitter = this.emitter;
            emitter.material(mat);
            emitter.cullFace(cullFace);
            QUADFACTORY_EXT.bake(emitter, element, elementFace, sprite, face, bakeProps);
            emitter.emit();
        }
    }
}

