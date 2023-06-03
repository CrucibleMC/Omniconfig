package io.github.crucible.omniconfig.mc1_7_10.handlers;

import java.io.IOException;
import java.util.function.Consumer;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLCommonHandler;
import io.github.crucible.omniconfig.mc1_7_10.OmniconfigMod;
import io.github.crucible.omniconfig.mc1_7_10.handlers.OmniPacketDispatcher.OmniPlayerMP;
import io.github.crucible.omniconfig.mc1_7_10.network.PacketSyncOmniconfig;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.lib.Environment;
import io.github.crucible.omniconfig.core.AbstractPacketDispatcher;
import io.github.crucible.omniconfig.core.Omniconfig;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
        return server != null && server.getConfigurationManager() != null ? new OmniServer(server) : null;
    }

    public static class OmniServer extends AbstractPacketDispatcher.AbstractServer<MinecraftServer, OmniPlayerMP> {
        public OmniServer(MinecraftServer server) {
            super(server);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void forEachPlayer(Consumer<OmniPlayerMP> consumer) {
            this.server.getConfigurationManager().playerEntityList.forEach(player -> {
                if (player instanceof EntityPlayerMP) {
                    consumer.accept(new OmniPlayerMP((EntityPlayerMP) player));
                }
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
                if (string != null && string.length() >= charLimit) {
                    Throwables.propagate(new IOException("Encoded string size " + string.length() + " is larger than allowed maximum " + charLimit));
                } else {
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
            else {
                Throwables.propagate(new IOException("Received string size " + str.length() + " is larger than allowed maximum " + charLimit));
            }

            return "null";
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
