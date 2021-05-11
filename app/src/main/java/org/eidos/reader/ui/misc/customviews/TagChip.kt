package org.eidos.reader.ui.misc.customviews

import android.content.Context
import com.google.android.material.chip.Chip

/**
 * This class must be called programmatically and not from XML as I have only included the
 * programmatic constructor.
 * WARNING: Do not change the text variable of this object.
 */
class TagChip(context: Context) : Chip(context) {
    /*
    TODO: probably needs to override onMeasure().
    Basically I want this chip to truncate its own text based on the width of the parent viewgroup.
    Naive solution: Hardcode. Can be used at first.
    Ideal solution: Get parent width and then truncate text based on its width (not char length).
     */

    var tag: String = ""
        set(tag: String) {
            // TODO: update the text variable of parent class based on the available length?
            // TODO: alternatively, set as per normal. everything will be handled on onMeasure().
            field = tag
            super.setText(tag)
        }

    // The below variable is meant to cockblock attempts to access text. Delete if necessary.
    private var text = ""

}