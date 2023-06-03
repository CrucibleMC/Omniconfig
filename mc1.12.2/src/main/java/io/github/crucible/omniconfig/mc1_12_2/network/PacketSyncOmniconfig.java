package io.github.crucible.omniconfig.mc1_12_2.network;

import java.util.Objects;

import javax.annotation.Nonnull;

import io.github.crucible.omniconfig.mc1_12_2.handlers.OmniPacketDispatcher;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.lib.Either;
import io.github.crucible.omniconfig.core.Omniconfig;
import io.github.crucible.omniconfig.core.OmniconfigRegistry;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.SynchronizationManager.SyncData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncOmniconfig implements IMessage {
    private final OmniPacketDispatcher dispatcher = OmniPacketDispatcher.INSTANCE;
    private Either<Omniconfig, SyncData> either;

    public PacketSyncOmniconfig(@Nonnull Omniconfig wrapper) {
        this.either = Either.fromFirst(Objects.requireNonNull(wrapper));
    }

    public PacketSyncOmniconfig() {
        // NO-OP
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.either = Either.fromSecond(SynchronizationManager.readData(this.dispatcher.getBufferIO(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        SynchronizationManager.writeData(this.either.getFirst(), this.dispatcher.getBufferIO(buf));
    }

    public static class Handler implements IMessageHandler<PacketSyncOmniconfig, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSyncOmniconfig message, MessageContext ctx) {
            OmniconfigCore.onRemoteServer = true;

            message.either.ifSecond(data ->
            OmniconfigRegistry.INSTANCE.getConfig(data.getFileID()).ifPresent(wrapper ->
            SynchronizationManager.updateData((Omniconfig) wrapper, data)));

            return null;
        }
    }

}


