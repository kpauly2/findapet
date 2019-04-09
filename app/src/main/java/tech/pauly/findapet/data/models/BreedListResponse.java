package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import androidx.annotation.Nullable;

@Root(name = "petfinder", strict = false)
public class BreedListResponse {

    @Element(name = "header")
    private Header header;

    @ElementList(name = "breeds", entry = "breed", required = false)
    private List<String> breedList;

    @Nullable
    public List<String> getBreedList() {
        return breedList;
    }
}
