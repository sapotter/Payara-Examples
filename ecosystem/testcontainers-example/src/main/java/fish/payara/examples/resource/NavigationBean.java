package fish.payara.examples.resource;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class NavigationBean implements Serializable {
    private String activePage = "home";

    public String getActivePage() { return activePage; }
    public void setActivePage(String activePage) { this.activePage = activePage; }

    public String goTo(String page) {
        this.activePage = page;
        return page + "?faces-redirect=true";
    }
}