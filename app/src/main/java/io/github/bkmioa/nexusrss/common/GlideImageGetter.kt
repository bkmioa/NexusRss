package io.github.bkmioa.nexusrss.common

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import io.github.bkmioa.nexusrss.base.GlideApp
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.lang.ref.WeakReference
import java.net.URI

class GlideImageGetter : Html.ImageGetter {
    private var container: TextView
    private var baseUri: URI? = null
    private var matchParentWidth: Boolean = false

    constructor(textView: TextView) {
        this.container = textView
        this.matchParentWidth = false
    }

    constructor(textView: TextView, baseUrl: String?) {
        this.container = textView
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl)
        }
    }

    constructor(textView: TextView, baseUrl: String?, matchParentWidth: Boolean) {
        this.container = textView
        this.matchParentWidth = matchParentWidth
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl)
        }
    }

    override fun getDrawable(source: String): Drawable {
        val urlDrawable = UrlDrawable()

        // get the actual source
        val asyncTask = ImageGetterAsyncTask(urlDrawable, this, container, matchParentWidth)

        asyncTask.load(source)

        // return reference to URLDrawable which will asynchronously load the image specified in the src tag
        return urlDrawable
    }

    /**
     * Static inner [AsyncTask] that keeps a [WeakReference] to the [UrlDrawable]
     * and [HtmlHttpImageGetter].
     *
     *
     * This way, if the AsyncTask has a longer life span than the UrlDrawable,
     * we won't leak the UrlDrawable or the HtmlRemoteImageGetter.
     */
    private class ImageGetterAsyncTask(urlDrawable: UrlDrawable, imageGetter: GlideImageGetter, container: View, private val matchParentWidth: Boolean) : SimpleTarget<Drawable>() {
        private val drawableReference: WeakReference<UrlDrawable> = WeakReference(urlDrawable)
        private val imageGetterReference: WeakReference<GlideImageGetter> = WeakReference(imageGetter)
        private val containerReference: WeakReference<View> = WeakReference(container)
        private var source: String? = null
        private var scale: Float = 1f


        private fun onPostExecute(result: Drawable?) {
            if (result == null) {
                Log.w(HtmlTextView.TAG, "Drawable result is null! (source: $source)")
                return
            }
            val urlDrawable = drawableReference.get() ?: return
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, (result.intrinsicWidth * scale).toInt(), (result.intrinsicHeight * scale).toInt())

            // change the reference of the current drawable to the result from the HTTP call
            urlDrawable.drawable = result

            val imageGetter = imageGetterReference.get() ?: return
            // redraw the image by invalidating the container
            imageGetter.container.invalidate()
            // re-set text to fix images overlapping text
            imageGetter.container.text = imageGetter.container.text
        }


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
            onPostExecute(drawable)
        }

        internal fun load(source: String) {
            this.source = source
            val url: String

            val imageGetter = imageGetterReference.get() ?: return
            if (imageGetter.baseUri != null) {
                url = imageGetter.baseUri!!.resolve(source).toString()
            } else {
                url = source
            }

            val container = this.containerReference.get()
            if (container != null) {
                GlideApp.with(container.context)
                        .load(url)
                        .into(this)
            }
        }
    }

    inner class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable!!.draw(canvas)
            }
        }
    }
}
