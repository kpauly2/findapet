package tech.pauly.findapet.shared.events;

import java.util.Arrays;
import java.util.Objects;

import tech.pauly.findapet.shared.PermissionHelper;

public class PermissionEvent extends BaseViewEvent {

    private String[] permissions;
    private PermissionListener listener;
    private int requestCode;

    public static PermissionEvent build(Object emitter) {
        return new PermissionEvent(emitter.getClass());
    }

    private PermissionEvent(Class emitter) {
        this.emitter = emitter;
    }

    public PermissionEvent requestPermission(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public PermissionEvent listener(PermissionListener listener) {
        this.listener = listener;
        return this;
    }

    public PermissionEvent code(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public String[] getPermissions() {
        return permissions;
    }
    public PermissionListener getListener() {
        return listener;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public interface PermissionListener {
        void onPermissionResult(PermissionHelper.PermissionRequestResponse response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionEvent that = (PermissionEvent) o;
        return requestCode == that.requestCode && Arrays.equals(permissions, that.permissions) && Objects.equals(listener, that.listener);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(listener, requestCode);
        result = 31 * result + Arrays.hashCode(permissions);
        return result;
    }
}
