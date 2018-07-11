package com.example.android.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class SensorsListView extends ListView {
    public SensorsListView(Context context) {
        super(context);
    }

    public SensorsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SensorsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        //ViewGroup.LayoutParams params = getLayoutParams();
        //params.height = getMeasuredHeight();
    }
}
