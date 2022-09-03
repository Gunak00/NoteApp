package com.example.notatnik_v1;

public class TaskListItem {
    private String name, date;
    private boolean isDone;

    public TaskListItem(String name, String date, boolean isDone) {
        this.name = name;
        this.date = date;
        this.isDone = isDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}


