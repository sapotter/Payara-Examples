package fish.payara.examples.service;

import jakarta.enterprise.context.Dependent;

import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Patron;

@Dependent

public class PatronService extends AbstractService<Patron, String> {

    public PatronService() {
        super(Patron.class);
    }
    
}
