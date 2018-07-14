package com.google.android.settings.gestures.assist;

import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;
import com.android.settings.widget.SeekBarPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.support.v7.preference.*;
import android.support.v7.preference.Preference;
import com.android.settings.R;

public class AssistGestureSeekBarPreference extends SeekBarPreference
{
    public AssistGestureSeekBarPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, android.support.v7.preference.R.attr.seekBarPreferenceStyle, com.android.internal.R.attr.seekBarPreferenceStyle), 0);
    }

    public AssistGestureSeekBarPreference(final Context context, final AttributeSet set, final int resourceId, final int n) {
        super(context, set, resourceId, n);
        setLayoutResource(R.layout.preference_assist_gesture_slider);
    }
}

