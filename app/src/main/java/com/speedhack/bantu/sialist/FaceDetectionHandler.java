package com.speedhack.bantu.sialist;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.PersonFace;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FaceDetectionHandler {
    private final String apiEndpoint = "https://southeastasia.api.cognitive.microsoft.com/face/v1.0";

    private final String subscriptionKey = "cd5312cc7ae84272897653681e23c788";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);
    private ArrayList<String> items = new ArrayList<>();

    public FaceDetectionHandler(){

    }

    public String getIdIdentity(final Bitmap image)throws InterruptedException,ConnectException,ExecutionException{
        UUID uuid = getFaceUUID(image);
        System.out.println(uuid);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Person");
        items.clear();
        String result = "tese";

        Query query = database.orderByKey();


        ValueEventListener listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    addStringToItems(postSnapshot.getKey());
                    System.out.println(postSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        query.addListenerForSingleValueEvent(listener);

        System.out.println(items.size());

        for(String id : items){
            UUID uuid2 = UUID.fromString(id);
            System.out.println(uuid2);
            if(isFaceIdentical(uuid,uuid2)){
                result = id;
                break;
            }
        }
        System.out.println(result);
        return result;

    }

    private void addStringToItems(String item){
        items.add(item);
    }

    public UUID getFaceUUID(final Bitmap image){
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

    public boolean isFaceIdentical(final UUID uuid1, final UUID uuid2)throws InterruptedException,ConnectException,ExecutionException{
        boolean retrunVal;
        AsyncTask<Void, String, Boolean> compareTask = new AsyncTask<Void, String, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... inputStreams) {
                try {
                    VerifyResult result = faceServiceClient.verify(uuid1,uuid2);
                    return result.isIdentical;
                } catch (ClientException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            };
        };

        compareTask.execute();
        retrunVal = compareTask.get();
        return retrunVal;
    }


/*
    private PersonFace getFaceRectangle(final UUID imageUUID){
        Person person = new Person();
        person.
        AsyncTask<Void,String,PersonFace> personAdder = new AsyncTask<Void, String, PersonFace>() {
            @Override
            protected PersonFace doInBackground(Void... voids) {
                faceServiceClient.
            }
        }
    }

    private void addPersonID(final UUID imageUUID){
        AsyncTask<Void,String,UUID> personAdder = new AsyncTask<Void, String, UUID>() {
            @Override
            protected UUID doInBackground(Void... voids) {
                AddPersistedFaceResult result = faceServiceClient.addPersonFace(s)
            }
        }
    }*/
}
