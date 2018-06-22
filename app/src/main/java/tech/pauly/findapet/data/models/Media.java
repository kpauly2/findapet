package tech.pauly.findapet.data.models;

import android.support.annotation.Nullable;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "media", strict = false)
public class Media {

    @ElementList(name = "photos", entry = "photo", required = false)
    private List<Photo> photoList;

    @Nullable
    public List<Photo> getPhotoList() {
        return photoList;
    }
}
