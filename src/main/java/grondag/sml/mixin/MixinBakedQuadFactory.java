package grondag.sml.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import grondag.sml.json.model.BakedQuadFactoryExt;
import grondag.sml.json.model.BakedQuadFactoryHelper;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Mixin(BakedQuadFactory.class)
public abstract class MixinBakedQuadFactory implements BakedQuadFactoryExt {
    @Shadow
    protected abstract ModelElementTexture uvLock(ModelElementTexture tex, Direction face, net.minecraft.client.render.model.ModelRotation rotation);

    @Shadow
    protected abstract void method_3461(int[] data, int vertexIndex, Direction face, ModelElementTexture tex, float[] uvs, Sprite sprite, ModelRotation texRotation, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation, boolean shade);

    @Shadow
    protected abstract void method_3462(int[] data, Direction face);
    
    @Override
    public void bake(QuadEmitter q, ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction face, ModelBakeSettings bakeProps) {
        final BakedQuadFactoryHelper help = BakedQuadFactoryHelper.get();
        final net.minecraft.client.render.model.json.ModelRotation modelRotation = element.rotation;

        ModelElementTexture tex = elementFace.textureData;
        if (bakeProps.isUvLocked()) {
            tex = uvLock(elementFace.textureData, face, bakeProps.getRotation());
        }

        final float[] uvs = help.uv;
        System.arraycopy(tex.uvs, 0, uvs, 0, BakedQuadFactoryHelper.UV_LEN);
        float uCent = (float)sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU());
        float vCent = (float)sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV());
        float uvCent = 4.0F / Math.max(vCent, uCent);
        float uAdj = (tex.uvs[0] + tex.uvs[0] + tex.uvs[2] + tex.uvs[2]) / 4.0F;
        float vAdj = (tex.uvs[1] + tex.uvs[1] + tex.uvs[3] + tex.uvs[3]) / 4.0F;
        tex.uvs[0] = MathHelper.lerp(uvCent, tex.uvs[0], uAdj);
        tex.uvs[2] = MathHelper.lerp(uvCent, tex.uvs[2], uAdj);
        tex.uvs[1] = MathHelper.lerp(uvCent, tex.uvs[1], vAdj);
        tex.uvs[3] = MathHelper.lerp(uvCent, tex.uvs[3], vAdj);
        int[] ints_1 = buildVertexData(help.data, tex, sprite, face, normalizePos(help.pos, element.from, element.to), bakeProps.getRotation(), modelRotation);
        Direction nominalFace = BakedQuadFactory.method_3467(ints_1);
        System.arraycopy(uvs, 0, tex.uvs, 0, BakedQuadFactoryHelper.UV_LEN);
        if (modelRotation == null) {
            method_3462(ints_1, nominalFace);
        }
        q.fromVanilla(ints_1, 0, false);
        q.colorIndex( elementFace.tintIndex);
    }

    private int[] buildVertexData(int[] target, ModelElementTexture tex, Sprite sprite, Direction face, float[] pos, ModelRotation texRotation, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation) {
        for(int i = 0; i < 4; ++i) {
            method_3461(target, i, face, tex, pos, sprite, texRotation, modelRotation, false);
        }
        return target;
    }

    private static float[] normalizePos(float [] targets, Vector3f vector3f_1, Vector3f vector3f_2) {
        targets[CubeFace.DirectionIds.WEST] = vector3f_1.x() / 16.0F;
        targets[CubeFace.DirectionIds.DOWN] = vector3f_1.y() / 16.0F;
        targets[CubeFace.DirectionIds.NORTH] = vector3f_1.z() / 16.0F;
        targets[CubeFace.DirectionIds.EAST] = vector3f_2.x() / 16.0F;
        targets[CubeFace.DirectionIds.UP] = vector3f_2.y() / 16.0F;
        targets[CubeFace.DirectionIds.SOUTH] = vector3f_2.z() / 16.0F;
        return targets;
    }
}
