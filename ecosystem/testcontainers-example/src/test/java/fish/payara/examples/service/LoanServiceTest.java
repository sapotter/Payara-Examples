package fish.payara.examples.service;

import fish.payara.examples.domain.Book;
import fish.payara.examples.domain.Loan;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@EnableWeld
class LoanServiceTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(LoanService.class)
            .build();

    @Mock
    private EntityManager entityManager;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanService = weld.select(LoanService.class).get();
        try {
            var field = AbstractService.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(loanService, entityManager);
        } catch (Exception e) {
            fail("Failed to set mock EntityManager: " + e.getMessage());
        }
    }

    @Test
    void createAndFind() {
        Loan loan = new Loan();
        loan.setLoanDate(LocalDateTime.now());
        doNothing().when(entityManager).persist(any(Loan.class));

        loanService.create(loan);
        verify(entityManager).persist(loan);

        when(entityManager.find(eq(Loan.class), eq(1))).thenReturn(loan);
        Loan found = loanService.find(1);
        assertNotNull(found);
    }

    @Test
    void findAll() {
        Loan l1 = new Loan();
        Loan l2 = new Loan();
        List<Loan> list = Arrays.asList(l1, l2);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery cq = mock(CriteriaQuery.class);
        Root<Book> root = mock(Root.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery()).thenReturn(cq);
        when(cq.from(Book.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);

        @SuppressWarnings("unchecked")
        TypedQuery<Loan> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);
        when(entityManager.createQuery(any(), eq(Loan.class))).thenReturn(typedQuery);

        List<Loan> result = loanService.findAll();
        assertEquals(2, result.size());
    }

}
