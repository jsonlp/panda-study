package com.lpan.study.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lpan.study.context.AppContext;
import com.lpan.study.view.SimleLoadingView;
import com.test.lpanstudyrecord.R;

/**
 * Created by lpan on 2016/12/19.
 */

public class LoadingAnimationFragment extends BaseFragment {

    private SimleLoadingView mSimleLoadingView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_loading_animation;
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        mSimleLoadingView = (SimleLoadingView) view.findViewById(R.id.smile);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSimleLoadingView.setVisibility(View.GONE);
                Toast.makeText(AppContext.getContext(), "  loading over ", Toast.LENGTH_SHORT).show();
            }
        }, 5000);

    }


}
