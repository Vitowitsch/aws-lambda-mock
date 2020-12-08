package botsandbytes.lambdamock.parser;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.javatuples.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

public class Inbound {

	private static final Logger logger = Logger.getLogger(LambdaFunctionHandler.class.getName());

	public boolean process(AmazonS3 s3, List<Pair<String, String>> s3Locations) {
		boolean success = true;
		for (Pair<String, String> loc : s3Locations) {
			logger.info(() -> (String.format("Received new file event: %s ", loc.getValue0() + "/" + loc.getValue1())));
			try {
				S3Object response = s3.getObject(new GetObjectRequest(loc.getValue0(), loc.getValue1()));
				try (S3ObjectInputStream is = response.getObjectContent(); ZipInputStream in = new ZipInputStream(is)) {
					// process files here
				}
			} catch (Exception e) {
				logger.severe(String.format(
						"Error getting object %s from bucket %s. Make sure they exist and"
								+ " your bucket is in the same region as this function.",
						loc.getValue0(), loc.getValue1()) + e);
				success = false;
			}
		}
		return success;
	}

}
