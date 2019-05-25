package grondag.sml.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import grondag.sml.json.encoding.ModelElement;
import grondag.sml.json.encoding.ModelElementFace;
import grondag.sml.json.encoding.ModelElementTexture;
import grondag.sml.json.model.BakedQuadFactoryExt;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Mixin(BakedQuadFactory.class)
public abstract class MixinBakedQuadFactory implements BakedQuadFactoryExt {
    private static final int UV_LEN = 4;
    
    @Shadow private static BakedQuadFactory.class_797[] field_4264
    
    private static class Helper {
        int data[] = new int[28];
        float uv[] = new float[UV_LEN];
    }
    
    private static final ThreadLocal<Helper> HELPERS = ThreadLocal.withInitial(Helper::new);
    
    @Override
    public void bake(QuadEmitter q, ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction face, ModelBakeSettings bakeProps) {
        final Helper help = HELPERS.get();
        final net.minecraft.client.render.model.json.ModelRotation modelRotation = element.rotation;
        
        ModelElementTexture tex = elementFace.textureData;
        if (bakeProps.isUvLocked()) {
           tex = sml_uvLock(elementFace.textureData, face, bakeProps.getRotation());
        }

        final float[] uvs = help.uv;
        System.arraycopy(tex.uvs, 0, uvs, 0, UV_LEN);
        float uCent = (float)sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU());
        float vCent = (float)sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV());
        float uvCent = 4.0F / Math.max(vCent, uCent);
        float uAdj = (tex.uvs[0] + tex.uvs[0] + tex.uvs[2] + tex.uvs[2]) / 4.0F;
        float vAdj = (tex.uvs[1] + tex.uvs[1] + tex.uvs[3] + tex.uvs[3]) / 4.0F;
        tex.uvs[0] = MathHelper.lerp(uvCent, tex.uvs[0], uAdj);
        tex.uvs[2] = MathHelper.lerp(uvCent, tex.uvs[2], uAdj);
        tex.uvs[1] = MathHelper.lerp(uvCent, tex.uvs[1], vAdj);
        tex.uvs[3] = MathHelper.lerp(uvCent, tex.uvs[3], vAdj);
        int[] ints_1 = this.method_3458(tex, sprite, face, this.method_3459(element.from, element.to), bakeProps.getRotation(), modelRotation, false);
        Direction direction_2 = method_3467(ints_1);
        System.arraycopy(uvs, 0, tex.uvs, 0, UV_LEN);
        if (modelRotation == null) {
           this.method_3462(ints_1, direction_2);
        }
        q.fromVanilla(ints_1, 0, false);
        q.colorIndex( elementFace.tintIndex);
    }
    
    private ModelElementTexture sml_uvLock(ModelElementTexture tex, Direction face, ModelRotation rotation) {
        return field_4264[uvLockIndex(rotation, face)].method_3469(tex);
     }
   
    private static final int MODEL_ROTATION_COUNT = ModelRotation.values().length;
    
    private static int uvLockIndex(ModelRotation rotation, Direction face) {
        return MODEL_ROTATION_COUNT * face.ordinal() + rotation.ordinal();
    }
}
