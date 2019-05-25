package grondag.sml.json.encoding;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;

@Environment(EnvType.CLIENT)
public class AndMultipartModelSelector implements MultipartModelSelector {
   private final Iterable<? extends MultipartModelSelector> selectors;

   public AndMultipartModelSelector(Iterable<? extends MultipartModelSelector> iterable_1) {
      this.selectors = iterable_1;
   }

   @Override
public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory_1) {
      List<Predicate<BlockState>> list_1 = (List<Predicate<BlockState>>)Streams.stream(this.selectors).map((multipartModelSelector_1) -> {
         return multipartModelSelector_1.getPredicate(stateFactory_1);
      }).collect(Collectors.toList());
      return (blockState_1) -> {
         return list_1.stream().allMatch((predicate_1) -> {
            return predicate_1.test(blockState_1);
         });
      };
   }
}
