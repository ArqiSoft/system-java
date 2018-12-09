package com.npspot.jtransitlight.publisher.model;


/**
 * Class provides the list of available supplier settings configured.
 */
public class SupplierSettings {

    private boolean persistent = false;

    public SupplierSettings() {
    }

    private SupplierSettings(Builder builder) {
        setPersistent(builder.persistent);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }


    public static final class Builder {
        private boolean persistent = false;

        private Builder() {
        }

        public Builder withPersistent(boolean val) {
            persistent = val;
            return this;
        }

        public SupplierSettings build() {
            return new SupplierSettings(this);
        }
    }

    @Override
    public String toString() {
        return "SupplierSettings{" +
                "persistent=" + persistent +
                '}';
    }
}
