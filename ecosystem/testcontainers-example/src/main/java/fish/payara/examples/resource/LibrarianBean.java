package fish.payara.examples.resource;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

import fish.payara.examples.domain.Librarian;
import fish.payara.examples.service.LibrarianService;

@Named("librarianBean")
@ViewScoped
public class LibrarianBean implements Serializable {

    @Inject
    private transient LibrarianService librarianService;

    private Librarian librarian = new Librarian();

    public Librarian getLibrarian() {
        return librarian;
    }

    public List<Librarian> getAllLibrarians() {
        return librarianService.findAll();
    }

    public String create() {
      
        return null;
    }
    public String save() {
        if (librarian.getLibrarianID() == null) {
             librarianService.create(librarian);
        } else {
             librarianService.edit(librarian);
        }
        librarian = new Librarian(); // reset
        return null;
    }

    public String remove(String librarianID) {
        librarianService.remove(librarianService.find(librarianID));
        return null;
    }

    public String edit(Librarian p) {
        this.librarian = p;
        return null;
    }

}