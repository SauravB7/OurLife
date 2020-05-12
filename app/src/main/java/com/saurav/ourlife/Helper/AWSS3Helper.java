package com.saurav.ourlife.Helper;

import android.content.Context;
import android.os.Environment;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

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

    public AWSS3Helper(String BUCKET_NAME, String ACCESS_KEY, String ACCESS_SECRET, Context context) {
        this.BUCKET_NAME = BUCKET_NAME;
        this.context = context;

        this.S3CLIENT = new AmazonS3Client(new BasicAWSCredentials(ACCESS_KEY, ACCESS_SECRET));
        this.S3CLIENT.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        this.setTransferUtility();
    }

    private void setTransferUtility(){
        transferUtility = new TransferUtility(S3CLIENT,
                context);
    }

    public AmazonS3 getS3CLIENT() {
        return S3CLIENT;
    }

    public TransferObserver uploadFile(File file, String folderName){
        String uploadKey = !folderName.isEmpty() && folderName.length() > 0
                ? folderName + "/" + file.getName()
                : file.getName();

        TransferObserver transferObserver = transferUtility.upload(
                BUCKET_NAME,           //The bucket to upload to
                uploadKey, //The key for the uploaded object
                file        //The file where the data to upload exists
        );
        return transferObserver;
    }

    public static void downloadFile(String fileURL) throws URISyntaxException {
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
            transferUtility.download(
                    S3URI.getBucket(),
                    S3URI.getKey(),
                    file
            );
        }
    }

    public List<String> listFileNames(String folderName) {
        ListObjectsV2Request req;
        if(!folderName.isEmpty() && folderName!= null) {
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
            if(!s3ObjectSummary.getKey().endsWith("/")) {
                objectList.add(s3ObjectSummary.getKey());
            }
        }
        return objectList;
    }

    public List<String> listFileURLs(final String folderName) {

        ListObjectsV2Request req;
        if(!folderName.isEmpty() && folderName!= null) {
            req = new ListObjectsV2Request()
                    .withBucketName(BUCKET_NAME)
                    .withPrefix(folderName + "/")
                    .withDelimiter("/");
        } else {
            req = new ListObjectsV2Request()
                    .withBucketName(BUCKET_NAME);
        }

        ListObjectsV2Result objects = S3CLIENT.listObjectsV2(req);
        ArrayList<String> objectURLs = new ArrayList<>(objects.getObjectSummaries().size());

        for (S3ObjectSummary s3ObjectSummary : objects.getObjectSummaries()) {
            if(!s3ObjectSummary.getKey().endsWith("/")) {
                objectURLs.add(generatePreSignedURL(s3ObjectSummary.getKey()));
            }
        }

        return objectURLs;
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
