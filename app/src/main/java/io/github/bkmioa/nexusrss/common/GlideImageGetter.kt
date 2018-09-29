package io.github.bkmioa.nexusrss.common

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.widget.TextView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import io.github.bkmioa.nexusrss.base.GlideApp
import java.lang.ref.WeakReference
import java.net.URI

class GlideImageGetter(private val container: TextView,
                       private val baseUrl: String? = null,
                       private var matchParentWidth: Boolean = false) : Html.ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val urlDrawable = UrlDrawable()

        val url = if (baseUrl != null) {
            URI.create(baseUrl).resolve(source.trim()).toString()
        } else {
            source
        }

        val maxWidth = container.width - container.paddingLeft - container.paddingRight
        val target = ImageGetterTarget(urlDrawable, this, container, matchParentWidth, maxWidth)

        GlideApp.with(container.context)
                .load(url)
                .fitCenter()
                .into(target)

        return urlDrawable
    }


    private class ImageGetterTarget(urlDrawable: UrlDrawable,
                                    imageGetter: GlideImageGetter,
                                    container: View,
                                    private val matchParentWidth: Boolean,
                                    maxWidth: Int) : SimpleTarget<Drawable>(maxWidth, Target.SIZE_ORIGINAL) {

        private val drawableReference: WeakReference<UrlDrawable> = WeakReference(urlDrawable)
        private val imageGetterReference: WeakReference<GlideImageGetter> = WeakReference(imageGetter)
        private val containerReference: WeakReference<View> = WeakReference(container)
        private var scale: Float = 1f


        private fun getScale(drawable: Drawable): Float {
            val container = containerReference.get()
            if (!matchParentWidth || container == null) {
                return 1f
            }
            val maxWidth = container.width.toFloat() - container.paddingLeft - container.paddingRight
            val originalDrawableWidth = drawable.intrinsicWidth.toFloat()
            return maxWidth / originalDrawableWidth
        }

        override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>) {
            scale = getScale(drawable)
            drawable.setBounds(0, 0, (drawable.intrinsicWidth * scale).toInt(), (drawable.intrinsicHeight * scale).toInt())

            val urlDrawable = drawableReference.get() ?: return

            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, (drawable.intrinsicWidth * scale).toInt(), (drawable.intrinsicHeight * scale).toInt())

            // change the reference of the current drawable to the result from the HTTP call
            urlDrawable.drawable = drawable

            val imageGetter = imageGetterReference.get() ?: return

            // redraw the image by invalidating the container
            imageGetter.container.invalidate()

            // re-set text to fix images overlapping text
            imageGetter.container.text = imageGetter.container.text
        }

    }

    inner class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            // override the draw to facilitate refresh function later
            drawable?.draw(canvas)
        }
    }
}
