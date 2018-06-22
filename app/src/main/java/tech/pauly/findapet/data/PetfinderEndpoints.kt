package tech.pauly.findapet.data

import tech.pauly.findapet.BuildConfig

object PetfinderEndpoints {
    val endpoint: String
        get() {
            val environment = BuildConfig.ENVIRONMENT
            return when (environment) {
                "prod" -> "http://api.petfinder.com/"
                "espresso" -> "http://localhost:8010/"
                else -> throw IllegalStateException("PetfinderEndpoints $environment is not a supported environment")
            }
        }
}
