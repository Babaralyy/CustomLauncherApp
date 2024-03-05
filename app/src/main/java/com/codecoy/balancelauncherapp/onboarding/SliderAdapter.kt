package com.codecoy.balancelauncherapp.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.codecoy.balancelauncherapp.R

class SliderAdapter(private val context: Context) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    private var images =
        intArrayOf(
            R.drawable.mob_apps,
            R.drawable.options_template_,
            R.drawable.no_news,
        )

    private var descriptionArray =
        arrayOf(
            "Your new home screen. \n" +
                "It’s that simple.",
            "Everything you need",
            "without everything you don’t.",
        )

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any,
    ): Boolean {
        return view == `object`
    }

    override fun instantiateItem(
        container: ViewGroup,
        position: Int,
    ): Any {
        layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slides_layout, container, false)

        val sliderImage = view.findViewById<ImageView>(R.id.slider_image)

        val tvDes = view.findViewById<TextView>(R.id.tvDes)

        sliderImage.setImageResource(images[position])

        tvDes.text = descriptionArray[position]

        container.addView(view)

        return view
    }

    override fun destroyItem(
        collection: ViewGroup,
        position: Int,
        view: Any,
    ) {
        collection.removeView(view as View)
    }
}
