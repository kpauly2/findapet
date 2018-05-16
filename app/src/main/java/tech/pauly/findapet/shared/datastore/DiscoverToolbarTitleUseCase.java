package tech.pauly.findapet.shared.datastore;

import android.support.annotation.StringRes;

public class DiscoverToolbarTitleUseCase implements UseCase {

    @StringRes
    private int title;

    public DiscoverToolbarTitleUseCase(@StringRes int title) {
        this.title = title;
    }

    @StringRes
    public int getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiscoverToolbarTitleUseCase that = (DiscoverToolbarTitleUseCase) o;

        return title == that.title;
    }

    @Override
    public int hashCode() {
        return title;
    }
}
