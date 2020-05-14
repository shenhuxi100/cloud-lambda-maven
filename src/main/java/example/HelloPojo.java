package example;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HelloPojo implements RequestHandler<RequestClass, ResponseClass>{

    @lombok.SneakyThrows
    public ResponseClass handleRequest(RequestClass request, Context context){
        String greetingString = String.format("Hello %s, %s.", request.firstName, request.lastName);
        StringBuilder sb = readS3Info();
        return new ResponseClass(sb.toString());
    }

//    public static void main(String[] args) throws IOException {
//        readS3Info();
//        return;
//    }

    private StringBuilder readS3Info() throws IOException {
        AmazonS3 s3 = AmazonS3Client.builder().build();

        S3Object object = s3.getObject(new GetObjectRequest("yongzhi-test-sssssss", "ssssssss.rtf"));
        System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
        StringBuilder sb = displayTextInputStream(object.getObjectContent());

//        s3.copyObject("yongzhi-test-sssssss", "ssssssss.rtf", "yongzhi-lambda-jar", "targetSsssss");
        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        try {
            Copy xfer = xfer_mgr.copy("yongzhi-test-sssssss", "ssssssss.rtf", "yongzhi-lambda-jar", "yyy");
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
            sb.append("\n" + "Copy done...");
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        object.close();
        return sb;
    }

    private StringBuilder displayTextInputStream(InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            sb.append(line);
        }
        return sb;
    }
}