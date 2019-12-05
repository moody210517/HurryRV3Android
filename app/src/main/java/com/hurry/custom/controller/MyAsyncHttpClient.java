package com.hurry.custom.controller;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.PreemtiveAuthorizationHttpRequestInterceptor;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 6/23/2018.
 */

public class MyAsyncHttpClient {
    public static final String LOG_TAG = "MyAsyncHttpClient";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_RANGE = "Content-Range";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ENCODING_GZIP = "gzip";
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int DEFAULT_MAX_RETRIES = 5;
    public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    private int maxConnections;
    private int connectTimeout;
    private int responseTimeout;
    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private ExecutorService threadPool;
    private final Map<Context, List<RequestHandle>> requestMap;
    private final Map<String, String> clientHeaderMap;
    private boolean isUrlEncodingEnabled;

    public MyAsyncHttpClient() {
        this(false, 80, 443);
    }

    public MyAsyncHttpClient(int httpPort) {
        this(false, httpPort, 443);
    }

    public MyAsyncHttpClient(int httpPort, int httpsPort) {
        this(false, httpPort, httpsPort);
    }

    public MyAsyncHttpClient(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
        this(getDefaultSchemeRegistry(fixNoHttpResponseException, httpPort, httpsPort));
    }

    private static SchemeRegistry getDefaultSchemeRegistry(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
        if(fixNoHttpResponseException) {
            Log.d("MyAsyncHttpClient", "Beware! Using the fix is insecure, as it doesn't verify SSL certificates.");
        }

        if(httpPort < 1) {
            httpPort = 80;
            Log.d("MyAsyncHttpClient", "Invalid HTTP port number specified, defaulting to 80");
        }

        if(httpsPort < 1) {
            httpsPort = 443;
            Log.d("MyAsyncHttpClient", "Invalid HTTPS port number specified, defaulting to 443");
        }

        SSLSocketFactory sslSocketFactory = null;
        if(fixNoHttpResponseException) {
            sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
        } else {
            //sslSocketFactory = SSLSocketFactory.getSocketFactory();

            try {
                sslSocketFactory  = new SimpleSSLSocketFactory(null);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            }
            sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //sslSocketFactory = MySSLSocketFactory.getSocketFactory();
//            try{
//                SSLContext sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, null, null);
//                SocketFactory socketFactory = sslContext.getSocketFactory();
//
//            }catch (Exception e){};
//            try{
//                SSLContext sslcontext = SSLContext.getInstance("TLSv1");
//                sslcontext.init(null, null, null);
//                sslSocketFactory = new MySSLSocketFactory(sslcontext.getSocketFactory());
//            }catch (Exception e){};
        }

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), httpPort));
        schemeRegistry.register(new Scheme("https", sslSocketFactory, httpsPort));
        return schemeRegistry;
    }

    public MyAsyncHttpClient(SchemeRegistry schemeRegistry) {
        this.maxConnections = 10;
        this.connectTimeout = 10000;
        this.responseTimeout = 10000;
        this.isUrlEncodingEnabled = true;
        BasicHttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, (long)this.connectTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, 10);

        HttpConnectionParams.setSoTimeout(httpParams, this.responseTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.connectTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        this.threadPool = this.getDefaultThreadPool();
        this.requestMap = Collections.synchronizedMap(new WeakHashMap());
        this.clientHeaderMap = new HashMap();
        this.httpContext = new SyncBasicHttpContext(new BasicHttpContext());


        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        this.httpClient =  new DefaultHttpClient(cm, httpParams); //new MyHttpClient(httpParameters, null);//new DefaultHttpClient(cm, httpParams);

        this.httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                if(!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }

                String header;
                for(Iterator i$ = MyAsyncHttpClient.this.clientHeaderMap.keySet().iterator(); i$.hasNext(); request.addHeader(header, (String) MyAsyncHttpClient.this.clientHeaderMap.get(header))) {
                    header = (String)i$.next();
                    if(request.containsHeader(header)) {
                        Header overwritten = request.getFirstHeader(header);
                        Log.d("MyAsyncHttpClient", String.format("Headers were overwritten! (%s | %s) overwrites (%s | %s)", new Object[]{header, MyAsyncHttpClient.this.clientHeaderMap.get(header), overwritten.getName(), overwritten.getValue()}));
                        request.removeHeader(overwritten);
                    }
                }

            }
        });
        this.httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                HttpEntity entity = response.getEntity();
                if(entity != null) {
                    Header encoding = entity.getContentEncoding();
                    if(encoding != null) {
                        HeaderElement[] arr$ = encoding.getElements();
                        int len$ = arr$.length;

                        for(int i$ = 0; i$ < len$; ++i$) {
                            HeaderElement element = arr$[i$];
                            if(element.getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(new  InflatingEntity(entity));
                                break;
                            }
                        }
                    }

                }
            }
        });
        this.httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                AuthState authState = (AuthState)context.getAttribute("http.auth.target-scope");
                CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
                HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
                if(authState.getAuthScheme() == null) {
                    AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                    Credentials creds = credsProvider.getCredentials(authScope);
                    if(creds != null) {
                        authState.setAuthScheme(new BasicScheme());
                        authState.setCredentials(creds);
                    }
                }

            }
        }, 0);
        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(5, 1500));
    }

    public static void allowRetryExceptionClass(Class<?> cls) {
        if(cls != null) {
            RetryHandler.addClassToWhitelist(cls);
        }

    }

    public static void blockRetryExceptionClass(Class<?> cls) {
        if(cls != null) {
            RetryHandler.addClassToBlacklist(cls);
        }

    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.httpContext.setAttribute("http.cookie-store", cookieStore);
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public ExecutorService getThreadPool() {
        return this.threadPool;
    }

    protected ExecutorService getDefaultThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public void setEnableRedirects(boolean enableRedirects, boolean enableRelativeRedirects, boolean enableCircularRedirects) {
        this.httpClient.getParams().setBooleanParameter("http.protocol.reject-relative-redirect", !enableRelativeRedirects);
        this.httpClient.getParams().setBooleanParameter("http.protocol.allow-circular-redirects", enableCircularRedirects);
        this.httpClient.setRedirectHandler(new MyRedirectHandler(enableRedirects));
    }

    public void setEnableRedirects(boolean enableRedirects, boolean enableRelativeRedirects) {
        this.setEnableRedirects(enableRedirects, enableRelativeRedirects, true);
    }

    public void setEnableRedirects(boolean enableRedirects) {
        this.setEnableRedirects(enableRedirects, enableRedirects, enableRedirects);
    }

    public void setRedirectHandler(RedirectHandler customRedirectHandler) {
        this.httpClient.setRedirectHandler(customRedirectHandler);
    }

    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        if(maxConnections < 1) {
            maxConnections = 10;
        }

        this.maxConnections = maxConnections;
        HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.maxConnections));
    }

    /** @deprecated */
    public int getTimeout() {
        return this.connectTimeout;
    }

    public void setTimeout(int value) {
        value = value < 1000?10000:value;
        this.setConnectTimeout(value);
        this.setResponseTimeout(value);
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int value) {
        this.connectTimeout = value < 1000?10000:value;
        HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, (long)this.connectTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.connectTimeout);
    }

    public int getResponseTimeout() {
        return this.responseTimeout;
    }

    public void setResponseTimeout(int value) {
        this.responseTimeout = value < 1000?10000:value;
        HttpParams httpParams = this.httpClient.getParams();
        HttpConnectionParams.setSoTimeout(httpParams, this.responseTimeout);
    }

    public void setProxy(String hostname, int port) {
        HttpHost proxy = new HttpHost(hostname, port);
        HttpParams httpParams = this.httpClient.getParams();
        httpParams.setParameter("http.route.default-proxy", proxy);
    }

    public void setProxy(String hostname, int port, String username, String password) {
        this.httpClient.getCredentialsProvider().setCredentials(new AuthScope(hostname, port), new UsernamePasswordCredentials(username, password));
        HttpHost proxy = new HttpHost(hostname, port);
        HttpParams httpParams = this.httpClient.getParams();
        httpParams.setParameter("http.route.default-proxy", proxy);
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
    }

    public void setMaxRetriesAndTimeout(int retries, int timeout) {
        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(retries, timeout));
    }

    public void removeAllHeaders() {
        this.clientHeaderMap.clear();
    }

    public void addHeader(String header, String value) {
        this.clientHeaderMap.put(header, value);
    }

    public void removeHeader(String header) {
        this.clientHeaderMap.remove(header);
    }

    public void setBasicAuth(String username, String password) {
        this.setBasicAuth(username, password, false);
    }

    public void setBasicAuth(String username, String password, boolean preemtive) {
        this.setBasicAuth(username, password, (AuthScope)null, preemtive);
    }

    public void setBasicAuth(String username, String password, AuthScope scope) {
        this.setBasicAuth(username, password, scope, false);
    }

    public void setBasicAuth(String username, String password, AuthScope scope, boolean preemtive) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        this.setCredentials(scope, credentials);
        this.setAuthenticationPreemptive(preemtive);
    }

    public void setCredentials(AuthScope authScope, Credentials credentials) {
        if(credentials == null) {
            Log.d("MyAsyncHttpClient", "Provided credentials are null, not setting");
        } else {
            this.httpClient.getCredentialsProvider().setCredentials(authScope == null?AuthScope.ANY:authScope, credentials);
        }
    }

    public void setAuthenticationPreemptive(boolean isPreemtive) {
        if(isPreemtive) {
            this.httpClient.addRequestInterceptor(new PreemtiveAuthorizationHttpRequestInterceptor(), 0);
        } else {
            this.httpClient.removeRequestInterceptorByClass(PreemtiveAuthorizationHttpRequestInterceptor.class);
        }

    }

    /** @deprecated */
    @Deprecated
    public void clearBasicAuth() {
        this.clearCredentialsProvider();
    }

    public void clearCredentialsProvider() {
        this.httpClient.getCredentialsProvider().clear();
    }

    public void cancelRequests(final Context context, final boolean mayInterruptIfRunning) {
        if(context == null) {
            Log.e("MyAsyncHttpClient", "Passed null Context to cancelRequests");
        } else {
            Runnable r = new Runnable() {
                public void run() {
                    List<RequestHandle> requestList = (List)  MyAsyncHttpClient.this.requestMap.get(context);
                    if(requestList != null) {
                        Iterator i$ = requestList.iterator();

                        while(i$.hasNext()) {
                            RequestHandle requestHandle = (RequestHandle)i$.next();
                            requestHandle.cancel(mayInterruptIfRunning);
                        }

                         MyAsyncHttpClient.this.requestMap.remove(context);
                    }

                }
            };
            if(Looper.myLooper() == Looper.getMainLooper()) {
                (new Thread(r)).start();
            } else {
                r.run();
            }

        }
    }

    public void cancelAllRequests(boolean mayInterruptIfRunning) {
        Iterator i$ = this.requestMap.values().iterator();

        while(true) {
            List requestList;
            do {
                if(!i$.hasNext()) {
                    this.requestMap.clear();
                    return;
                }

                requestList = (List)i$.next();
            } while(requestList == null);

            Iterator iterator = requestList.iterator();

            while(iterator.hasNext()) {
                RequestHandle requestHandle = (RequestHandle)i$.next();
                requestHandle.cancel(mayInterruptIfRunning);
            }
        }
    }

    public RequestHandle head(String url, ResponseHandlerInterface responseHandler) {
        return this.head((Context)null, url, (RequestParams)null, responseHandler);
    }

    public RequestHandle head(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.head((Context)null, url, params, responseHandler);
    }

    public RequestHandle head(Context context, String url, ResponseHandlerInterface responseHandler) {
        return this.head(context, url, (RequestParams)null, responseHandler);
    }

    public RequestHandle head(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.httpClient, this.httpContext, new HttpHead(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)), (String)null, responseHandler, context);
    }

    public RequestHandle head(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpUriRequest request = new HttpHead(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if(headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.httpClient, this.httpContext, request, (String)null, responseHandler, context);
    }

    public RequestHandle get(String url, ResponseHandlerInterface responseHandler) {
        return this.get((Context)null, url, (RequestParams)null, responseHandler);
    }

    public RequestHandle get(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.get((Context)null, url, params, responseHandler);
    }

    public RequestHandle get(Context context, String url, ResponseHandlerInterface responseHandler) {
        return this.get(context, url, (RequestParams)null, responseHandler);
    }

    public RequestHandle get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.httpClient, this.httpContext, new HttpGet(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)), (String)null, responseHandler, context);
    }

    public RequestHandle get(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if(headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.httpClient, this.httpContext, request, (String)null, responseHandler, context);
    }

    public RequestHandle post(String url, ResponseHandlerInterface responseHandler) {
        return this.post((Context)null, url, (RequestParams)null, responseHandler);
    }

    public RequestHandle post(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.post((Context)null, url, params, responseHandler);
    }

    public RequestHandle post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.post(context, url, this.paramsToEntity(params, responseHandler), (String)null, responseHandler);
    }

    public RequestHandle post(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.httpClient, this.httpContext, this.addEntityToRequestBase(new HttpPost(URI.create(url).normalize()), entity), contentType, responseHandler, context);
    }

    public RequestHandle post(Context context, String url, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface responseHandler) {
        HttpEntityEnclosingRequestBase request = new HttpPost(URI.create(url).normalize());
        if(params != null) {
            request.setEntity(this.paramsToEntity(params, responseHandler));
        }

        if(headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle post(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        HttpEntityEnclosingRequestBase request = this.addEntityToRequestBase(new HttpPost(URI.create(url).normalize()), entity);
        if(headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle put(String url, ResponseHandlerInterface responseHandler) {
        return this.put((Context)null, url, (RequestParams)null, responseHandler);
    }

    public RequestHandle put(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.put((Context)null, url, params, responseHandler);
    }

    public RequestHandle put(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.put(context, url, this.paramsToEntity(params, responseHandler), (String)null, responseHandler);
    }

    public RequestHandle put(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.httpClient, this.httpContext, this.addEntityToRequestBase(new HttpPut(URI.create(url).normalize()), entity), contentType, responseHandler, context);
    }

    public RequestHandle put(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        HttpEntityEnclosingRequestBase request = this.addEntityToRequestBase(new HttpPut(URI.create(url).normalize()), entity);
        if(headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle delete(String url, ResponseHandlerInterface responseHandler) {
        return this.delete((Context)null, url, responseHandler);
    }

    public RequestHandle delete(Context context, String url, ResponseHandlerInterface responseHandler) {
        HttpDelete delete = new HttpDelete(URI.create(url).normalize());
        return this.sendRequest(this.httpClient, this.httpContext, delete, (String)null, responseHandler, context);
    }

    public RequestHandle delete(Context context, String url, Header[] headers, ResponseHandlerInterface responseHandler) {
        HttpDelete delete = new HttpDelete(URI.create(url).normalize());
        if(headers != null) {
            delete.setHeaders(headers);
        }

        return this.sendRequest(this.httpClient, this.httpContext, delete, (String)null, responseHandler, context);
    }

    public RequestHandle delete(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpDelete httpDelete = new HttpDelete(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if(headers != null) {
            httpDelete.setHeaders(headers);
        }

        return this.sendRequest(this.httpClient, this.httpContext, httpDelete, (String)null, responseHandler, context);
    }

    protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
        return new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler);
    }

    protected RequestHandle sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
        if(uriRequest == null) {
            throw new IllegalArgumentException("HttpUriRequest must not be null");
        } else if(responseHandler == null) {
            throw new IllegalArgumentException("ResponseHandler must not be null");
        } else if(responseHandler.getUseSynchronousMode()) {
            throw new IllegalArgumentException("Synchronous ResponseHandler used in MyAsyncHttpClient. You should create your response handler in a looper thread or use SyncHttpClient instead.");
        } else {
            if(contentType != null) {
                if(uriRequest instanceof HttpEntityEnclosingRequestBase && ((HttpEntityEnclosingRequestBase)uriRequest).getEntity() != null) {
                    Log.w("MyAsyncHttpClient", "Passed contentType will be ignored because HttpEntity sets content type");
                } else {
                    uriRequest.setHeader("Content-Type", contentType);
                }
            }

            responseHandler.setRequestHeaders(uriRequest.getAllHeaders());
            responseHandler.setRequestURI(uriRequest.getURI());
            AsyncHttpRequest request = this.newAsyncHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context);
            this.threadPool.submit(request);
            RequestHandle requestHandle = new RequestHandle(request);
            if(context != null) {
                List<RequestHandle> requestList = (List)this.requestMap.get(context);
                Map var10 = this.requestMap;
                synchronized(this.requestMap) {
                    if(requestList == null) {
                        requestList = Collections.synchronizedList(new LinkedList());
                        this.requestMap.put(context, requestList);
                    }
                }

                if(responseHandler instanceof RangeFileAsyncHttpResponseHandler) {
                    ((RangeFileAsyncHttpResponseHandler)responseHandler).updateRequestHeaders(uriRequest);
                }

                requestList.add(requestHandle);
                Iterator iterator = requestList.iterator();

                while(iterator.hasNext()) {
                    if(((RequestHandle)iterator.next()).shouldBeGarbageCollected()) {
                        iterator.remove();
                    }
                }
            }

            return requestHandle;
        }
    }

    public void setURLEncodingEnabled(boolean enabled) {
        this.isUrlEncodingEnabled = enabled;
    }

    public static String getUrlWithQueryString(boolean shouldEncodeUrl, String url, RequestParams params) {
        if(url == null) {
            return null;
        } else {
            if(shouldEncodeUrl) {
                url = url.replace(" ", "%20");
            }

            if(params != null) {
                String paramString =  params.toString();//params.getParamString().trim();
                if(!paramString.equals("") && !paramString.equals("?")) {
                    url = url + (url.contains("?")?"&":"?");
                    url = url + paramString;
                }
            }

            return url;
        }
    }

    public static boolean isInputStreamGZIPCompressed(PushbackInputStream inputStream) throws IOException {
        if(inputStream == null) {
            return false;
        } else {
            byte[] signature = new byte[2];
            int readStatus = inputStream.read(signature);
            inputStream.unread(signature);
            int streamHeader = signature[0] & 255 | signature[1] << 8 & '\uff00';
            return readStatus == 2 && '謟' == streamHeader;
        }
    }

    public static void silentCloseInputStream(InputStream is) {
        try {
            if(is != null) {
                is.close();
            }
        } catch (IOException var2) {
            Log.w("MyAsyncHttpClient", "Cannot close input stream", var2);
        }

    }

    public static void silentCloseOutputStream(OutputStream os) {
        try {
            if(os != null) {
                os.close();
            }
        } catch (IOException var2) {
            Log.w("MyAsyncHttpClient", "Cannot close output stream", var2);
        }

    }

    private HttpEntity paramsToEntity(RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpEntity entity = null;

        try {
            if(params != null) {
                entity = params.getEntity(responseHandler);
            }
        } catch (IOException var5) {
            if(responseHandler != null) {
                responseHandler.sendFailureMessage(0, (Header[])null, (byte[])null, var5);
            } else {
                var5.printStackTrace();
            }
        }

        return entity;
    }

    public boolean isUrlEncodingEnabled() {
        return this.isUrlEncodingEnabled;
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if(entity != null) {
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    public static void endEntityViaReflection(HttpEntity entity) {
        if(entity instanceof HttpEntityWrapper) {
            try {
                Field f = null;
                Field[] fields = HttpEntityWrapper.class.getDeclaredFields();
                Field[] arr$ = fields;
                int len$ = fields.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    Field ff = arr$[i$];
                    if(ff.getName().equals("wrappedEntity")) {
                        f = ff;
                        break;
                    }
                }

                if(f != null) {
                    f.setAccessible(true);
                    HttpEntity wrapped = (HttpEntity)f.get(entity);
                    if(wrapped != null) {
                        wrapped.consumeContent();
                    }
                }
            } catch (Throwable var7) {
                Log.e("MyAsyncHttpClient", "wrappedEntity consume", var7);
            }
        }

    }

    private static class InflatingEntity extends HttpEntityWrapper {
        InputStream wrappedStream;
        PushbackInputStream pushbackStream;
        GZIPInputStream gzippedStream;

        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        public InputStream getContent() throws IOException {
            this.wrappedStream = this.wrappedEntity.getContent();
            this.pushbackStream = new PushbackInputStream(this.wrappedStream, 2);
            if( MyAsyncHttpClient.isInputStreamGZIPCompressed(this.pushbackStream)) {
                this.gzippedStream = new GZIPInputStream(this.pushbackStream);
                return this.gzippedStream;
            } else {
                return this.pushbackStream;
            }
        }

        public long getContentLength() {
            return this.wrappedEntity == null?0L:this.wrappedEntity.getContentLength();
        }

        public void consumeContent() throws IOException {
             MyAsyncHttpClient.silentCloseInputStream(this.wrappedStream);
             MyAsyncHttpClient.silentCloseInputStream(this.pushbackStream);
             MyAsyncHttpClient.silentCloseInputStream(this.gzippedStream);
            super.consumeContent();
        }
    }
}

