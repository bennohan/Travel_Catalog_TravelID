package com.bennohan.travelcatalogtravelid.ui.detail_travel

import androidx.lifecycle.viewModelScope
import com.bennohan.travelcatalogtravelid.api.ApiService
import com.bennohan.travelcatalogtravelid.base.BaseObserver
import com.bennohan.travelcatalogtravelid.base.BaseViewModel
import com.bennohan.travelcatalogtravelid.database.Destination
import com.bennohan.travelcatalogtravelid.database.constant.Const
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.crocodic.core.helper.log.Log
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailDestinationViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val observer: BaseObserver
) : BaseViewModel() {

    private var _dataDestination = MutableSharedFlow<Destination?>()
    var dataDestination = _dataDestination.asSharedFlow()

    private var _dataImage = MutableSharedFlow<String>()
    var dataImage = _dataImage.asSharedFlow()

    private var _dataReviewList = MutableSharedFlow<List<Destination?>>()
    var dataReviewList = _dataReviewList.asSharedFlow()

    //List Destination Function
    fun getDestinationById(
        id: Int,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        observer(
            block = { apiService.destinationById(id) },
            toast =false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Destination>(gson)
                    val image =
                        response.getJSONObject(ApiCode.DATA).getJSONArray("photo").toString()
                    _dataImage.emit(image)
                    _dataDestination.emit(data)

                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun saveDestination(
        id: Int,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        observer(
            block = { apiService.saveDestination(id) },
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val responseSave = response.getBoolean("saved")
                    if (responseSave) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Saved"))
                        session.setValue(Const.SAVED.SAVE_DESTINATION, true)
                    } else {
                        _apiResponse.emit(ApiResponse().responseSuccess("unSaved"))
                        session.setValue(Const.SAVED.SAVE_DESTINATION, false)
                    }
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun addReview(
        rating: Int,
        review_description: String,
        id: Int,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        observer(
            block = { apiService.addReview(rating, review_description, id) },
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("Review Added"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


    fun getDestinationReviewList(
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        observer(
            block = { apiService.getDestinationReviewList() },
            toast =false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _dataReviewList.emit(data)
                    android.util.Log.d("cek api token", Const.TOKEN.ACCESS_TOKEN)
                    _apiResponse.emit(ApiResponse().responseSuccess("Get Destination Review Success"))

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    android.util.Log.d("cek api token", Const.TOKEN.ACCESS_TOKEN)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


}