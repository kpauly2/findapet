package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Element;

public class Contact {

    @Element(required = false)
    private String address1;

    @Element(required = false)
    private String address2;

    @Element(required = false)
    private String city;

    @Element(required = false)
    private String state;

    @Element(required = false)
    private String zip;

    @Element(required = false)
    private String phone;

    @Element(required = false)
    private String fax;

    @Element(required = false)
    private String email;
}
