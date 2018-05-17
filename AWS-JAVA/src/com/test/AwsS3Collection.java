package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.test.Utilities.EmailUtil;
import com.test.pojo.DIY;

public class AwsS3Collection {

	private static final String BUCKETNAME = "agco-fuse-production-lake";
	// must be lower case
	private static final String ClIENTTREGION = "us-east-1";
	private static final String JSONHOLDER ="json";
	private static final String CSVHOLDER = "csv";
	private static final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(ClIENTTREGION)
			.withCredentials(new ProfileCredentialsProvider()).build();
	private static List<DIY> diyL = new ArrayList<DIY>();
	
	public static void main(String... args) {
		List<String> missingParameter = new ArrayList<String>();
		try {

			// S3Client
			// * The ProfileCredentialsProvider will return your [default]
			// * credential profile by reading from the credentials file located at
			// * (~/.aws/credentials) which was set up with python cls
			
			
			
			S3EventNotification event;
			
			
			System.out.println(s3Client.getBucketPolicy(BUCKETNAME).getPolicyText());
			System.out.println(s3Client.getBucketAcl(BUCKETNAME));

			// Location of bucket client data info ACL info
			System.out.println("getBucketLocation: " + s3Client.getBucketLocation(BUCKETNAME));
			System.out.println("getBucketLifecycleConfiguration: "
					+ s3Client.getBucketLifecycleConfiguration(BUCKETNAME).getRules());
			System.out.println("getBucketLoggingConfiguration: "
					+ s3Client.getBucketLoggingConfiguration(BUCKETNAME).getDestinationBucketName());
			System.out.println("getBucketPolicy: " + s3Client.getBucketPolicy(BUCKETNAME).getPolicyText());
			System.out.println("getRegionName: " + s3Client.getRegionName());

			// with define key
//			S3Object s3Collection = s3Client.getObject(BUCKETNAME,
//					"Hard Coded Path");
//			System.out.println(s3Collection);
//			System.out.println("Last Date Modified: " + s3Collection.getObjectMetadata().getLastModified());
//			System.out.println("Tagging Count: " + s3Collection.getTaggingCount());
//			System.out.println("Object Content: " + s3Collection.getObjectContent());
//			System.out.println("S3Object key:  " + s3Collection.getKey() + "\n\n\n");
//
//			s3Collection.close();

			 //listing object with prefix case senstive
			
			System.out.println("Listing object with prefix");
			ObjectListing objectList = s3Client
					.listObjects(new ListObjectsRequest().withBucketName(BUCKETNAME).withPrefix("json"));
			collectBucketObjects(objectList);
			
			for(int x =0;x<diyL.size();x++) {
				System.out.println(diyL.get(x));
			}
			
			
			
			// collect list of s3 buckets
			// limit the amount of keys that we want
			System.out.println("Listing contents of s3 buckets with lov2r");
			ListObjectsV2Request lovr = new ListObjectsV2Request().withBucketName(BUCKETNAME).withMaxKeys(2);
			ListObjectsV2Result result;
//			int count = 5;
//			do {
//
//				// store list inside of ListObjectsV2Result Object
//				result = s3Client.listObjectsV2(lovr);
//				for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
//					if (objectSummary.getSize() != 0) {
//						count -= 1;
//
//						// format string to display with size: actual size
//						System.out.printf("- %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
//						System.out.println("getTag : " + objectSummary.getETag());
//						S3Object s3Object = s3Client.getObject(BUCKETNAME, objectSummary.getKey());
//						System.out.println("METADATA: "+s3Object.getObjectMetadata().getContentType());
//						System.out.println("Object Content Data: "+s3Object.getObjectContent());
//						BufferedReader bf = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
//						String line;
//						while((line = bf.readLine())!= null) {
//						System.out.println(line);	
//						}
//					}
//				}
//				if (count == 0) {
//					break;
//
//				}
//				// maxKeys was set to two
//				// if there where more than the max keys in the bucket get a continuation token
//				String token = result.getNextContinuationToken();
//				//System.out.println("Next Continuation Token: " + token);
//				//Sets the optional continuation token. Continuation token allows a list to be continued from a specific point
//				lovr.setContinuationToken(token);
//
//			} while (result.isTruncated());
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (SdkClientException ed) {
			ed.printStackTrace();
		}
		
		
	}
	
	public static List<DIY> collectBucketObjects(ObjectListing objectList){
		int count1 = 0;
		for (S3ObjectSummary objectLis : objectList.getObjectSummaries()) {
			//s3 bucket java api does not actually contain a method for creation of data so we must go off naming convention
			//if(objectLis.toString().contains("2018-01-01")) {
			if (objectLis.getSize() != 0) {
				count1 += 1;
				System.out.printf("- %s (size: %d)\n", objectLis.getKey(), objectLis.getSize());
				S3Object s3Object = s3Client.getObject(BUCKETNAME, objectLis.getKey());
				//prevents data from being pulled by bufferedReader pulls multiple lines from file
				//String json = IOUtils.toString(s3Object.getObjectContent());
				
				BufferedReader bf = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
				String line;
				try {
					while((line = bf.readLine())!= null) {
					//System.out.println(line);
					String json =  line;
					JSONObject jsonObj = new JSONObject(line);
					//System.out.println(jsonObj.getJSONObject("diagnostic_code"));
					System.out.println(jsonObj.toString());
					//event = S3EventNotification.parseJson(json);
					DIY diy = new ObjectMapper().readValue(line, DIY.class);
					diyL.add(diy);
					//System.out.println(event.toString());
					}
					
				}catch(UnrecognizedPropertyException un){
					System.out.println(un.getMessage());
					//
					sendEmail(un.getMessage(),"Sai.Tadepu@agcocorp.com");
					un.printStackTrace();
					System.exit(1);
					
					
				}catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				event = S3EventNotification.parseJson(json);
//				System.out.println(event);
			}
	//	}
			if (count1 ==10) {
				break;
			}
		}
		System.out.println("Count: "+count1);
		return diyL;
	}
	
	
	public static void sendEmail(String body , String UserEmail) {
		String relayHost = "smtpapps.atlanta.agcocorp.com";
		String email = UserEmail;
		String subject = "Verfication needed There was a change in the following object";
		Properties props= System.getProperties();
		props.put("mail.smtp.host", relayHost);
		Session session = Session.getInstance(props,null);
		EmailUtil.sendEmail(session, email, subject, body);
		
		
		
	}
	
}
