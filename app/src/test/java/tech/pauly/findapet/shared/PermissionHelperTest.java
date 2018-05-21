package tech.pauly.findapet.shared;

import android.support.v4.app.SupportActivity;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import tech.pauly.findapet.shared.events.PermissionEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PermissionHelperTest {

    private PermissionHelper subject;

    @Before
    public void setup() {
        subject = new PermissionHelper();
    }

    @Test
    public void requestPermission_requestsPermissionAndAddsEventToMap() {
        PermissionEvent event = mock(PermissionEvent.class);
        when(event.getRequestCode()).thenReturn(1);
        when(event.getPermissions()).thenReturn(Arrays.array("permission"));
        SupportActivity activity = mock(SupportActivity.class);

        subject.requestPermission(activity, event);

        assertThat(subject.getRequestMap().get(1)).isEqualTo(event);
        verify(activity).requestPermissions(Arrays.array("permission"), 1);
    }

    @Test
    public void onRequestPermissionResult_permissionsExistsInMap_fireEventForEachPermissionResultAndRemovePermissionFromMap() {
        final List<PermissionHelper.PermissionRequestResponse> responses = new ArrayList<>();
        PermissionEvent event = mock(PermissionEvent.class);
        when(event.getRequestCode()).thenReturn(1);
        when(event.getPermissions()).thenReturn(Arrays.array("permission1", "permission2"));
        when(event.getListener()).thenReturn(responses::add);
        SupportActivity activity = mock(SupportActivity.class);
        subject.requestPermission(activity, event);

        subject.onRequestPermissionsResult(1, Arrays.array("permission1", "permission2"), new int[] {0, 1});

        assertThat(subject.getRequestMap().size()).isEqualTo(0);
        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).getPermission()).isEqualTo("permission1");
        assertThat(responses.get(0).isGranted()).isEqualTo(true);
        assertThat(responses.get(1).getPermission()).isEqualTo("permission2");
        assertThat(responses.get(1).isGranted()).isEqualTo(false);
    }

    @Test
    public void onRequestPermissionResult_permissionDoesNotExistInMap_doNothing() {
        final List<PermissionHelper.PermissionRequestResponse> responses = new ArrayList<>();
        PermissionEvent event = mock(PermissionEvent.class);
        when(event.getRequestCode()).thenReturn(1);
        when(event.getPermissions()).thenReturn(Arrays.array("permission1", "permission2"));
        when(event.getListener()).thenReturn(responses::add);

        subject.onRequestPermissionsResult(1, Arrays.array("permission"), new int[] {0, 1});

        assertThat(subject.getRequestMap().size()).isEqualTo(0);
        assertThat(responses.size()).isEqualTo(0);
    }
}