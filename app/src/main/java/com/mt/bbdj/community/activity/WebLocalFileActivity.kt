package com.mt.bbdj.community.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import com.mt.bbdj.R
import kotlinx.android.synthetic.main.activity_web_local_file.*

class WebLocalFileActivity : AppCompatActivity(R.layout.activity_web_local_file){


    companion object {
        fun openActivity(activity: Activity, htmlFile :String, title: String) {
            Intent().apply {
                setClass(activity, WebLocalFileActivity::class.java)
                putExtra("htmlFile", htmlFile)
                putExtra("title", title)
                activity.startActivityForResult(this, 1)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv_title.text = intent.getStringExtra("title")

        val webSettings: WebSettings = webView.settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.settings.safeBrowsingEnabled = false
        }


        //支持屏幕缩放
        webSettings.setSupportZoom(false)
        webSettings.textZoom = 100
        webSettings.builtInZoomControls = false
        webView.isVerticalScrollBarEnabled = false

        val file = intent.getStringExtra("htmlFile")
//        webview.loadUrl("file:///android_asset/file.html")
        webView.loadUrl("file:///android_asset/web_file/$file")

    }
}