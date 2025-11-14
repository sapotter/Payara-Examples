package fish.payara.examples.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Librarian;
import fish.payara.examples.service.LibrarianService;

@FacesConverter(value = "librarianConverter", managed = true)
public class LibrarianConverter implements Converter<Librarian> {

    @Inject
    private LibrarianService librarianService;

    @Override
    public Librarian getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return librarianService.find(String.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Librarian librarian) {
        if (librarian == null || librarian.getLibrarianID() == null) {
            return "";
        }
        return librarian.getLibrarianID().toString();
    }
}