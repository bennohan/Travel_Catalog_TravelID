package com.bennohan.travelcatalogtravelid.helper

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bennohan.travelcatalogtravelid.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File

class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrl: String?) {

            view.setImageDrawable(null)

            if (imageUrl.isNullOrEmpty()) {
                //Do nothing

            } else {
                imageUrl.let {
                    Glide
                        .with(view.context)
                        .load(imageUrl)
                        .apply(RequestOptions.centerCropTransform())
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(view)
                }

            }

        }
        @JvmStatic
        @BindingAdapter(value = ["imageUrlCircle"], requireAll = false)
        fun loadImageRecipeCircle(view: ImageView, imageUrl: String?) {

            view.setImageDrawable(null)

            if (imageUrl.isNullOrEmpty()) {
                Glide
                    .with(view.context)
                    .load(imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(view)


            } else {
                imageUrl.let {
                    Glide
                        .with(view.context)
                        .load(imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(view)

                }

            }


        }

        fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100) {
            outputStream().use { out ->
                bitmap.compress(format, quality, out)
                out.flush()
            }
        }


    }
}