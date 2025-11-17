package fish.payara.examples.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Patron;
import fish.payara.examples.service.PatronService;

@FacesConverter(value = "patronConverter", managed = true)
public class PatronConverter implements Converter<Patron> {

    @Inject
    private PatronService patronService;

    @Override
    public Patron getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return patronService.find(String.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Patron patron) {
        if (patron == null || patron.getPatronID() == null) {
            return "";
        }
        return patron.getPatronID().toString();
    }
}