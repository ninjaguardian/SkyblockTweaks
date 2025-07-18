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
package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.hud.bases.BarHud;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class HealthBar extends BarHud {

    public static final HealthBar INSTANCE = new HealthBar();

    private HealthBar() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().healthBar.x,
                () -> SBTConfig.huds().healthBar.y,
                () -> SBTConfig.huds().healthBar.scale,
                () -> SBTConfig.huds().healthBar.anchor,
                x -> SBTConfig.huds().healthBar.x = x,
                y -> SBTConfig.huds().healthBar.y = y,
                scale -> SBTConfig.huds().healthBar.scale = scale,
                anchor ->SBTConfig.huds().healthBar.anchor= anchor
        );
    }


    @Override
    public int getColor() {
        return SkyblockData.Stats.health > SkyblockData.Stats.maxHealth ? SBTConfig.huds().healthBar.colorAbsorption : SBTConfig.huds().healthBar.color;
    }

    @Override
    public float getFill() {
        return SkyblockData.Stats.health / SkyblockData.Stats.maxHealth;
    }

    @Override
    public @NotNull HudName getName() {
        return new HudName("Health Bar", "HP Bar", Colors.RED);
    }


    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().healthBar.enabled && (SkyblockData.location != Location.RIFT || !SBTConfig.huds().healthBar.hideInRift)) || fromHudScreen;
    }


    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.15f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.RED;

        @SerialEntry
        public int colorAbsorption = Colors.ORANGE;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public boolean hideInRift = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("healthBar.enabled"))
                    .description(keyD("healthBar.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.healthBar.enabled,
                            () -> config.huds.healthBar.enabled,
                            value -> config.huds.healthBar.enabled = value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("healthBar.color"))
                    .description(keyD("healthBar.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.healthBar.color),
                            () ->  new Color(config.huds.healthBar.color),
                            value -> config.huds.healthBar.color = value.getRGB()

                    )
                    .build();
            var absorbColor = Option.<Color>createBuilder()
                    .name(key("healthBar.colorAbsorption"))
                    .description(keyD("healthBar.colorAbsorption"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.healthBar.colorAbsorption),
                            () ->  new Color(config.huds.healthBar.colorAbsorption),
                            value -> config.huds.healthBar.colorAbsorption = value.getRGB()

                    )
                    .build();
            var rift = Option.<Boolean>createBuilder()
                    .name(key("healthBar.hideInRift"))
                    .description(keyD("healthBar.hideInRift"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.healthBar.hideInRift,
                            () -> config.huds.healthBar.hideInRift,
                            value -> config.huds.healthBar.hideInRift = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("healthBar.scale"))
                    .description(keyD("healthBar.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.healthBar.scale,
                            () -> config.huds.healthBar.scale,
                            value -> config.huds.healthBar.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("healthBar"))
                    .description(keyD("healthBar"))
                    .option(enabled)
                    .option(rift)
                    .option(color)
                    .option(absorbColor)
                    .option(scale)
                    .collapsed(true)
                    .build();


        }
    }
}
