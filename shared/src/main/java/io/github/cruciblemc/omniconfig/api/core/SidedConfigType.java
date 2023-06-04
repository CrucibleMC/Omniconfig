package io.github.cruciblemc.omniconfig.api.core;

import io.github.cruciblemc.omniconfig.OmniconfigCore;
import io.github.cruciblemc.omniconfig.api.lib.Environment;
import org.jetbrains.annotations.Nullable;

/**
 * Reflects sided type for omniconfig files.
 * If config file has {@link SidedConfigType} different from {@link #COMMON},
 * it will only be generated in its respective environment, even if code is
 * executed in any environment. However, be aware that invoking any methods of
 * property instances associated with such configuration will throw
 * {@link IllegalAccessError} if such invokation happens in invalid environment.
 *
 * @author Aizistral
 */

public enum SidedConfigType {
    /**
     * Client-only configuration file.
     * Will never be generated on dedicated server.
     */
    CLIENT,

    /**
     * Server-only configuration file.
     * Will only ever be generated on dedicated server.
     */
    SERVER,

    /**
     * Common configuration file.
     * Not restricted to any particular environment.
     */
    COMMON;

    /**
     * @return True if this config type is restricted to particular
     * environment; false otherwise.
     */
    public boolean isSided() {
        return this != COMMON;
    }

    /**
     * @return Particular {@link Environment} this config type is
     * restricted to, or null if it is not restricted.
     */
    @Nullable
    public Environment getSide() {
        if (this == CLIENT)
            return Environment.CLIENT;
        else if (this == SERVER)
            return Environment.DEDICATED_SERVER;
        else
            return null;
    }

    /**
     * Executes supplied runnable if and only if this the environment
     * is valid for given config type.
     *
     * @param run Runnable to execute.
     */
    public void executeSided(Runnable run) {
        if (this.isSided()) {
            assert this.getSide() != null; // This will never be null, make the IDE shut up.
            OmniconfigCore.executeInEnvironment(this.getSide(), () -> run);
        } else {
            run.run();
        }
    }
}