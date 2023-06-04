package io.github.cruciblemc.omniconfig.mc1_7_10;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.github.cruciblemc.omniconfig.Metadata;
import io.github.cruciblemc.omniconfig.mc1_7_10.handlers.EventHandler;
import io.github.cruciblemc.omniconfig.mc1_7_10.handlers.OmniPacketDispatcher;
import io.github.cruciblemc.omniconfig.mc1_7_10.network.PacketSyncOmniconfig;
import io.github.cruciblemc.omniconfig.OmniconfigCore;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Metadata.MOD_ID, name = Metadata.MOD_NAME, version = Metadata.VERSION, acceptableRemoteVersions = "*")
public class OmniconfigMod {
    public static SimpleNetworkWrapper packetPipeline;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OmniconfigCore.logger.info("Pre-initialization happens!");

        OmniPacketDispatcher.INSTANCE.getClass();

        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);

        packetPipeline = new SimpleNetworkWrapper(Metadata.MOD_ID);
        packetPipeline.registerMessage(PacketSyncOmniconfig.Handler.class, PacketSyncOmniconfig.class, 0, Side.CLIENT);
    }

}