package fish.payara.examples.service;

import fish.payara.examples.domain.Book;
import fish.payara.examples.domain.Patron;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@EnableWeld
class PatronServiceTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(PatronService.class)
            .build();

    @Mock
    private EntityManager entityManager;

    private PatronService patronService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patronService = weld.select(PatronService.class).get();
        try {
            var field = AbstractService.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(patronService, entityManager);
        } catch (Exception e) {
            fail("Failed to set mock EntityManager: " + e.getMessage());
        }
    }

    @Test
    void testCreateAndFind() {
        Patron p = new Patron();
        p.setName("John Doe");
        doNothing().when(entityManager).persist(any(Patron.class));

        patronService.create(p);
        verify(entityManager).persist(p);

        when(entityManager.find(eq(Patron.class), eq("id-1"))).thenReturn(p);
        Patron found = patronService.find("id-1");
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }

    @Test
    void testFindAll() {
        Patron p1 = new Patron(); p1.setName("A");
        Patron p2 = new Patron(); p2.setName("B");
        List<Patron> list = Arrays.asList(p1, p2);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery cq = mock(CriteriaQuery.class);
        Root<Book> root = mock(Root.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery()).thenReturn(cq);
        when(cq.from(Book.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);

        @SuppressWarnings("unchecked")
        TypedQuery<Patron> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);
        when(entityManager.createQuery(any(), eq(Patron.class))).thenReturn(typedQuery);

        List<Patron> result = patronService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

}
