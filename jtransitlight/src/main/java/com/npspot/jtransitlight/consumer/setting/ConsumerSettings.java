package com.npspot.jtransitlight.consumer.setting;

/**
 * Class provides the list of available consumer settings configured.
 */
public final class ConsumerSettings {

    private boolean durable = false;
    private boolean exclusive = false;
    private boolean autoDelete = false;
    private boolean autoAck = true;
    private Long messageTtl;
    private Long queueTtl;

    public ConsumerSettings() {
    }

    public ConsumerSettings(boolean durable) {
        this.durable = durable;
    }

    private ConsumerSettings(Builder builder) {
        setDurable(builder.durable);
        setExclusive(builder.exclusive);
        setAutoDelete(builder.autoDelete);
        setAutoAck(builder.autoAck);
        setMessageTtl(builder.messageTtl);
        setQueueTtl(builder.queueTtl);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public boolean isAutoAck() {
        return autoAck;
    }

    public void setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
    }

    public Long getMessageTtl() {
        return messageTtl;
    }

    public void setMessageTtl(Long messageTtl) {
        this.messageTtl = messageTtl;
    }

    public Long getQueueTtl() {
        return queueTtl;
    }

    public void setQueueTtl(Long queueTtl) {
        this.queueTtl = queueTtl;
    }


    public static final class Builder {
        private boolean durable = false;
        private boolean exclusive = false;
        private boolean autoDelete = false;
        private boolean autoAck = true;
        private Long messageTtl;
        private Long queueTtl;

        private Builder() {
        }

        public Builder withDurable(boolean val) {
            durable = val;
            return this;
        }

        public Builder withExclusive(boolean val) {
            exclusive = val;
            return this;
        }

        public Builder withAutoDelete(boolean val) {
            autoDelete = val;
            return this;
        }

        public Builder withAutoAck(boolean val) {
            autoAck = val;
            return this;
        }

        public Builder withMessageTtl(Long val) {
            messageTtl = val;
            return this;
        }

        public Builder withQueueTtl(Long val) {
            queueTtl = val;
            return this;
        }

        public ConsumerSettings build() {
            return new ConsumerSettings(this);
        }
    }

    @Override
    public String toString() {
        return "ConsumerSettings{" +
                "durable=" + durable +
                ", exclusive=" + exclusive +
                ", autoDelete=" + autoDelete +
                ", autoAck=" + autoAck +
                ", messageTtl=" + messageTtl +
                ", queueTtl=" + queueTtl +
                '}';
    }
}
