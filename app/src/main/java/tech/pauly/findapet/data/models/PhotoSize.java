package tech.pauly.findapet.data.models;

public enum PhotoSize {
    LARGE("x"),                 //large (max 500x500)
    THUMBNAIL("t"),             //thumbnail (max 50 pixels high)
    PETNOTE("pn"),              //petnote (max 300x250)
    PET_NOTE_THUMBNAIL("pnt"),  //petnote thumbnail (max 60 pixels wide)
    FEATURED_PET_MODULE("fpm"); //featured pet module (max 95 pixels wide)

    private String serverName;

    PhotoSize(String serverName) {
        this.serverName = serverName;
    }

    static PhotoSize fromString(String name) {
        for (PhotoSize size : PhotoSize.values()) {
            if (size.serverName.equals(name)) {
                return size;
            }
        }
        throw new IllegalArgumentException("No matching PhotoSize for name " + name);
    }
}
