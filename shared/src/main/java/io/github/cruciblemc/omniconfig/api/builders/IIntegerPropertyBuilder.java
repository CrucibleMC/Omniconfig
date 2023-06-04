package io.github.cruciblemc.omniconfig.api.builders;

import io.github.cruciblemc.omniconfig.api.properties.IIntegerProperty;

import java.util.function.Function;

public interface IIntegerPropertyBuilder extends IAbstractPropertyBuilder<IIntegerProperty, IIntegerPropertyBuilder> {

    /**
     * Specifies maximum value this property will be allowed to take.
     *
     * @param maxValue Maximal value of your choice.
     * @return This sub-builder instance.
     */
    IIntegerPropertyBuilder max(int maxValue);

    /**
     * Specifies minimum value this property will be allowed to take.
     *
     * @param minValue Minimal value of your choice.
     * @return This sub-builder instance.
     */
    IIntegerPropertyBuilder min(int minValue);

    /**
     * Specifies both minimum and maximum values thes property will
     * be allowed to take. Result of this invokation will be equal
     * to doing something like this:
     *
     * <pre>
     * propertyBuilder.min(-minMax);
     * propertyBuilder.max(minMax);
     * </pre>
     *
     * @param minMax Min-max bound of your choice.
     * @return This sub-builder instance.
     */
    IIntegerPropertyBuilder minMax(int minMax);

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    IIntegerPropertyBuilder validator(Function<Integer, Integer> validator);

}
