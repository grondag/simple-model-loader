package grondag.sml.json.encoding;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;

@Environment(EnvType.CLIENT)
public class SimpleMultipartModelSelector implements MultipartModelSelector {
    private static final Splitter VALUE_SPLITTER = Splitter.on('|').omitEmptyStrings();
    private final String key;
    private final String valueString;

    public SimpleMultipartModelSelector(String string_1, String string_2) {
        this.key = string_1;
        this.valueString = string_2;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory_1) {
        Property<?> property_1 = stateFactory_1.getProperty(this.key);
        if (property_1 == null) {
            throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, ((Block)stateFactory_1.getBaseObject()).toString()));
        } else {
            String string_1 = this.valueString;
            boolean boolean_1 = !string_1.isEmpty() && string_1.charAt(0) == '!';
            if (boolean_1) {
                string_1 = string_1.substring(1);
            }

            List<String> list_1 = VALUE_SPLITTER.splitToList(string_1);
            if (list_1.isEmpty()) {
                throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.valueString, this.key, ((Block)stateFactory_1.getBaseObject()).toString()));
            } else {
                Predicate<BlockState> predicate_2;
                if (list_1.size() == 1) {
                    predicate_2 = this.createPredicate(stateFactory_1, property_1, string_1);
                } else {
                    List<Predicate<BlockState>> list_2 = (List<Predicate<BlockState>>)list_1.stream().map((string_1x) -> {
                        return this.createPredicate(stateFactory_1, property_1, string_1x);
                    }).collect(Collectors.toList());
                    predicate_2 = (blockState_1) -> {
                        return list_2.stream().anyMatch((predicate_1) -> {
                            return predicate_1.test((BlockState) blockState_1);
                        });
                    };
                }

                return boolean_1 ? predicate_2.negate() : predicate_2;
            }
        }
    }

    private Predicate<BlockState> createPredicate(StateFactory<Block, BlockState> stateFactory_1, Property<?> property_1, String string_1) {
        Optional<?> optional_1 = property_1.getValue(string_1);
        if (!optional_1.isPresent()) {
            throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", string_1, this.key, ((Block)stateFactory_1.getBaseObject()).toString(), this.valueString));
        } else {
            return (blockState_1) -> {
                return blockState_1.get(property_1).equals(optional_1.get());
            };
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.valueString).toString();
    }
}
