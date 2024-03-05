package com.codecoy.balancelauncherapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.databinding.FragmentRestrictGuidesBinding
import com.codecoy.balancelauncherapp.ui.activities.MainActivity

class RestrictGuidesFragment : Fragment() {
    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentRestrictGuidesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentRestrictGuidesBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnOpenSettings.setOnClickListener {
            openAppSettings()
        }

        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = activity.packageName // Replace with your app's package name
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
