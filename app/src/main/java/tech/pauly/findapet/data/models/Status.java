package tech.pauly.findapet.data.models;

import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "status")
public class Status {
    @Element
    private int code;

    @Element(required = false)
    private String message;

    public StatusCode getCode() {
        return StatusCode.Companion.fromInt(code);
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}