package fish.payara.examples.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import fish.payara.examples.domain.Book;
import fish.payara.examples.service.BookService;

@FacesConverter(value = "bookConverter", managed = true)
public class BookConverter implements Converter<Book> {

    @Inject
    private BookService bookService;

    @Override
    public Book getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return bookService.find(String.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Book book) {
        if (book == null || book.getIsbn() == null) {
            return "";
        }
        return book.getIsbn().toString();
    }
}