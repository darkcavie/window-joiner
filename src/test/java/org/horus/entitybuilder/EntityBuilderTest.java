package org.horus.entitybuilder;

import org.horus.rejection.Cause;
import org.horus.rejection.Rejection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EntityBuilderTest {

    private EntityBuilder<MockEntity, MockContract> builder;

    @BeforeEach
    void setUp() {
        builder = new MockBuilder();
    }

    @Test
    void setConstructorNullFails() {
        assertThrows(NullPointerException.class, () -> builder.setConstructor(null));
    }

    @Test
    void addRejectionConsumerNullIsFine() {
        assertDoesNotThrow(() -> builder.addRejectionConsumer(null));
    }

    @Test
    void loadSourceNullIsFine() {
        assertDoesNotThrow(() -> builder.loadSource(null));
    }

    @Test
    void buildThereIsNotConstructorFails() {
        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    void buildSourceIsNull() {
        final Optional<MockEntity> optEntity;
        final AtomicInteger counter;

        counter = new AtomicInteger();
        builder.setConstructor(MockEntity::new);
        builder.addRejectionConsumer(rejection -> {
            counter.incrementAndGet();
            assertNotNull(rejection);
            assertNull(rejection.getRejected());
        });
        optEntity = builder.build();
        assertFalse(optEntity.isPresent());
        assertEquals(1, counter.get());
    }

    @Test
    void buildFails() {
        final Optional<MockEntity> optEntity;
        final MockContract mockContract;
        final AtomicInteger counter;

        counter = new AtomicInteger();
        mockContract = new MockContract("A1");
        builder.setConstructor(MockEntity::new);
        builder.addRejectionConsumer(rejection -> {
            final Optional<Cause> optCause;
            final Cause cause;

            counter.incrementAndGet();
            assertEquals(mockContract, rejection.getRejected());
            optCause = rejection.causeStream().findFirst();
            assertTrue(optCause.isPresent());
            cause = optCause.get();
            assertFalse(cause.getCause().isBlank());
            assertFalse(cause.getFormattedMessage().isBlank());
            assertNotNull(cause.getMessageParameters());
            assertTrue(cause.optThrowable().isPresent());
        });
        builder.loadSource(mockContract);
        optEntity = builder.build();
        assertFalse(optEntity.isPresent());
    }

    @Test
    void sendRejection() {
        builder.sendRejection(Rejection.nullRejection());
    }

    @Test
    void putInstantFromMillisFails() {
        builder.putInstantFromMillis("mockTime", -Long.MAX_VALUE, Assertions::assertNotNull);
    }

    private static class MockBuilder extends EntityBuilder<MockEntity, MockContract> {

        @Override
        protected void assemble(MockEntity entity, MockContract source) {
            put("code", source.getCode(), entity::setCode);
        }

    }
}