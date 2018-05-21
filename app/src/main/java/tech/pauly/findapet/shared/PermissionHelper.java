package tech.pauly.findapet.shared;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.SupportActivity;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.pauly.findapet.shared.events.PermissionEvent;

@Singleton
public class PermissionHelper {

    private Map<Integer, PermissionEvent> requestMap = new HashMap<>();

    @Inject
    public PermissionHelper() {}

    public void requestPermission(SupportActivity activity, PermissionEvent permissionEvent) {
        requestMap.put(permissionEvent.getRequestCode(), permissionEvent);
        activity.requestPermissions(permissionEvent.getPermissions(), permissionEvent.getRequestCode());
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionEvent permissionEvent = requestMap.get(requestCode);
        if (permissionEvent != null) {
            for (int i = 0; i < permissions.length; i++) {
                boolean result = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                permissionEvent.getListener().onPermissionResult(new PermissionRequestResponse(permissions[i], result));
            }
            requestMap.remove(requestCode);
        }
    }

    @VisibleForTesting
    Map<Integer, PermissionEvent> getRequestMap() {
        return requestMap;
    }

    public class PermissionRequestResponse {
        private String permission;
        private boolean granted;

        public PermissionRequestResponse(String permissions, boolean results) {
            this.permission = permissions;
            this.granted = results;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isGranted() {
            return granted;
        }
    }
}
