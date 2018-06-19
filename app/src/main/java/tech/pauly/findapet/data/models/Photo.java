package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "photo")
public class Photo {

    @Attribute
    private String id;

    @Attribute
    private String size;

    @Text
    private String url;

    public String getId() {
        return id;
    }

    public PhotoSize getSize() {
        return PhotoSize.Companion.fromString(size);
    }

    public String getUrl() {
        return url;
    }
}
