package com.codecoy.balancelauncherapp.ui.adapters
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.balancelauncherapp.callbacks.AppLauncherCallback
import com.codecoy.balancelauncherapp.databinding.InstallAppLayBinding
import com.codecoy.balancelauncherapp.roomdb.entities.AppsDataEntity

class InstalledAppsAdapter(
    private val context: Context? = null,
    private var appList: MutableList<AppsDataEntity>? = null,
    private val launcherCallback: AppLauncherCallback? = null,
) : RecyclerView.Adapter<InstalledAppsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val mBinding = InstallAppLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val appInfo = appList?.get(position)

        holder.mBinding.tvApp.text = appInfo?.appName

        if (appInfo?.notCount!! > 0)
            {
                holder.mBinding.ivDot.visibility = View.VISIBLE
            } else {
            holder.mBinding.ivDot.visibility = View.GONE
        }

        holder.mBinding.tvApp.setOnClickListener {
            launcherCallback?.launchApp(appInfo.packageName.toString())
        }

        holder.mBinding.tvApp.setOnLongClickListener {
            launcherCallback?.manageApp(appInfo.packageName.toString(), holder.mBinding)
            true
        }
    }

    override fun getItemCount(): Int {
        return appList?.size ?: 0
    }

    class ViewHolder(val mBinding: InstallAppLayBinding) : RecyclerView.ViewHolder(mBinding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterList: ArrayList<AppsDataEntity>) {
        appList = filterList
        notifyDataSetChanged()
    }
}
