package com.speedhack.bantu.sialist;


import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FaceDetectionHandler {
    private final String apiEndpoint = "https://southeastasia.api.cognitive.microsoft.com/face/v1.0";

    private final String subscriptionKey = "cd5312cc7ae84272897653681e23c788";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    public FaceDetectionHandler(){

    }

    public boolean compareIdentity(){

    }

    private UUID getFaceUUID(final Bitmap image){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final UUID str_result;
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, UUID> detectTask =
                new AsyncTask<InputStream, String, UUID>() {
                    String exceptionMessage = "";

                    @Override
                    protected UUID doInBackground(InputStream... params) {
                        try {
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null          // returnFaceAttributes:
                            );

                            if (result == null||result.length>1){
                                return null;
                            }
                            return result[0].faceId;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        //detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        //detectionProgressDialog.setMessage(progress[0]);
                    }
                    @Override
                    protected void onPostExecute(UUID result) {
                        //TODO: update face frames
                        if (result == null) return;
                    }
                };

        detectTask.execute(inputStream);
        try {
            str_result = detectTask.get(12, TimeUnit.SECONDS);
            return str_result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }
}
