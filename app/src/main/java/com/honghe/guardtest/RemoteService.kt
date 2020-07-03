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
import com.honghe.guardtest.RemoteService

class RemoteService : Service() {
    private var mBinder: MyBinder? = null
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
            try {
                Log.e(TAG, "connected with " + iMyAidlInterface.serviceName)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.e(TAG, "onServiceDisconnected: 链接断开，重新启动 LocalService")
            Toast.makeText(this@RemoteService, "链接断开，重新启动 LocalService", Toast.LENGTH_LONG).show()
            startService(Intent(this@RemoteService, LocalService::class.java))
            bindService(Intent(this@RemoteService, LocalService::class.java), this, Context.BIND_IMPORTANT)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand: RemoteService 启动")
        Toast.makeText(this, "RemoteService 启动", Toast.LENGTH_LONG).show()
        bindService(Intent(this, LocalService::class.java), connection, Context.BIND_IMPORTANT)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        mBinder = MyBinder()
        return mBinder
    }

    private inner class MyBinder : IMyAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun getServiceName(): String {
            return RemoteService::class.java.name
        }

        @Throws(RemoteException::class)
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String) {
        }
    }

    companion object {
        private val TAG = RemoteService::class.java.name
    }
}