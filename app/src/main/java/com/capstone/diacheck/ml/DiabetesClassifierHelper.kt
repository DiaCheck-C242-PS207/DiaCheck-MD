package com.capstone.diacheck.ml

import android.content.Context

class DiabetesClassifierHelper(
    var threshold: Float = 0.1f,
    var maxResults: Int = 3,
    val modelName: String = "diabetes.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var diabetesClassifier: DiabetesClassifier? = null

    init {
        setupDiabetesClassifier()
    }

    private fun setupDiabetesClassifier() {
        val optionsBuilder = DiabetesClassifier.DiabetesClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            diabetesClassifier = DiabetesClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    companion object {
        private const val TAG = "DiabetesClassifierHelper"
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }
}