package com.jibestream.worldmap;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;

import com.jibestream.worldmap.map.MapsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {

    @Rule
    public final ActivityRule<MapsActivity> activity = new ActivityRule<>(MapsActivity.class);

    @Test
    public void shouldBeAbleToLaunchMainScreen() {
        onView(withId(R.id.map)).check(ViewAssertions.matches(isDisplayed()));
    }

}
