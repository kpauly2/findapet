package tech.pauly.findapet.utils;

import java.util.regex.Pattern;

public class MockWebServerRequestPatterns {
    public static final Pattern FETCH_ANIMALS = Pattern.compile("GET /pet\\.find");
}
