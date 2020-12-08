package botsandbytes.lambdamock.parser;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class LambdaFunctionHandler implements RequestHandler<SNSEvent, Boolean> {

	private AmazonS3 s3;

	public LambdaFunctionHandler() {
		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
		ClientConfiguration clientCfg = new ClientConfiguration();
		s3 = builder.withClientConfiguration(clientCfg).withRegion("eu-central-1").build();

	}

	@Override
	public Boolean handleRequest(SNSEvent event, Context context) {
		context.getLogger().log("Received event: " + event.getClass() + " " + event);
		List<Pair<String, String>> s3Locations = new ArrayList<>();
		for (SNSRecord record : event.getRecords()) {
			String json = record.getSNS().getMessage();
			S3EventNotification s3Event = S3EventNotification.parseJson(json);
			for (S3EventNotificationRecord s3Record : s3Event.getRecords()) {
				s3Locations
						.add(new Pair<>(s3Record.getS3().getBucket().getName(), s3Record.getS3().getObject().getKey()));
			}
		}
		return new Inbound().process(s3, s3Locations);
	}

}