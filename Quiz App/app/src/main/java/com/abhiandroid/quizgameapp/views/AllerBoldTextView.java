package com.abhiandroid.quizgameapp.views;

/**
 * Developed by AbhiAndroid.com
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 *  // Custom Textview with "Regular" type font family.
 */
public class AllerBoldTextView extends android.support.v7.widget.AppCompatTextView {
    public AllerBoldTextView(Context context) {
        super(context);
        setTypeface();
    }

    public AllerBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public AllerBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

   /* public RegularTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    /**
     * // This method is used to set font family type.
     */
    private void setTypeface(){
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/aller_Bd.ttf");
        setTypeface(tf ,1);
    }
}


/**
 * Developed by AbhiAndroid.com
 */