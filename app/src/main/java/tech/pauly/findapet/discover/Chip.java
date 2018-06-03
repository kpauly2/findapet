package tech.pauly.findapet.discover;

public class Chip {
    private Type type;
    private String text;

    public Chip(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        LOCATION,
        FILTER
    }
}
