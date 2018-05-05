package tech.pauly.findapet.data.models;

import android.support.annotation.Nullable;

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

    @Nullable
    public String getAddress1() {
        return address1;
    }

    @Nullable
    public String getAddress2() {
        return address2;
    }

    @Nullable
    public String getCity() {
        return city;
    }

    @Nullable
    public String getState() {
        return state;
    }

    @Nullable
    public String getZip() {
        return zip;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    @Nullable
    public String getFax() {
        return fax;
    }

    @Nullable
    public String getEmail() {
        return email;
    }
}
