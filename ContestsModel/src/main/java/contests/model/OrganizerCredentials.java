package contests.model;

import java.io.Serializable;

public class OrganizerCredentials implements Serializable {
    private String email;
    private String password;

    public OrganizerCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
