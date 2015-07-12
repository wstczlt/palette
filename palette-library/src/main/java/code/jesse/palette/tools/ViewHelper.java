/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package code.jesse.palette.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.Editable;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Iterator;
import java.util.WeakHashMap;


/**
 * The core class of AQuery. Contains all the methods available from an view object.
 *
 * @see {@code https://github.com/androidquery}
 */
public class ViewHelper {

    private static final int FLAG_HARDWARE_ACCELERATED = 0x01000000;

    private View mRoot;
    private View mView;
    private Activity mAct;
    private final SparseArray<View> mHolder;

    public ViewHelper() {
        this.mHolder = new SparseArray<View>();
    }

    public ViewHelper(View root) {
        this();
        this.mRoot = root;
        this.mView = root;
    }

    public ViewHelper(Activity act) {
        this();
        this.mAct = act;
    }

    private ViewHelper create(View view) {
        return new ViewHelper(view);
    }

    private View findView(int id) {
        View result = mHolder.get(id);
        if (result != null) {
            return result;
        }
        if (mRoot != null) {
            result = mRoot.findViewById(id);
        } else if (mAct != null) {
            result = mAct.findViewById(id);
        }
        mHolder.put(id, result);
        return result;
    }

    private View findView(String tag) {

        // ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0)
        View result = null;
        if (mRoot != null) {
            result = mRoot.findViewWithTag(tag);
        } else if (mAct != null) {
            // result = act.findViewById(id);
            View top = ((ViewGroup) mAct.findViewById(android.R.id.content)).getChildAt(0);
            if (top != null) {
                result = top.findViewWithTag(tag);
            }
        }
        return result;

    }

    private View findView(int... path) {

        View result = findView(path[0]);

        for (int i = 1; i < path.length && result != null; i++) {
            result = result.findViewById(path[i]);
        }

        return result;

    }



    /**
     * Return a new AQuery object that uses the found view as a root.
     *
     * @param id the id
     * @return new AQuery object
     */
    public ViewHelper find(int id) {
        View view = findView(id);
        return create(view);
    }

    /**
     * Return a new AQuery object that uses the found parent as a root.
     * If no parent with matching id is found, operating view will be null and isExist() will return
     * false.
     * 
     *
     * @param id the parent id
     * @return new AQuery object
     */
    public ViewHelper parent(int id) {

        View node = mView;
        View result = null;

        while (node != null) {
            if (node.getId() == id) {
                result = node;
                break;
            }
            ViewParent p = node.getParent();
            if (!(p instanceof View)) break;
            node = (View) p;
        }

        return create(result);
    }


    /**
     * Recycle this AQuery object.
     * 
     * The method is designed to avoid recreating an AQuery object repeatedly, such as when in list
     * adapter getView method.
     *
     * @param root The new root of the recycled AQuery.
     * @return self
     */
    public ViewHelper recycle(View root) {
        this.mRoot = root;
        this.mView = root;
        this.mAct = null;
        return this;
    }

    /**
     * Return the current operating view.
     *
     * @return the view
     */
    public View getView() {
        return mView;
    }

    /**
     * Points the current operating view to the first view found with the id under the root.
     *
     * @param id the id
     * @return self
     */
    public ViewHelper id(int id) {

        return id(findView(id));
    }

    /**
     * Points the current operating view to the specified view.
     *
     * @param view
     * @return self
     */
    public ViewHelper id(View view) {
        this.mView = view;
        return this;
    }


    /**
     * Points the current operating view to the specified view with tag.
     *
     * @param tag
     * @return self
     */

    public ViewHelper id(String tag) {
        return id(findView(tag));
    }

    /**
     * Find the first view with first id, under that view, find again with 2nd id, etc...
     *
     * @param path The id path.
     * @return self
     */
    public ViewHelper id(int... path) {

        return id(findView(path));
    }


    /**
     * Set the text of a TextView.
     *
     * @param resid the resid
     * @return self
     */
    public ViewHelper text(int resid) {
        if (mView instanceof TextView) {
            TextView tv = (TextView) mView;
            tv.setText(resid);
        }
        return this;
    }

    /**
     * Set the text of a TextView with localized formatted string
     * from application's package's default string table
     *
     * @param resid the resid
     * @return self
     * @see Context#getString(int, Object...)
     */
    public ViewHelper text(int resid, Object... formatArgs) {
        Context context = getContext();
        if (context != null) {
            CharSequence text = context.getString(resid, formatArgs);
            text(text);
        }
        return this;
    }

    /**
     * Set the text of a TextView.
     *
     * @param text the text
     * @return self
     */
    public ViewHelper text(CharSequence text) {

        if (mView instanceof TextView) {
            TextView tv = (TextView) mView;
            tv.setText(text);
        }

        return this;
    }

    /**
     * Set the text of a TextView. Hide the view (gone) if text is empty.
     *
     * @param text the text
     * @param goneIfEmpty hide if text is null or length is 0
     * @return self
     */

    public ViewHelper text(CharSequence text, boolean goneIfEmpty) {

        if (goneIfEmpty && (text == null || text.length() == 0)) {
            return gone();
        } else {
            return text(text);
        }
    }



    /**
     * Set the text of a TextView.
     *
     * @param text the text
     * @return self
     */
    public ViewHelper text(Spanned text) {

        if (mView instanceof TextView) {
            TextView tv = (TextView) mView;
            tv.setText(text);
        }
        return this;
    }

    /**
     * Set progress.
     * 
     * @param progress the progress
     * @return self
     */
    public ViewHelper progress(int progress) {
        if (mView instanceof ProgressBar) {
            ((ProgressBar) mView).setProgress(progress);
        }
        return this;
    }

    /**
     * Set secondaryProgress.
     * 
     * @param secondaryProgress the secondaryProgress
     * @return self
     */
    public ViewHelper secondaryProgress(int secondaryProgress) {
        if (mView instanceof ProgressBar) {
            ((ProgressBar) mView).setSecondaryProgress(secondaryProgress);
        }
        return this;
    }

    /**
     * Set the text color of a TextView. Note that it's not a color resource id.
     *
     * @param color color code in ARGB
     * @return self
     */
    public ViewHelper textColor(int color) {

        if (mView instanceof TextView) {
            TextView tv = (TextView) mView;
            tv.setTextColor(color);
        }
        return this;
    }

    /**
     * Set the text color of a TextView from a color resource id.
     *
     * @param id color resource id
     * @return self
     */
    public ViewHelper textColorId(int id) {

        return textColor(getContext().getResources().getColor(id));
    }


    /**
     * Set the text typeface of a TextView.
     *
     * @param tf typeface
     * @return self
     */
    public ViewHelper typeface(Typeface tf) {

        if (mView instanceof TextView) {
            TextView tv = (TextView) mView;
            tv.setTypeface(tf);
        }
        return this;
    }

    /**
     * Set the text size (in sp) of a TextView.
     *
     * @param size size
     * @return self
     */
    public ViewHelper textSize(float size) {

        if (mView instanceof TextView) {
            TextView tv = (TextView) mView;
            tv.setTextSize(size);
        }
        return this;
    }


    /**
     * Set the adapter of an AdapterView.
     *
     * @param adapter adapter
     * @return self
     */
    public ViewHelper adapter(Adapter adapter) {

        if (mView instanceof AdapterView) {
            AdapterView av = (AdapterView) mView;
            av.setAdapter(adapter);
        }

        return this;
    }

    /**
     * Set the adapter of an ExpandableListView.
     *
     * @param adapter adapter
     * @return self
     */
    public ViewHelper adapter(ExpandableListAdapter adapter) {

        if (mView instanceof ExpandableListView) {
            ExpandableListView av = (ExpandableListView) mView;
            av.setAdapter(adapter);
        }

        return this;
    }

    /**
     * Set the image of an ImageView.
     *
     * @param resid the resource id
     * @return self
     */
    public ViewHelper image(int resid) {

        if (mView instanceof ImageView) {
            ImageView iv = (ImageView) mView;
            if (resid == 0) {
                iv.setImageBitmap(null);
            } else {
                iv.setImageResource(resid);
            }
        }

        return this;
    }

    /**
     * Set the image of an ImageView.
     *
     * @param drawable the drawable
     * @return self
     */
    public ViewHelper image(Drawable drawable) {

        if (mView instanceof ImageView) {
            ImageView iv = (ImageView) mView;
            iv.setImageDrawable(drawable);
        }

        return this;
    }

    /**
     * Set the image of an ImageView.
     *
     * @param bm Bitmap
     * @return self
     */
    public ViewHelper image(Bitmap bm) {

        if (mView instanceof ImageView) {
            ImageView iv = (ImageView) mView;
            iv.setImageBitmap(bm);
        }

        return this;
    }


    /**
     * Set tag object of a view.
     *
     * @param tag
     * @return self
     */
    public ViewHelper tag(Object tag) {

        if (mView != null) {
            mView.setTag(tag);
        }

        return this;
    }

    /**
     * Set tag object of a view.
     *
     * @param key
     * @param tag
     * @return self
     */
    public ViewHelper tag(int key, Object tag) {

        if (mView != null) {
            mView.setTag(key, tag);
        }

        return this;
    }

    /**
     * Set a view to be transparent.
     *
     * @param transparent the transparent
     * @return self
     */
    public ViewHelper transparent(boolean transparent) {

        if (mView != null) {
            Utility.transparent(mView, transparent);
        }

        return this;
    }

    /**
     * Enable a view.
     *
     * @param enabled state
     * @return self
     */
    public ViewHelper enabled(boolean enabled) {

        if (mView != null) {
            mView.setEnabled(enabled);
        }

        return this;
    }

    /**
     * Set checked state of a compound button.
     *
     * @param checked state
     * @return self
     */
    public ViewHelper checked(boolean checked) {

        if (mView instanceof CompoundButton) {
            CompoundButton cb = (CompoundButton) mView;
            cb.setChecked(checked);
        }

        return this;
    }

    /**
     * Get checked state of a compound button.
     *
     * @return checked
     */
    public boolean isChecked() {

        boolean checked = false;

        if (mView instanceof CompoundButton) {
            CompoundButton cb = (CompoundButton) mView;
            checked = cb.isChecked();
        }

        return checked;
    }

    /**
     * Set clickable for a view.
     *
     * @param clickable
     * @return self
     */
    public ViewHelper clickable(boolean clickable) {

        if (mView != null) {
            mView.setClickable(clickable);
        }

        return this;
    }


    /**
     * Set view visibility to View.GONE.
     *
     * @return self
     */
    public ViewHelper gone() {
        /*
         * if(view != null && view.getVisibility() != View.GONE){
         * view.setVisibility(View.GONE);
         * }
         * return this;
         */
        return visibility(View.GONE);
    }

    /**
     * Set view visibility to View.INVISIBLE.
     *
     * @return self
     */
    public ViewHelper invisible() {

        /*
         * if(view != null && view.getVisibility() != View.INVISIBLE){
         * view.setVisibility(View.INVISIBLE);
         * }
         * return this;
         */
        return visibility(View.INVISIBLE);
    }

    /**
     * Set view visibility to View.VISIBLE.
     *
     * @return self
     */
    public ViewHelper visible() {

        /*
         * if(view != null && view.getVisibility() != View.VISIBLE){
         * view.setVisibility(View.VISIBLE);
         * }
         * return this;
         */
        return visibility(View.VISIBLE);
    }

    /**
     * Set view visibility, such as View.VISIBLE.
     *
     * @return self
     */
    public ViewHelper visibility(int visibility) {

        if (mView != null && mView.getVisibility() != visibility) {
            mView.setVisibility(visibility);
        }

        return this;
    }


    /**
     * Set view background.
     *
     * @param id the id
     * @return self
     */
    public ViewHelper background(int id) {

        if (mView != null) {

            if (id != 0) {
                mView.setBackgroundResource(id);
            } else {
                mView.setBackgroundDrawable(null);
            }

        }

        return this;
    }

    /**
     * Set view background color.
     *
     * @param color color code in ARGB
     * @return self
     */
    public ViewHelper backgroundColor(int color) {

        if (mView != null) {
            mView.setBackgroundColor(color);
        }

        return this;
    }

    /**
     * Set view background color.
     *
     * @param colorResId color code in resource id
     * @return self
     */
    public ViewHelper backgroundColorId(int colorResId) {

        if (mView != null) {
            mView.setBackgroundColor(getContext().getResources().getColor(colorResId));
        }

        return this;
    }

    /**
     * Notify a ListView that the data of it's adapter is changed.
     *
     * @return self
     */
    public ViewHelper dataChanged() {

        if (mView instanceof AdapterView) {

            AdapterView<?> av = (AdapterView<?>) mView;
            Adapter a = av.getAdapter();

            if (a instanceof BaseAdapter) {
                BaseAdapter ba = (BaseAdapter) a;
                ba.notifyDataSetChanged();
            }

        }


        return this;
    }



    /**
     * Checks if the current view exist.
     *
     * @return true, if is exist
     */
    public boolean isExist() {
        return mView != null;
    }

    /**
     * Gets the tag of the view.
     *
     * @return tag
     */
    public Object getTag() {
        Object result = null;
        if (mView != null) {
            result = mView.getTag();
        }
        return result;
    }

    /**
     * Gets the tag of the view.
     * 
     * @param id the id
     * 
     * @return tag
     */
    public Object getTag(int id) {
        Object result = null;
        if (mView != null) {
            result = mView.getTag(id);
        }
        return result;
    }

    /**
     * Gets the current view as an image view.
     *
     * @return ImageView
     */
    public ImageView getImageView() {
        return (ImageView) mView;
    }

    /**
     * Gets the current view as an Gallery.
     *
     * @return Gallery
     */
    public Gallery getGallery() {
        return (Gallery) mView;
    }


    /**
     * Gets the current view as an ViewGroup.
     *
     * @return ViewGroup
     */
    public ViewGroup getViewGroup() {
        return (ViewGroup) mView;
    }


    /**
     * Gets the current view as a text view.
     *
     * @return TextView
     */
    public TextView getTextView() {
        return (TextView) mView;
    }

    /**
     * Gets the current view as an edit text.
     *
     * @return EditText
     */
    public EditText getEditText() {
        return (EditText) mView;
    }

    /**
     * Gets the current view as an progress bar.
     *
     * @return ProgressBar
     */
    public ProgressBar getProgressBar() {
        return (ProgressBar) mView;
    }

    /**
     * Gets the current view as seek bar.
     *
     * @return SeekBar
     */

    public SeekBar getSeekBar() {
        return (SeekBar) mView;
    }

    /**
     * Gets the current view as a button.
     *
     * @return Button
     */
    public Button getButton() {
        return (Button) mView;
    }

    /**
     * Gets the current view as a checkbox.
     *
     * @return CheckBox
     */
    public CheckBox getCheckBox() {
        return (CheckBox) mView;
    }

    /**
     * Gets the current view as a ListView.
     *
     * @return ListView
     */
    public ListView getListView() {
        return (ListView) mView;
    }

    /**
     * Gets the current view as a ExpandableListView.
     *
     * @return ExpandableListView
     */
    public ExpandableListView getExpandableListView() {
        return (ExpandableListView) mView;
    }

    /**
     * Gets the current view as a GridView.
     *
     * @return GridView
     */
    public GridView getGridView() {
        return (GridView) mView;
    }

    /**
     * Gets the current view as a RatingBar.
     *
     * @return RatingBar
     */
    public RatingBar getRatingBar() {
        return (RatingBar) mView;
    }

    /**
     * Gets the current view as a WebView.
     *
     * @return WebView
     */
    public WebView getWebView() {
        return (WebView) mView;
    }

    /**
     * Gets the current view as a spinner.
     *
     * @return Spinner
     */
    public Spinner getSpinner() {
        return (Spinner) mView;
    }

    /**
     * Gets the editable.
     *
     * @return the editable
     */
    public Editable getEditable() {

        Editable result = null;

        if (mView instanceof EditText) {
            result = ((EditText) mView).getEditableText();
        }

        return result;
    }

    /**
     * Gets the text of a TextView.
     *
     * @return the text
     */
    public CharSequence getText() {

        CharSequence result = null;

        if (mView instanceof TextView) {
            result = ((TextView) mView).getText();
        }

        return result;
    }

    /**
     * Gets the selected item if current view is an adapter view.
     *
     * @return selected
     */
    public Object getSelectedItem() {

        Object result = null;

        if (mView instanceof AdapterView<?>) {
            result = ((AdapterView<?>) mView).getSelectedItem();
        }

        return result;

    }


    /**
     * Gets the selected item position if current view is an adapter view.
     *
     * Returns AdapterView.INVALID_POSITION if not valid.
     *
     * @return selected position
     */
    public int getSelectedItemPosition() {

        int result = AdapterView.INVALID_POSITION;

        if (mView instanceof AdapterView<?>) {
            result = ((AdapterView<?>) mView).getSelectedItemPosition();
        }

        return result;

    }


    /**
     * Register a callback method for when the view is clicked.
     *
     * @param listener The callback method.
     * @return self
     */
    public ViewHelper clicked(OnClickListener listener) {

        if (mView != null) {
            mView.setOnClickListener(listener);
        }

        return this;
    }

    /**
     * Register a callback method for when the view is long clicked.
     *
     * @param listener The callback method.
     * @return self
     */
    public ViewHelper longClicked(OnLongClickListener listener) {

        if (mView != null) {
            mView.setOnLongClickListener(listener);
        }

        return this;
    }


    /**
     * Register a callback method for when an item is clicked in the ListView.
     *
     * @param listener The callback method.
     * @return self
     */
    public ViewHelper itemClicked(OnItemClickListener listener) {

        if (mView instanceof AdapterView) {

            AdapterView<?> alv = (AdapterView<?>) mView;
            alv.setOnItemClickListener(listener);
        }

        return this;

    }

    /**
     * Register a callback method for when an item is long clicked in the ListView.
     *
     * @param listener The callback method.
     * @return self
     */
    public ViewHelper itemLongClicked(OnItemLongClickListener listener) {

        if (mView instanceof AdapterView) {

            AdapterView<?> alv = (AdapterView<?>) mView;
            alv.setOnItemLongClickListener(listener);


        }

        return this;

    }


    /**
     * Register a callback method for when an item is selected.
     *
     * @param listener The item selected listener.
     * @return self
     */
    public ViewHelper itemSelected(OnItemSelectedListener listener) {

        if (mView instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) mView;
            alv.setOnItemSelectedListener(listener);
        }

        return this;

    }


    /**
     * Set selected item of an AdapterView.
     *
     * @param position The position of the item to be selected.
     * @return self
     */
    public ViewHelper setSelection(int position) {

        if (mView instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) mView;
            alv.setSelection(position);
        }

        return this;

    }


    /**
     * Set the activity to be hardware accelerated. Only applies when device API is 11+.
     *
     * @return self
     */
    public ViewHelper hardwareAccelerated11() {

        if (mAct != null) {
            mAct.getWindow().setFlags(FLAG_HARDWARE_ACCELERATED, FLAG_HARDWARE_ACCELERATED);
        }

        return this;
    }



    /**
     * Clear a view. Applies to ImageView, WebView, and TextView.
     *
     * @return self
     */
    public ViewHelper clear() {

        if (mView != null) {

            if (mView instanceof ImageView) {
                ImageView iv = ((ImageView) mView);
                iv.setImageBitmap(null);
            } else if (mView instanceof WebView) {
                WebView wv = ((WebView) mView);
                wv.stopLoading();
                wv.clearView();
            } else if (mView instanceof TextView) {
                TextView tv = ((TextView) mView);
                tv.setText("");
            }


        }

        return this;
    }



    /**
     * Set the margin of a view. Notes all parameters are in DIP, not in pixel.
     *
     * @param leftDip the left dip
     * @param topDip the top dip
     * @param rightDip the right dip
     * @param bottomDip the bottom dip
     * @return self
     */
    public ViewHelper margin(float leftDip, float topDip, float rightDip, float bottomDip) {

        if (mView != null) {

            LayoutParams lp = mView.getLayoutParams();

            if (lp instanceof MarginLayoutParams) {

                Context context = getContext();

                int left = Utility.dip2pixel(context, leftDip);
                int top = Utility.dip2pixel(context, topDip);
                int right = Utility.dip2pixel(context, rightDip);
                int bottom = Utility.dip2pixel(context, bottomDip);

                ((MarginLayoutParams) lp).setMargins(left, top, right, bottom);
                mView.setLayoutParams(lp);
            }

        }

        return this;
    }

    public ViewHelper marginPixel(int left, int top, int right, int bottom) {
        if (mView != null) {
            LayoutParams lp = mView.getLayoutParams();
            if (lp instanceof MarginLayoutParams) {
                ((MarginLayoutParams) lp).setMargins(left, top, right, bottom);
                mView.setLayoutParams(lp);
            }
        }
        return this;
    }

    public ViewHelper marginTopPixel(int top) {
        if (mView != null) {
            LayoutParams lp = mView.getLayoutParams();
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                marginLp.topMargin = top;
                mView.setLayoutParams(lp);
            }

        }
        return this;
    }

    /**
     * Set the width of a view in dip.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or
     * ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param dip width in dip
     * @return self
     */

    public ViewHelper width(int dip) {
        size(true, dip, true);
        return this;
    }

    /**
     * Set the height of a view in dip.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or
     * ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param dip height in dip
     * @return self
     */

    public ViewHelper height(int dip) {
        size(false, dip, true);
        return this;
    }

    /**
     * Set the width of a view in dip or pixel.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or
     * ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param width width
     * @param dip dip or pixel
     * @return self
     */

    public ViewHelper width(int width, boolean dip) {
        size(true, width, dip);
        return this;
    }

    /**
     * Set the height of a view in dip or pixel.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or
     * ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param height height
     * @param dip dip or pixel
     * @return self
     */

    public ViewHelper height(int height, boolean dip) {
        size(false, height, dip);
        return this;
    }

    private void size(boolean width, int n, boolean dip) {

        if (mView != null) {

            LayoutParams lp = mView.getLayoutParams();

            Context context = getContext();

            if (n > 0 && dip) {
                n = Utility.dip2pixel(context, n);
            }

            if (width) {
                lp.width = n;
            } else {
                lp.height = n;
            }

            mView.setLayoutParams(lp);

        }

    }



    /**
     * Return the context of activity or view.
     *
     * @return Context
     */

    public Context getContext() {
        if (mAct != null) {
            return mAct;
        }
        if (mRoot != null) {
            return mRoot.getContext();
        }
        return null;
    }

    /**
     * Starts an animation on the view.
     * 
     * <br>
     * contributed by: marcosbeirigo
     * 
     * @param animId Id of the desired animation.
     * @return self
     * 
     */
    public ViewHelper animate(int animId) {
        return animate(animId, null);
    }

    /**
     * Starts an animation on the view.
     * 
     * <br>
     * contributed by: marcosbeirigo
     * 
     * @param animId Id of the desired animation.
     * @param listener The listener to receive notifications from the animation on its events.
     * @return self
     * 
     * 
     */
    public ViewHelper animate(int animId, AnimationListener listener) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), animId);
        anim.setAnimationListener(listener);
        return animate(anim);
    }

    /**
     * Starts an animation on the view.
     * 
     * <br>
     * contributed by: marcosbeirigo
     * 
     * @param anim The desired animation.
     * @return self
     * 
     */
    public ViewHelper animate(Animation anim) {
        if (mView != null && anim != null) {
            mView.startAnimation(anim);
        }
        return this;
    }

    /**
     * Trigger click event
     * 
     * <br>
     * contributed by: neocoin
     * 
     * @return self
     * 
     * @see View#performClick()
     */
    public ViewHelper click() {
        if (mView != null) {
            mView.performClick();
        }
        return this;
    }

    /**
     * Trigger long click event
     * 
     * <br>
     * contributed by: neocoin
     * 
     * @return self
     * 
     * @see View#performClick()
     */
    public ViewHelper longClick() {
        if (mView != null) {
            mView.performLongClick();
        }
        return this;
    }


    // weak hash map that holds the dialogs so they will never memory leaked
    private static WeakHashMap<Dialog, Void> dialogs = new WeakHashMap<Dialog, Void>();

    /**
     * Show a dialog. Method dismiss() or dismissAll() should be called later.
     * 
     * @return self
     * 
     */
    public ViewHelper show(Dialog dialog) {

        try {
            if (dialog != null) {
                dialog.show();
                dialogs.put(dialog, null);
            }
        } catch (Exception e) {}

        return this;
    }

    /**
     * Dismiss a dialog previously shown with show().
     * 
     * @return self
     * 
     */
    public ViewHelper dismiss(Dialog dialog) {

        try {
            if (dialog != null) {
                dialogs.remove(dialog);
                dialog.dismiss();
            }
        } catch (Exception e) {}

        return this;
    }

    /**
     * Dismiss any AQuery dialogs.
     * 
     * @return self
     * 
     */
    public ViewHelper dismiss() {

        Iterator<Dialog> keys = dialogs.keySet().iterator();

        while (keys.hasNext()) {

            Dialog d = keys.next();
            try {
                d.dismiss();
            } catch (Exception e) {}
            keys.remove();

        }
        return this;

    }

    public ViewHelper add(View child) {
        if (mView instanceof ViewGroup) {
            ((ViewGroup) mView).addView(child);
        }
        return this;
    }

    public ViewHelper add(View child, int index) {
        if (mView instanceof ViewGroup) {
            ((ViewGroup) mView).addView(child, index);
        }
        return this;
    }

    public ViewHelper remove(View child) {
        if (mView instanceof ViewGroup) {
            ((ViewGroup) mView).removeView(child);
        }
        return this;
    }

    public ViewHelper removeAt(int index) {
        if (mView instanceof ViewGroup && index < ((ViewGroup) mView).getChildCount()) {
            ((ViewGroup) mView).removeViewAt(index);
        }
        return this;
    }

    public ViewHelper removeAll() {
        if (mView instanceof ViewGroup) {
            ((ViewGroup) mView).removeAllViews();
        }
        return this;
    }


    /**
     * Inflate a view from xml layout.
     * 
     * This method is similar to LayoutInflater.inflate() but with sanity checks against the
     * layout type of the convert view.
     * 
     * If the convertView is null or the convertView type doesn't matches layoutId type, a new view
     * is inflated. Otherwise the convertView will be returned for reuse.
     * 
     * @param convertView the view to be reused
     * @param layoutId the desired view type
     * @param root the view root for layout params, can be null
     * @return self
     * 
     */
    public View inflate(View convertView, int layoutId, ViewGroup root) {

        if (convertView != null) {
            Integer layout = (Integer) convertView.getTag(Tags.TAG_LAYOUT_ID);
            if (layout != null && layout.intValue() == layoutId) {
                return convertView;
            }
        }

        LayoutInflater inflater = null;

        if (mAct != null) {
            inflater = mAct.getLayoutInflater();
        } else {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(layoutId, root, false);
        view.setTag(Tags.TAG_LAYOUT_ID, layoutId);

        return view;

    }


    public ViewHelper expand(int position, boolean expand) {

        if (mView instanceof ExpandableListView) {

            ExpandableListView elv = (ExpandableListView) mView;
            if (expand) {
                elv.expandGroup(position);
            } else {
                elv.collapseGroup(position);
            }
        }

        return this;
    }

    public ViewHelper expand(boolean expand) {

        if (mView instanceof ExpandableListView) {

            ExpandableListView elv = (ExpandableListView) mView;
            ExpandableListAdapter ela = elv.getExpandableListAdapter();

            if (ela != null) {

                int count = ela.getGroupCount();

                for (int i = 0; i < count; i++) {
                    if (expand) {
                        elv.expandGroup(i);
                    } else {
                        elv.collapseGroup(i);
                    }
                }

            }


        }

        return this;
    }

    /**
     * Utility methods. Warning: Methods might changed in future versions.
     *
     */

    public static class Utility {

        public static void transparent(View view, boolean transparent) {

            float alpha = 1;
            if (transparent) alpha = 0.5f;

            setAlpha(view, alpha);
        }


        private static void setAlpha(View view, float alphaValue) {

            if (alphaValue == 1) {
                view.clearAnimation();
            } else {
                AlphaAnimation alpha = new AlphaAnimation(alphaValue, alphaValue);
                alpha.setDuration(0); // Make animation instant
                alpha.setFillAfter(true); // Tell it to persist after the animation ends
                view.startAnimation(alpha);
            }

        }



        public static int dip2pixel(Context context, float n) {
            int value =
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources()
                            .getDisplayMetrics());
            return value;
        }

        public static float pixel2dip(Context context, float n) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = n / (metrics.densityDpi / 160f);
            return dp;

        }

        public static void ensureUIThread() {

            if (!isUIThread()) {
                throw new IllegalStateException("Not UI Thread");
            }

        }

        public static boolean isUIThread() {

            long uiId = Looper.getMainLooper().getThread().getId();
            long cId = Thread.currentThread().getId();

            return uiId == cId;
        }


    }
}
