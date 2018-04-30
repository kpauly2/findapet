package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "status")
public class Status {
    @Element
    private int code;

    @Element(required = false)
    private String message;
}