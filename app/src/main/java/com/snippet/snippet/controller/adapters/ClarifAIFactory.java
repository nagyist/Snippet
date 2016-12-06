package com.snippet.snippet.controller.adapters;

import android.content.Context;

import com.snippet.snippet.R;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;

/**
 * Factory class for instantiating instances of the ClarifAI interface
 * @author Jordan Burklund
 */

public class ClarifAIFactory {
    private Context context;

    public ClarifAIFactory(Context context) {
        this.context = context;
    }

    public ClarifaiClient createClient() {
        return new ClarifaiBuilder(context.getString(R.string.ClarifAI_AppID),
                context.getString(R.string.ClarifAI_Secret)).buildSync();
    }
}
