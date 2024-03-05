package com.codecoy.balancelauncherapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.FragmentLoginBinding
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Constant
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG


class LoginFragment : Fragment() {

    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnLaunch.setOnClickListener {

            checkCredentials()



        }
    }

    private fun checkCredentials() {

        val email = mBinding.etEmail.text.toString().trim()
        val password = mBinding.etPassword.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty() && isValidEmail(email) ) {
            login(email, password)
        } else {
            Toast.makeText(activity, "Please provide valid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(email: String, password: String) {
        mBinding.progressBar.visibility = View.VISIBLE
        if (email == Constant.EMAIL && password == Constant.PASSWORD){

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                } catch (e: Exception) {
                    Log.i(TAG, "exception:: ${e.message}")
                }

                mBinding.progressBar.visibility = View.GONE

            }, 600)
        } else {
            mBinding.progressBar.visibility = View.GONE
            Toast.makeText(activity, "Please provide valid credentials", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}