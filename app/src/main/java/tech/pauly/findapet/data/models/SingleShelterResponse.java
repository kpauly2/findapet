package tech.pauly.findapet.data.models;

import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "petfinder", strict = false)
public class SingleShelterResponse {

    @Element(name = "header")
    private Header header;

    @Element(required = false)
    private int lastOffset;

    @Element(name = "shelter", required = false)
    private Shelter shelter;

    @Nullable
    public Shelter getShelter() {
        return shelter;
    }

    @Nullable
    public int getLastOffset() {
        return lastOffset;
    }

    public Header getHeader() {
        return header;
    }
}
