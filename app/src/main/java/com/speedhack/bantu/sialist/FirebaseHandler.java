package com.speedhack.bantu.sialist;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.ConnectException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FirebaseHandler {


    FirebaseDatabase database;
    FaceDetectionHandler faceDetectionHandler;
    public FirebaseHandler(){
        database = FirebaseDatabase.getInstance();
        faceDetectionHandler = new FaceDetectionHandler();
    }

    public void addDataToDatabase(Bitmap face, String fullname, String email,
                                  String gender, String nomorHP, String password){
        DatabaseReference id = database.getReference("Person").child(faceDetectionHandler.getFaceUUID(face).toString());
        id.child("Nama").setValue(fullname);
        id.child("Email").setValue(email);
        //id.child("DateOfBirth").setValue(fullname);
        id.child("Gender").setValue(gender);
        id.child("nomorHP").setValue(nomorHP);
        id.child("Password").setValue(password);
    }

    public void updateValue(String email, String key, String newValue){
        DatabaseReference id = database.getReference(email);
        id.child(key).setValue(newValue);
    }

    public boolean isFaceAuthenticated(Bitmap face, String password)throws InterruptedException,ConnectException,ExecutionException {
        DatabaseReference id = database.getReference("Person").child(faceDetectionHandler.getIdIdentity(face));
        if(password.equals(id.child("Password").toString())){
            return true;
        }else{
            return false;
        }
    }
}
