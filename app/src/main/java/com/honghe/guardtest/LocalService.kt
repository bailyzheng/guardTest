package com.honghe.guardtest

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import com.honghe.guardtest.LocalService
import java.util.*
import kotlin.system.exitProcess

class LocalService : Service() {
    private var mBinder: MyBinder? = null
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
            try {
                Log.e("LocalService", "connected with " + iMyAidlInterface.serviceName)
                if (MyApplication.mainActivity == null) {
                    Log.e("LocalService", "try to start MainActivity")
                    restartApp(this@LocalService)
//                    val intent = Intent(this@LocalService.baseContext, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    application.startActivity(intent)
                } else {
                    Log.e("LocalService", "MainActivity started")
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Toast.makeText(this@LocalService, "链接断开，重新启动 RemoteService", Toast.LENGTH_LONG).show()
            Log.e(TAG, "onServiceDisconnected: 链接断开，重新启动 RemoteService")
            startService(Intent(this@LocalService, RemoteService::class.java))
            bindService(Intent(this@LocalService, RemoteService::class.java), this, Context.BIND_IMPORTANT)
        }
    }

    fun restartApp(context: Context) {
        val outIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        outIntent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        val mPendingIntentId = Random().nextInt()
        val mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, outIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent)
        exitApp(context)

//            val outIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
//            outIntent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(outIntent)

//            System.exit(-1)
    }

    fun exitApp(context: Context) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val appTaskList = activityManager.appTasks
            for (appTask in appTaskList) {
                appTask.finishAndRemoveTask()
            }
        }
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand: LocalService 启动")
        Toast.makeText(this, "LocalService 启动", Toast.LENGTH_LONG).show()
        startService(Intent(this@LocalService, RemoteService::class.java))
        bindService(Intent(this@LocalService, RemoteService::class.java), connection, Context.BIND_IMPORTANT)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        mBinder = MyBinder()
        return mBinder!!
    }

    private inner class MyBinder : IMyAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun getServiceName(): String {
            return LocalService::class.java.name
        }

        @Throws(RemoteException::class)
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String) {
        }
    }

    companion object {
        private val TAG = LocalService::class.java.name
    }
}