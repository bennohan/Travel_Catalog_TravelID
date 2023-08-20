package com.bennohan.travelcatalogtravelid.ui.home

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.travelcatalogtravelid.R
import com.bennohan.travelcatalogtravelid.base.BaseActivity
import com.bennohan.travelcatalogtravelid.database.Destination
import com.bennohan.travelcatalogtravelid.database.UserDao
import com.bennohan.travelcatalogtravelid.database.constant.Const
import com.bennohan.travelcatalogtravelid.databinding.ActivityHomeBinding
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding , HomeViewModel>(R.layout.activity_home) {

    @Inject
    lateinit var userDao: UserDao


    private val adapterDestination by lazy {
        object : ReactiveListAdapter<ItemDestinationBinding, Destination>(R.layout.item_destination) {
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
                            .load(item.photo[0])
                            .apply(RequestOptions.centerCropTransform())
                            .into(holder.binding.ivThumbnailTour)
                    }else{
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
                android.util.Log.d("cek photo",item.photo.toString())
                }


                holder.binding.constraint.setOnClickListener {
                    openActivity<DetailDestinationActivity>{
                        putExtra(Const.DESTINATION.ID_DESTINATION,item.id)
                    }

                }

            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvTravel.adapter = adapterDestination
        getDestinationList()
        observe()


        binding.ivIconProfile.setOnClickListener {
            openActivity<ProfileActivity>()
        }

        binding.btnFilter.setOnClickListener {
            val bottomSheetFragment = FragmentBottomSheet()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

        }

    }


    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_filter_ui)

        val buttonFilter = dialog.findViewById<Button>(R.id.btn_dialog_filter)

        buttonFilter.setOnClickListener {
            tos("button Clicked")
        }

        dialog.show()
       }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                tos("Login Success")
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
                    userDao.getUser().collect {
                        binding.user = it
                    }
                }
            }
        }
    }

    private fun getDestinationList() {
        viewModel.getListDestination()
    }
}