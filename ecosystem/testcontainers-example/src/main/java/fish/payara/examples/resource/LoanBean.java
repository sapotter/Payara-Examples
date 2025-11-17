package fish.payara.examples.resource;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

import fish.payara.examples.domain.Loan;
import fish.payara.examples.service.LoanService;

@Named("loanBean")
@ViewScoped
public class LoanBean implements Serializable {

    @Inject
    private transient LoanService loanService;

    private Loan loan = new Loan();

    public Loan getLoan() {
        return loan;
    }

    public List<Loan> getAllLoans() {
        return loanService.findAll();
    }

    public String create() {
      
        return null;
    }
    public String save() {
        if (loan.getLoanID() == null) {
             loanService.create(loan);
        } else {
             loanService.edit(loan);
        }
        loan = new Loan(); // reset
        return null;
    }

    public String remove(Integer loanID) {
        loanService.remove(loanService.find(loanID));
        return null;
    }

    public String edit(Loan p) {
        this.loan = p;
        return null;
    }

}