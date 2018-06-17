package tech.pauly.findapet.shared

import android.content.Context
import android.support.v4.app.SupportActivity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.Arrays
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import tech.pauly.findapet.shared.events.PermissionEvent
import java.util.*

class PermissionHelperTest {

    private val context: Context = mock()

    private lateinit var subject: PermissionHelper

    @Before
    fun setup() {
        subject = PermissionHelper(context)
    }

    @Test
    fun requestPermission_requestsPermissionAndAddsEventToMap() {
        val event = mock<PermissionEvent>()
        val activity = mock<SupportActivity>()
        whenever(event.requestCode).thenReturn(1)
        whenever(event.permissions).thenReturn(Arrays.array("permission"))

        subject.requestPermission(activity, event)

        assertThat(subject.requestMap[1]).isEqualTo(event)
        verify(activity).requestPermissions(Arrays.array("permission"), 1)
    }

    @Test
    fun onRequestPermissionResult_permissionsExistsInMap_fireEventForEachPermissionResultAndRemovePermissionFromMap() {
        val responses = ArrayList<PermissionHelper.PermissionRequestResponse>()
        val event = mock<PermissionEvent>()
        val activity = mock<SupportActivity>()
        whenever(event.requestCode).thenReturn(1)
        whenever(event.permissions).thenReturn(Arrays.array("permission1", "permission2"))
        whenever(event.listener).thenReturn(PermissionEvent.PermissionListener { responses.add(it) })
        subject.requestPermission(activity, event)

        subject.onRequestPermissionsResult(1, Arrays.array("permission1", "permission2"), intArrayOf(0, 1))

        assertThat(subject.requestMap.size).isEqualTo(0)
        assertThat(responses.size).isEqualTo(2)
        assertThat(responses[0].permission).isEqualTo("permission1")
        assertThat(responses[0].isGranted).isEqualTo(true)
        assertThat(responses[1].permission).isEqualTo("permission2")
        assertThat(responses[1].isGranted).isEqualTo(false)
    }

    @Test
    fun onRequestPermissionResult_permissionDoesNotExistInMap_doNothing() {
        val responses = ArrayList<PermissionHelper.PermissionRequestResponse>()
        val event = mock<PermissionEvent>()
        whenever(event.requestCode).thenReturn(1)
        whenever(event.permissions).thenReturn(Arrays.array("permission1", "permission2"))
        whenever(event.listener).thenReturn(PermissionEvent.PermissionListener { responses.add(it) })

        subject.onRequestPermissionsResult(1, Arrays.array("permission"), intArrayOf(0, 1))

        assertThat(subject.requestMap.size).isEqualTo(0)
        assertThat(responses.size).isEqualTo(0)
    }
}