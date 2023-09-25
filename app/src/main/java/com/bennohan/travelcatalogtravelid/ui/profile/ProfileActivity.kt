package com.bennohan.travelcatalogtravelid.ui.profile

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.travelcatalogtravelid.R
import com.bennohan.travelcatalogtravelid.base.BaseActivity
import com.bennohan.travelcatalogtravelid.database.UserDao
import com.bennohan.travelcatalogtravelid.databinding.ActivityProfileBinding
import com.bennohan.travelcatalogtravelid.helper.ViewBindingHelper.Companion.writeBitmap
import com.bennohan.travelcatalogtravelid.ui.login.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.crocodic.core.helper.BitmapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var userDao: UserDao

    private var emailOrPhone: String? = null
    private var filePhoto: File? = null
    private var isTextChangedByUser = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        tvEmailOrPhone()

        setupTextWatcher(binding.etName)

        binding.btnOpenPhoto.setOnClickListener {
            openPictureDialog()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEditProfile.setOnClickListener {
            editProfile()
        }

        binding.btnLogout.setOnClickListener {
            logoutDialog()
        }

    }

    private fun editProfile() {
        val name = binding.etName.textOf()
        val photo = filePhoto

        if (photo == null) {
            viewModel.editProfile(name)
        } else {
            viewModel.editProfilePhoto(name, photo)
            tos("$filePhoto")
        }

    }

    //TODO TEST
    private fun setupTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isTextChangedByUser) {
                    // Show a toast message with the updated text
                    val updatedText = s.toString()
                    binding.btnEditProfile.visibility = View.VISIBLE
                } else {
                    isTextChangedByUser = true
                    binding.btnEditProfile.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }


    private fun logoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_logout)

        val buttonLogout = dialog.findViewById<Button>(R.id.btn_dialog_logout)
        val buttonCancel = dialog.findViewById<Button>(R.id.btn_dialog_cancel)
//
        buttonLogout.setOnClickListener {
            viewModel.logout()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

    private fun tvEmailOrPhone() {
        //Function to addjust the text view base on email or phone
        if (emailOrPhone?.matches(Regex("\\d+")) == true) {
            Log.d("cek number or email", emailOrPhone.toString())
            // Eksekusi tindakan atau fungsi yang ingin Anda lakukan jika nilai hanya berisi angka.
            // Contoh:
            binding.textViewPhone.visibility = View.VISIBLE
            binding.imageViewPhone.visibility = View.VISIBLE
            tos("phone")
        } else {
            Log.d("cek email", emailOrPhone.toString())
            binding.textViewEmail.visibility = View.VISIBLE
            binding.imageViewEmail.visibility = View.VISIBLE
        }


    }


    private fun openPictureDialog() {
        val myArray: Array<String> = arrayOf("take from camera 1", "insert from gallery 2")
        MaterialAlertDialogBuilder(this).apply {
            setItems(myArray) { _, which ->
                // The 'which' argument contains the index position of the selected item
                when (which) {
                    0 -> (this@ProfileActivity).activityLauncher.openCamera(
                        this@ProfileActivity,
                        "${this@ProfileActivity.packageName}.fileprovider"
                    ) { file, _ ->
                        uploadAvatar(file)
                    }
                    1 -> (this@ProfileActivity).activityLauncher.openGallery(
                        this@ProfileActivity
                    ) { file, _ ->
                        uploadAvatar(file)
                    }
                }
            }
        }.create().show()
    }

    private fun uploadAvatar(file: File?) {
        if (file == null) {
            binding?.root?.snacked("error")
            return
        }

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val resizeBitmap = BitmapHelper.resizeBitmap(bitmap, 512f)

        file.delete()

        //Result Photo
        val uploadFile = this.createImageFile().also { it.writeBitmap(resizeBitmap) }

        //Processing the photo result
        filePhoto = uploadFile
//        binding?.btnEditProfile?.visibility = View.VISIBLE
        Log.d("cek isi photo", uploadFile.toString())

        if (uploadFile != null) {
            binding?.ivIconProfileNew?.visibility = View.VISIBLE
            binding.btnEditProfile.visibility = View.VISIBLE
            binding.ivIconProfileNew.let {
                Glide
                    .with(this)
                    .load(uploadFile)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(it)
            }
        } else {
//            binding?.ivUserEditedView?.visibility = View.GONE
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
                                when (it.message) {
                                    " Edit Profile Success" -> {
                                        loadingDialog.dismiss()
                                        tos("Edit Profile Success")
                                    }
                                    "Logout" -> {
                                        openActivity<LoginActivity> {
                                            finishAffinity()
                                        }
                                    }
                                }

                                binding.root.snacked("Profile Edited")
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
                    userDao.getUser().collect { user ->
                        binding.user = user
                        emailOrPhone = user.phoneOrEmail
                    }
                }
            }
        }
    }
}