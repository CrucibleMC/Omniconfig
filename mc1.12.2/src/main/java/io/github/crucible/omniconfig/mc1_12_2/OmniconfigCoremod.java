package io.github.crucible.omniconfig.mc1_12_2;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.lib.Environment;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name("omniconfig")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 1000)
public class OmniconfigCoremod implements IFMLLoadingPlugin {

    @Override
    public void injectData(Map<String, Object> data) {
        OmniconfigCore.logger.info("Initializing Omniconfig.");
        OmniconfigCore.INSTANCE.init((File) data.get("mcLocation"),
                FMLLaunchHandler.side() == net.minecraftforge.fml.relauncher.Side.CLIENT ? Environment.CLIENT : Environment.DEDICATED_SERVER);
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
