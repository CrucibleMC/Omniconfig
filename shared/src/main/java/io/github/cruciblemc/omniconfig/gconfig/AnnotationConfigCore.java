package io.github.cruciblemc.omniconfig.gconfig;

import io.github.cruciblemc.omniconfig.OmniconfigCore;
import io.github.cruciblemc.omniconfig.api.annotation.IAnnotationConfigRegistry;
import io.github.cruciblemc.omniconfig.api.core.IOmniconfig;

import java.util.*;

public class AnnotationConfigCore implements IAnnotationConfigRegistry {
    public static final AnnotationConfigCore INSTANCE = new AnnotationConfigCore();
    private final Map<Class<?>, IOmniconfig> annotationConfigMap = new HashMap<>();

    private AnnotationConfigCore() {
        // NO-OP
    }

    public void addAnnotationConfig(Class<?> configClass) {
        if (!this.annotationConfigMap.containsKey(configClass)) {
            OmniconfigCore.logger.info("Registering annotation config class: " + configClass);
            this.annotationConfigMap.put(configClass, new AnnotationConfigReader(configClass).read());
        } else
            throw new IllegalArgumentException("Annotation config class " + configClass + "was already registered!");
    }

    @Override
    public Collection<Class<?>> getRegisteredAnnotationConfigs() {
        return Collections.unmodifiableCollection(this.annotationConfigMap.keySet());
    }

    @Override
    public Optional<IOmniconfig> getAssociatedOmniconfig(Class<?> annotationConfigClass) {
        return Optional.ofNullable(this.annotationConfigMap.get(annotationConfigClass));
    }


}
