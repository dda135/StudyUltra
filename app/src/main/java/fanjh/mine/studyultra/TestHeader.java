package fanjh.mine.studyultra;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import fanjh.mine.lib.PtrFrameLayout;
import fanjh.mine.lib.PtrUIHandler;
import fanjh.mine.lib.indicator.PtrIndicator;

/**
 * Created by faker on 2017/4/26.
 */
public class TestHeader extends TextView implements PtrUIHandler{
    private boolean isBegining;

    public TestHeader(Context context) {
        this(context,null);
    }

    public TestHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(getResources().getColor(R.color.colorAccent));
        setTextSize(20);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        if(!isBegining){
            setText("onUIReset");
        }
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        if(!isBegining){
            setText("onUIRefreshPrepare");
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        isBegining = true;
        setText("onUIRefreshBegin");
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        isBegining = false;
        setText("onUIRefreshComplete");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        if(!isBegining){
            setText("onUIPositionChange");
        }
    }
}
