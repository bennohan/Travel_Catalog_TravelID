package com.bennohan.travelcatalogtravelid.base

import com.bennohan.travelcatalogtravelid.api.ApiService
import com.bennohan.travelcatalogtravelid.database.constant.Const
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import javax.inject.Inject

class BaseObserver @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession
) {
    operator fun invoke(
        block: suspend () -> String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        toast: Boolean = false,
        responseListener: ApiObserver.ResponseListener
    ){
        ApiObserver(block, responseListener = object : ApiObserver.ResponseListener{
            override suspend fun onSuccess(response: JSONObject) {
                responseListener.onSuccess(response)
            }

            override suspend fun onError(response: ApiResponse) {
//                responseListener.onError(response)
                ApiObserver(
                    {apiService.refreshToken()},
                    responseListener = object : ApiObserver.ResponseListener{
                        override suspend fun onSuccess(response: JSONObject) {
                            val token = response.getString("token")
                            session.setValue(Const.TOKEN.ACCESS_TOKEN,token)
                            ApiObserver(block, responseListener = responseListener)
                        }

                        override suspend fun onError(response: ApiResponse) {
                            responseListener.onError(response)
                        }
                    }
                )
            }

            override suspend fun onExpired(response: ApiResponse) {
                responseListener.onExpired(response)
            }
        })
    }
}
