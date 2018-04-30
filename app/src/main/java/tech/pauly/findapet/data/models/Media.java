package tech.pauly.findapet.data.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "media")
public class Media {

    @ElementList(name = "photos", entry = "photo")
    private List<Photo> photoList;
}
