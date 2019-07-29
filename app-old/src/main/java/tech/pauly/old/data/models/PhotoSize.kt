package tech.pauly.old.data.models

enum class PhotoSize(private val serverName: String) {

    LARGE("x"), //large (max 500x500)
    THUMBNAIL("t"), //thumbnail (max 50 pixels high)
    PETNOTE("pn"), //petnote (max 300x250)
    PET_NOTE_THUMBNAIL("pnt"), //petnote thumbnail (max 60 pixels wide)
    FEATURED_PET_MODULE("fpm"); //featured pet module (max 95 pixels wide)

    companion object {
        fun fromString(name: String): PhotoSize {
            return PhotoSize.values().single { it.serverName.equals(name, true) }
        }
    }
}
