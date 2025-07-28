package com.homesteadbloom.controllers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

import java.util.concurrent.TimeUnit;

import static com.homesteadbloom.conf.Configuration.*;
import static io.restassured.RestAssured.given;

public class BaseController {
    public RequestSpecification requestSpecification;

    public BaseController() {
        if (IGNORE_SSL) RestAssured.useRelaxedHTTPSValidation();
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .setConfig(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", CONNECTION_TIMEOUT_MILLIS)));
        if (ENABLE_LOGGING)
            requestSpecBuilder.addFilter(new RequestLoggingFilter()).addFilter(new ResponseLoggingFilter());
        requestSpecification = requestSpecBuilder.build();
        RestAssured.responseSpecification = new ResponseSpecBuilder()
                .expectBody("isEmpty()", Matchers.is(false))
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan((long) MAX_RESPONSE_TIME_SECONDS), TimeUnit.SECONDS)
                .build();
    }

    public String refreshToken(String refreshToken) {
        if (PROXY_ENABLED) {
            RestAssured.proxy(new ProxySpecification(
                    PROXY_HOST,
                    PROXY_PORT, PROXY_SCHEME)
                    .withAuth(PROXY_USERNAME, PROXY_PASSWORD));
        }
        ValidatableResponse tokenResponse = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body("{\"refresh_token\": \"" + refreshToken + "\"}")
                .post("/auth/refresh")
                .then();
        if (tokenResponse.extract().statusCode() != 200) {
            System.out.println("[ERROR] Can't get the access token: HTTP " + tokenResponse.extract().statusCode() +
                    " - " + tokenResponse.extract().asString());
            System.out.println("RToken value was " + refreshToken);
            System.exit(-1);
        }
        System.out.println("[DEBUG] access token received");
        String accessToken = tokenResponse.extract().jsonPath().getString("access_token");
        ACCESS_TOKEN = accessToken;
        ((RequestSpecificationImpl) requestSpecification).replaceHeader("Authorization",
                "Bearer " + ACCESS_TOKEN);
        return accessToken;
    }
}
