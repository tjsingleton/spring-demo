package name.tjsingleton.todoapp;

import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Todo {
    private UUID id;
    private String description;
    private boolean completed;

    public Todo() {
    }

    public Todo(Todo aTodo) {
        this.id = aTodo.getId();
        this.description = aTodo.getDescription();
        this.completed = aTodo.isCompleted();
    }

    public Todo(UUID id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
