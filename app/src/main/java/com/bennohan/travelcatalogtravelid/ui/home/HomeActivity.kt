package com.bennohan.travelcatalogtravelid.ui.home

import android.app.Dialog
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.bennohan.travelcatalogtravelid.R
import com.bennohan.travelcatalogtravelid.base.BaseActivity
import com.bennohan.travelcatalogtravelid.database.Destination
import com.bennohan.travelcatalogtravelid.database.UserDao
import com.bennohan.travelcatalogtravelid.database.constant.Const
import com.bennohan.travelcatalogtravelid.databinding.ActivityHomeBinding
import com.bennohan.travelcatalogtravelid.databinding.ItemCategoryBinding
import com.bennohan.travelcatalogtravelid.databinding.ItemDestinationBinding
import com.bennohan.travelcatalogtravelid.ui.FragmentBottomSheet
import com.bennohan.travelcatalogtravelid.ui.detail_travel.DetailDestinationActivity
import com.bennohan.travelcatalogtravelid.ui.profile.ProfileActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO DATA CATEGORY BELUM DI OLAH
//FILTER DILANJUTKAN
@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    @Inject
    lateinit var userDao: UserDao


    private val adapterDestination by lazy {
        object :
            ReactiveListAdapter<ItemDestinationBinding, Destination>(R.layout.item_destination) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemDestinationBinding, Destination>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)



                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)

                }



                holder.binding.ivThumbnailTour.let {
                    if (item.photo?.isNotEmpty() == true) {
                        Glide
                            .with(this@HomeActivity)
                            .load(item.photo)
                            .apply(RequestOptions.centerCropTransform())
                            .into(holder.binding.ivThumbnailTour)
                    } else {
                        Glide
                            .with(this@HomeActivity)
                            .load(R.drawable.ic_baseline_image_placeholder)
                            .apply(RequestOptions.centerCropTransform())
                            .into(holder.binding.ivThumbnailTour)
                    }
//                    Glide
//                        .with(this@HomeActivity)
//                        .load(item.photo)
//                        .apply(RequestOptions.centerCropTransform())
//                        .into(holder.binding.ivThumbnailTour)
                    android.util.Log.d("cek photo", item.photo.toString())
                }


                holder.binding.constraint.setOnClickListener {
                    openActivity<DetailDestinationActivity> {
                        putExtra(Const.DESTINATION.ID_DESTINATION, item.id)
                    }

                }

            }

        }
    }

    private val adapterCategory by lazy {
        object : ReactiveListAdapter<ItemCategoryBinding, Destination>(R.layout.item_category) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemCategoryBinding, Destination>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)



                item?.let { itm ->
                    holder.bind(itm)

                }

                holder.binding.textView.text = item.category
                holder.binding.textView.setOnClickListener {
                    tos("${item.category}")

                    //TODO DATA DI OLAH
                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvTravel.adapter = adapterDestination
        getDestinationList()
        destinationCategory()
        observe()


        binding.ivIconProfile.setOnClickListener {
            openActivity<ProfileActivity>()
        }

        binding.btnFilter.setOnClickListener {
            showBottomSheetDialog()
        }

        binding.btnFilterHistorical.setOnClickListener {
            viewModel.getListDestinationByCategory(5)
        }
        binding.btnFilterBeach.setOnClickListener {
            viewModel.getListDestinationByCategory(2)
        }
        binding.btnFilterMountain.setOnClickListener {
            viewModel.getListDestinationByCategory(6)
        }
        binding.btnFilterEducation.setOnClickListener {
            viewModel.getListDestinationByCategory(4)
        }
        binding.btnFilterReligious.setOnClickListener {
            viewModel.getListDestinationByCategory(9)
        }

    }



    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
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
                    viewModel.listDestination.collect { destination ->
                        adapterDestination.submitList(destination)
                    }
                }
                launch {
                    viewModel.filterListDestination.collect { filterDestination ->
                        adapterDestination.submitList(filterDestination)
                    }
                }
                launch {
                    userDao.getUser().collect {
                        binding.user = it
                    }
                }
                launch {
                    viewModel.listDestinationCategory.collect { listCategory ->
                        Log.d("cek list category",listCategory.toString())
                        adapterCategory.submitList(listCategory)
                    }
                }
            }
        }
    }

    private fun getDestinationList() {
        viewModel.getListDestination()
    }

    private fun destinationCategory() {
        viewModel.getListDestinationCategory()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.fragment_bottom_sheet, null)

        // Find and set up UI components inside the bottom sheet layout
        val buttonInsideDialog = view.findViewById<Button>(R.id.btn_dialog_filter)
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_category)
        rvCategory.adapter = adapterCategory
        buttonInsideDialog.setOnClickListener {
            // Handle button click inside the bottom sheet dialog
            bottomSheetDialog.dismiss() // Close the dialog if needed
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

}