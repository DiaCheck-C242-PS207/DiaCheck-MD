package com.capstone.diacheck.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DiabetesClassifierHelper(
    val context: Context,
    val modelName: String = "diabetes.tflite"
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

    fun predict(inputData: FloatArray): FloatArray? {
        if (interpreter == null) {
            throw IllegalStateException("Model belum diinisialisasi.")
        }

        val inputShape = interpreter!!.getInputTensor(0).shape() // [1, 7]
        val outputShape = interpreter!!.getOutputTensor(0).shape() // [1, 1]

        val inputBuffer = ByteBuffer.allocateDirect(4 * inputShape[1])
        inputBuffer.order(ByteOrder.nativeOrder())
        for (value in inputData) {
            inputBuffer.putFloat(value)
        }

        val outputBuffer = ByteBuffer.allocateDirect(4 * outputShape[1])
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter!!.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val results = FloatArray(outputShape[1])
        outputBuffer.asFloatBuffer().get(results)

        return results
    }

    fun close() {
        interpreter?.close()
    }
}
