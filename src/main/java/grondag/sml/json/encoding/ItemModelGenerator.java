package grondag.sml.json.encoding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ItemModelGenerator {
   public static final List<String> LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

   public JsonUnbakedModel create(Function<Identifier, Sprite> function_1, JsonUnbakedModel jsonUnbakedModel_1) {
      Map<String, String> map_1 = Maps.newHashMap();
      List<ModelElement> list_1 = Lists.newArrayList();

      for(int int_1 = 0; int_1 < LAYERS.size(); ++int_1) {
         String string_1 = (String)LAYERS.get(int_1);
         if (!jsonUnbakedModel_1.textureExists(string_1)) {
            break;
         }

         String string_2 = jsonUnbakedModel_1.resolveTexture(string_1);
         map_1.put(string_1, string_2);
         Sprite sprite_1 = (Sprite)function_1.apply(new Identifier(string_2));
         list_1.addAll(this.method_3480(int_1, string_1, sprite_1));
      }

      map_1.put("particle", jsonUnbakedModel_1.textureExists("particle") ? jsonUnbakedModel_1.resolveTexture("particle") : (String)map_1.get("layer0"));
      JsonUnbakedModel jsonUnbakedModel_2 = new JsonUnbakedModel((Identifier)null, list_1, map_1, false, false, jsonUnbakedModel_1.getTransformations(), jsonUnbakedModel_1.getOverrides());
      jsonUnbakedModel_2.id = jsonUnbakedModel_1.id;
      return jsonUnbakedModel_2;
   }

   private List<ModelElement> method_3480(int int_1, String string_1, Sprite sprite_1) {
      Map<Direction, ModelElementFace> map_1 = Maps.newHashMap();
      map_1.put(Direction.SOUTH, new ModelElementFace((Direction)null, int_1, string_1, new ModelElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      map_1.put(Direction.NORTH, new ModelElementFace((Direction)null, int_1, string_1, new ModelElementTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      List<ModelElement> list_1 = Lists.newArrayList();
      list_1.add(new ModelElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map_1, (ModelRotation)null, true));
      list_1.addAll(this.method_3481(sprite_1, string_1, int_1));
      return list_1;
   }

   private List<ModelElement> method_3481(Sprite sprite_1, String string_1, int int_1) {
      float float_1 = (float)sprite_1.getWidth();
      float float_2 = (float)sprite_1.getHeight();
      List<ModelElement> list_1 = Lists.newArrayList();
      Iterator var7 = this.method_3478(sprite_1).iterator();

      while(var7.hasNext()) {
         ItemModelGenerator.class_802 itemModelGenerator$class_802_1 = (ItemModelGenerator.class_802)var7.next();
         float float_3 = 0.0F;
         float float_4 = 0.0F;
         float float_5 = 0.0F;
         float float_6 = 0.0F;
         float float_7 = 0.0F;
         float float_8 = 0.0F;
         float float_9 = 0.0F;
         float float_10 = 0.0F;
         float float_11 = 16.0F / float_1;
         float float_12 = 16.0F / float_2;
         float float_13 = (float)itemModelGenerator$class_802_1.method_3487();
         float float_14 = (float)itemModelGenerator$class_802_1.method_3485();
         float float_15 = (float)itemModelGenerator$class_802_1.method_3486();
         ItemModelGenerator.class_803 itemModelGenerator$class_803_1 = itemModelGenerator$class_802_1.method_3484();
         switch(itemModelGenerator$class_803_1) {
         case UP:
            float_7 = float_13;
            float_3 = float_13;
            float_5 = float_8 = float_14 + 1.0F;
            float_9 = float_15;
            float_4 = float_15;
            float_6 = float_15;
            float_10 = float_15 + 1.0F;
            break;
         case DOWN:
            float_9 = float_15;
            float_10 = float_15 + 1.0F;
            float_7 = float_13;
            float_3 = float_13;
            float_5 = float_8 = float_14 + 1.0F;
            float_4 = float_15 + 1.0F;
            float_6 = float_15 + 1.0F;
            break;
         case LEFT:
            float_7 = float_15;
            float_3 = float_15;
            float_5 = float_15;
            float_8 = float_15 + 1.0F;
            float_10 = float_13;
            float_4 = float_13;
            float_6 = float_9 = float_14 + 1.0F;
            break;
         case RIGHT:
            float_7 = float_15;
            float_8 = float_15 + 1.0F;
            float_3 = float_15 + 1.0F;
            float_5 = float_15 + 1.0F;
            float_10 = float_13;
            float_4 = float_13;
            float_6 = float_9 = float_14 + 1.0F;
         }

         float_3 *= float_11;
         float_5 *= float_11;
         float_4 *= float_12;
         float_6 *= float_12;
         float_4 = 16.0F - float_4;
         float_6 = 16.0F - float_6;
         float_7 *= float_11;
         float_8 *= float_11;
         float_9 *= float_12;
         float_10 *= float_12;
         Map<Direction, ModelElementFace> map_1 = Maps.newHashMap();
         map_1.put(itemModelGenerator$class_803_1.method_3488(), new ModelElementFace((Direction)null, int_1, string_1, new ModelElementTexture(new float[]{float_7, float_9, float_8, float_10}, 0)));
         switch(itemModelGenerator$class_803_1) {
         case UP:
            list_1.add(new ModelElement(new Vector3f(float_3, float_4, 7.5F), new Vector3f(float_5, float_4, 8.5F), map_1, (ModelRotation)null, true));
            break;
         case DOWN:
            list_1.add(new ModelElement(new Vector3f(float_3, float_6, 7.5F), new Vector3f(float_5, float_6, 8.5F), map_1, (ModelRotation)null, true));
            break;
         case LEFT:
            list_1.add(new ModelElement(new Vector3f(float_3, float_4, 7.5F), new Vector3f(float_3, float_6, 8.5F), map_1, (ModelRotation)null, true));
            break;
         case RIGHT:
            list_1.add(new ModelElement(new Vector3f(float_5, float_4, 7.5F), new Vector3f(float_5, float_6, 8.5F), map_1, (ModelRotation)null, true));
         }
      }

      return list_1;
   }

   private List<ItemModelGenerator.class_802> method_3478(Sprite sprite_1) {
      int int_1 = sprite_1.getWidth();
      int int_2 = sprite_1.getHeight();
      List<ItemModelGenerator.class_802> list_1 = Lists.newArrayList();

      for(int int_3 = 0; int_3 < sprite_1.getFrameCount(); ++int_3) {
         for(int int_4 = 0; int_4 < int_2; ++int_4) {
            for(int int_5 = 0; int_5 < int_1; ++int_5) {
               boolean boolean_1 = !this.method_3477(sprite_1, int_3, int_5, int_4, int_1, int_2);
               this.method_3476(ItemModelGenerator.class_803.UP, list_1, sprite_1, int_3, int_5, int_4, int_1, int_2, boolean_1);
               this.method_3476(ItemModelGenerator.class_803.DOWN, list_1, sprite_1, int_3, int_5, int_4, int_1, int_2, boolean_1);
               this.method_3476(ItemModelGenerator.class_803.LEFT, list_1, sprite_1, int_3, int_5, int_4, int_1, int_2, boolean_1);
               this.method_3476(ItemModelGenerator.class_803.RIGHT, list_1, sprite_1, int_3, int_5, int_4, int_1, int_2, boolean_1);
            }
         }
      }

      return list_1;
   }

   private void method_3476(ItemModelGenerator.class_803 itemModelGenerator$class_803_1, List<ItemModelGenerator.class_802> list_1, Sprite sprite_1, int int_1, int int_2, int int_3, int int_4, int int_5, boolean boolean_1) {
      boolean boolean_2 = this.method_3477(sprite_1, int_1, int_2 + itemModelGenerator$class_803_1.method_3490(), int_3 + itemModelGenerator$class_803_1.method_3489(), int_4, int_5) && boolean_1;
      if (boolean_2) {
         this.method_3482(list_1, itemModelGenerator$class_803_1, int_2, int_3);
      }

   }

   private void method_3482(List<ItemModelGenerator.class_802> list_1, ItemModelGenerator.class_803 itemModelGenerator$class_803_1, int int_1, int int_2) {
      ItemModelGenerator.class_802 itemModelGenerator$class_802_1 = null;
      Iterator var6 = list_1.iterator();

      while(var6.hasNext()) {
         ItemModelGenerator.class_802 itemModelGenerator$class_802_2 = (ItemModelGenerator.class_802)var6.next();
         if (itemModelGenerator$class_802_2.method_3484() == itemModelGenerator$class_803_1) {
            int int_3 = itemModelGenerator$class_803_1.method_3491() ? int_2 : int_1;
            if (itemModelGenerator$class_802_2.method_3486() == int_3) {
               itemModelGenerator$class_802_1 = itemModelGenerator$class_802_2;
               break;
            }
         }
      }

      int int_4 = itemModelGenerator$class_803_1.method_3491() ? int_2 : int_1;
      int int_5 = itemModelGenerator$class_803_1.method_3491() ? int_1 : int_2;
      if (itemModelGenerator$class_802_1 == null) {
         list_1.add(new ItemModelGenerator.class_802(itemModelGenerator$class_803_1, int_5, int_4));
      } else {
         itemModelGenerator$class_802_1.method_3483(int_5);
      }

   }

   private boolean method_3477(Sprite sprite_1, int int_1, int int_2, int int_3, int int_4, int int_5) {
      return int_2 >= 0 && int_3 >= 0 && int_2 < int_4 && int_3 < int_5 ? sprite_1.isPixelTransparent(int_1, int_2, int_3) : true;
   }

   @Environment(EnvType.CLIENT)
   static class class_802 {
      private final ItemModelGenerator.class_803 field_4271;
      private int field_4274;
      private int field_4273;
      private final int field_4272;

      public class_802(ItemModelGenerator.class_803 itemModelGenerator$class_803_1, int int_1, int int_2) {
         this.field_4271 = itemModelGenerator$class_803_1;
         this.field_4274 = int_1;
         this.field_4273 = int_1;
         this.field_4272 = int_2;
      }

      public void method_3483(int int_1) {
         if (int_1 < this.field_4274) {
            this.field_4274 = int_1;
         } else if (int_1 > this.field_4273) {
            this.field_4273 = int_1;
         }

      }

      public ItemModelGenerator.class_803 method_3484() {
         return this.field_4271;
      }

      public int method_3487() {
         return this.field_4274;
      }

      public int method_3485() {
         return this.field_4273;
      }

      public int method_3486() {
         return this.field_4272;
      }
   }

   @Environment(EnvType.CLIENT)
   static enum class_803 {
      UP(Direction.UP, 0, -1),
      DOWN(Direction.DOWN, 0, 1),
      LEFT(Direction.EAST, -1, 0),
      RIGHT(Direction.WEST, 1, 0);

      private final Direction field_4276;
      private final int field_4280;
      private final int field_4279;

      private class_803(Direction direction_1, int int_1, int int_2) {
         this.field_4276 = direction_1;
         this.field_4280 = int_1;
         this.field_4279 = int_2;
      }

      public Direction method_3488() {
         return this.field_4276;
      }

      public int method_3490() {
         return this.field_4280;
      }

      public int method_3489() {
         return this.field_4279;
      }

      private boolean method_3491() {
         return this == DOWN || this == UP;
      }
   }
}
