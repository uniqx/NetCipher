/*
 * Copyright (c) 2018 Michael Pöhn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.netcipher.webkit;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.guardianproject.netcipher.webkit.WebkitProxy;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        boolean proxySuccess = false;
        webView = (WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new TestWebViewClient());
        //webView.setWebChromeClient(new TestWebChromeClient());

        try {
            proxySuccess = false; // WebkitProxy.setProxy(SampleApplication.class.getName(), this.getApplicationContext(), webView, "localhost", 8118);
            webView.loadUrl("https://guardianproject.info/code/netcipher/");
        } catch (Exception e) {
            Log.e("###", "Could not start WebkitProxy", e);
        }

        TextView status = (TextView) findViewById(R.id.status);
        if (proxySuccess) {
            status.setText(String.format("(localhost:8118) WebView proxy setup successful"));
        } else {
            status.setText(String.format("(localhost:8118) WebView proxy setup NOT successful"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    class TestWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

            Log.i("###", "intercept!");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    //URLConnection con = new URL(request.getUrl().toString()).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8118)));
                    URLConnection con = new URL(request.getUrl().toString()).openConnection();
                    Log.i("###", "url=" + con.getURL().toString() +
                            "\ncontentType=" + con.getContentType());
                    Log.i("###", "contentType=" + con.getContentType());
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    String mimeType = con.getContentType().split("; ")[0];
                    WebResourceResponse response = new WebResourceResponse(mimeType, con.getContentEncoding(), in);
                    Map<String, String> h = new HashMap<>();
                    for (String key : con.getHeaderFields().keySet()) {
                        h.put(key, con.getHeaderField(key));
                    }
                    //response.setResponseHeaders(h);
                    return response;

                    //InputStream in = new ByteArrayInputStream("<html><body><h1>Intercepted!</h1></body></html>".getBytes("utf-8"));
                    //return new WebResourceResponse("text/html", "utf-8", in);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }

}
