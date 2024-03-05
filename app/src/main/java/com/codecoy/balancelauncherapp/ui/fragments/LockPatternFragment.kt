package com.codecoy.balancelauncherapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.FragmentLockPatternBinding
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG

class LockPatternFragment : Fragment() {
    private lateinit var activity: MainActivity

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
                        Utils.savePasscode(activity, text)
                        try {
                            val action = LockPatternFragmentDirections.actionLockPatternFragmentToConfirmLockFragment()
                            findNavController().navigate(action)
                            mBinding.etPasscode.setText("")
                        } catch (e: Exception) {
                            Log.i(TAG, "afterTextChanged: ${e.message}")
                        }
                    }
            }
        }

    private lateinit var mBinding: FragmentLockPatternBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentLockPatternBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
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
