package fish.payara.examples.service;

import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AbstractServiceTest {

    @Test
    void findOrEmptyReturnsEmptyOnNoResult() {
        Optional<Object> result = AbstractService.findOrEmpty(() -> { throw new NoResultException(); });
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findOrEmptyReturnsValueWhenPresent() {
        Object obj = new Object();
        Optional<Object> result = AbstractService.findOrEmpty(() -> obj);
        assertTrue(result.isPresent());
        assertSame(obj, result.get());
    }

}
