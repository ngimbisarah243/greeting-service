package org.model;

public class Greeting {
    private int id;
    private String greetingText;

    public Greeting() {}

    public Greeting(int id, String greetingText) {
        this.id = id;
        this.greetingText = greetingText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGreetingText() {
        return greetingText;
    }

    public void setGreetingText(String greetingText) {
        this.greetingText = greetingText;
    }
}

