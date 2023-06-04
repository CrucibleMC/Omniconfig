package io.github.cruciblemc.omniconfig.mc1_12_2;

import io.github.cruciblemc.omniconfig.Metadata;
import io.github.cruciblemc.omniconfig.mc1_12_2.handlers.EventHandler;
import io.github.cruciblemc.omniconfig.mc1_12_2.handlers.OmniPacketDispatcher;
import io.github.cruciblemc.omniconfig.mc1_12_2.network.PacketSyncOmniconfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Metadata.MOD_ID, name = Metadata.MOD_NAME, version = Metadata.VERSION, acceptableRemoteVersions = "*")
public class OmniconfigMod {
    public static SimpleNetworkWrapper packetPipeline;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OmniPacketDispatcher.INSTANCE.getClass();

        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);

        packetPipeline = new SimpleNetworkWrapper(Metadata.MOD_ID);
        packetPipeline.registerMessage(PacketSyncOmniconfig.Handler.class, PacketSyncOmniconfig.class, 0, Side.CLIENT);
    }

}
