package botsandbytes.lambdamock.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;

import botsandbytes.lambdamock.parser.DataObject;
import botsandbytes.lambdamock.parser.Inbound;

public class LambdaTestHandler implements RequestHandler<S3Event, Boolean> {

	private AmazonS3 s3;

	public LambdaTestHandler(AmazonS3 s3) {
		this.s3 = s3;
	}

	@Override
	public Boolean handleRequest(S3Event event, Context context) {
		context.getLogger().log("Received event: " + event.getClass() + " " + event);
		List<Pair<String, String>> s3Locations = new ArrayList<>();
		s3Locations.add(new Pair<String, String>(event.getRecords().get(0).getS3().getBucket().getName(),
				event.getRecords().get(0).getS3().getObject().getKey()));
		return new Inbound().process(s3, s3Locations);

	}

	public Map<String, List<DataObject>> getMap(List<DataObject> strings) {
		return strings.stream().collect(Collectors.groupingBy(DataObject::getVariable));
	}

}