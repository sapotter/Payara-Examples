package fish.payara.examples.service;

import jakarta.enterprise.context.Dependent;

import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Loan;

@Dependent

public class LoanService extends AbstractService<Loan, Integer> {

    public LoanService() {
        super(Loan.class);
    }
    
}
