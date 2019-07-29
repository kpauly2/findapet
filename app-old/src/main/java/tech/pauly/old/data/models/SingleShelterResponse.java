package tech.pauly.old.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import androidx.annotation.Nullable;

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
