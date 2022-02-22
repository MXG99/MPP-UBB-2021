package contests.model;

import java.io.Serializable;

public class Participant implements Identifiable<Long>, Serializable {
    private Long id;
    private Long firstActivityId;
    private Long secondActivityId;
    private Integer age;
    private Long categoryId;
    private String firstName;
    private String lastName;

    public Participant(String firstName, String lastName, Long firstActivityId, Long secondActivityId, Integer age) {
        this.firstActivityId = firstActivityId;
        this.secondActivityId = secondActivityId;
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Participant(Long firstActivityId, Long secondActivity, Long category, String firstName, String lastName) {
        this.firstActivityId = firstActivityId;
        this.secondActivityId = secondActivity;
        this.categoryId = category;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getFirstActivityId() {
        return firstActivityId;
    }

    public void setFirstActivityId(Long firstActivityId) {
        this.firstActivityId = firstActivityId;
    }

    public Long getSecondActivityId() {
        return secondActivityId;
    }

    public void setSecondActivityId(Long secondActivityId) {
        this.secondActivityId = secondActivityId;
    }

    public Long getCategory() {
        return categoryId;
    }

    public void setCategory(Long categoryId) {
        this.categoryId = categoryId;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "firstActivityId=" + firstActivityId +
                ", secondActivityId=" + secondActivityId +
                ", categoryId=" + categoryId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
