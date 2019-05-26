package grondag.sml.mixin;

import java.util.Random;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

@Mixin(MultipartBakedModel.class)
public class MixinMultipartBakedModel implements FabricBakedModel {

    private boolean isVanilla = true;

    @Override
    public boolean isVanillaAdapter() {
        return isVanilla;
    }

    @Override
    public void emitBlockQuads(ExtendedBlockView blockView, BlockState state, BlockPos pos,
            Supplier<Random> randomSupplier, RenderContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        // TODO Auto-generated method stub
        
    }
}
