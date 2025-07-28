package tests.common;

import com.homesteadbloom.controllers.BaseController;
import com.homesteadbloom.testng.TestEventsListener;
import io.restassured.RestAssured;
import io.restassured.specification.ProxySpecification;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Listeners;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.homesteadbloom.conf.Configuration.*;
import static org.testng.Assert.assertEquals;

@Listeners({TestEventsListener.class})
public class BaseTest {
    public static boolean proxyEnabled = false;
    public static LinkedHashMap<String, Long> responseTimeMap = new LinkedHashMap<>();

    public void commonBeforeSuite() {
        // Do something for all tenants before suite:
        if (new File("allure-results").exists()) {
            try {
                FileUtils.deleteDirectory(new File("allure-results"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (PROXY_ENABLED && !proxyEnabled) {
            RestAssured.proxy(new ProxySpecification(
                    PROXY_HOST,
                    PROXY_PORT, PROXY_SCHEME)
                    .withAuth(PROXY_USERNAME, PROXY_PASSWORD));
            proxyEnabled = true;
        }
        new BaseController().refreshToken(REFRESH_TOKEN);
    }

    public void commonAfterSuite() {
        // Do something for all tenants after suite:
        long maxResponseTime = 5000;
        StringBuilder errorsForAllureReport = new StringBuilder();
        responseTimeMap = sortMap(responseTimeMap);
        responseTimeMap.forEach((endpoint, time) -> {
            if (time > maxResponseTime) {
                errorsForAllureReport.append("\n");
                errorsForAllureReport.append(endpoint);
                errorsForAllureReport.append(" - response time millis ");
                errorsForAllureReport.append(time);
            }
        });
        if (!errorsForAllureReport.toString().isEmpty()) {
            errorsForAllureReport.append("\n");
        }
        assertEquals(errorsForAllureReport.toString(), "", "Some endpoints response time more than " +
                maxResponseTime + " milliseconds\n" + errorsForAllureReport);
        if (errorsForAllureReport.toString().isEmpty()) {
            System.out.println("WOW All endpoints response times were under " + maxResponseTime + " milliseconds!");
        }
    }

    public static LinkedHashMap<String, Long> sortMap(HashMap<String, Long> map) {
        LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<>();
        ArrayList<Long> list = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            list.add(entry.getValue());
        }
        list.sort(Collections.reverseOrder());
        for (long num : list) {
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }
        return sortedMap;
    }
}
