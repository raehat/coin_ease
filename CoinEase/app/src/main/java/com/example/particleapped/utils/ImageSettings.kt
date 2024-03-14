package com.example.particleapped.utils

import com.microblink.blinkid.entities.recognizers.Recognizer
import com.microblink.blinkid.entities.recognizers.blinkid.imageoptions.FaceImageOptions
import com.microblink.blinkid.entities.recognizers.blinkid.imageoptions.FullDocumentImageOptions

object ImageSettings {
    fun enableAllImages(recognizer: Recognizer<*>): Recognizer<*> {
        if (recognizer is FullDocumentImageOptions) {
            val options = recognizer as FullDocumentImageOptions
            options.setReturnFullDocumentImage(true)
        }
        if (recognizer is FaceImageOptions) {
            val options = recognizer as FaceImageOptions
            options.setReturnFaceImage(true)
        }
        return recognizer
    }
}
