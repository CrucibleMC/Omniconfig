package io.github.cruciblemc.omniconfig.api;

import io.github.cruciblemc.omniconfig.OmniconfigCore;
import io.github.cruciblemc.omniconfig.api.annotation.AnnotationConfig;
import io.github.cruciblemc.omniconfig.api.annotation.IAnnotationConfigRegistry;
import io.github.cruciblemc.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.cruciblemc.omniconfig.api.core.IOmniconfigRegistry;
import io.github.cruciblemc.omniconfig.api.core.SidedConfigType;
import io.github.cruciblemc.omniconfig.api.lib.Environment;
import io.github.cruciblemc.omniconfig.api.lib.Version;
import io.github.cruciblemc.omniconfig.core.Omniconfig;
import io.github.cruciblemc.omniconfig.core.OmniconfigRegistry;
import io.github.cruciblemc.omniconfig.gconfig.AnnotationConfigCore;

import java.io.File;

/**
 * The heart and core of Omniconfig API.
 *
 * @author Aizistral
 */

public class OmniconfigAPI {

    private OmniconfigAPI() {
        // Can't touch this
    }

    /**
     * Start new {@link IOmniconfigBuilder}, with {@link SidedConfigType#COMMON} and
     * version equal to <code>new {@link Version}("1.0.0")</code>.
     *
     * @see #configBuilder(String, Version, SidedConfigType)
     */
    public static IOmniconfigBuilder configBuilder(String fileName) {
        return Omniconfig.builder(OmniconfigCore.INSTANCE.sanitizeName(fileName), new Version("1.0.0"), true, SidedConfigType.COMMON);
    }

    /**
     * Start new {@link IOmniconfigBuilder}, with sided type {@link SidedConfigType#COMMON}.
     *
     * @see #configBuilder(String, Version, SidedConfigType)
     */
    public static IOmniconfigBuilder configBuilder(String fileName, Version version) {
        return Omniconfig.builder(OmniconfigCore.INSTANCE.sanitizeName(fileName), version, true, SidedConfigType.COMMON);
    }

    /**
     * Start new {@link IOmniconfigBuilder}.
     *
     * @param fileName  Name of config file. Suffix ".cfg" is optional, if not specified it will
     *                  automatically be added to file name. Subdirectories are allowed; for instance, you can
     *                  specify <code>specialmod/Configuration.cfg</code> to have you config inside
     *                  <code>config/specialmod</code> folder in Minecraft directory.
     * @param version   Current version of your file.
     * @param sidedType Sided type.
     * @return New {@link IOmniconfigBuilder}.
     * @see SidedConfigType
     */
    public static IOmniconfigBuilder configBuilder(String fileName, Version version, SidedConfigType sidedType) {
        return Omniconfig.builder(OmniconfigCore.INSTANCE.sanitizeName(fileName), version, true, sidedType);
    }

    /**
     * Register class as annotation config.<br>
     * Annotation config class must be decorated with {@link AnnotationConfig} annotation,
     * and is generally anticipated to contain one or more non-final static fields decorated with
     * annotations from {@link io.github.crucible.omniconfig.api.annotation.properties} package.<br>
     * {@link IOmniconfigBuilder} will automatically be created for registered class, using data provided
     * by annotations to build appropriate config file, and fill in field values once it is loaded.
     *
     * @param annotationConfigClass Class to register as annotation config.
     */
    public static void registerAnnotationConfig(Class<?> annotationConfigClass) {
        AnnotationConfigCore.INSTANCE.addAnnotationConfig(annotationConfigClass);
    }

    /**
     * @return {@link IOmniconfigRegistry}, API interface for interacting with Omniconfig registry.
     */
    public static IOmniconfigRegistry getOmniconfigRegistry() {
        return OmniconfigRegistry.INSTANCE;
    }

    /**
     * @return {@link IAnnotationConfigRegistry}, API interface for interacting specifically
     * with registry of annotation config classes.
     */
    public static IAnnotationConfigRegistry getAnnotationConfigRegistry() {
        return AnnotationConfigCore.INSTANCE;
    }

    /**
     * @return Default directory for configuration files. Normally this will be
     * <code>.minecraft/config</code>
     */
    public static File getConfigFolder() {
        return OmniconfigCore.INSTANCE.getConfigFolder();
    }

    /**
     * Serves as version-independent way to verify which sided Minecraft
     * environment we currently reside within.
     *
     * @return Current {@link Environment} we are in.
     * @see Environment
     */
    public static Environment getEnvironment() {
        return OmniconfigCore.INSTANCE.getEnvironment();
    }
}