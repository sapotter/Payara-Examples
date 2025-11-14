package fish.payara.examples.domain;

import jakarta.persistence.*;
import java.util.Objects;

import java.util.List;
import jakarta.json.bind.annotation.JsonbTransient;

@NamedQueries({
    @NamedQuery(name = "Librarian.findByLibrarianID", query = "SELECT e FROM Librarian e WHERE e.librarianID = :librarianID"),
    @NamedQuery(name = "Librarian.findByName", query = "SELECT e FROM Librarian e WHERE e.name = :name"),
    @NamedQuery(name = "Librarian.findByDepartment", query = "SELECT e FROM Librarian e WHERE e.department = :department")
})
@Entity
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String librarianID;

    private String name;

    private String department;

    @JsonbTransient
    @OneToMany(mappedBy = "librarian")
    private List<Loan> loansManageds;


    // Getters and setters

    public String getLibrarianID() {
        return librarianID;
    }

    public void setLibrarianID(String librarianID) {
        this.librarianID = librarianID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Loan> getLoansManageds() {
        return loansManageds;
    }

    public void setLoansManageds(List<Loan> loansManageds) {
        this.loansManageds = loansManageds;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.librarianID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Librarian other = (Librarian) obj;
        return Objects.equals(this.librarianID, other.librarianID);
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

}
