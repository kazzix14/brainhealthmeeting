package com.example.brainhealth_meeting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadPhotoAsyncTask extends AsyncTask<String, Integer, JSONObject> {

    private AsyncCallback mAsyncCallback = null;

    public DownloadPhotoAsyncTask(AsyncCallback _asyncCallback) {
        mAsyncCallback = _asyncCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncCallback.preExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... _progress) {
        super.onProgressUpdate(_progress);
        mAsyncCallback.progressUpdate(_progress[0]);
    }

    @Override
    protected void onPostExecute(JSONObject _result) {
        super.onPostExecute(_result);
        mAsyncCallback.postExecute(_result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mAsyncCallback.cancel();
    }

    @Override
    protected JSONObject doInBackground(String... _uri) {
        JSONObject result = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(_uri[0]);

            // 接続オープン。
            conn = (HttpURLConnection) url.openConnection();

            // HTTPメソッドをGETに設定。
            conn.setRequestMethod(_uri[1]);

            // リダイレクトしないように設定。
            conn.setInstanceFollowRedirects(false);

            // 読み込みタイムアウト
            conn.setReadTimeout(20000);

            // 接続タイムアウト
            conn.setConnectTimeout(20000);

            // 接続開始。
            conn.connect();

            // HTTPレスポンスコード = 200 の場合、レスポンスボディを読み込む。
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                result = new JSONObject(sb.toString());

                br.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        // ダウンロードできなかった場合、androidアイコンを設定。
        if (result == null) {
            result = null;//((BitmapDrawable)this.context.get().getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
        }

        return result;
    }

    public interface AsyncCallback {
        void preExecute();

        void postExecute(JSONObject result);

        void progressUpdate(int progress);

        void cancel();
    }
}