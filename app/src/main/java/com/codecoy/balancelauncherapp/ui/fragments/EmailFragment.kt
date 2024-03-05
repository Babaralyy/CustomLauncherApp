package com.codecoy.balancelauncherapp.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.data.responsemodel.CheckEmailBody
import com.codecoy.balancelauncherapp.data.responsemodel.CheckEmailResponse
import com.codecoy.balancelauncherapp.data.responsemodel.RecoveryEmailBody
import com.codecoy.balancelauncherapp.data.responsemodel.RecoveryEmailResponse
import com.codecoy.balancelauncherapp.databinding.FragmentEmailBinding
import com.codecoy.balancelauncherapp.network.ApiCall
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Constant
import com.codecoy.balancelauncherapp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailFragment : Fragment() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentEmailBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.btnContinue.setOnClickListener {
            checkCredentials()
        }

        mBinding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun checkCredentials() {
        val email = mBinding.etRecoveryEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(activity, "Please enter your recovery email!", Toast.LENGTH_SHORT).show()
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(activity, "Please enter valid recovery email!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                checkEmail(email)
            }
        }
    }

    private fun checkEmail(email: String) {
        val dialog = Constant.getDialog(activity)
        dialog.show()

        val emailBody = CheckEmailBody(email)

        val emailApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val emailCAll = emailApi.checkEmail(emailBody)

        coroutineScope.launch {
            emailCAll.enqueue(
                object : Callback<CheckEmailResponse> {
                    override fun onResponse(
                        call: Call<CheckEmailResponse>,
                        response: Response<CheckEmailResponse>,
                    ) {
                        if (response.code() == 200) {
                            dialog.dismiss()

                            val emailData = response.body()

                            if (emailData != null) {
                                Toast.makeText(
                                    activity,
                                    "Recovery email added successfully ${emailData.data?.email}",
                                    Toast.LENGTH_SHORT,
                                ).show()

                                Utils.saveRecoveryEmail(activity, emailData.data?.email.toString())

                                try {
                                    val action =
                                        EmailFragmentDirections.actionEmailFragmentToLockPatternFragment()
                                    findNavController().navigate(action)
                                } catch (e: Exception) {
                                    Log.i(Utils.TAG, "inIt: ${e.message}")
                                }
                            }

                        /* if (emailData != null && emailData.data?.passcode?.isNotEmpty() == true) {

                             Toast.makeText(activity, "Your recovery email already added, please check", Toast.LENGTH_LONG).show()

                             Utils.saveRecoveryEmail(activity, emailData.data?.email.toString())
                             Utils.passCode = emailData.data?.passcode.toString()

                             try {
                                 val action = EmailFragmentDirections.actionEmailFragmentToConfirmLockFragment()
                                 findNavController().navigate(action)
                             }catch (e: Exception){
                                 Log.i(Utils.TAG, "inIt: ${e.message}")
                             }


                         }
                         else if (emailData != null && emailData.data?.passcode?.isEmpty() == true){

                             Toast.makeText(activity, "Recovery email added successfully ${emailData.data?.email}", Toast.LENGTH_SHORT).show()

                             Utils.saveRecoveryEmail(activity, emailData.data?.email.toString())

                             try {
                                 val action = EmailFragmentDirections.actionEmailFragmentToLockPatternFragment()
                                 findNavController().navigate(action)
                             }catch (e: Exception){
                                 Log.i(Utils.TAG, "inIt: ${e.message}")
                             }

                         }*/
                            else {
                                Toast.makeText(
                                    activity,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        } else if (response.code() == 400) {
                            addRecoveryEmail(email, dialog)
                        } else {
                            dialog.dismiss()
                            Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        mBinding.etRecoveryEmail.setText("")
                    }

                    override fun onFailure(
                        call: Call<CheckEmailResponse>,
                        t: Throwable,
                    ) {
                        dialog.dismiss()
                        Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }
    }

    private fun addRecoveryEmail(
        email: String,
        dialog: Dialog,
    ) {
        val emailBody = RecoveryEmailBody(email, "1")

        val emailApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val emailCAll = emailApi.addRecoveryEmail(emailBody)

        coroutineScope.launch {
            emailCAll.enqueue(
                object : Callback<RecoveryEmailResponse> {
                    override fun onResponse(
                        call: Call<RecoveryEmailResponse>,
                        response: Response<RecoveryEmailResponse>,
                    ) {
                        if (response.code() == 201) {
                            dialog.dismiss()

                            val recoveryEmailData = response.body()

                            if (recoveryEmailData != null) {
                                Toast.makeText(
                                    activity,
                                    "Recovery email added successfully ${recoveryEmailData.data?.email}",
                                    Toast.LENGTH_SHORT,
                                ).show()

                                Utils.saveRecoveryEmail(
                                    activity,
                                    recoveryEmailData.data?.email.toString(),
                                )

                                try {
                                    val action =
                                        EmailFragmentDirections.actionEmailFragmentToLockPatternFragment()
                                    findNavController().navigate(action)
                                } catch (e: Exception) {
                                    Log.i(Utils.TAG, "inIt: ${e.message}")
                                }
                            } else {
                                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else if (response.code() == 200) {
                            dialog.dismiss()

                            val recoveryEmailData = response.body()

                            if (recoveryEmailData != null) {
                                if (!recoveryEmailData.data?.passcode.isNullOrEmpty()) {
                                    Toast.makeText(
                                        activity,
                                        "Your recovery email already added, an email has been sent please check",
                                        Toast.LENGTH_LONG,
                                    ).show()

                                    Utils.saveRecoveryEmail(
                                        activity,
                                        recoveryEmailData.data?.email.toString(),
                                    )
                                    Utils.passCode = recoveryEmailData.data?.passcode.toString()

                                    try {
                                        val action =
                                            EmailFragmentDirections.actionEmailFragmentToConfirmLockFragment()
                                        findNavController().navigate(action)
                                    } catch (e: Exception) {
                                        Log.i(Utils.TAG, "inIt: ${e.message}")
                                    }
                                } else {
                                    Utils.saveRecoveryEmail(
                                        activity,
                                        recoveryEmailData.data?.email.toString(),
                                    )
                                    try {
                                        val action =
                                            EmailFragmentDirections.actionEmailFragmentToLockPatternFragment()
                                        findNavController().navigate(action)
                                    } catch (e: Exception) {
                                        Log.i(Utils.TAG, "inIt: ${e.message}")
                                    }
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
                        call: Call<RecoveryEmailResponse>,
                        t: Throwable,
                    ) {
                        dialog.dismiss()
                        Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
