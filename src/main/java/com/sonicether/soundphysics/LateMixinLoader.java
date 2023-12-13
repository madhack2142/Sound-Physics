package com.sonicether.soundphysics;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LateMixinLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("soundphysics.gliby.mixin.json", "soundphysics.comp.mixin.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        switch (mixinConfig) {
            case "soundphysics.gliby.mixin.json":
                return Loader.isModLoaded("gvc") && Config.glibyVCPatching;
            case "soundphysics.glibysrc.mixin.json":
                return Loader.isModLoaded("gvc") && Config.glibyVCSrcPatching;
            case "soundphysics.comp.mixin.json":
                return Loader.isModLoaded("computronics") && Config.computronicsPatching;
            case "soundphysics.umc.mixin.json":
                return Loader.isModLoaded("universalmodcore") && Config.irPatching;
            case "soundphysics.midnight.mixin.json":
                return Loader.isModLoaded("midnight") && Config.midnightPatching;
            default: return ILateMixinLoader.super.shouldMixinConfigQueue(mixinConfig);
        }
    }
}
