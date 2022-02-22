package contests.model.dtos;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    String id;
    String firstName;
    String lastName;
    String firstActivity;
    String secondActivity;
    String category;

    public ParticipantDTO(String id, String firstName, String lastName, String firstActivity, String secondActivity, String category) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.firstActivity = firstActivity;
        this.secondActivity = secondActivity;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstActivity() {
        return firstActivity;
    }

    public void setFirstActivity(String firstActivity) {
        this.firstActivity = firstActivity;
    }

    public String getSecondActivity() {
        return secondActivity;
    }

    public void setSecondActivity(String secondActivity) {
        this.secondActivity = secondActivity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
