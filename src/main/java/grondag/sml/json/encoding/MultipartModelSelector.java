package grondag.sml.json.encoding;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;

@Environment(EnvType.CLIENT)
public interface MultipartModelSelector {
   MultipartModelSelector TRUE = (stateFactory_1) -> {
      return (blockState_1) -> {
         return true;
      };
   };
   MultipartModelSelector FALSE = (stateFactory_1) -> {
      return (blockState_1) -> {
         return false;
      };
   };

   Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> var1);
}

