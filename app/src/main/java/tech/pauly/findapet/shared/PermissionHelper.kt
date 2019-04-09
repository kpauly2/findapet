package tech.pauly.findapet.shared

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import tech.pauly.findapet.dependencyinjection.ForApplication
import tech.pauly.findapet.shared.events.PermissionEvent
import tech.pauly.findapet.utils.BindingAdapters
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class PermissionHelper @Inject
constructor(@param:ForApplication private val context: Context) {

    val requestMap = HashMap<Int, PermissionEvent>()

    open fun requestPermission(activity: Activity, permissionEvent: PermissionEvent) {
        requestMap[permissionEvent.requestCode] = permissionEvent
        activity.requestPermissions(permissionEvent.permissions, permissionEvent.requestCode)
    }

    open fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val permissionEvent = requestMap[requestCode] ?: return
        permissions.forEachIndexed { index, permission ->
            val result = grantResults[index] == PackageManager.PERMISSION_GRANTED
            permissionEvent.listener.onPermissionResult(PermissionRequestResponse(permission, result))
        }
        requestMap.remove(requestCode)
    }

    open fun hasPermissions(vararg permissions: String): Boolean {
        var hasPermission = true
        hasPermission = permissions.all {
            hasPermission and (ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED)
        }
        return hasPermission
    }
}

open class PermissionRequestResponse(val permission: String, val isGranted: Boolean)