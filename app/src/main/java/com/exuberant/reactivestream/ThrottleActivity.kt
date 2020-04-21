package com.exuberant.reactivestream

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_throttle.*
import java.util.concurrent.TimeUnit

class ThrottleActivity : AppCompatActivity() {

    private var countForThrottled = 0
    private var countForNonThrottled = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_throttle)
        //sugar()
        nonSugar()
    }

    @SuppressLint("CheckResult")
    private fun nonSugar() {
        val emitter = PublishSubject.create<View>()
        button.setOnClickListener {
            emitter.onNext(it)
        }

        //Observer have 4 lifecycle states
        val observer = object : Observer<View> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: View) {
                incrementThrottled()
            }

            override fun onError(e: Throwable) {}

        }

        //Consumer has only 1 lifecycle state
        val consumer = object : Consumer<View> {
            override fun accept(t: View?) {
                incrementThrottled()
            }

        }

        emitter
            .map(object : io.reactivex.functions.Function<View, View> {
                override fun apply(t: View): View {
                    incrementNonThrottled()
                    return t
                }
            })
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe(consumer)
    }

    @SuppressLint("CheckResult")
    private fun sugar() {
        button.clicks()
            .map {
                incrementNonThrottled()
            }
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                incrementThrottled()
            }

        //Debounce resets timer while Throttle counts cumulative time
    }

    private fun incrementThrottled() {
        countForThrottled++
        throttledCount.text = countForThrottled.toString()
    }

    private fun incrementNonThrottled() {
        countForNonThrottled++
        nonThrottledCount.text = countForNonThrottled.toString()
    }

}