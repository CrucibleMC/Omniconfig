package io.github.cruciblemc.omniconfig.mc1_7_10;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.cruciblemc.omniconfig.OmniconfigCore;
import io.github.cruciblemc.omniconfig.api.lib.Environment;

import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name("omniconfig")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 1000)
public class OmniconfigCoremod implements IFMLLoadingPlugin {

    @Override
    public void injectData(Map<String, Object> data) {
        OmniconfigCore.INSTANCE.init((File) data.get("mcLocation"),
                FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT ? Environment.CLIENT : Environment.DEDICATED_SERVER);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
