package com.example.mystory

import android.content.ContentValues.TAG
import android.util.Config
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mystory.api.ApiConfig
import com.example.mystory.api.ApiService
import com.example.mystory.datamodel.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(): ViewModel() {

    private val _itemStory = MutableLiveData<List<ListStoryItem>>()
    val itemStory: LiveData<List<ListStoryItem>> = _itemStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun showThisListStory(token: String): Flow<PagingData<ListStoryItem>> {
        val client = ApiConfig.createApiService()

        Log.d("MainViewModel", "Please let me go here")
        _isLoading.value = false

        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { MyPagingSource(client, token) }
        ).flow

    }


    fun showListStory(token: String) {
        _isLoading.value = true
        val client = ApiConfig
            .createApiService()
            .getAllStories("Bearer $token")

        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _itemStory.value = response.body()?.listStory
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun uploadImage(
        loginResponse: LoginResponse,
        description: String,
        imageMultipart: MultipartBody.Part,
        callback: Utils.ApiCallbackString
    ) {
        _isLoading.value = true
        val service =
            ApiConfig.createApiService().addStories("Bearer ${loginResponse.loginResult?.token}", description, imageMultipart)
        service.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback.onResponse(response.body() != null, SUCCESS)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")

                    val jsonObject =
                        JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    callback.onResponse(false, message)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                callback.onResponse(false, t.message.toString())
            }
        })

    }

    companion object {
        private const val TAG = "AddStoryViewModel"
        private const val SUCCESS = "success"
    }


}
