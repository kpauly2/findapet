package tech.pauly.findapet.data.models;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum Sex {
    U(0, R.string.unknown, null),
    M(1, R.string.male, "M"),
    F(2, R.string.female, "F");

    private int code;
    private int formattedName;
    private String serverName;

    Sex(int code, @StringRes int formattedName, @Nullable String serverName) {
        this.code = code;
        this.formattedName = formattedName;
        this.serverName = serverName;
    }

    public int getCode() {
        return code;
    }

    public int getFormattedName() {
        return formattedName;
    }

    public String getServerName() {
        return serverName;
    }
}
