package com.epa.coroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.epa.coroutines.ui.DashboardFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.suspendCancellableCoroutine

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, DashboardFragment())
                .commitAllowingStateLoss()
        }
    }
}