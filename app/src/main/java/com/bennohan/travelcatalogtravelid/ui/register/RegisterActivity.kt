package com.bennohan.travelcatalogtravelid.ui.register

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.travelcatalogtravelid.R
import com.bennohan.travelcatalogtravelid.base.BaseActivity
import com.bennohan.travelcatalogtravelid.databinding.ActivityRegisterBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding,RegisterViewModel>(R.layout.activity_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        tvLogin()

        binding.btnRegister.setOnClickListener {
            register()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun register() {
        val name = binding.etName.textOf()
        val phoneOrEmail = binding.etPhone.textOf()
        val password = binding.etPassword.textOf()
        val confirmPassword = binding.etConfirmPassword.textOf()

        if (binding.etName.isEmptyRequired(R.string.mustFillName) || binding.etPhone.isEmptyRequired(
                R.string.mustFillPhone
            ) || binding.etPassword.isEmptyRequired(R.string.mustFillPassword) || binding.etConfirmPassword.isEmptyRequired(
                R.string.mustFillConfirmPassword
            )
        ) {
            return
        }

        fun isValidIndonesianPhoneNumber(phoneNumber: String): Boolean {
            val regex = Regex("^\\+62\\d{9,15}$|^0\\d{9,11}$")
            return regex.matches(phoneNumber)
        }
        if (phoneOrEmail.matches(Regex("\\d+"))) {
            tos("condition1")
            if (!isValidIndonesianPhoneNumber(phoneOrEmail)) {
                // Nomor Telephone valid
                binding.etPhone.error = "Nomor Telephone Tidak Valid"
                return
            }
        }else{
            if (binding.etPassword.textOf() == binding.etConfirmPassword.textOf()) {
                // Password is valid
                tos("condition2")
                binding.tvPasswordLength.visibility = View.GONE
                binding.tvPasswordNotMatch.visibility = View.GONE
            } else {
                tos("condition3")
                binding.tvPasswordLength.visibility = View.GONE
                binding.tvPasswordNotMatch.visibility = View.VISIBLE
                return
            }
        }
//        viewModel.register(name,phoneOrEmail, password, confirmPassword)
        tos("register")
    }

    private fun tvLogin(){
        val spannableString = SpannableString("Already have an account? Login")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                finish()
            }
        }
        spannableString.setSpan(clickableSpan, 25, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLoginOption.text = spannableString
        binding.tvLoginOption.movementMethod = LinkMovementMethod.getInstance() // Required for clickable spans to work

    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                tos("Register Success")
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }

                    }
                }
            }
        }
    }


}