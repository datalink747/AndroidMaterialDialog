/*
 * Copyright 2014 - 2016 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.dialog.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A custom view pager, which allows to disable switching between fragments using swipe gestures.
 *
 * @author Michael Rapp
 * @since 3.2.0
 */
public class ViewPager extends android.support.v4.view.ViewPager {

    /**
     * True, if switching between fragments using swipe gestures is enabled, false otherwise.
     */
    private boolean swipeEnabled;

    /**
     * Creates a new view pager, which allows to disable switching between fragments using swipe
     * gestures.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     */
    public ViewPager(@NonNull final Context context) {
        super(context);
    }

    /**
     * Creates a new view pager, which allows to disable switching between fragments using swipe
     * gestures.
     *
     * @param context
     *         The context, which should be used by the view, as an instance of the class {@link
     *         Context}. The context may not be null
     * @param attributeSet
     *         The attribute set, the view's attributes should be obtained from, as an instance of
     *         the type {@link AttributeSet} or null, if no attributes should be obtained
     */
    public ViewPager(@NonNull final Context context, @Nullable final AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * Returns, whether switching between fragments using swipe gestures is enabled, or not.
     *
     * @return True, if switching between fragments using swipe gestures is enabled, false otherwise
     */
    public final boolean isSwipeEnabled() {
        return swipeEnabled;
    }

    /**
     * Sets, whether switching between fragments using swipe gestures should be enabled, false
     * otherwise.
     *
     * @param enable
     *         True, if switching between fragments using swipe gestures should be enabled, false
     *         otherwise
     */
    public final void enableSwipe(final boolean enable) {
        this.swipeEnabled = enable;
    }

    @Override
    public final boolean onInterceptTouchEvent(final MotionEvent event) {
        return swipeEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public final boolean onTouchEvent(final MotionEvent event) {
        return swipeEnabled && super.onTouchEvent(event);
    }

}