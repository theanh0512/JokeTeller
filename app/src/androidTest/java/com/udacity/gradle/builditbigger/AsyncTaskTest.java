package com.udacity.gradle.builditbigger;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pham on 14/12/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AsyncTaskTest {
    final CountDownLatch signal = new CountDownLatch(1);
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
    private EndpointAsyncTask mTask;

    @Test
    public void testNonEmptyString() throws Throwable {

        mTask = new EndpointAsyncTask(mActivityRule.getActivity()) {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                signal.countDown();
            }
        };
        mTask.execute();
        signal.await(30, TimeUnit.SECONDS);
        if (!EndpointAsyncTask.updated) throw new AssertionError("Empty String");
    }
}
