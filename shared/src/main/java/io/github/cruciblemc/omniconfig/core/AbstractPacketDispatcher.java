package io.github.cruciblemc.omniconfig.core;

import io.github.cruciblemc.omniconfig.core.AbstractPacketDispatcher.AbstractPlayerMP;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AbstractPacketDispatcher<T, E extends AbstractPlayerMP<?>> {

    public abstract AbstractBufferIO<T> getBufferIO(T buffer);

    @Nullable
    public abstract AbstractServer<?, E> getServer();

    public static abstract class AbstractPlayerMP<T> {
        protected final T player;

        public AbstractPlayerMP(T player) {
            this.player = player;
        }

        public T get() {
            return this.player;
        }

        public abstract void sendSyncPacket(Omniconfig wrapper);

        public abstract boolean areWeRemoteServer();

        public abstract String getProfileName();
    }

    public static abstract class AbstractServer<T, E extends AbstractPlayerMP<?>> {
        protected final T server;

        public AbstractServer(T server) {
            this.server = server;
        }

        public T get() {
            return this.server;
        }

        public abstract void forEachPlayer(Consumer<E> consumer);
    }

    public static abstract class AbstractBufferIO<T> {
        protected final T buffer;

        protected AbstractBufferIO(T buffer) {
            this.buffer = buffer;
        }

        public abstract void writeString(String string, int charLimit);

        public abstract void writeLong(long value);

        public abstract void writeInt(int value);

        public abstract void writeDouble(double value);

        public abstract void writeFloat(float value);

        public abstract String readString(int charLimit);

        public abstract long readLong();

        public abstract int readInt();

        public abstract double readDouble();

        public abstract float readFloat();
    }

}
