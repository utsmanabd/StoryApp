package com.everybodv.storyapp.util

class Const {
    companion object {
        const val TOKEN = "token"
        const val LOGIN = "login_auth"
        const val DETAIL = "detail"
        const val SUCCESS = "upload_success"
        const val CAMERAX_RESULT = 200
        const val REQ_CODE = 23
        val REQ_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)
        val emailPattern = Regex("[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+")
    }
}