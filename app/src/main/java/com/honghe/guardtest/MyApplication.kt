package com.honghe.guardtest

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Process

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (isMainProcess(applicationContext)) {
            startService(Intent(this, LocalService::class.java))
        } else {
            return
        }
    }

    /**
     * 获取当前进程名
     */
    fun getCurrentProcessName(context: Context): String {
        val pid = Process.myPid()
        var processName = ""
        val manager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                processName = process.processName
            }
        }
        return processName
    }

    fun isMainProcess(context: Context): Boolean {
        /**
         * 是否为主进程
         */
        val isMainProcess: Boolean
        isMainProcess = context.applicationContext.packageName == getCurrentProcessName(context)
        return isMainProcess
    }

    companion object {
        var mainActivity: MainActivity? = null

    }
}