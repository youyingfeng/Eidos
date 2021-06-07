package org.eidos.reader.ui.misc.utilities

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import coil.ImageLoader
import coil.request.ImageRequest

class URLImageGetter
    constructor(
        private val textView: TextView,     // FIXME: this is a potential memory leak!
        private val imageLoader: ImageLoader,
        private val urlFormatter: ((String) -> String)? = null
    )
    : Html.ImageGetter {
    override fun getDrawable(source: String): Drawable {
        val formattedURL = urlFormatter?.invoke(source) ?: source

        val drawablePlaceHolder = DrawablePlaceHolder()

        imageLoader.enqueue(
            ImageRequest.Builder(textView.context)
                .data(formattedURL)
                .target {
                    result ->
                        drawablePlaceHolder.updateDrawable(result)
                        // experimental: this should work
                        textView.invalidateDrawable(drawablePlaceHolder)
                        // if above doesn't work, comment above and uncomment one of the below
                        textView.invalidate()
//                        textView.text = textView.text

                }
                .build()
        )

        return drawablePlaceHolder
    }

    private class DrawablePlaceHolder : BitmapDrawable() {

        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }

        fun updateDrawable(drawable: Drawable) {
            // FIXME: drawable should expand to width of parent
            this.drawable = drawable
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            drawable.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)
        }
    }
}