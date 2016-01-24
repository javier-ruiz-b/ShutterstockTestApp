package com.javierruiz.shutterstocktestapp;

import android.test.ApplicationTestCase;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<ShutterstockApp> {
    public ApplicationTest() {
        super(ShutterstockApp.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    @Test
    public void testDagger2Components() throws IOException {
        assertNotNull(getApplication().getComponent());
        assertNotNull(getApplication().getComponent().fileDownloader());
        assertNotNull(getApplication().getComponent().shutterstockClient());
    }


}