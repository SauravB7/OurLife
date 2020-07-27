package com.saurav.ourlife.Helper;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.saurav.ourlife.Activities.FullscreenImageActivity;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AWSS3Helper {
    AmazonS3 S3CLIENT;
    private String BUCKET_NAME;
    private static TransferUtility transferUtility;
    private Context context;

    public AWSS3Helper(Context context) {
        this.BUCKET_NAME = GenericHelper.getConfigValue(context, "s3.bucketName");
        this.context = context;

        initiateS3();
    }

    private CognitoCachingCredentialsProvider initCognitoUser() {
        String POOL_ID = GenericHelper.getConfigValue(context, "cognito.identityPoolId");

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                POOL_ID, // Identity pool ID
                Regions.AP_SOUTH_1 // Region
        );

        return credentialsProvider;
    }

    private void initiateS3() {
        this.S3CLIENT = new AmazonS3Client(initCognitoUser());
        this.S3CLIENT.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        this.setTransferUtility();
    }

    private void setTransferUtility(){
        transferUtility = new TransferUtility(S3CLIENT,
                context);
    }

    /*public TransferObserver uploadFile(File file, String folderName){
        String uploadKey = !folderName.isEmpty() && folderName.length() > 0
                ? folderName + "/" + file.getName()
                : file.getName();

        TransferObserver transferObserver = transferUtility.upload(
                BUCKET_NAME,           //The bucket to upload to
                uploadKey, //The key for the uploaded object
                file        //The file where the data to upload exists
        );
        return transferObserver;
    }*/

    public static void downloadFile(String fileURL, Context context) throws URISyntaxException {
        File file;
        URI fileToBeDownloaded = new URI(fileURL);
        AmazonS3URI S3URI = new AmazonS3URI(fileToBeDownloaded);

        String downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "OurLife";
        File folder = new File(downloadFolder);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        file = new File(downloadFolder + File.separator + S3URI.getKey());

        if(!file.exists()) {
            TransferObserver observer = transferUtility.download(S3URI.getBucket(), S3URI.getKey(), file);
            ((FullscreenImageActivity)context).downloadProgress.setVisibility(View.VISIBLE);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if(state == TransferState.COMPLETED || state == TransferState.FAILED) {
                        ((FullscreenImageActivity)context).downloadProgress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int currentProgress = (int) ((bytesCurrent / bytesTotal) * 100);
                    ((FullscreenImageActivity)context).downloadProgress.setProgress(currentProgress);
                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        }
    }

    public List<String> listFileNames(String folderName) {
        ListObjectsV2Request req;
        if (!folderName.isEmpty() && folderName != null) {
            req = new ListObjectsV2Request()
                    .withBucketName(BUCKET_NAME)
                    .withPrefix(folderName + "/")
                    .withDelimiter("/");
        } else {
            req = new ListObjectsV2Request()
                    .withBucketName(BUCKET_NAME);
        }

        ListObjectsV2Result objects = S3CLIENT.listObjectsV2(req);
        ArrayList<String> objectList = new ArrayList<>(objects.getObjectSummaries().size());

        for (S3ObjectSummary s3ObjectSummary : objects.getObjectSummaries()) {
            if (!s3ObjectSummary.getKey().endsWith("/")) {
                objectList.add(s3ObjectSummary.getKey());
            }
        }
        return objectList;
    }

    public List<String> listFileURLs(String prefix) {
        String delimiter = "/";
        if(!prefix.endsWith(delimiter)) {
            prefix += delimiter;
        }

        ListObjectsV2Request req;
        req = new ListObjectsV2Request()
                .withBucketName(BUCKET_NAME)
                .withPrefix(prefix)
                .withDelimiter("/");

        ListObjectsV2Result objects = S3CLIENT.listObjectsV2(req);
        List<String> objectURLs = new ArrayList<>(objects.getObjectSummaries().size());

        for (S3ObjectSummary s3ObjectSummary : objects.getObjectSummaries()) {
            if(!s3ObjectSummary.getKey().endsWith("/")) {
                objectURLs.add(generatePreSignedURL(s3ObjectSummary.getKey()));
            }
        }

        return objectURLs;
    }

    public List<String> listFolderNames(String prefix) {
        String delimiter = "/";
        if(!prefix.endsWith(delimiter)) {
            prefix += delimiter;
        }

        ListObjectsRequest req = new ListObjectsRequest()
                .withBucketName(BUCKET_NAME)
                .withPrefix(prefix)
                .withDelimiter(delimiter);

        ObjectListing objects = S3CLIENT.listObjects(req);
        return objects.getCommonPrefixes();
    }

    private String generatePreSignedURL(String key) {
        Date expiration = new Date();
        long expTimeInMillis = expiration.getTime() + 1000 * 60 * 60;
        expiration.setTime(expTimeInMillis);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(BUCKET_NAME, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = S3CLIENT.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }
}
