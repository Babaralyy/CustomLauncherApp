package com.codecoy.balancelauncherapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.FragmentOnboardingOneBinding
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Utils

class OnboardingOneFragment : Fragment() {
    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentOnboardingOneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentOnboardingOneBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnLaunch.setOnClickListener {
            val onboard = Utils.fetchOnBoardFromPref(activity)

            if (onboard)
                {
                    val action = OnboardingOneFragmentDirections.actionOnboardingOneFragmentToPermissionsFragment()
                    moveToNext(action)
                } else {
                val action = OnboardingOneFragmentDirections.actionOnboardingOneFragmentToOnboardFragment()
                moveToNext(action)
            }

            Utils.saveGetStartedIntoPref(activity, true)
        }
    }

    private fun moveToNext(action: NavDirections) {
        val navOptions =
            NavOptions.Builder()
                .setEnterAnim(R.anim.fragment_slide_in_right)
                .setExitAnim(R.anim.fragment_slide_out_left)
                .setPopEnterAnim(R.anim.fragment_slide_in_right)
                .setPopExitAnim(R.anim.fragment_slide_out_left)
                .build()

        findNavController().navigate(action, navOptions)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
