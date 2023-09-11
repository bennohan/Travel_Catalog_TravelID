package com.bennohan.travelcatalogtravelid.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
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
import com.bennohan.travelcatalogtravelid.ui.detail_travel.DetailDestinationActivity
import com.bennohan.travelcatalogtravelid.ui.profile.ProfileActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO Filter Category belum buat fungsi di view-model
//PC
//FILTER DILANJUTKAN
@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    @Inject
    lateinit var userDao: UserDao
    private var dataDestination = ArrayList<Destination?>()
    private var dataDestinationByCategory = ArrayList<Destination?>()
    private var dataDestinationByProvince = ArrayList<Destination?>()
    private var dataCategory = ArrayList<Destination?>()
    private var dataProvince = ArrayList<Destination?>()
    private var categoryId: Int? = null
    private var provinceId: Int? = null
    private lateinit var buttonInsideDialog: Button


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
                            .load(item.photo[0])
                            .apply(RequestOptions.centerCropTransform())
                            .into(holder.binding.ivThumbnailTour)
                    } else {
                        Glide
                            .with(this@HomeActivity)
                            .load(R.drawable.ic_baseline_image_placeholder)
                            .apply(RequestOptions.centerCropTransform())
                            .into(holder.binding.ivThumbnailTour)
                    }
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
                    holder.binding.textView.text = itm.category
                    holder.binding.cardCategory.setBackgroundColor(
                        if (item.selectedCategory) {
                            applicationContext.getColor(R.color.button_selected)
                        } else {
                            applicationContext.getColor(R.color.white)
                        }
                    )
                }


                holder.binding.cardCategory.setOnClickListener {
                    tos("${item.category}")
                    tos("${item.id}")
                    categoryId = item.id
                    Log.d("cek categoryId", categoryId.toString())
                    dataCategory.forEachIndexed { index, variant ->
                        variant?.selectedCategory = index == position
                    }
                    notifyDataSetChanged()
                }

            }
        }
    }

    private val adapterProvince by lazy {
        object : ReactiveListAdapter<ItemCategoryBinding, Destination>(R.layout.item_category) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemCategoryBinding, Destination>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)

                item?.let { itm ->
                    holder.bind(itm)
                    holder.binding.textView.text = itm.name
                    holder.binding.cardCategory.setBackgroundColor(
                        if (item.selectedProvince) {
                            applicationContext.getColor(R.color.button_selected)
                        } else {
                            applicationContext.getColor(R.color.white)
                        }
                    )
                }


                holder.binding.cardCategory.setOnClickListener {
                    tos("${item.name}")
                    tos("$position")
                    provinceId = item.id
                    dataProvince.forEachIndexed { index, variant ->
                        variant?.selectedProvince = index == position
                    }
                    notifyDataSetChanged()
                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvTravel.adapter = adapterDestination
        getDestinationList()
        destinationCategory()
        destinationProvince()
        observe()
        search()

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

    private fun search() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                val filter = dataDestination.filter {
                    it?.name?.contains(
                        "$text",
                        true
                    ) == true
                }
                adapterDestination.submitList(filter)

//                if (filter.isEmpty()) {
//                    binding!!.tvNoteNotFound.visibility = View.VISIBLE
//                } else {
//                    binding!!.tvNoteNotFound.visibility = View.GONE
//                }
            } else {
                adapterDestination.submitList(dataDestination)
            }
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
                        dataDestination.clear()
                        dataDestination.addAll(destination)
                        adapterDestination.submitList(destination)
                    }
                }
                launch {
                    viewModel.filterListDestinationByCategory.collect { filterDestinationByCategory ->
                        dataDestinationByCategory.clear()
                        dataDestinationByCategory.addAll(filterDestinationByCategory)
                        tos("category")
//                        adapterDestination.submitList(dataDestinationByCategory + dataDestinationByProvince)
                    }
                }
                launch {
                    viewModel.filterListDestinationByProvince.collect { filterDestinationByProvince ->
                        dataDestinationByProvince.clear()
                        dataDestinationByProvince.addAll(filterDestinationByProvince)
                        tos("province")
//                        adapterDestination.submitList(dataDestinationByCategory + dataDestinationByProvince)
//                        adapterDestination.submitList(filterDestinationByProvince)
                    }
                }
                launch {
                    userDao.getUser().collect {
                        binding.user = it
                    }
                }
                launch {
                    viewModel.listDestinationCategory.collect { listCategory ->
                        dataCategory.clear()
                        dataCategory.addAll(listCategory)
                        Log.d("cek data category", dataCategory.toString())
                        adapterCategory.submitList(listCategory)
                    }
                }
                launch {
                    viewModel.listDestinationProvince.collect { listProvince ->
                        dataProvince.clear()
                        dataProvince.addAll(listProvince)
                        Log.d("cek list category", listProvince.toString())
                        adapterProvince.submitList(listProvince)
                    }
                }
            }
        }
    }

    //List Destination
    private fun getDestinationList() {
        viewModel.getListDestination()
    }

    //List Category
    private fun destinationCategory() {
        viewModel.getListCategory()
    }

    //List Province
    private fun destinationProvince() {
        viewModel.getListProvince()
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.fragment_bottom_sheet, null)

        // Find and set up UI components inside the bottom sheet layout
        val buttonInsideDialog = view.findViewById<Button>(R.id.btn_dialog_filter)
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_category)
        val rvProvince = view.findViewById<RecyclerView>(R.id.rv_province)
        rvCategory.adapter = adapterCategory
        rvProvince.adapter = adapterProvince

        Log.d("cek categoryId dialog", categoryId.toString())
        buttonInsideDialog.setOnClickListener {
            //Get List Destination By Category
            categoryId?.let { it1 -> viewModel.getListDestinationByCategory(it1) }
            provinceId?.let { it1 -> viewModel.getListDestinationByProvince(it1) }
            // Handle button click inside the bottom sheet dialog
            bottomSheetDialog.dismiss() // Close the dialog if needed

        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

}