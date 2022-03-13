package org.horus.entitybuilder;

import org.horus.rejection.Cause;
import org.horus.rejection.Rejection;
import org.horus.rejection.RejectionConsumer;
import org.slf4j.Logger;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class EntityBuilder<E, T> {

    private static final Logger LOG = getLogger(EntityBuilder.class);

    private final List<RejectionConsumer<T>> rejectionConsumers;

    private final List<Cause> causes;

    private Supplier<E> constructor;

    private T source;

    public EntityBuilder() {
        rejectionConsumers = new ArrayList<>();
        causes = new ArrayList<>();
    }

    public EntityBuilder<E, T> setConstructor(Supplier<E> constructor) {
        this.constructor = requireNonNull(constructor, "The constructor is mandatory");
        return this;
    }

    public EntityBuilder<E, T> addRejectionConsumer(RejectionConsumer<T> rejectionConsumer) {
        if(rejectionConsumer != null) {
            rejectionConsumers.add(rejectionConsumer);
        }
        return this;
    }

    public EntityBuilder<E, T> loadSource(T source) {
        if(source == null) {
            LOG.warn("Null source received");
        }
        this.source = source;
        return this;
    }

    public Optional<E> build() {
        final Rejection<T> rejection;
        final E entity;

        entity = requireNonNull(constructor, "The constructor must have been set").get();
        if(source == null) {
            sendRejection(Rejection.nullRejection());
            return Optional.empty();
        }
        assemble(entity, source);
        if (causes.isEmpty()) {
            return Optional.of(entity);
        }
        rejection = new RejectionImpl<>(source, List.copyOf(causes));
        sendRejection(rejection);
        return Optional.empty();
    }

    /**
     * Calls for load values into the entity from the source
     * see puts method
     * @see #put(String, Object, Consumer)
     * @param entity A not null entity
     * @param source A not null contract source
     */
    protected abstract void assemble(E entity, T source);

    protected void sendRejection(Rejection<T> rejection) {
        requireNonNull(rejection, "Parameter rejection is mandatory");
        if(rejectionConsumers.isEmpty()) {
            LOG.error("Not published rejection: {}", rejection);
            return;
        }
        rejectionConsumers.forEach(c -> c.accept(rejection));
    }

    protected <V> void put(final String fieldName, final V value, final Consumer<V> setter) {
        requireNonNull(fieldName, "Field name parameter is mandatory");
        requireNonNull(value, "Value parameter is mandatory");
        requireNonNull(setter, "Setter parameter is mandatory");
        try {
            setter.accept(value);
        } catch(RuntimeException rex) {
            LOG.warn("Error putting value {} in field {} with message {}.",
                    value, fieldName, rex.getMessage());
            causes.add(new ExceptionCause(fieldName, value, rex));
        }
    }

    protected void putInstantFromMillis(final String fieldName, final long millis, final Consumer<Instant> setter) {
        final Instant instant;

        instant = Instant.ofEpochMilli(millis);
        put(fieldName, instant, setter);
    }

}
