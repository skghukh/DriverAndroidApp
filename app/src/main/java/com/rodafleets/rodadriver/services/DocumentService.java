package com.rodafleets.rodadriver.services;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sverma4 on 12/24/17.
 */

public class DocumentService {

    private StorageReference mStorageRef;
    private static ExecutorService firebaseOperationThreadPool = Executors.newFixedThreadPool(1);

    public static StorageTask<UploadTask.TaskSnapshot> uploadDriverLicense(Uri fileUri, String fileName, int docType) {
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String folder = "license";
        if (docType == 0) {
            folder = "license";
        } else if (docType == 1) {
            folder = "rc";
        } else if (docType == 2) {
            folder = "permit";
        }

        //Uri file = Uri.fromFile(new File(filePath));
        StorageReference riversRef = storageRef.child(folder+"/"+fileName);
        final StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = riversRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        System.out.println("File is uploaded successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        System.out.println("File upload is failure");
                    }
                });

        return taskSnapshotStorageTask;

       /* firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                final UploadTask.TaskSnapshot result = taskSnapshotStorageTask.getResult();
                System.out.println(result);
            }
        });*/

    }

    ;
}
