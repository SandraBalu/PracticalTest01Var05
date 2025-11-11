package ro.pub.cs.systems.eim.practicaltest01var05

import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import java.util.Random

class ProcessingThread(private val context: Context) : Thread() {

    @Volatile
    private var isRunning = true

    private val random = Random()

    override fun run() {
        Log.d(
            "ProcessingThread",
            "Thread has started! PID: ${Process.myPid()} TID: ${Process.myTid()}"
        )

        // Rulează până când este oprit de serviciu
        while (isRunning) {
            sendMessage()
            sleepOneSecond()
        }

        Log.d("ProcessingThread", "Thread has finished!")
    }

    private fun sendMessage() {
        // Generează 4 numere random
        val n1 = random.nextInt(0, 100)
        val n2 = random.nextInt(0, 100)
        val n3 = random.nextInt(0, 100)
        val n4 = random.nextInt(0, 100)

        // Intent de broadcast cu acțiune custom
        val intent = Intent().apply {
            action = "com.example.practicaltest01vr07.PROCESSING_THREAD"
            putExtra("input1", n1)
            putExtra("input2", n2)
            putExtra("input3", n3)
            putExtra("input4", n4)
        }

        Log.d(
            "ProcessingThread",
            "Sending broadcast: $n1, $n2, $n3, $n4"
        )

        // Trimite broadcast-ul
        context.sendBroadcast(intent)
    }

    private fun sleepOneSecond() {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun stopThread() {
        isRunning = false
        interrupt()
    }
}
