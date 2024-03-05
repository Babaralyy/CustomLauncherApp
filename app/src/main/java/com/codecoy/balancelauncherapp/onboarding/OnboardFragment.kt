package com.codecoy.balancelauncherapp.onboarding

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.FragmentOnboardBinding
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG

class OnboardFragment : Fragment() {
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var dots: MutableList<TextView>

    private lateinit var activity: MainActivity

    private var currentPos = 0

    var isLast = false

    private lateinit var mBinding: FragmentOnboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentOnboardBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        sliderAdapter = SliderAdapter(requireActivity())
        mBinding.vpSlider.adapter = sliderAdapter

        mBinding.dotsIndicator.attachTo(mBinding.vpSlider)

        mBinding.vpSlider.addOnPageChangeListener(changeListener)

        mBinding.tvNext.setOnClickListener {
            moveNext()
        }

        mBinding.tvSkip.setOnClickListener {
            val navOptions =
                NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_slide_in_right)
                    .setExitAnim(R.anim.fragment_slide_out_left)
                    .setPopEnterAnim(R.anim.fragment_slide_in_right)
                    .setPopExitAnim(R.anim.fragment_slide_out_left)
                    .build()

            try {
                Utils.saveOnBoardIntoPref(activity, true)
                val action = OnboardFragmentDirections.actionOnboardFragmentToPermissionsFragment()
                findNavController().navigate(action, navOptions)
            } catch (e: Exception) {
                Log.i(TAG, "moveNext:: ${e.message} ")
            }
        }
        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private var changeListener: ViewPager.OnPageChangeListener =
        object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                Log.i("TAG", "onPageScrolled: $position")
            }

            override fun onPageSelected(position: Int) {
                currentPos = position

                isLast = position == 2

                Log.i("TAG", "onPageSelected: $position")
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.i("TAG", "onPageScrollStateChanged: $state")
            }
        }

    private fun moveNext() {
        Log.i("TAG", "next: b$currentPos")

        if (isLast)
            {
                val navOptions =
                    NavOptions.Builder()
                        .setEnterAnim(R.anim.fragment_slide_in_right)
                        .setExitAnim(R.anim.fragment_slide_out_left)
                        .setPopEnterAnim(R.anim.fragment_slide_in_right)
                        .setPopExitAnim(R.anim.fragment_slide_out_left)
                        .build()

                try {
                    val action = OnboardFragmentDirections.actionOnboardFragmentToPermissionsFragment()

                    Utils.saveOnBoardIntoPref(activity, true)

                    findNavController().navigate(action, navOptions)
                } catch (e: Exception) {
                    Log.i(TAG, "moveNext:: ${e.message} ")
                }
            }

        mBinding.vpSlider.currentItem = currentPos + 1

        Log.i("TAG", "next: a$currentPos")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
