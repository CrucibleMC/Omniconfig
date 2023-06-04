package io.github.cruciblemc.omniconfig.core.properties;

import io.github.cruciblemc.omniconfig.api.builders.IBooleanPropertyBuilder;
import io.github.cruciblemc.omniconfig.api.properties.IBooleanProperty;
import io.github.cruciblemc.omniconfig.backing.Configuration;
import io.github.cruciblemc.omniconfig.core.Omniconfig;

import java.util.function.Function;

public class BooleanParameter extends AbstractParameter<IBooleanProperty> implements IBooleanProperty {
    protected final boolean defaultValue;
    protected final Function<Boolean, Boolean> validator;
    protected boolean value;

    public BooleanParameter(Builder builder) {
        super(builder);

        this.defaultValue = builder.defaultValue;
        this.validator = builder.validator;

        this.finishConstruction(builder);
    }

    @Override
    public boolean getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    @Override
    public boolean getDefault() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized());
        if (this.validator != null) {
            config.pushValidator(this.validator);
        }
        this.value = config.getBoolean(this.name, this.category, this.defaultValue, this.comment);
    }

    @Override
    public String valueToString() {
        return Boolean.toString(this.value);
    }

    @Override
    public void parseFromString(String value) {
        try {
            this.value = Boolean.parseBoolean(value);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }

    @Override
    protected boolean valueMatchesDefault(Configuration inConfig) {
        this.load(inConfig);
        return this.value == this.defaultValue;
    }

    @Override
    protected boolean valuesMatchIn(Configuration one, Configuration two) {
        this.load(one);
        boolean valueOne = this.value;
        this.load(two);

        return valueOne == this.value;
    }

    public static Builder builder(Omniconfig.Builder parent, String name, boolean defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<IBooleanProperty, Builder> implements IBooleanPropertyBuilder {
        protected final boolean defaultValue;
        protected Function<Boolean, Boolean> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, boolean defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        @Override
        public Builder validator(Function<Boolean, Boolean> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public BooleanParameter build() {
            this.finishBuilding();
            return new BooleanParameter(this);
        }
    }

}