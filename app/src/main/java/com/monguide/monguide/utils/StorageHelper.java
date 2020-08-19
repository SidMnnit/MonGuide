package com.monguide.monguide.utils;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageHelper {

    public static StorageReference getReferenceToRootStorage(){
        return FirebaseStorage.getInstance().getReference();
}

    public static StorageReference getReferenceToAllProfilePictures(){
        return getReferenceToRootStorage().child("profile-pictures");
    }

    public static StorageReference getReferenceToProfilePictureOfParticularUser(String uid){
        return getReferenceToAllProfilePictures().child(uid);
    }

}
