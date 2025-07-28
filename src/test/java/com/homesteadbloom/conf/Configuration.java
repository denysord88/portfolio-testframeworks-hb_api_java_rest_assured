package com.homesteadbloom.conf;

import com.homesteadbloom.controllers.BaseController;
import io.github.cdimascio.dotenv.Dotenv;

public class Configuration {
    private static final Dotenv dotenv = Dotenv
            .configure()
            .ignoreIfMissing()
            .load();

    public static final String BASE_URL = get("TA_BE_BASE_URL");
    public static final String BE_TESTS_PACKAGE = get("TA_BE_TESTS_PACKAGE");
    public static final int TESTING_THREADS = Integer.parseInt(get("TA_BE_TESTING_THREADS"));
    public static final String REFRESH_TOKEN = get("TA_BE_REFRESH_TOKEN");
    public static final boolean ENABLE_LOGGING = Boolean.parseBoolean(get("TA_BE_ENABLE_LOGGING"));
    public static final String[] EXCLUDED_GROUPS = get("TA_BE_EXCLUDED_GROUPS").split(",");
    public static final String[] INCLUDED_GROUPS = get("TA_BE_INCLUDED_GROUPS").split(",");
    public static final boolean IGNORE_SSL = Boolean.parseBoolean(get("TA_BE_IGNORE_SSL"));
    public static final int MAX_RESPONSE_TIME_SECONDS = Integer.parseInt(get("TA_BE_MAX_RESPONSE_TIME_SECONDS"));
    public static final int CONNECTION_TIMEOUT_MILLIS = Integer.parseInt(get("TA_BE_CONNECTION_TIMEOUT_SECONDS")) * 1000;
    public static final int RETRY_LIMIT = Integer.parseInt(get("TA_BE_RETRY_LIMIT"));
    public static final boolean PROXY_ENABLED = Boolean.parseBoolean(get("TA_BE_PROXY_ENABLED"));
    public static final String PROXY_SCHEME = get("TA_BE_PROXY_SCHEME");
    public static final String PROXY_HOST = get("TA_BE_PROXY_HOST");
    public static final int PROXY_PORT = Integer.parseInt(get("TA_BE_PROXY_PORT"));
    public static final String PROXY_USERNAME = get("TA_BE_PROXY_USERNAME");
    public static final String PROXY_PASSWORD = get("TA_BE_PROXY_PASSWORD");
    public static String ACCESS_TOKEN = new BaseController().refreshToken(REFRESH_TOKEN);

    public static String get(String parameterName) {
        String property = System.getProperty(parameterName);
        if (property == null) {
            property = dotenv.get(parameterName);
        }
        return property;
    }
}
