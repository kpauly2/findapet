package tech.pauly.findapet.data.models;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import androidx.annotation.Nullable;

@Root(name = "petfinder", strict = false)
public class SingleAnimalResponse {

    @Element(name = "header")
    private Header header;

    @Element(required = false)
    private int lastOffset;

    @Element(name = "pet", required = false)
    private InternetAnimal animal;

    @Nullable
    public InternetAnimal getAnimal() {
        return animal;
    }

    @Nullable
    public int getLastOffset() {
        return lastOffset;
    }

    public Header getHeader() {
        return header;
    }
}
