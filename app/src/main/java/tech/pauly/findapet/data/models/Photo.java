package tech.pauly.findapet.data.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "photo")
public class Photo {

    @Attribute
    private String id;

    @Attribute
    private PhotoSize size;

    @Text
    private String url;
}
