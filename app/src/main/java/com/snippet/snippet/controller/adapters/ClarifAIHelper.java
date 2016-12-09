package com.snippet.snippet.controller.adapters;

import android.content.Context;
import android.util.Log;

import com.snippet.snippet.controller.TagListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

/**
 * Created by jordanbrobots on 12/5/16.
 */

public class ClarifAIHelper {

    private Context context;
    private ClarifaiClient client;

    public ClarifAIHelper(Context context) {
        this.context = context;
        client = new ClarifAIFactory(context).createClient();
    }

    public void sendToClarifAI(String path, final TagListener tagReceiver) {
        //Generate and send the Asynchronous request to predict labels for the image
        Log.i("SendToClarifai", path);
        File imageToSend = new File(path);
        client.getDefaultModels().generalModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageToSend)))
                .executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
                    @Override
                    public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                        Log.i("ClarifAI", "Response Successful");
                        ArrayList<String> tags = new ArrayList<String>();
                        for (ClarifaiOutput<Concept> output : clarifaiOutputs) {
                            for (Concept prediction : output.data()) {
                                Log.i("ClarifAIPred", String.format("%s: %f", prediction.name(), prediction.value()));
                                tags.add(prediction.name());
                            }
                        }
                        tagReceiver.onReceiveTags(tags);
                    }

                    @Override
                    public void onClarifaiResponseUnsuccessful(int errorCode) {
                        Log.w("ClarifAI", "Response Unsuccessful");
                    }

                    @Override
                    public void onClarifaiResponseNetworkError(IOException e) {
                        Log.e("ClarifAI", e.getMessage());
                    }
                });
    }
}
