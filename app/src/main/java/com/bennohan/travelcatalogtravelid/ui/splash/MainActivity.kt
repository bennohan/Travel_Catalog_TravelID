package com.bennohan.travelcatalogtravelid.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.bennohan.travelcatalogtravelid.R
import com.bennohan.travelcatalogtravelid.database.UserDao
import com.bennohan.travelcatalogtravelid.databinding.ActivityMainBinding
import com.bennohan.travelcatalogtravelid.ui.home.HomeActivity
import com.bennohan.travelcatalogtravelid.ui.login.LoginActivity
import com.bennohan.travelcatalogtravelid.ui.register.RegisterActivity
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : NoViewModelActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                val user = userDao.isLogin()
                android.util.Log.d("cek is login", user.toString())
                if (!user) {
                    binding.btnLogin.setOnClickListener {
                        openActivity<LoginActivity> {
                            finish()
                        }
                    }
                    binding.btnRegister.setOnClickListener {
                        openActivity<RegisterActivity>{
                            finish()
                        }
                    }
                } else {
                openActivity<HomeActivity> {
                    finish()
                }
                }
            }

        }, 4000)


    }
}