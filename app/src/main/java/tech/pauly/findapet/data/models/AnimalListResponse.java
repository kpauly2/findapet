package tech.pauly.findapet.data.models;

import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "petfinder", strict = false)
public class AnimalListResponse {

    @Element(name = "header")
    private Header header;

    @Element(required = false)
    private int lastOffset;

    @ElementList(name = "pets", entry = "pet", required = false)
    private List<InternetAnimal> animalList;

    @Nullable
    public List<InternetAnimal> getAnimalList() {
        return animalList;
    }

    @Nullable
    public int getLastOffset() {
        return lastOffset;
    }

    public Header getHeader() {
        return header;
    }
}
