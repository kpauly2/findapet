package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Element;

public enum AnimalType {
    Dog,
    Cat,
    @Element(name = "Small&Furry") SmallFurry,
    BarnYard,
    Bird,
    Horse,
    Rabbit,
    Reptile
}
