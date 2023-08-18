package com.bennohan.travelcatalogtravelid.ui.login

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
import com.bennohan.travelcatalogtravelid.databinding.ActivityLoginBinding
import com.bennohan.travelcatalogtravelid.ui.home.HomeActivity
import com.bennohan.travelcatalogtravelid.ui.register.RegisterActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding , LoginViewModel>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvRegister()
        observe()

        binding.btnLogin.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val phone = binding.etPhone.textOf()
        val password = binding.etPassword.textOf()

        if (binding.etPhone.isEmptyRequired(R.string.mustFillPhone)|| binding.etPassword.isEmptyRequired(R.string.mustFillPassword)){
            return
        }

        viewModel.login(phone,password)
    }

    private fun tvRegister(){
        val spannableString = SpannableString("Don’t have an account? Register")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openActivity<RegisterActivity>()
            }
        }
        spannableString.setSpan(clickableSpan, 23, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvRegisterOption.text = spannableString
        binding.tvRegisterOption.movementMethod = LinkMovementMethod.getInstance() // Required for clickable spans to work

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                openActivity<HomeActivity> {
                                    finish()
                                }
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
            }
        }
    }
}