package tech.pauly.findapet.data.models

//TODO: Convert to inline class when they're stable
typealias AnimalUrl = String
val AnimalUrl.fullUrl: String
    get() {
        if (this.contains("-") && this.contains(".png")) {
            return "file://$this"
        }
        return this
    }
