package io.github.cruciblemc.omniconfig.api.annotation.properties;

import io.github.cruciblemc.omniconfig.api.OmniconfigConstants;
import io.github.cruciblemc.omniconfig.api.properties.IAbstractProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Decorate static {@link String} field with this annotation
 * to mark it as annotation config property. Default value of property
 * will be equal to value annotated field initially has.
 *
 * @author Aizistral
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigString {

    /**
     * @return Name of this property in config file.
     * If not specified, field name will be used instead.
     */
    String name() default "";

    /**
     * @return Config category this property should belong to.
     * It is allowed to specify subcategory hierarchy by separating
     * category names with {@link OmniconfigConstants#CATEGORY_SPLITTER}.
     */
    String category() default OmniconfigConstants.GENERAL_CATEGORY;

    /**
     * @return Human-readable comment describing the purpose
     * of this config property.
     */
    String comment() default OmniconfigConstants.DEFAULT_COMMENT;

    /**
     * @return Whether or not this config property should be synchronized
     * from client to server.
     * @see IAbstractProperty#isSynchronized()
     */
    boolean sync() default false;

}
