package com.snippet.snippet;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.snippet.snippet.controller.DatabaseUtils;

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
public class SnippetUtilsTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.snippet.snippet", appContext.getPackageName());
    }

    @Test
    public void testDatabaseUtils() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseUtils.removeAllTables(appContext);

        for(int i = 0; i < 10; i++) {
            DatabaseUtils.addFilePathToDB(appContext, "Testing Path " + i, i%2 == 0);
            DatabaseUtils.addTagToDB(appContext, "Testing Tag " + i);
        }

        for(int i = 0; i < 10; i++) {
            DatabaseUtils.addPairToDB(appContext, DatabaseUtils.getFileIDFromPath(appContext, "Testing Path 1"), DatabaseUtils.getTagIDFromTag(appContext, "Testing Tag " + i));
            DatabaseUtils.addPairToDB(appContext, DatabaseUtils.getFileIDFromPath(appContext, "Testing Path 4"), DatabaseUtils.getTagIDFromTag(appContext, "Testing Tag " + i));
        }

        DatabaseUtils.addPairToDB(appContext, DatabaseUtils.getFileIDFromPath(appContext, "Testing Path 6"), DatabaseUtils.getTagIDFromTag(appContext, "Testing Tag 5"));

        List<String> paths = DatabaseUtils.getImagePathsWithTag(appContext, "Testing Tag 1");
        System.out.println("Number of paths using 'Testing Tag 1': " + paths.size());

        assertTrue(paths.size() == 2);

        assertTrue(paths.contains("Testing Path 1"));
        assertTrue(paths.contains("Testing Path 4"));

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Testing Tag 1");
        tags.add("Testing Tag 5");
        paths = DatabaseUtils.getImagePathsWithTag(appContext, tags);

        assertTrue(paths.size() == 3);

        assertTrue(paths.contains("Testing Path 1"));
        assertTrue(paths.contains("Testing Path 4"));
        assertTrue(paths.contains("Testing Path 6"));

        paths = DatabaseUtils.getUntaggedImagePathsWithTag(appContext, tags);

        assertTrue(paths.size() == 1);

        assertTrue(paths.contains("Testing Path 1"));
        assertFalse(paths.contains("Testing Path 4"));
        assertFalse(paths.contains("Testing Path 6"));

        paths = DatabaseUtils.getUntaggedImagesFromDB(appContext);

        assertTrue(paths.size() == 5);

        for(int i = 0; i < 10; i++) {
            if(i%2 == 0) {
                assertFalse(paths.contains("Testing Path " + i));
            }
            else {
                assertTrue(paths.contains("Testing Path " + i));
            }
        }

        paths = DatabaseUtils.getTaggedImagePathsWithTag(appContext, tags);

        assertTrue(paths.size() == 2);

        assertFalse(paths.contains("Testing Path 1"));
        assertTrue(paths.contains("Testing Path 4"));
        assertTrue(paths.contains("Testing Path 6"));

        paths = DatabaseUtils.getTaggedImagesFromDB(appContext);

        assertTrue(paths.size() == 5);

        for(int i = 0; i < 10; i++) {
            if(i%2 == 0) {
                assertTrue(paths.contains("Testing Path " + i));
            }
            else {
                assertFalse(paths.contains("Testing Path " + i));
            }
        }

        List<String> t2 = DatabaseUtils.getTagsFromFilePath(appContext, "Testing Path 1");

        assertTrue(t2.size() == 10);

        for(int i = 0; i < 10; i++) {
            assertTrue(t2.contains("Testing Tag " + i));
        }

        t2 = DatabaseUtils.getTagsFromFilePath(appContext, "Testing Path 6");

        assertTrue(t2.size() == 1);

        assertTrue(t2.contains("Testing Tag 5"));

        assertFalse(DatabaseUtils.getAutoTaggedFromFilePath(appContext, "Testing Path 1"));
        assertTrue(DatabaseUtils.setAutoTaggedFromFilePath(appContext, "Testing Path 1", true) == 1);
        assertTrue(DatabaseUtils.getAutoTaggedFromFilePath(appContext, "Testing Path 1"));

        DatabaseUtils.removeAllTables(appContext);
    }
}
