package com.honghe.guardtest

class JniLoader {
    companion object {
        init {
            System.loadLibrary("firstndk")
        }
    }

    val helloString: String?
        external get
}