package com.homesteadbloom.exec;

import com.homesteadbloom.testng.TestEventsListener;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.homesteadbloom.conf.Configuration.*;

public class Main {
    public static void main(String[] args) {
        Main testsExecutor = new Main();
        List<XmlPackage> packages = new LinkedList<>();
        packages.add(new XmlPackage("tests.common"));
        packages.add(new XmlPackage("tests.common.*"));
        packages.add(new XmlPackage("tests.common.*.*"));
        packages.add(new XmlPackage(BE_TESTS_PACKAGE));
        packages.add(new XmlPackage(BE_TESTS_PACKAGE + ".*"));
        packages.add(new XmlPackage(BE_TESTS_PACKAGE + ".*.*"));
        testsExecutor.runTests(packages, "BaseSuite");
    }

    private void runTests(List<XmlPackage> packages, String suiteName) {
        XmlSuite suite = new XmlSuite();
        suite.setName(suiteName);
        suite.setParallel(XmlSuite.ParallelMode.METHODS);
        suite.setThreadCount(TESTING_THREADS);

        XmlTest test = new XmlTest(suite);
        test.setName("Tests");
        test.setParallel(XmlSuite.ParallelMode.METHODS);
        test.setThreadCount(TESTING_THREADS);
        test.setXmlPackages(packages);
        if (INCLUDED_GROUPS.length > 0 && !INCLUDED_GROUPS[0].isEmpty())
            test.setIncludedGroups(Arrays.asList(INCLUDED_GROUPS));
        test.setExcludedGroups(Arrays.asList(EXCLUDED_GROUPS));

        List<XmlSuite> suites = new LinkedList<>();
        suites.add(suite);


        if (new File("TestNG.xml").exists()) {
            new File("TestNG.xml").delete();
        }
        writeTextToFile("TestNG.xml", suite.toXml());

        TestNG tng = new TestNG();
        tng.setXmlSuites(suites);
        tng.setUseDefaultListeners(false);
        TestListenerAdapter tla = new TestListenerAdapter();
        tng.addListener(tla);
        tng.addListener(new TestEventsListener());
        tng.run();
    }

    public static void writeTextToFile(String filePath, String text) {
        if (new File(filePath).exists()) return;
        if (filePath.contains("/")) checkAndFixFoldersPath(filePath.substring(0, filePath.lastIndexOf("/")));
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkAndFixFoldersPath(String unixFormatPath) {
        String[] pathTree = unixFormatPath.replace("./", "").split("/");
        String currentLevel = "./";
        for (String s : pathTree) {
            if (s.isEmpty()) continue;
            currentLevel += s + "/";
            new File(currentLevel).mkdir();
        }
    }

    public static String readTextFromFile(String filePath) {
        if (new File(filePath).exists()) return null;
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }
}
