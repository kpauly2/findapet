package tech.pauly.findapet.shared.datastore

import androidx.annotation.StringRes

open class DiscoverToolbarTitleUseCase(@StringRes open val title: Int) : UseCase {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiscoverToolbarTitleUseCase

        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        return title
    }
}