package com.codecoy.balancelauncherapp.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codecoy.balancelauncherapp.data.responsemodel.CheckEmailBody
import com.codecoy.balancelauncherapp.data.responsemodel.CheckEmailResponse
import com.codecoy.balancelauncherapp.data.responsemodel.RecoveryEmailBody
import com.codecoy.balancelauncherapp.data.responsemodel.RecoveryEmailResponse
import com.codecoy.balancelauncherapp.databinding.ActivityEmailBinding
import com.codecoy.balancelauncherapp.network.ApiCall
import com.codecoy.balancelauncherapp.utils.Constant
import com.codecoy.balancelauncherapp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val goBack = false

    private lateinit var mBinding: ActivityEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        inIt()
    }

    private fun inIt() {
        mBinding.btnContinue.setOnClickListener {
            checkCredentials()
        }

        mBinding.ivBack.setOnClickListener {
            val intent = Intent(this@EmailActivity, LockPatternActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkCredentials() {
        val email = mBinding.etRecoveryEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your recovery email!", Toast.LENGTH_SHORT).show()
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter valid recovery email!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                checkEmail(email)
            }
        }
    }

    private fun checkEmail(email: String) {
        val dialog = Constant.getDialog(this)
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
                            val emailData = response.body()

                            if (emailData != null) {
                                addRecoveryEmail(email, dialog)
                            } else {
                                Toast.makeText(
                                    this@EmailActivity,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        } else if (response.code() == 400) {
                            dialog.dismiss()
                            mBinding.tvError.text =
                                "This email is not linked to any account. Please enter a valid email."

                            mBinding.etRecoveryEmail.setText("")
                        } else {
                            dialog.dismiss()
                            Toast.makeText(this@EmailActivity, response.message(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(
                        call: Call<CheckEmailResponse>,
                        t: Throwable,
                    ) {
                        dialog.dismiss()
                        Toast.makeText(this@EmailActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }
    }

    private fun addRecoveryEmail(
        email: String,
        dialog: Dialog,
    ) {
        val emailBody = RecoveryEmailBody(email, "0")

        val emailApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val emailCAll = emailApi.addRecoveryEmail(emailBody)

        coroutineScope.launch {
            emailCAll.enqueue(
                object : Callback<RecoveryEmailResponse> {
                    override fun onResponse(
                        call: Call<RecoveryEmailResponse>,
                        response: Response<RecoveryEmailResponse>,
                    ) {
                        if (response.code() == 200) {
                            dialog.dismiss()

                            val recoveryEmailData = response.body()

                            if (recoveryEmailData != null) {
                                if (!recoveryEmailData.data?.passcode.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@EmailActivity,
                                        "An email has been sent please check",
                                        Toast.LENGTH_LONG,
                                    ).show()

                                    try {
                                        Utils.saveConfirmPasscode(
                                            this@EmailActivity,
                                            recoveryEmailData.data?.passcode.toString(),
                                        )

                                        val intent =
                                            Intent(this@EmailActivity, LockPatternActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } catch (e: Exception) {
                                        Log.i(Utils.TAG, "inIt: ${e.message}")
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@EmailActivity,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        } else {
                            dialog.dismiss()
                            Toast.makeText(this@EmailActivity, response.message(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(
                        call: Call<RecoveryEmailResponse>,
                        t: Throwable,
                    ) {
                        dialog.dismiss()
                        Toast.makeText(this@EmailActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun onBackPressed() {
        if (goBack) {
            super.onBackPressed()
        }
    }
}
