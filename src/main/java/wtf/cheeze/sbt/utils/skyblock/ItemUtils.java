/*
 * Copyright (C) 2025 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package wtf.cheeze.sbt.utils.skyblock;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemUtils {

    public static boolean isPickaxe(Item item) {
        return item.equals(Items.WOODEN_PICKAXE) ||
                item.equals(Items.STONE_PICKAXE) ||
                item.equals(Items.IRON_PICKAXE) ||
                item.equals(Items.GOLDEN_PICKAXE) ||
                item.equals(Items.DIAMOND_PICKAXE) ||
                item.equals(Items.NETHERITE_PICKAXE);
    }

    public static String getSkyblockId(ItemStack stack) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) return "";
        var customId = data.nbt.get("id");
        if (customId == null) return "";

        return customId.asString().orElse("");
    }

    public static String getReforge(ItemStack stack) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) return "";
        var reforge = data.nbt.get("modifier");
        if (reforge == null) return "";

        return reforge.asString().orElse("");
    }


   public static ItemStack getVanilla(String minecraftID) {
        return getVanilla(minecraftID, 1);
   }
    public static ItemStack getVanilla(String minecraftID, int count) {
        return new ItemStack(getRegistryEntry(minecraftID), count);
    }

    public static ItemStack getVanilla(Item item, int count) {
        return new ItemStack(item, count);
    }

    public static ItemStack getVanilla(Item item) {
        return new ItemStack(item, 1);
    }

    public static ItemStack getSkyblock(String minecraftID, String skyblockID) {
        return getSkyblock(minecraftID, skyblockID, false);
    }

    public static ItemStack getSkyblock(String minecraftID, String skyblockID, int count) {
        return getSkyblock(minecraftID, skyblockID, false, count);
    }

    public static ItemStack getSkyblock(String minecraftID, String skyblockID, boolean enchanted) {
        return getSkyblock(minecraftID, skyblockID, enchanted, 1);
    }



    public static ItemStack getSkyblock(String minecraftID, String skyblockID, boolean enchanted, int count) {
        return new ItemStack(
                getRegistryEntry(minecraftID),
                count,
                ComponentChanges.builder()
                        .add(DataComponentTypes.CUSTOM_DATA, getSkyblockItemNBT(skyblockID))
                        .add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchanted)
                        .build());
    }


    /**
     * Creates an item stack representing a player head with the given skull name and skyblock ID
     * @param skyblockID The skyblock ID of the item
     * @param enchanted Whether the item should have an enchantment glint
     * @param skullName The name of the skull texture, must be present in the {@code skullmap.json} file
     */
    public static ItemStack getHead(String skyblockID, boolean enchanted, String skullName) {
        return new ItemStack(
                getRegistryEntry("player_head"),
                1,
                ComponentChanges.builder()
                        .add(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.empty(), headProps(SkullMap.get(skullName))))
                        .add(DataComponentTypes.CUSTOM_DATA, getSkyblockItemNBT(skyblockID))
                        .add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchanted)
                        .build()
        );
    }
    public static ItemStack getHead(String skyblockID, String skullName) {
        return getHead(skyblockID, false, skullName);
    }

    public static ItemStack getHead(String both) {
        return getHead(both, false, both);
    }

    public static ItemStack justHead(String skullName) {
        return new ItemStack(
                getRegistryEntry("player_head"),
                1,
                ComponentChanges.builder()
                        .add(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.empty(), headProps(SkullMap.get(skullName))))
                        .build()
        );
    }

    private static PropertyMap headProps(String texture) {
        if (texture == null) {
            return new PropertyMap();
        }
        try {
            var props = new PropertyMap();
            props.put("textures", new Property("textures", texture));
            return props;
        } catch (Exception e) {
            ErrorHandler.handle(e, "Error while creating skull texture", ErrorLevel.WARNING);
            return new PropertyMap();
        }
    }


    private static NbtComponent getSkyblockItemNBT(String skyblockID) {
        var tag = new NbtCompound();
        tag.putString("id", skyblockID);
        return NbtComponent.of(tag);
    }

    private static RegistryEntry<Item> getRegistryEntry(String minecraftID) {
        return RegistryEntry.of(Registries.ITEM.get(Identifier.of(minecraftID)));
    }

    public static class SkullMap {
        private SkullMap() {}
        public Map<String, String> skulls = new HashMap<>();

        public static String get(String key) {
            return getInstance().skulls.get(key);
        }

        private static SkullMap INSTANCE;


        public static SkullMap getInstance() {
            if (INSTANCE == null) {
                try {
                    INSTANCE = SkyblockTweaks.GSON.fromJson(
                             new BufferedReader(new InputStreamReader(SkyblockTweaks.class.getResourceAsStream("/skullmap.json")))
                            , SkullMap.class);
                } catch (Exception e) {
                    ErrorHandler.handle(e, "Error while loading skull map", ErrorLevel.CRITICAL);
                    INSTANCE = new SkullMap();
                }
            }
            return INSTANCE;
        }
    }
}
