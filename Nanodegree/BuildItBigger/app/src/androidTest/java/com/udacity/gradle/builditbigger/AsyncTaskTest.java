package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AsyncTaskTest {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Test
    public void test_asynctask_retrieve_joke() {
        String result = null;

        EndpointAsyncTask endpointsAsyncTask = new EndpointAsyncTask();
        endpointsAsyncTask.execute(appContext);

        try {
            result = endpointsAsyncTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(result);
    }
}