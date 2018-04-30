package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "petfinder", strict = false)
public class AnimalListResponse {

    @Element(name = "header")
    private Header header;

    @Element
    private int lastOffset;

    @ElementList(name = "pets", entry = "pet")
    private List<Animal> animalList;

    public List<Animal> getAnimalList() {
        return animalList;
    }
}
