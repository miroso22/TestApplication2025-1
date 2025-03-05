package com.example.testapplication2025_1

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun requestPermissions(permissions: List<String>): State<Boolean> {
    val context = LocalContext.current
    var grantedPermissionsCount by remember { mutableIntStateOf(permissions.count { context.hasPermission(it) }) }
    val allGranted = remember { derivedStateOf { grantedPermissionsCount == permissions.size } }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { grantedPermissionsCount++ }
    )

    LaunchedEffect(grantedPermissionsCount) {
        val permission = permissions.firstOrNull { !context.hasPermission(it) } ?: return@LaunchedEffect
        launcher.launch(permission)
    }

    return allGranted
}

fun Context.hasPermission(permission: String) =
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED