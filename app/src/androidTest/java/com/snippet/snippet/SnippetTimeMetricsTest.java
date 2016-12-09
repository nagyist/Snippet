package com.snippet.snippet;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.snippet.snippet.controller.DatabaseUtils;
import com.snippet.snippet.model.DatabaseHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SnippetTimeMetricsTest {
    
    @Test
    public void testDatabaseMetrics() throws Exception {
        for(int i = 0; i < 10; i++) {
            runMetrics();
        }
    }

    public void runMetrics() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseUtils.removeAllTables(appContext);
        DatabaseUtils.createDatabaseTables(appContext);

        List<String> filePaths = new ArrayList<>();
        List<String> tags = new ArrayList<>();

        long stopwatchStart = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            filePaths.add("Testing Path " + i);
            DatabaseUtils.addFilePathToDB(appContext, "Testing Path " + i, i%2 == 0);
        }
        Log.d("ADD 100 PATHS TO DB", "Time to query: " + Long.toString(System.currentTimeMillis() - stopwatchStart));

        stopwatchStart = System.currentTimeMillis();
        List<String> queriedPaths = DatabaseUtils.getUntaggedImagePathsWithoutTags(appContext);
        Log.d("UNTAGGED IMAGE W/O TAGS", "Time to query: " + Long.toString(System.currentTimeMillis() - stopwatchStart));

        assertTrue(queriedPaths.size() == 50);
        for(int i = 0; i < 100; i++) {
            if(i%2 == 0) {
                assertFalse(queriedPaths.contains("Testing Path " + i));
            }
            else {
                assertTrue(queriedPaths.contains("Testing Path " + i));
            }
        }

        stopwatchStart = System.currentTimeMillis();
        queriedPaths = DatabaseUtils.getTaggedImagePathsWithoutTags(appContext);
        Log.d("TAGGED IMAGE W/O TAGS", "Time to query: " + Long.toString(System.currentTimeMillis() - stopwatchStart));

        assertTrue(queriedPaths.size() == 50);
        for(int i = 0; i < 100; i++) {
            if(i%2 == 0) {
                assertTrue(queriedPaths.contains("Testing Path " + i));
            }
            else {
                assertFalse(queriedPaths.contains("Testing Path " + i));
            }
        }

        stopwatchStart = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            tags.add("Tag " + i);
            DatabaseUtils.addTagToDB(appContext, tags.get(i));
        }
        Log.d("ADD 100 TAGS TO DB", "Time to query: " + Long.toString(System.currentTimeMillis() - stopwatchStart));

        stopwatchStart = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            DatabaseUtils.addTagToFilePath(appContext, tags.get(i), filePaths.get(i));
        }
        Log.d("ADD 100 PAIRS TO DB", "Time to query: " + Long.toString(System.currentTimeMillis() - stopwatchStart));

        stopwatchStart = System.currentTimeMillis();
        List<String> queriedResults = DatabaseUtils.getTaggedImagePathsWithTag(appContext, tags);
        Log.d("TAGGED W/ ALL TAGS", "Time to query: " + Long.toString(System.currentTimeMillis() - stopwatchStart));

        assertTrue(queriedResults.size() == 50);

        stopwatchStart = System.currentTimeMillis();
        queriedResults = DatabaseUtils.getUntaggedImagePathsWithTag(appContext, tags);
        Log.d("UNTAGGED W/ ALL TAGS", "Time to query: " + Long.toString(System.currentTimeMillis() - stopwatchStart));

        assertTrue(queriedResults.size() == 50);

        DatabaseUtils.removeAllTables(appContext);

        Log.d(" ", " ");
    }
}
