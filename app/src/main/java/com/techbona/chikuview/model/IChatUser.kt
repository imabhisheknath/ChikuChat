
package com.techbona.chikuview.model
import android.graphics.Bitmap

interface IChatUser {
    fun getId(): String
    fun getName(): String?
    fun getIcon(): Bitmap?
    fun setIcon(bmp: Bitmap)
}
