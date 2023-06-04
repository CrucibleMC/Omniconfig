package io.github.cruciblemc.omniconfig.mc1_12_2.handlers;

import com.google.common.base.Charsets;
import io.github.cruciblemc.omniconfig.api.OmniconfigAPI;
import io.github.cruciblemc.omniconfig.api.lib.Environment;
import io.github.cruciblemc.omniconfig.core.AbstractPacketDispatcher;
import io.github.cruciblemc.omniconfig.core.Omniconfig;
import io.github.cruciblemc.omniconfig.core.SynchronizationManager;
import io.github.cruciblemc.omniconfig.mc1_12_2.OmniconfigMod;
import io.github.cruciblemc.omniconfig.mc1_12_2.handlers.OmniPacketDispatcher.OmniPlayerMP;
import io.github.cruciblemc.omniconfig.mc1_12_2.network.PacketSyncOmniconfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.function.Consumer;

public class OmniPacketDispatcher extends AbstractPacketDispatcher<ByteBuf, OmniPlayerMP> {
    public static final OmniPacketDispatcher INSTANCE = new OmniPacketDispatcher();

    private OmniPacketDispatcher() {
        SynchronizationManager.setPacketDispatcher(this);
    }

    @Override
    public OmniBufferIO getBufferIO(ByteBuf buffer) {
        return new OmniBufferIO(buffer);
    }

    @Override
    public OmniServer getServer() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return server != null && server.getPlayerList() != null ? new OmniServer(server) : null;
    }

    public static class OmniServer extends AbstractPacketDispatcher.AbstractServer<MinecraftServer, OmniPlayerMP> {
        public OmniServer(MinecraftServer server) {
            super(server);
        }

        @Override
        public void forEachPlayer(Consumer<OmniPlayerMP> consumer) {
            this.server.getPlayerList().getPlayers().forEach(player -> {
                consumer.accept(new OmniPlayerMP(player));
            });
        }
    }

    public static class OmniPlayerMP extends AbstractPacketDispatcher.AbstractPlayerMP<EntityPlayerMP> {
        public OmniPlayerMP(EntityPlayerMP player) {
            super(player);
        }

        @Override
        public void sendSyncPacket(Omniconfig wrapper) {
            OmniconfigMod.packetPipeline.sendTo(new PacketSyncOmniconfig(wrapper), this.player);
        }

        @Override
        public boolean areWeRemoteServer() {
            if (OmniconfigAPI.getEnvironment() == Environment.DEDICATED_SERVER)
                return true;
            else
                return this.player.mcServer != null && !this.player.getGameProfile().getName().equals(this.player.mcServer.getServerOwner());
        }

        @Override
        public String getProfileName() {
            return this.player.getGameProfile().getName();
        }
    }

    public static class OmniBufferIO extends AbstractPacketDispatcher.AbstractBufferIO<ByteBuf> {
        protected OmniBufferIO(ByteBuf buffer) {
            super(buffer);
        }

        @Override
        public void writeString(String string, int charLimit) {
            if (string != null && string.length() > 0 && string.length() <= charLimit) {
                byte[] bytes = string.getBytes(Charsets.UTF_8);
                this.buffer.writeShort(bytes.length);
                this.buffer.writeBytes(bytes);
            } else {
                if (string != null && string.length() >= charLimit)
                    throw new RuntimeException(new IOException("Encoded string size " + string.length() + " is larger than allowed maximum " + charLimit));
                else {
                    this.buffer.writeShort(-1);
                }
            }
        }

        @Override
        public void writeLong(long value) {
            this.buffer.writeLong(value);
        }

        @Override
        public void writeInt(int value) {
            this.buffer.writeInt(value);
        }

        @Override
        public void writeDouble(double value) {
            this.buffer.writeDouble(value);
        }

        @Override
        public void writeFloat(float value) {
            this.buffer.writeFloat(value);
        }

        @Override
        public String readString(int charLimit) {
            short size = this.buffer.readShort();

            if (size < 0)
                return "null";

            byte[] strBytes = new byte[size];
            this.buffer.readBytes(strBytes);
            String str = new String(strBytes, Charsets.UTF_8);

            if (str.length() <= charLimit)
                return str;
            else
                throw new RuntimeException(new IOException("Received string size " + str.length() + " is larger than allowed maximum " + charLimit));
        }

        @Override
        public long readLong() {
            return this.buffer.readLong();
        }

        @Override
        public int readInt() {
            return this.buffer.readInt();
        }

        @Override
        public double readDouble() {
            return this.buffer.readDouble();
        }

        @Override
        public float readFloat() {
            return this.buffer.readFloat();
        }

    }

}
