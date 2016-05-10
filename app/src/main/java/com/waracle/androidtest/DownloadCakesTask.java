/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.waracle.androidtest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronously loading Cake information from the Waracle API
 * <p/>
 * Class Description
 *
 * @author michaelakakpo
 * @version 1/10/15.
 */
class DownloadCakesTask extends AsyncTask<Void, Void, List<Cake>> {

    private final String TAG = DownloadCakesTask.class.getSimpleName();
    private Activity mActivity;

    // Constructor to allow task to be called with fresh activity instance
    public DownloadCakesTask(Activity activity) {
        onAttach(activity);
    }

    // Attaching and the task to the parent activity
    public void onAttach(Activity activity) {
        this.mActivity = activity;
    }

    // Detaching the parent activity
    public void onDetach() {
        this.mActivity = null;
    }

    @Override
    protected List<Cake> doInBackground(Void... urls) {
        Log.d(TAG, "doInBackground");

        /* These two need to be declared outside the try/catch
        so that they can be closed in the finally block. */

        // Http Connection
        HttpURLConnection connection = null;
        // Buffer for reading InputStream from response
        BufferedReader bufferedReader = null;
        // InputStream
        InputStream inputStream = null;
        // The JSON response from the server is stored as a raw JSON string.
        String cakeResponseString = null;

        // List of Artists after parsing the JSON response
        List<Cake> listOfCakes = new ArrayList<>();

        try {

            // BASE URL to append any further query params etc onto
            final String CAKES_BASE_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";

            // Main Url the connection will be opened on
            URL cakesURL = new URL(CAKES_BASE_URL);

            Log.d("Cakes URL: ", cakesURL.toString());

            // Create the request and open the connection on the url.
            connection = (HttpURLConnection) cakesURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Check the stream of bytes from the JSON response is not empty
            inputStream = connection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            if (inputStream == null) {
                // No bytes to stream from the response
                cakeResponseString = null;
            }

            // Convert the stream of bytes to a stream of characters {@link InputStreamReader}
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            }

            // Read the characters from the response and build a string out of it
            String line;
            if (bufferedReader != null) {
                while ((line = bufferedReader.readLine()) != null) {
                    // Build a string out of the contents of the BufferedReader
                    stringBuilder.append(line).append("\n");
                }
            }

            // Check the String that is being built from reading the string
            if (stringBuilder.length() == 0) {
                // If the stream was empty then no need to parse the JSON response.
                cakeResponseString = null;
            }
            // Store the response after successfully reading it
            cakeResponseString = stringBuilder.toString();

            Log.d(TAG, "Cake response: " + cakeResponseString);
        } catch (IOException e) {
            Log.e(TAG, "Error closing stream: " + e.getMessage());
                /* If the connection didn't successfully retrieve the cake data,
                there's no point in attempting to parse it. */
            cakeResponseString = null;
        } finally {
            // Ensure that regardless of outcome, the connection is are disconnected
            if (connection != null) {
                connection.disconnect();
            }

            // Ensure that regardless of outcome, the InputStream is are closed
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Ensure that regardless of outcome, the buffer is are closed
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing stream {}" + e.getMessage());
                }
            }
        }
        // Attempt to parse Json response and extract required fields
        try {
            listOfCakes = parseArticleResponseData(cakeResponseString);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return listOfCakes;
    }

    // Parsing the Json String and extracting the required Article fields
    public List<Cake> parseArticleResponseData(String response) throws JSONException {

        // JSON objects that need to be extracted form the JSON response
        final String CAKE_TITLE = "title";
        final String CAKE_DESCRIPTION = "desc";
        final String CAKE_IMAGE = "image";

        // List of cakes
        List<Cake> listOfCakes = new ArrayList<>();

        // response string is converted into an object so it can be traversed to extract individual objects
        JSONArray cakeItems = new JSONArray(response);

        for (int currentCake = 0; currentCake < cakeItems.length(); currentCake++) {

            Cake cakeItem = new Cake();

            // retrieve relevant artist fields
            JSONObject currentCakeItem = cakeItems.getJSONObject(currentCake);
            String cakeTitle = currentCakeItem.getString(CAKE_TITLE);
            String cakeDescription = currentCakeItem.getString(CAKE_DESCRIPTION);
            String cakeImageURL = currentCakeItem.getString(CAKE_IMAGE);

            // store article info
            cakeItem.setTitle(cakeTitle);
            cakeItem.setImage(cakeImageURL);
            cakeItem.setDescription(cakeDescription);
            Log.i("Cake id", "" + cakeTitle);
            Log.i("Cake description: ", cakeDescription);
            Log.i("Cake image url", cakeImageURL);

            // create list of cakes
            listOfCakes.add(cakeItem);

        }
        Log.d("Cakes: ", "# of cakes " + listOfCakes.size());
        return listOfCakes;
    }

    // onPostExecute displays the results of the AsyncTask loading the cakes.
    @Override
    protected void onPostExecute(List<Cake> result) {
        Log.d(TAG, "onPostUpdate()");
        if (mActivity != null) {
            // If the activity is null don't bother updating, else update UI
            ((MainActivity) mActivity).updateCakesList(result);
        }
    }



}
