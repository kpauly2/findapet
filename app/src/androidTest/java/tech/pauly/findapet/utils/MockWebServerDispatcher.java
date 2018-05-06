package tech.pauly.findapet.utils;

import android.util.Log;

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
                                 .setBody(RobotUtils.loadResource(this, responseFilename));
    }
}
