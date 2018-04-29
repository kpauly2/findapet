package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "pet", strict = false)
public class Animal {

    @Element
    private int id;

    @Element
    private String shelterId;

    @Element(required = false)
    private String shelterPetId;

    @Element
    private String name;

    @Element(name = "animal")
    private AnimalType type;

    @ElementList(name = "breeds", entry = "breed")
    private List<String> breed;

    @Element
    private String mix;

    @Element
    private Age age;

    @Element
    private Sex sex;

    @Element
    private AnimalSize size;

    @ElementList(entry = "option")
    private List<Option> options;

    @Element
    private String description;

    @Element
    private String lastUpdate;

    @Element
    private Media media;

    @Element
    private Contact contact;
}
