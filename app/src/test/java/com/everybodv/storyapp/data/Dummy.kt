package com.everybodv.storyapp.data

import com.everybodv.storyapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object Dummy {

    fun dummyStory(): List<ListStoryItem>{
        val data :MutableList<ListStoryItem> = arrayListOf()

        for (i in 0 .. 200){
            val list = ListStoryItem(
                "link",
                "2002-06-11",
                "didi",
                "SIB",
                "$i",
                -6.72818,
                10.0020
            )
            data.add(list)
        }
        return data
    }

    fun dummyDesc() : RequestBody {
        val text ="didi"
        return text.toRequestBody()
    }
    fun dummyImage() : MultipartBody.Part {
        val text ="didi"
        return MultipartBody.Part.create(text.toRequestBody())
    }

    fun loginResult(): LoginResponse {
        return LoginResponse(
            LoginResult(
                userId = "user-iiwxRtdw0tuEm3ix",
                name = "tester0079",
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlpd3hSdGR3MHR1RW0zaXgiLCJpYXQiOjE2ODMxODA1NDJ9.zap7y8HYs9489e8QAQu95h9hhggApS-ohebBqgJeqcw"
            ),
            false, "success"
        )
    }
    fun register(): RegisterResponse{
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }
    fun uploadStory(): StoryUploadResponse{
        return StoryUploadResponse(
            error = false,
            message = "success"
        )
    }

}