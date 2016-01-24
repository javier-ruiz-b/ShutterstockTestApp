package com.javierruiz.shutterstocktestapp.ui;

import android.support.test.espresso.action.EspressoKey;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.javierruiz.shutterstocktestapp.R;
import com.javierruiz.shutterstocktestapp.activity.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Javier on 24.01.2016.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testSearchButton() {
        onView(withId(R.id.action_search))
                .perform(click());

        onView(isAssignableFrom(EditText.class))
                .perform(typeText("test"), pressKey(KeyEvent.KEYCODE_ENTER));

        onData(anything())
                .inAdapterView(withId(R.id.gridView))
                .atPosition(1)
                .onChildView(withId(R.id.imageView))
                .perform(click());
    }


}
