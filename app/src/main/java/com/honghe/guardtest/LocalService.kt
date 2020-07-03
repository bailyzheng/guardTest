package com.honghe.guardtest

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import com.honghe.guardtest.LocalService

class LocalService : Service() {
    private var mBinder: MyBinder? = null
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
            try {
                Log.e("LocalService", "connected with " + iMyAidlInterface.serviceName)
                if (MyApplication.mainActivity == null) {
                    val intent = Intent(this@LocalService.baseContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    application.startActivity(intent)
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