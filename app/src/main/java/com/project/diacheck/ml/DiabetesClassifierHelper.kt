package com.project.diacheck.ml

import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DiabetesClassifierHelper(
    val context: Context,
    private val modelName: String = "diabetes.tflite"
) {
    private var interpreter: Interpreter? = null

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val assetFileDescriptor = context.assets.openFd(modelName)
            val inputStream = assetFileDescriptor.createInputStream()
            val model = ByteArray(assetFileDescriptor.length.toInt())
            inputStream.read(model)
            inputStream.close()

            val buffer = ByteBuffer.allocateDirect(model.size)
            buffer.order(ByteOrder.nativeOrder())
            buffer.put(model)

            interpreter = Interpreter(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun normalizeInput(input: FloatArray): FloatArray {
        val scalerJson = context.assets.open("scaler.json").bufferedReader().use { it.readText() }
        val scalerParams = JSONObject(scalerJson)

        val mean = scalerParams.getJSONArray("mean")
        val scale = scalerParams.getJSONArray("scale")

        return input.mapIndexed { i, value ->
            (value - mean.getDouble(i).toFloat()) / scale.getDouble(i).toFloat()
        }.toFloatArray()
    }

    fun predict(inputData: FloatArray): String {
        if (interpreter == null) {
            throw IllegalStateException("Model belum diinisialisasi.")
        }

        val normalizedInput = normalizeInput(inputData)

        val inputShape = interpreter!!.getInputTensor(0).shape() // [1, 7]
        val outputShape = interpreter!!.getOutputTensor(0).shape() // [1, 1]

        val inputBuffer = ByteBuffer.allocateDirect(4 * inputShape[1])
        inputBuffer.order(ByteOrder.nativeOrder())
        for (value in normalizedInput) {
            inputBuffer.putFloat(value)
        }

        val outputBuffer = ByteBuffer.allocateDirect(4 * outputShape[1])
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter!!.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val results = FloatArray(outputShape[1])
        outputBuffer.asFloatBuffer().get(results)

        val percentage = (results[0] * 100).toInt()

        return "$percentage%"
    }
}
