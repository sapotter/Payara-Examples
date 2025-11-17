package fish.payara.examples.service;

import jakarta.enterprise.context.Dependent;

import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Librarian;

@Dependent

public class LibrarianService extends AbstractService<Librarian, String> {

    public LibrarianService() {
        super(Librarian.class);
    }
    
}
