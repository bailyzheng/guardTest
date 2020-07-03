package com.honghe.guardtest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyApplication.mainActivity = this@MainActivity
    }

    fun openLocal(view: View?) {
        startService(Intent(this, LocalService::class.java))
    }

    fun closeLocal(view: View?) {
//        stopService(new Intent(this, LocalService.class));
        val jniLoader = JniLoader()
        val tv_mess = findViewById<TextView>(R.id.tv_message)
        tv_mess.text = jniLoader.helloString
    }

    fun closeRemote(view: View?) {
        stopService(Intent(this, RemoteService::class.java))
    }
}