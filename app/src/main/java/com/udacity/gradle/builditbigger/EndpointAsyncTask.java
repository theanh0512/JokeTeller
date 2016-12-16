package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ultimatefit.pham.joketeller.backend.myApi.MyApi;

import java.io.IOException;

import pham.ultimatefit.joker.JokeActivity;

/**
 * Created by Pham on 14/12/2016.
 */

public class EndpointAsyncTask extends AsyncTask<Integer, Integer, String> {
    public static boolean updated = false;
    private static MyApi myApiService = null;
    private Context context;
    String data;

    public EndpointAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Integer... params) {
        updated = false;
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://192.168.97.6:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            publishProgress(50);
            myApiService = builder.build();
        }
        try {
            data = myApiService.getJoke().execute().getData();
            publishProgress(90);
        } catch (IOException e) {
            return e.getMessage();
        }
        return data;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(MainActivity.progressBar!=null){
            MainActivity.progressBar.setVisibility(View.VISIBLE);
            MainActivity.progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        updated = true;
        Intent intent = new Intent(context, JokeActivity.class);
        intent.putExtra(JokeActivity.JOKE_KEY, result);
        context.startActivity(intent);
        MainActivity.progressBar.setVisibility(View.INVISIBLE);
    }
}
