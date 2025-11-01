package com.bhcj.telling.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MesureListView extends ListView {

    public MesureListView(Context context) {
        super(context);
    }

    public MesureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
