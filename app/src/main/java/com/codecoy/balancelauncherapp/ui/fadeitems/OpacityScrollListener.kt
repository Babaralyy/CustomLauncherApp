package com.codecoy.balancelauncherapp.ui.fadeitems

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OpacityScrollListener(private val recyclerView: RecyclerView) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(
        recyclerView: RecyclerView,
        dx: Int,
        dy: Int,
    ) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        changeOpacityOfVisibleItems(firstVisibleItemPosition)
    }

    private fun changeOpacityOfVisibleItems(firstVisibleItemPosition: Int) {
        val childCount = recyclerView.childCount

        val alphaFactor = 0.5f // Adjust the opacity factor as needed

        if (childCount <= 6) {
            for (i in 0 until childCount) {
                val child = recyclerView.getChildAt(i)

                child.alpha = 1f
            }
        } else {
            for (i in 0 until childCount) {
                val child = recyclerView.getChildAt(i)

                if (i <= childCount - 3) {
                    child.alpha = 1f
                } else {
                    child.alpha = alphaFactor
                }
            }
        }
    }
}
