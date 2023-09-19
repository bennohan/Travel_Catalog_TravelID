package com.bennohan.travelcatalogtravelid.ui.detail_travel

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.bennohan.travelcatalogtravelid.R
import com.bennohan.travelcatalogtravelid.base.BaseActivity
import com.bennohan.travelcatalogtravelid.database.constant.Const
import com.bennohan.travelcatalogtravelid.databinding.ActivityDetailDestinationBinding
import com.bennohan.travelcatalogtravelid.databinding.ItemReviewBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.tos
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

//TODO ADD REVIEW
@AndroidEntryPoint
class DetailDestinationActivity :
    BaseActivity<ActivityDetailDestinationBinding, DetailDestinationViewModel>(R.layout.activity_detail_destination) {

    private lateinit var destinationName: String
    private var destinationId: Int? = null

    private lateinit var sharedPreferences: SharedPreferences

    private val adapterDestinationReview by lazy {
        object :
            ReactiveListAdapter<ItemReviewBinding, com.bennohan.travelcatalogtravelid.database.Destination>(
                R.layout.item_review
            ) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemReviewBinding, com.bennohan.travelcatalogtravelid.database.Destination>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)

                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)
                }

                val floatValue = if (item?.rating?.isNotEmpty() == true) {
                    item.rating.replace(",", ".").toFloat()
                } else {
                    0.0f // or any default value you want to set
                }


                holder.binding.rbDestinationRating.rating = floatValue


            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        getDestinationById()
        showBottomSheetDialogGetReview()
        observe()
        resultCondition()

        binding.btnAddReview.setOnClickListener {
            showBottomSheetDialogAddReview()
        }


        binding.tvDestinationCategory.setOnClickListener {
            viewModel.getDestinationReviewList()
        }

        binding.rbDestinationRating.setOnClickListener {
            tos("clickAble")
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDestinationSave.setOnClickListener {
            savedDestination()
        }

        binding.btnOpenMaps.setOnClickListener {
            sendLocationIntent()
        }


    }

    private fun resultCondition() {
        when (sharedPreferences.getBoolean("result", false)) {
            true -> {
                // Code will Run If   result  true
                binding.btnDestinationSave.setImageResource(R.drawable.ic_baseline_bookmark_24)
            }
            false -> {
                // Code will Run If   result  False
                binding.btnDestinationSave.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
            }

        }
    }

    private fun savedDestination() {
        destinationId?.let { viewModel.saveDestination(it) }
    }

    private fun setBooleanResult(value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("result", value)
        editor.apply()
    }


    private fun getDestinationById() {
        val id = intent.getIntExtra(Const.DESTINATION.ID_DESTINATION, 0)
        viewModel.getDestinationById(id)
        destinationId = id
    }

    private fun sendLocationIntent() {
        val intentUri = Uri.parse("google.navigation:q=${destinationName}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                when (it.message) {
                                    "Saved" -> {
                                        tos("Saved")
                                        loadingDialog.dismiss()
                                        setBooleanResult(true).apply {
                                            binding.btnDestinationSave.setImageResource(R.drawable.ic_baseline_bookmark_24)

                                        }
                                    }
                                    "unSaved" -> {
                                        tos("unSaved")
                                        loadingDialog.dismiss()
                                        setBooleanResult(false).apply {
                                            binding.btnDestinationSave.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                                        }
                                    }
                                    "Review Added" -> {
                                        tos("Review Added")
                                        showBottomSheetDialogGetReview()

                                    }
                                    "Get Destination Review Success" -> {
                                        loadingDialog.dismiss()
                                    }
                                }
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
                        destinationName = it?.name.toString()


                        val floatValue = if (it?.rating?.isNotEmpty() == true) {
                            it.rating.replace(",", ".").toFloat()
                        } else {
                            0.0f // or any default value you want to set
                        }
                        binding.rbDestinationRating.rating = floatValue


                        val imageList = if (it?.photo != null) {
                            it.photo
                        } else {
                            emptyList()
                        }

                        if (imageList.isEmpty()) {
                            binding.ivDestinationPlaceholder.visibility = View.VISIBLE
                        } else {
                            val imageSlider = findViewById<ImageSlider>(R.id.iv_destination)


                            val imageUrls = mutableListOf<SlideModel>()
                            for (imageUrl in imageList) {
                                val slideModel = SlideModel(imageUrl)
                                imageUrls.add(slideModel)
                            }

                            imageSlider.setImageList(imageUrls)
                            Log.d("cek dataImageUrls", imageUrls.toString())
                        }

                    }
                }
                launch {
                    viewModel.dataReviewList.collect {
                        adapterDestinationReview.submitList(it)
                        Log.d("cek dataReview", it.toString())
                    }
                }

            }
        }
    }

    private fun showBottomSheetDialogAddReview() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.fragment_bottom_review_sheet, null)
        var ratingUser: Int? = null

        // Find and set up UI components inside the bottom sheet layout
        val btnSubmitReview = view.findViewById<Button>(R.id.btn_submit_review)
        val etAddReview = view.findViewById<EditText>(R.id.et_addReview)
        val tvCategory = view.findViewById<TextView>(R.id.tv_category)
        val rbDestinationRating = view.findViewById<RatingBar>(R.id.rb_destination_rating)
        val reviewDescription = etAddReview.text


        tvCategory.setOnClickListener {
            Log.d("cek isi", reviewDescription.toString())
            tos("{$reviewDescription}")
        }

//        rbDestinationRating.setOnClickListener {
//            tos("clickAble")
//        }

        rbDestinationRating.setOnRatingBarChangeListener { _, rating, _ ->
            // Update the TextView with the selected rating as an integer
            tos("Rating: ${rating.toInt()}")
            ratingUser = rating.toInt()
        }


        btnSubmitReview.setOnClickListener {
            Log.d("RatingUser", "Rating: $ratingUser")
            Log.d("destinationId", "ID: $destinationId")
            ratingUser?.let { it1 ->
                destinationId?.let { it2 ->
                    viewModel.addReview(
                        it1, reviewDescription.toString(),
                        it2
                    )
                }
            }
            bottomSheetDialog.dismiss() // Close the dialog if needed

        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun showBottomSheetDialogGetReview() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.fragment_bottom_review_list_sheet, null)

        // Find and set up UI components inside the bottom sheet layout
        val rvReview = view.findViewById<RecyclerView>(R.id.rv_review)
        rvReview.adapter = adapterDestinationReview


        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


}