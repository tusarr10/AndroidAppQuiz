package com.wrteam.quiz.helper;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Dell on 25-01-2016.
 */
public class MathJaxWebView extends WebView {

    public MathJaxWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        clearCache(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(Color.TRANSPARENT);
        getSettings().setJavaScriptEnabled(true);
        setHorizontalScrollBarEnabled(false);
/*        getSettings().setBuiltInZoomControls(false);
        setHorizontalScrollBarEnabled(false);*/

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setText(final String text) {


        loadDataWithBaseURL("http://bar",
                "<head>" +
                        "<style>" +
                        "img{max-width:100%}\r\n" +
                        "audio{background:#2888e1;padding:10px;height: 47px;}" +
                        "</style>" +
                        "<script type=\"text/x-mathjax-config\">\n" +
                        "  MathJax.Hub.Config({\n" +
                        "    extensions: [\"tex2jax.js\"],\n" +
                        "    tex2jax: {\n" +
                        "      inlineMath: [ ['$','$'], [\"\\\\(\",\"\\\\)\"] ],\n" +
                        "      displayMath: [ ['$$','$$'], [\"\\\\[\",\"\\\\]\"] ],\n" +
                        "      processEscapes: true\n" +
                        "    },\n" +
                        "    \"CommonHTML\": { linebreaks: { automatic: true } }\n" +
                        "  });\n" +
                        "</script>\n" +
                        "<script src=\"//mathjax.rstudio.com/latest/MathJax.js?config=TeX-MML-AM_CHTML\"></script>" +
                        /* "<script type=\"text/javascript\" async src=\"file:///android_asset/MathJax/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>" +*/
                        "" +
                        "</head>" +
                        "<body style=\"text-align:center\">" +
                        text +
                        "</body>" +
                        "</html>", "text/html", "utf-8", "");

        Log.v("text", text);

    }

}
