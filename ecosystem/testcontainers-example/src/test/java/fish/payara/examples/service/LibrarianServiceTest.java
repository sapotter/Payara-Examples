package fish.payara.examples.service;

import fish.payara.examples.domain.Book;
import fish.payara.examples.domain.Librarian;
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
class LibrarianServiceTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(LibrarianService.class)
            .build();

    @Mock
    private EntityManager entityManager;

    private LibrarianService librarianService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        librarianService = weld.select(LibrarianService.class).get();
        try {
            var field = AbstractService.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(librarianService, entityManager);
        } catch (Exception e) {
            fail("Failed to set mock EntityManager: " + e.getMessage());
        }
    }

    @Test
    void createAndFind() {
        Librarian l = new Librarian();
        l.setName("Libby");
        doNothing().when(entityManager).persist(any(Librarian.class));

        librarianService.create(l);
        verify(entityManager).persist(l);

        when(entityManager.find(eq(Librarian.class), eq("lib-1"))).thenReturn(l);
        Librarian found = librarianService.find("lib-1");
        assertNotNull(found);
    }

    @Test
    void findAll() {
        Librarian l1 = new Librarian();
        Librarian l2 = new Librarian();
        List<Librarian> list = Arrays.asList(l1, l2);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery cq = mock(CriteriaQuery.class);
        Root<Book> root = mock(Root.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery()).thenReturn(cq);
        when(cq.from(Book.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);

        @SuppressWarnings("unchecked")
        TypedQuery<Librarian> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);
        when(entityManager.createQuery(any(), eq(Librarian.class))).thenReturn(typedQuery);

        List<Librarian> result = librarianService.findAll();
        assertEquals(2, result.size());
    }

}
