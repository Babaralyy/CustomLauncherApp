package com.codecoy.balancelauncherapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.databinding.FragmentPasscodeBinding
import com.codecoy.balancelauncherapp.utils.Utils.TAG

class PasscodeFragment : Fragment() {
    private lateinit var mBinding: FragmentPasscodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentPasscodeBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnContinue.setOnClickListener {
            try {
                val action = PasscodeFragmentDirections.actionPasscodeFragmentToEmailFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }

        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
