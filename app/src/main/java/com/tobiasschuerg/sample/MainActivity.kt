package com.tobiasschuerg.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.setOnClickListener { resetProgress() }
    }

    private fun resetProgress() {
        job?.cancel()

        val sec = 20
        val steps = 50

        progress.progressMax = sec * steps
        // progress.getText = { progress: Int, max: Int, percentage: Float -> (progress / steps).toString() + "s" }

        job = GlobalScope.launch {
            for (i in (sec * steps) downTo 0) {
                launch(Dispatchers.Main) {
                    Log.i("progress", i.toString())
                    progress.setProgress(i % (sec * steps + 1))
                }
                delay(1000 / steps.toLong())
            }
        }
    }

}
