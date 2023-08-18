package com.bennohan.travelcatalogtravelid.ui.detail_travel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.travelcatalogtravelid.R
import com.bennohan.travelcatalogtravelid.base.BaseActivity
import com.bennohan.travelcatalogtravelid.database.constant.Const
import com.bennohan.travelcatalogtravelid.databinding.ActivityDetailDestinationBinding
import com.crocodic.core.api.ApiStatus
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailDestinationActivity :
    BaseActivity<ActivityDetailDestinationBinding, DetailDestinationViewModel>(R.layout.activity_detail_destination) {

//    private val imageList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getDestinationById()
        observe()

        binding.btnBack.setOnClickListener {
            finish()
        }

//        val imageSlider = findViewById<ImageSlider>(R.id.iv_destination)
//        imageSlider.setImageList()

    }

    private fun getDestinationById() {
        val id = intent.getIntExtra(Const.DESTINATION.ID_DESTINATION, 0)
        Log.d("cek getInt Id", id.toString())
        viewModel.getDestinationById(id)
    }

//     private fun initSlider(data: List<String>) {
//        val imageList = ArrayList<SlideModel>()
//        data.forEach { _ ->
//            imageList.add(SlideModel(String()))
//        }
//        binding.ivDestination.setImageList(imageList, ScaleTypes.CENTER_CROP)
//    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }

                    }
                }
                launch {
                    viewModel.dataDestination.collect {
                        Log.d("cek dataDestination", it.toString())
                        binding.destination = it
//                        val imageList = it?.photo ?: emptyList()
                        val imageList = if (it?.photo != null) {
                            it.photo
                        } else {
                            emptyList()
                        }
                        val imageSlider = findViewById<ImageSlider>(R.id.iv_destination)
                        val imageUrls = mutableListOf<SlideModel>()
                        for (imageUrl in imageList) {
                            val slideModel = SlideModel(imageUrl)
                            imageUrls.add(slideModel)
                        }
                        imageSlider.setImageList(imageUrls)
                        Log.d("cek dataImageUrls",imageUrls.toString())
                        Log.d("cek dataImage",imageList.toString())
                    }
                }
//                launch {
//                    viewModel.dataImage.collect {
//                        Log.d("cek dataImage", it.toString())
//                        imageList.add(SlideModel(it).toString())
//                    }
//                }

            }
        }
    }


}