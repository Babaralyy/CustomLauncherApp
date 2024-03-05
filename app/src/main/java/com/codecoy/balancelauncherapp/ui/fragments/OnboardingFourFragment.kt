package com.codecoy.balancelauncherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.FragmentOnboardingFourBinding

class OnboardingFourFragment : Fragment() {
    private lateinit var mBinding: FragmentOnboardingFourBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentOnboardingFourBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnContinue.setOnClickListener {
            val navOptions =
                NavOptions.Builder()
                    .setEnterAnim(R.anim.bottom_slide_up)
                    .setExitAnim(R.anim.slide_out_to_bottom)
                    .setPopEnterAnim(R.anim.bottom_slide_up)
                    .setPopExitAnim(R.anim.slide_out_to_bottom)
                    .build()

//            val action = OnboardingFourFragmentDirections.actionOnboardingFourFragmentToOnboardingFiveFragment()
//            findNavController().navigate(action, navOptions)
        }
    }
}
