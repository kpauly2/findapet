package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "header")
public class Header {

    @Element
    private String version;

    @Element
    private String timestamp;

    @Element
    private Status status;
}
