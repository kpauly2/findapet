package tech.pauly.findapet.utils;

import android.support.test.espresso.core.internal.deps.guava.io.ByteStreams;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

public class MockWebServerDispatcher extends Dispatcher {

    private Map<Pattern, MockResponse> mockedCalls = new HashMap<>();

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String requestLine = request.getRequestLine();

        for (Pattern p : mockedCalls.keySet()) {
            if (p.matcher(requestLine).find()) {
                return mockedCalls.get(p);
            }
        }

        Log.e(getClass().getName(), "Failed to match:" + requestLine);
        return new MockResponse().setResponseCode(HTTP_NOT_FOUND)
                                 .setBody("Failed to match " + requestLine);
    }

    public void mockCall(Pattern pattern, String responseFilename) {
        mockedCalls.put(pattern, getMockResponse(responseFilename));
    }

    private MockResponse getMockResponse(String responseFilename) {
        return new MockResponse().setResponseCode(HTTP_OK)
                                 .setBody(loadResource(responseFilename));
    }

    private String loadResource(String responseFilename) {
        String body = null;
        try {
            InputStream responseStream = getClass().getResourceAsStream("/" + responseFilename + ".xml");
            if (responseStream == null) {
                throw new IOException("Resource not found: " + responseFilename);
            }
            body = new String(ByteStreams.toByteArray(responseStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }
}
