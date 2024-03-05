package com.codecoy.balancelauncherapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.data.responsemodel.CreatePasswordBody
import com.codecoy.balancelauncherapp.data.responsemodel.CreatePasswordResponse
import com.codecoy.balancelauncherapp.databinding.FragmentConfirmLockBinding
import com.codecoy.balancelauncherapp.network.ApiCall
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Constant
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.isNetworkConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmLockFragment : Fragment() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var activity: MainActivity

    private var previousPass: String? = null

    private val textWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {
                // This method is called when the text is changing
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length == 4) {
                    if (previousPass == text) {
                        if (activity.isNetworkConnected())
                            {
                                addPassword(text)
                            } else {
                            Toast.makeText(activity, "Connect to the internet", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, "Password mismatched", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun addPassword(previousPass: String) {
        val dialog = Constant.getDialog(activity)
        dialog.show()
        val email = Utils.fetchRecoveryEmail(activity)

        val passwordApi = Constant.getRetrofitInstance().create(ApiCall::class.java)

        if (!email.isNullOrEmpty()) {
            coroutineScope.launch {
                val createPasswordBody = CreatePasswordBody(email, previousPass, "0")
                val passwordCall = passwordApi.addPassword(createPasswordBody)

                passwordCall.enqueue(
                    object : Callback<CreatePasswordResponse> {
                        override fun onResponse(
                            call: Call<CreatePasswordResponse>,
                            response: Response<CreatePasswordResponse>,
                        ) {
                            if (response.code() == 200) {
                                dialog.dismiss()

                                val passwordData = response.body()

                                if (passwordData != null) {
                                    if (!passwordData.data?.passcode.isNullOrEmpty()) {
                                        try {
                                            Utils.saveConfirmPasscode(activity, previousPass)
                                            val action =
                                                ConfirmLockFragmentDirections.actionConfirmLockFragmentToFreedomFragment()
                                            findNavController().navigate(action)
                                        } catch (e: Exception) {
                                            Log.i(Utils.TAG, "afterTextChanged: ${e.message}")
                                        }
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                dialog.dismiss()
                                Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<CreatePasswordResponse>,
                            t: Throwable,
                        ) {
                            dialog.dismiss()
                            Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }
        } else {
            dialog.dismiss()
        }
    }

    private lateinit var mBinding: FragmentConfirmLockBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentConfirmLockBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        previousPass = Utils.fetchPasscode(activity)
        if (previousPass.isNullOrEmpty())
            {
                previousPass = Utils.passCode
            }

        clickOnKeyPad()

        mBinding.etPasscode.addTextChangedListener(textWatcher)

        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.btnCutText.setOnClickListener {
            val originalText = mBinding.etPasscode.text.toString().trim()

            if (originalText.isNotEmpty()) {
                val editedText = originalText.substring(0, originalText.length - 1)
                mBinding.etPasscode.setText(editedText)
            }
        }
    }

    private fun clickOnKeyPad() {
        for (i in 0 until mBinding.gridLayout.childCount) {
            val cellView: View = mBinding.gridLayout.getChildAt(i)

            cellView.setOnClickListener {
                when (cellView.id) {
                    R.id.cell1 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "1")
                    }

                    R.id.cell2 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "2")
                    }

                    R.id.cell3 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "3")
                    }

                    R.id.cell4 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "4")
                    }

                    R.id.cell5 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "5")
                    }

                    R.id.cell6 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "6")
                    }

                    R.id.cell7 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "7")
                    }

                    R.id.cell8 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "8")
                    }

                    R.id.cell9 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "9")
                    }

                    R.id.cell0 -> {
                        val code = mBinding.etPasscode.text.toString().trim()
                        mBinding.etPasscode.setText(code + "0")
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
