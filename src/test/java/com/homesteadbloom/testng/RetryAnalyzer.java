package com.homesteadbloom.testng;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import static com.homesteadbloom.conf.Configuration.RETRY_LIMIT;

public class RetryAnalyzer implements IRetryAnalyzer {
    int counter = 0;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (counter < RETRY_LIMIT) {
            counter++;
            return true;
        }
        return false;
    }
}
