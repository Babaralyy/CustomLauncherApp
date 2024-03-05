package com.codecoy.balancelauncherapp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.ActivityLockPatternBinding
import com.codecoy.balancelauncherapp.utils.Utils

class LockPatternActivity : AppCompatActivity() {
    private val isBack = false
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
                if (text.length == 4)
                    {
                        if (previousPass == text)
                            {
                                try {
                                    uninstallApp(this@LockPatternActivity.packageName)
                                    Utils.isReadyToUninstall = false
                                    finish()
                                } catch (e: Exception) {
                                    Log.i(Utils.TAG, "afterTextChanged: ${e.message}")
                                }
                            } else {
                            Toast.makeText(this@LockPatternActivity, "Password mismatched", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    private lateinit var mBinding: ActivityLockPatternBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLockPatternBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        inIt()
    }

    private fun inIt() {
        clickOnKeyPad()

        previousPass = Utils.fetchConfirmPasscode(this)

        mBinding.etPasscode.addTextChangedListener(textWatcher)

        mBinding.btnClose.setOnClickListener {
            finish()
        }

        mBinding.btnCutText.setOnClickListener {
            val originalText = mBinding.etPasscode.text.toString().trim()

            if (originalText.isNotEmpty()) {
                val editedText = originalText.substring(0, originalText.length - 1)
                mBinding.etPasscode.setText(editedText)
            }
        }

        mBinding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this@LockPatternActivity, EmailActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun uninstallApp(packageName: String) {
        val intent =
            Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
            }

        intent.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
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

    override fun onBackPressed() {
        if (isBack)
            {
                super.onBackPressed()
            }
    }
}
