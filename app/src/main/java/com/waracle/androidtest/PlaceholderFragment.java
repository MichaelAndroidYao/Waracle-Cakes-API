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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment is responsible for loading
 * and displaying a list of cakes with images.
 *
 * @author michaelakakpo
 * @version 1/10/15.
 */

public class PlaceholderFragment extends Fragment {

    private final static String TAG = PlaceholderFragment.class.getSimpleName();

    // Current activity
    MainActivity mCurrentActivity;

    // AsyncTask for downloading cakes
    DownloadCakesTask mDownloadCakesTask;

    // Needs to update the adapter to display cakes
    CakeAdapter mCakeAdapter;

    // Network connectivity message
    private TextView mTextViewNetworkMessage;

    private final List<Cake> listOfCakes = new ArrayList<>();

    public PlaceholderFragment() {
    /* No args constructor */
    }

    public static PlaceholderFragment newInstance() {
        return new PlaceholderFragment();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
        this.mCurrentActivity = (MainActivity) context;
        // Check if the AsyncTask has an attached activity, if not then attach an activity instance
        if (mDownloadCakesTask != null) {
            mDownloadCakesTask.onAttach(mCurrentActivity);
        }
    }

    // Initiating a request for the list of cakes
    public void beginTask() {
        mDownloadCakesTask = new DownloadCakesTask(mCurrentActivity);
        mDownloadCakesTask.execute();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Avoid creating and destroying Fragment every time configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mTextViewNetworkMessage = (TextView) rootView.findViewById(R.id.txt_network_connection_status);

        ListView mListView = (ListView) rootView.findViewById(R.id.list);

        // Initialise and set the adapter
        mCakeAdapter = new CakeAdapter(getContext(), listOfCakes);
        mListView.setAdapter(mCakeAdapter);

        return rootView;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        loadData();
    }

    // Check if network connection is present then attempt to load cakes
    private void loadData() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Asynchronously load the cakes (not blocking the main thread)
            beginTask();
        } else {
            // let user know the connection is not available
            mTextViewNetworkMessage.setVisibility(View.VISIBLE);
            mTextViewNetworkMessage.setText(R.string.network_no_connection_message);
            Log.d(TAG, "No network connection available()");
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
        if (mDownloadCakesTask != null) {
            // Notify the AsyncTask the calling activity is now not available
            mDownloadCakesTask.onDetach();
        }
    }
}



