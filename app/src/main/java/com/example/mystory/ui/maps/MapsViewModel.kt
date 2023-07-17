package com.example.mystory.ui.maps

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mystory.api.ApiConfig
import com.example.mystory.api.ApiService
import com.example.mystory.data.ListStoryItem
import com.example.mystory.data.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = ApiConfig.createApiService()

    private val _locations = MutableLiveData<List<ListStoryItem>?>()
    val locations: MutableLiveData<List<ListStoryItem>?> get() = _locations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getAllProductsLocation(token: String) {
        val location = "1"
        Log.d("MapsViewModel", "This is token: $token and this is location $location")
        val call = apiService.getAllLocationStories("Bearer $token", location)

        call.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _locations.value = response.body()?.listStory
                        }
                    }
                } else {
                    Log.e("MainViewModel", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("MainViewModel", "onFailure: ${t.message}")
            }
        })

    }


}
