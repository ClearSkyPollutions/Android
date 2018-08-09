package com.example.android.activities;


import android.content.Intent;
import android.os.IBinder;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.Root;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import com.example.android.models.Sensor;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {


    @Rule
    public ActivityTestRule<WelcomeActivity> mActivityTestRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void welcomeActivityTest() {

        nextSlide();
        nextSlide();
        nextSlide();
        nextSlide();
        nextSlide();

        ViewInteraction textView = onView(
                allOf(withId(R.id.title), withText("Share your data"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rootLayout),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Share your data")));

        ViewInteraction startButton = onView(
                allOf(withId(R.id.confirmSensors),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rootLayout),
                                        1),
                                2),
                        isDisplayed()));
        startButton.perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(
                allOf(withId(R.id.ville),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.CardFront),
                                        0),
                                0),
                        isDisplayed()))
                .check(doesNotExist());

        previousSlide();
        previousSlide();
        previousSlide();

        onData(
                allOf(
                        is(instanceOf(Sensor.class)), withName(equalTo("DHT22"))))
                .onChildView(allOf(withId(R.id.switchSensor), isDisplayed()))
                .perform(click());
        onData(
                allOf(
                        is(instanceOf(Sensor.class)), withName(equalTo("MQ-3"))))
                .onChildView(allOf(withId(R.id.switchSensor), isDisplayed()))
                .perform(click());

        nextSlide();
        nextSlide();

        //@TODO: proper IP and port addresses (hostname of container and port 80 ?)
        onView(
                allOf(withId(R.id.add_ip_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_ip),
                                        0),
                                0),
                        isDisplayed()))
                .perform(click())
                .perform(click())
                .perform(replaceText("127.0.0.1"))
                .perform(closeSoftKeyboard())
                .perform(pressImeActionButton());


        onView(
                allOf(withId(R.id.add_port_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_port),
                                        0),
                                0),
                        isDisplayed()))
                .perform(click())
                .perform(replaceText("80"))
                .perform(closeSoftKeyboard())
                .perform(pressImeActionButton());

        nextSlide();
        nextSlide();

        onView(
                allOf(withId(R.id.share),
                        isDisplayed()))
                .perform(click());

        //@TODO:proper IP and port addresses (hostname du container et 80 ?)
        onView(
                allOf(withId(R.id.add_ip_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_ip),
                                        0),
                                0),
                        isDisplayed()))
                .perform(click())
                .perform(replaceText("127.0.0.1"))
                .perform(closeSoftKeyboard())
                .perform(pressImeActionButton());

        onView(
                allOf(withId(R.id.add_port_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_port),
                                        0),
                                0),
                        isDisplayed()))
                .perform(click())
                .perform(replaceText("5000"))
                .perform(closeSoftKeyboard())
                .perform(pressImeActionButton());

        startButton.perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(
                allOf(withId(R.id.ville),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.CardFront),
                                        0),
                                0),
                        isDisplayed()))
                .check(matches(isDisplayed()));

    }

    public static void nextSlide(){
        onView(
                allOf(withId(R.id.viewPager),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()))
                .perform(swipeLeft());
    }

    public static void previousSlide(){
        onView(
                allOf(withId(R.id.viewPager),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()))
                .perform(swipeRight());
    }

    public static TypeSafeMatcher<Sensor> withName(Matcher nameMatcher){
        return new TypeSafeMatcher<Sensor>(){
            @Override
            public boolean matchesSafely(Sensor sensor) {
                return nameMatcher.matches(sensor.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with item content: ");
                nameMatcher.describeTo(description);
            }
        };
    }

    private class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            Log.d("Type : ", "" + type);
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                return windowToken == appToken;
            }
            return false;
        }
    }
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
