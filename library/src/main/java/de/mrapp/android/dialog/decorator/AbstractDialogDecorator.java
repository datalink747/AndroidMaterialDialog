/*
 * Copyright 2014 - 2017 Michael Rapp
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
package de.mrapp.android.dialog.decorator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import de.mrapp.android.dialog.R;
import de.mrapp.android.dialog.ScrollableArea;
import de.mrapp.android.dialog.ScrollableArea.Area;
import de.mrapp.android.dialog.model.Dialog;
import de.mrapp.android.dialog.model.DialogDecorator;
import de.mrapp.android.dialog.view.DialogRootView;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * An abstract base class for all decorators, which allow to modify the view hierarchy of a dialog.
 *
 * @param <DialogType>
 *         The type of the dialog, whose view hierarchy is modified by the decorator
 * @author Michael Rapp
 * @since 3.2.0
 */
public abstract class AbstractDialogDecorator<DialogType extends Dialog>
        implements DialogDecorator {

    /**
     * The dialog, whose view hierarchy is modified by the decorator.
     */
    private final DialogType dialog;

    /**
     * The window of the dialog, whose view hierarchy is modified by the decorator.
     */
    private Window window;

    /**
     * The root view of the view hierarchy, which is modified by the decorator.
     */
    private View view;

    /**
     * The root view of the dialog, whose view hierarchy is modified by the decorator.
     */
    private DialogRootView dialogRootView;

    /**
     * The root view of the content of the dialog, whose view hierarchy is modified by the
     * decorator.
     */
    private ViewGroup contentRootView;

    /**
     * The scroll view, which contains the scrollable areas of the dialog.
     */
    private ScrollView scrollView;

    /**
     * Returns the index, a view, which corresponds to a specific area, should be added at.
     *
     * @param parent
     *         The parent, the view should be added to, as an instance of the class {@link
     *         ViewGroup}. The parent may not be null
     * @param area
     *         The area, the view, which should be added, corresponds to, as a value of the enum
     *         {@link Area}
     * @return The index, the view should be added at, as an {@link Integer} value
     */
    private int getIndexOfArea(@NonNull final ViewGroup parent, final Area area) {
        int index = 0;

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            Area childArea = (Area) child.getTag();

            if (childArea.getIndex() > area.getIndex()) {
                return index;
            }

            index++;
        }

        return index;
    }

    /**
     * The method, which is invoked, when the decorator is attached to the view hierarchy.
     *
     * @param window
     *         The window of the dialog, whose view hierarchy is modified by the dialog, as an
     *         instance of the class {@link Window}. The window may not be null
     * @param view
     *         The root view of the dialog, which is modified by the decorator, as an instance of
     *         the class {@link View}. The view may not be null
     */
    protected abstract void onAttach(@NonNull final Window window, @NonNull final View view);

    /**
     * The method, which is invoked, when the decorator is detached from the view hierarchy.
     */
    protected abstract void onDetach();

    /**
     * Creates a new decorator, which allows to modify the view hierarchy of a dialog.
     *
     * @param dialog
     *         The dialog, whose view hierarchy should be modified by the decorator, as an instance
     *         of the generic type DialogType. The dialog may not be null
     */
    public AbstractDialogDecorator(@NonNull final DialogType dialog) {
        ensureNotNull(dialog, "The dialog may not be null");
        this.dialog = dialog;
        this.window = null;
        this.view = null;
        this.dialogRootView = null;
        this.contentRootView = null;
    }

    /**
     * Returns the dialog, whose view hierarchy is modified by the decorator.
     *
     * @return The dialog, whose view hierarchy is modified by the decorator, as an instance of the
     * generic type DialogType. The dialog may not be null
     */
    @NonNull
    protected final DialogType getDialog() {
        return dialog;
    }

    /**
     * Adds a specific area to the dialog.
     *
     * @param view
     *         The view, which corresponds to the area, should be added, as an instance of the class
     *         {@link View}. The view may not ben null
     * @param scrollableArea
     *         The scrollable area of the dialog, as an instance of the class {@link
     *         ScrollableArea}. The scrollable area may not be null
     * @param area
     *         The area, which should be added, as a value of the enum {@link Area}
     */
    protected final void addArea(@NonNull final View view,
                                 @NonNull final ScrollableArea scrollableArea,
                                 @NonNull final Area area) {
        if (getContentRootView() != null) {
            view.setTag(area);

            if (scrollableArea.isScrollable(area)) {
                if (scrollView == null) {
                    scrollView = getContentRootView().findViewById(R.id.scroll_view);

                    if (scrollView == null) {
                        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        scrollView = (ScrollView) layoutInflater
                                .inflate(R.layout.material_dialog_scroll_view, getContentRootView(),
                                        false);
                        scrollView.setTag(scrollableArea.getTopScrollableArea());
                        if (scrollableArea.getBottomScrollableArea().getIndex() -
                                scrollableArea.getTopScrollableArea().getIndex() > 0) {
                            LinearLayout scrollContainer = new LinearLayout(getContext());
                            scrollContainer.setOrientation(LinearLayout.VERTICAL);
                            scrollView
                                    .addView(scrollContainer, ScrollView.LayoutParams.MATCH_PARENT,
                                            ScrollView.LayoutParams.MATCH_PARENT);
                        }

                        getContentRootView().addView(scrollView);
                    }
                }

                ViewGroup scrollContainer =
                        scrollView.getChildCount() > 0 ? (ViewGroup) scrollView.getChildAt(0) :
                                scrollView;
                scrollContainer.addView(view, getIndexOfArea(scrollContainer, area));
            } else {
                getContentRootView().addView(view, getIndexOfArea(getContentRootView(), area));
            }
        }
    }

    /**
     * Returns the window of the dialog, whose view hierarchy is modified by the decorator.
     *
     * @return The window of the dialog, whose view hierarchy is modified by the decorator, as an
     * instance of the class {@link Window} or null, if the decorator is not attached
     */
    @Nullable
    public final Window getWindow() {
        return window;
    }

    /**
     * Returns the root view of the view hierarchy, which is modified by the decorator.
     *
     * @return The root view of the view hierarchy, which is modified by the decorator, as an
     * instance of the class {@link View} or null, if the decorator is not attached
     */
    @Nullable
    public final View getView() {
        return view;
    }

    /**
     * Returns the root view of the dialog, whose view hierarchy is modified by the decorator.
     *
     * @return The root view of the dialog, whose view hierarchy is modified by the decorator, as an
     * instance of the class {@link DialogRootView} or null, if the decorator is not attached
     */
    @Nullable
    public final DialogRootView getRootView() {
        return dialogRootView;
    }

    /**
     * Returns the root view of the content of the dialog, whose view hierarchy is modified by the
     * decorator.
     *
     * @return The root view of the content of the dialog, whose view hierarchy is modified by the
     * decorator, as an instance of the class {@link ViewGroup} or null, if the decorator is not
     * attached
     */
    @Nullable
    public final ViewGroup getContentRootView() {
        return contentRootView;
    }

    /**
     * Returns the scroll view, which contains the dialog's scrollable areas.
     *
     * @return The scroll view, which contains the dialog's scrollable areas, as an instance of the
     * class {@link ScrollView} or null, if the decorator is not attached or if the dialog does not
     * contain any scrollable areas
     */
    @Nullable
    public final ScrollView getScrollView() {
        return scrollView;
    }

    @Override
    public final Context getContext() {
        return dialog.getContext();
    }

    /**
     * Attaches the decorator to the view hierarchy. This enables the decorator to modify the view
     * hierarchy until it is detached.
     *
     * @param window
     *         The window of the dialog, whose view hierarchy should be modified by the decorator,
     *         as an instance of the class {@link Window}. The window may not be null
     * @param view
     *         The root view of the view hierarchy, which should be modified by the decorator, as an
     *         instance of the class {@link View}. The view may not be null
     * @param scrollableArea
     *         The scrollable area of the dialog, as an instance of the class {@link
     *         ScrollableArea}. The scrollable area may not be null
     */
    public final void attach(@NonNull final Window window, @NonNull final View view,
                             @NonNull final ScrollableArea scrollableArea) {
        ensureNotNull(window, "The window may not be null");
        ensureNotNull(view, "The view may not be null");
        this.window = window;
        this.view = view;
        this.dialogRootView = view.findViewById(R.id.root);
        this.contentRootView = view.findViewById(R.id.content_root);
        onAttach(window, view);
        addViews(scrollableArea);
    }

    /**
     * Adds the views, which are managed by the decorator, to the dialog.
     *
     * @param scrollableArea
     *         The scrollable area of the dialog, as an instance of the class {@link
     *         ScrollableArea}. The scrollable area may not be null
     */
    public void addViews(@NonNull final ScrollableArea scrollableArea) {

    }

    /**
     * Detaches the decorator from the view hierarchy. This prevents the decorator from modifying
     * the view hierarchy until it is attached again.
     */
    public final void detach() {
        this.window = null;
        this.view = null;
        this.dialogRootView = null;
        this.contentRootView = null;
        this.scrollView = null;
        onDetach();
    }

}