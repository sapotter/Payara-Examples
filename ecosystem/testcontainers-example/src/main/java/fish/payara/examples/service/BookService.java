package fish.payara.examples.service;

import jakarta.enterprise.context.Dependent;

import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Book;

@Dependent

public class BookService extends AbstractService<Book, String> {

    public BookService() {
        super(Book.class);
    }
    
}
