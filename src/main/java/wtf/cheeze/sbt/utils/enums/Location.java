/*
 * Copyright (C) 2024 MisterCheezeCake
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
package wtf.cheeze.sbt.utils.enums;


import com.google.common.collect.ImmutableMap;
import com.ibm.icu.impl.locale.XCldrStub;
import net.minecraft.item.Items;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Inspired by Skyblocker's Location enum
public enum Location {
    PRIVATE_ISLAND("dynamic", "Private Location"),
    GARDEN("garden", "The Garden"),
    HUB("hub", "The Hub"),
    FARMING("farming_1", "The Barn"),
    PARK("foraging_1", "The Park"),
    SPIDERS_DEN("combat_1", "The Spider's Den"),
    END("combat_3", "The End"),
    CRIMSON_ISLE("crimson_isle", "The Crimson Isle"),
    GOLD_MINE("mining_1", "The Gold Mine"),
    DEEP_CAVERNS("mining_2", "The Deep Caverns"),
    DWARVEN_MINES("mining_3", "The Dwarven Mines"),
    CRYSTAL_HOLLOWS("crystal_hollows", "The Crystal Hollows"),
    GLACITE_MINESHAFT("mineshaft", "Glacite Mineshaft"),
    DUNGEON_HUB("dungeon_hub", "The Dungeon Hub"),
    JERRYS_WORKSHOP("winter", "Jerry's Workshop"),
    RIFT("rift", "The Rift"),
    DARK_AUCTION("dark_auction", "The Dark Auction"),
    DUNGEON("dungeon", "Dungeons"),
    KUUDRA("kuudra", "Kuudra"),
    BACKWATER_BAYOU("fishing_1", "Backwater Bayou"),
    GALATEA("foraging_2", "Galatea"),
    UNKNOWN("unknown", "Unknown Location");

    Location(String mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    private final String mode;
    private final String name;

    public String getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, Location> BY_MODE;

    static {
        BY_MODE = new HashMap<>();
        for (Location location : values()) {
            BY_MODE.put(location.mode.toLowerCase(), location);
        }
    }

    public static Location fromMode(String mode) {
        return BY_MODE.getOrDefault(mode.toLowerCase(), UNKNOWN);
    }
}
