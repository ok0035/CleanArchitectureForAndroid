package com.zerodeg.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(@ActivityContext private val context: Context) {

    private lateinit var onPermissionResult: (String, Boolean) -> Unit

    private lateinit var onAllPermissionGranted: () -> Unit
    private lateinit var onFileWriteGranted: (Uri) -> Unit
    private lateinit var onFileWriteDenied: () -> Unit

    private val requestMultiplePermissionsLauncher = (context as AppCompatActivity).registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        var allGranted = true
        permissions.forEach { (permission, isGranted) ->
            onPermissionResult(permission, isGranted)
            if(!isGranted) allGranted = false
        }
        if(allGranted) onAllPermissionGranted()
    }

    private val requestWritePermissionLauncher: ActivityResultLauncher<Intent> =
        (context as AppCompatActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    onFileWriteGranted(uri)
                }
            } else {
                onFileWriteDenied()
            }
        }

    fun setupPermissions(
        onPermissionResult: (String, Boolean) -> Unit,
        onAllPermissionGranted: () -> Unit
    ) {
        this.onPermissionResult = onPermissionResult
        this.onAllPermissionGranted = onAllPermissionGranted
    }

    fun setupFileWriteRequest(onFileWriteGranted: (Uri) -> Unit, onFileWriteDenied: () -> Unit) {
        this.onFileWriteGranted = onFileWriteGranted
        this.onFileWriteDenied = onFileWriteDenied
    }

    fun requestPermissions(vararg permissions: String) {
        if (permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            permissions.forEach { permission ->
                onPermissionResult(permission, true)
            }
        } else {
            requestMultiplePermissionsLauncher.launch(Array(permissions.size) {i -> permissions[i]})
        }
    }

    fun requestFileWritePermission(uri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_APP_GALLERY)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, DocumentFile.fromSingleUri(context, uri)?.name)
        }
        requestWritePermissionLauncher.launch(intent)
    }
}