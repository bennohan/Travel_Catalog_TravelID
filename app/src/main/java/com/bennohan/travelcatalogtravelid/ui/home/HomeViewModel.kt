package com.bennohan.travelcatalogtravelid.ui.home

import androidx.lifecycle.viewModelScope
import com.bennohan.travelcatalogtravelid.api.ApiService
import com.bennohan.travelcatalogtravelid.base.BaseViewModel
import com.bennohan.travelcatalogtravelid.database.Destination
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toList
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
    ) :  BaseViewModel() {

    private var _listDestination = MutableSharedFlow<List<Destination?>>()
    var listDestination = _listDestination.asSharedFlow()
    private var _filterListDestination = MutableSharedFlow<List<Destination?>>()
    var filterListDestination = _filterListDestination.asSharedFlow()


    //List Destination Function
    fun getListDestination(
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.destinationList() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _listDestination.emit(data)
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
        ApiObserver({ apiService.getDestinationByCategory(idCategory) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Destination>(gson)
                    _filterListDestination.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


}