package fish.payara.examples.service;

import fish.payara.examples.domain.Book;
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
class BookServiceTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(BookService.class)
            .build();

    @Mock
    private EntityManager entityManager;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = weld.select(BookService.class).get();
        // Use reflection to set the mock EntityManager
        try {
            var field = AbstractService.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(bookService, entityManager);
        } catch (Exception e) {
            fail("Failed to set mock EntityManager: " + e.getMessage());
        }
    }

    @Test
    void testCreateBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPages(200);

        doNothing().when(entityManager).persist(any(Book.class));
        
        bookService.create(book);
        
        verify(entityManager).persist(book);
    }

    @Test
    void testFindBook() {
        String isbn = "123L";
        Book expectedBook = new Book();
        expectedBook.setIsbn(isbn);
        expectedBook.setTitle("Test Book");

        when(entityManager.find(eq(Book.class), eq(isbn))).thenReturn(expectedBook);

        Book foundBook = bookService.find(isbn);
        
        assertNotNull(foundBook);
        assertEquals(isbn, foundBook.getIsbn());
        assertEquals("Test Book", foundBook.getTitle());
    }

    @Test
    void testFindAllBooks() {
        Book book1 = new Book();
        book1.setIsbn("123L");
        book1.setTitle("Book 1");

        Book book2 = new Book();
        book2.setIsbn("456L");
        book2.setTitle("Book 2");

        List<Book> expectedBooks = Arrays.asList(book1, book2);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery cq = mock(CriteriaQuery.class);
        Root<Book> root = mock(Root.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery()).thenReturn(cq);
        when(cq.from(Book.class)).thenReturn(root);
        when(cq.select(root)).thenReturn(cq);

        TypedQuery<Book> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedBooks);

        List<Book> foundBooks = bookService.findAll();

        assertNotNull(foundBooks);
        assertEquals(2, foundBooks.size());
        assertEquals("Book 1", foundBooks.get(0).getTitle());
        assertEquals("Book 2", foundBooks.get(1).getTitle());
    }


    @Test
    void testEditBook() {
        Book book = new Book();
        book.setIsbn("123L");
        book.setTitle("Original Title");

        Book updatedBook = new Book();
        updatedBook.setIsbn("123L");
        updatedBook.setTitle("Updated Title");

        when(entityManager.merge(book)).thenReturn(updatedBook);

        Book result = bookService.edit(book);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(entityManager).merge(book);
    }

    @Test
    void testRemoveBook() {
        Book book = new Book();
        book.setIsbn("123L");
        book.setTitle("Test Book");

        when(entityManager.merge(book)).thenReturn(book);
        doNothing().when(entityManager).remove(book);

        bookService.remove(book);

        verify(entityManager).merge(book);
        verify(entityManager).remove(book);
    }
}