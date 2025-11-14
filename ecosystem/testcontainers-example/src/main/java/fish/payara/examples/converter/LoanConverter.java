package fish.payara.examples.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Loan;
import fish.payara.examples.service.LoanService;

@FacesConverter(value = "loanConverter", managed = true)
public class LoanConverter implements Converter<Loan> {

    @Inject
    private LoanService loanService;

    @Override
    public Loan getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return loanService.find(Integer.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Loan loan) {
        if (loan == null || loan.getLoanID() == null) {
            return "";
        }
        return loan.getLoanID().toString();
    }
}