package io.github.cruciblemc.omniconfig.api.builders;

import io.github.cruciblemc.omniconfig.api.properties.IEnumProperty;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public interface IEnumPropertyBuilder<T extends Enum<T>> extends IAbstractPropertyBuilder<IEnumProperty<T>, IEnumPropertyBuilder<T>> {

    /**
     * Specifies list of valid enum values this property can contain one of.
     *
     * @param values Valid values.
     * @return This sub-builder instance.
     */
    IEnumPropertyBuilder<T> validValues(T... values);

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    IEnumPropertyBuilder<T> validator(Function<T, T> validator);

}