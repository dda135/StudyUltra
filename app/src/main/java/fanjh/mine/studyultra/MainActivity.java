package fanjh.mine.studyultra;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ScrollView;

import fanjh.mine.lib.PtrFrameLayout;
import fanjh.mine.lib.PtrHandler;

public class MainActivity extends FragmentActivity {
    private PtrFrameLayout mParentLayout;
    private TestHeader mHeaderView;
    private ScrollView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParentLayout = (PtrFrameLayout) findViewById(R.id.pcfl_layout);
        mHeaderView = (TestHeader) findViewById(R.id.th_header);
        mContentView = (ScrollView) findViewById(R.id.sv_content);
        mParentLayout.addPtrUIHandler(mHeaderView);
        mParentLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !mContentView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mParentLayout.refreshComplete();
                    }
                },3000);
            }
        });
    }
}
