package io.github.crucible.omniconfig.api.lib;


import io.github.crucible.omniconfig.api.OmniconfigAPI;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

/**
 * Version-independent representation of sided Minecraft environment.
 *
 * @author Aizistral
 */

public enum Environment {

    /**
     * The client side. Specifically, an environment where rendering capability exists.
     * Usually in the game client.
     */
    CLIENT,

    /**
     * The server side. Specifically, an environment where NO rendering capability exists.
     * Usually on the dedicated server.
     */
    DEDICATED_SERVER;

    /**
     * @return If this is the server environment
     */
    public boolean isServer() {
        return !this.isClient();
    }

    /**
     * @return if this is the Client environment
     */
    public boolean isClient() {
        return this == CLIENT;
    }

    /**
     * Execute some arbitrary code if we are in this specific environment.
     * Double lamda wrapping to avoid classloading the target.
     *
     * @param code Your code.
     */

    public void execute(Supplier<Runnable> code) {
        if (OmniconfigAPI.getEnvironment() == this) {
            code.get().run();
        }
    }

    /**
     * If we are on dedicated server, constructs the <code>commonClass</code> and returns the instance;
     * otherwise constructs and returns the instance of <code>clientClass</code>. Both classes must have
     * a constructor that accepts no arguments; its visibility however does not matter.<br><br>
     * This can be used as more flexible replacement for Forge's <code>@SidedProxy</code>, as this method
     * is version-independent and allows to assign result to <code>final</code> field.
     *
     * @param commonClass Server-sided proxy class.
     * @param clientClass Client-sided proxy class.
     * @return Whatever we managed to instantiate.
     */

    public static <T> T createProxy(Class<T> commonClass, Class<? extends T> clientClass) {
        try {
            Class<? extends T> chosenClass = OmniconfigAPI.getEnvironment() == DEDICATED_SERVER ? commonClass : clientClass;
            Constructor<? extends T> constructor = chosenClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

}
