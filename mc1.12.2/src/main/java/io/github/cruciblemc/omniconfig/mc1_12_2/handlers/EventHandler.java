package io.github.cruciblemc.omniconfig.mc1_12_2.handlers;

import io.github.cruciblemc.omniconfig.core.SynchronizationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            /*
             * This event seems to be the only way to detect player logging out of server
             * on client side, since PlayerEvent.PlayerLoggedOutEvent seems to only be fired
             * on the server side.
             */

            if (player == null) {
                SynchronizationManager.dropRemoteConfigs();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            SynchronizationManager.syncAllToPlayer(new OmniPacketDispatcher.OmniPlayerMP((EntityPlayerMP) event.player));
        }
    }

}
