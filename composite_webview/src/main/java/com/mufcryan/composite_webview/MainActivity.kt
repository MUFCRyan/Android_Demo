package com.mufcryan.composite_webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val mWebView: WebView? = null
    private var mMyJavaScriptInterface: WebAppInterface? = null

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView.settings.javaScriptEnabled = true
        mMyJavaScriptInterface = WebAppInterface()
        //webView.addJavascriptInterface(mMyJavaScriptInterface, "AndroidFunction")
        webView.webViewClient = WebClient()
        var url1 = "http://h5.sns.ivydad.com.cn/detail/297?name=文章详情"
        var url2 = "http://exploiter.ivydad.com/invite/newFriend/e2cf0e15547711e68ed590e2baab7e5c"
        webView.loadUrl(url1)
    }

    internal inner class WebClient : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            view.loadUrl("javascript:AndroidFunction.resize(document.body.scrollHeight)")
        }
    }

    /**
     * WebView interface to communicate with Javascript
     */
    inner class WebAppInterface {

        @JavascriptInterface
        fun resize(height: Float) {
            val webViewHeight = height * resources.displayMetrics.density
            tv_bottom.text = "webView 高度为 : " + webViewHeight
        }
    }
}
