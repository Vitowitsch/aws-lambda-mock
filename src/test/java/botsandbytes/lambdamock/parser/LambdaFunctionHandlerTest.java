package botsandbytes.lambdamock.parser;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@RunWith(MockitoJUnitRunner.class)
public class LambdaFunctionHandlerTest {

	private S3Event event;

	@Mock
	private AmazonS3 s3Client;
	@Mock
	private S3Object s3Object;

	@Captor
	private ArgumentCaptor<GetObjectRequest> getObjectRequest;

	@Before
	public void setUp() throws IOException {
		event = TestUtils.parse("/s3-event.put.json", S3Event.class);

		S3Object mockObject = new S3Object();
		Path resourceDirectory = Paths.get("src", "test", "resources");
		File file = new File(resourceDirectory.toString() + "/test.zip");
		mockObject.setObjectContent(new FileInputStream(file));
		when(s3Client.getObject(getObjectRequest.capture())).thenReturn(mockObject);
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Your Function Name");
		return ctx;
	}

	@Test
	public void testLambdaFunctionHandler() {
		LambdaTestHandler handler = new LambdaTestHandler(s3Client);
		Context ctx = createContext();
		boolean output = handler.handleRequest(event, ctx);
		Assert.assertTrue(output);
	}
}
