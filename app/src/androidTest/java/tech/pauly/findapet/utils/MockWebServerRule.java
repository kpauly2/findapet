package tech.pauly.findapet.utils;

import android.content.Context;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import okhttp3.mockwebserver.MockWebServer;

public class MockWebServerRule implements TestRule {

    private MockWebServer mockWebServer = new MockWebServer();
    private MockWebServerDispatcher dispatcher = new MockWebServerDispatcher();

    public MockWebServerDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                mockWebServer.setDispatcher(dispatcher);
                mockWebServer.start(8010);
                try {
                    base.evaluate();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    mockWebServer.shutdown();
                }
            }
        };
    }
}
