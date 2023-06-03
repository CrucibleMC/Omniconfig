package io.github.crucible.omniconfig.api.builders;

import io.github.crucible.omniconfig.api.properties.IDoubleProperty;

import java.util.function.Function;

/**
 * Specialized sub-builder for {@link IDoubleProperty}.
 *
 * @author Aizistral
 */

public interface IDoublePropertyBuilder extends IAbstractPropertyBuilder<IDoubleProperty, IDoublePropertyBuilder> {

    /**
     * Specifies maximum value this property will be allowed to take.
     *
     * @param maxValue Maximal value of your choice.
     * @return This sub-builder instance.
     */
    IDoublePropertyBuilder max(double maxValue);

    /**
     * Specifies minimum value this property will be allowed to take.
     *
     * @param minValue Minimal value of your choice.
     * @return This sub-builder instance.
     */
    IDoublePropertyBuilder min(double minValue);

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
    IDoublePropertyBuilder minMax(double minMax);

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    IDoublePropertyBuilder validator(Function<Double, Double> validator);

}
