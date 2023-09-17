package com.bennohan.travelcatalogtravelid.ui.home

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
import com.crocodic.core.helper.log.Log
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val observer: BaseObserver,
    ) :  BaseViewModel() {

    private var _listDestination = MutableSharedFlow<List<Destination?>>()
    var listDestination = _listDestination.asSharedFlow()

    private var _filterListDestinationByCatrgory= MutableSharedFlow<List<Destination?>>()
    var filterListDestinationByCategory = _filterListDestinationByCatrgory.asSharedFlow()

    private var _filterListDestinationByProvince = MutableSharedFlow<List<Destination?>>()
    var filterListDestinationByProvince = _filterListDestinationByProvince.asSharedFlow()

    private var _listDestinationCategory = MutableSharedFlow<List<Destination?>>()
    var listDestinationCategory = _listDestinationCategory.asSharedFlow()

    private var _listDestinationProvince = MutableSharedFlow<List<Destination?>>()
    var listDestinationProvince = _listDestinationProvince.asSharedFlow()


    //List Destination Function
    fun getListDestination(
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        observer(
            block = { apiService.destinationList() },
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _listDestination.emit(data)
                    android.util.Log.d("cek api token",Const.TOKEN.ACCESS_TOKEN)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    android.util.Log.d("cek api token",Const.TOKEN.ACCESS_TOKEN)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


    fun getListCategory(
        ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.destinationCategory() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _listDestinationCategory.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getListProvince(
        ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.destinationProvince() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _listDestinationProvince.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getListDestinationByCategory(
        idCategory: Int,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        observer(
            block = { apiService.getDestinationByCategory(idCategory) },
            toast =false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _filterListDestinationByCatrgory.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getListDestinationByProvince(
        idCategory: Int,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        observer(
        block = { apiService.getDestinationByProvince(idCategory) },
        toast =  false,
        responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _filterListDestinationByProvince.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

}