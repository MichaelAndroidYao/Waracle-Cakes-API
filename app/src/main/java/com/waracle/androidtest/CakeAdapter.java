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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the row for each article in the list
 *
 * @author michaelakakpo
 * @version 1/10/15.
 */
class CakeAdapter extends ArrayAdapter {

    private final Context mContext;

    private List<Cake> listOfCakes = new ArrayList<>();

    public CakeAdapter(Context context, List objects) {
        super(context, R.layout.cake_row, objects);
        this.mContext = context;
        this.listOfCakes = objects;
    }

    /**
     * Each row to be drawn will be drawn by retrieving the details of the
     * * appropriate Cake object that is that position in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cake currentCake = getItem(position);
        CakeViewHolder cakeViewHolder;

        // represents the row being inflated and recycled
        View rowCake = convertView;

        // First time instantiating rows of a article so needs to be inflated
        if (rowCake == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowCake = inflater.inflate(R.layout.cake_row, parent, false);
            cakeViewHolder = new CakeViewHolder(rowCake);

            rowCake.setTag(cakeViewHolder);
        } else {
            //recycling the viewholder once the convertview is no longer null
            cakeViewHolder = (CakeViewHolder) rowCake.getTag();
        }
        cakeViewHolder.getTitle().setText(currentCake.getTitle());
        cakeViewHolder.getDescription().setText(currentCake.getDescription());

        Picasso.with(mContext)
                .load(currentCake.getImage())
                .resize(256, 256) // TODO - Use the default params not hardcoded fields
                .centerCrop()
                .noPlaceholder()
                .noFade()
//                .error(R.drawable.ic_launcher_fab48) // TODO - find img for placeholder & for error
                .tag(cakeViewHolder)
                .into(cakeViewHolder.getImage());

        return rowCake;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getCount() {
        return listOfCakes.size();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Cake getItem(int position) {
        return listOfCakes.get(position);
    }

    /* Add the items from the updated Cakes to the list and notify any observers and refresh the changes */
    public void addItemsToList(List<Cake> restoredCakes) {
        if (restoredCakes != null) {
            listOfCakes.clear();
            listOfCakes.addAll(restoredCakes);
            notifyDataSetChanged();
        }
    }
}
