package com.zen_vy.chat.helpers;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class RecycleViewTestHelper {

    public static Matcher<View> atPosition(
            int position,
            Matcher<View> itemMatcher
    ) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder =
                        recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // Item not displayed
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
