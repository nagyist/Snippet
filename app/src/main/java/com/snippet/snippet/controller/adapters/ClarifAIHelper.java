package com.snippet.snippet.controller.adapters;

import android.content.Context;
import android.util.Log;

import com.snippet.snippet.controller.TagListener;

import java.io.IOException;
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
        client.getDefaultModels().generalModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(path)))
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
    /*
    //Initialize the ClarifAI Client
        client = new ClarifAIFactory(this).createClient();

        try {
            // Grab an image locally just for testing sending byte arrays for images
            InputStream fileis = getResources().openRawResource(R.raw.android);
            byte[] bytes = new byte[fileis.available()];
            fileis.read(bytes);

            //Generate and send the Asynchronous request to predict labels for the image
            client.getDefaultModels().generalModel().predict()
                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(bytes)))
                    .executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
                        @Override
                        public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                            Log.i("ClarifAI", "Response Successful");
                            List<ClarifaiOutput<Concept>> predictions = client.getDefaultModels().generalModel().predict()
                                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of("https://pbs.twimg.com/profile_images/616076655547682816/6gMRtQyY.jpg")))
                                    .executeSync().get();
                            for (ClarifaiOutput<Concept> output : predictions) {
                                for (Concept prediction : output.data()) {
                                    Log.i("ClarifAIPred", String.format("%s: %f", prediction.name(), prediction.value()));
                                }
                            }
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
        } catch (IOException e) {
            Log.e("ClarifAI", "InputStream exception: "+e.getMessage());
        }
     */
}
