package com.lpan.study.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lpan.R;
import com.lpan.study.activity.FullScreenActivity;
import com.lpan.study.activity.TransparentActivity;
import com.lpan.study.constants.Constants;

/**
 * Created by lpan on 2016/12/19.
 */

public class FragmentUtils {

    public static void navigateToInNewActivity(Context context, Fragment fragment, Bundle bundle) {
        Intent intent = new Intent(context, TransparentActivity.class);

        intent.putExtra(Constants.EXTRAS_CLASS_NAME, fragment.getClass()
                .getName());
        intent.putExtra(Constants.EXTRAS_BUNDLE, bundle);
        context.startActivity(intent);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activityAnimate(activity, bundle);
        }
    }

    public static void navigateWithNoAnimation(Context context, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.NO_ANIMATION, true);
        navigateToInNewActivity(context, fragment, bundle);
    }

    public static void navigateWithUpAnimation(Context context, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.ANIMATION_UP, true);
        navigateToInNewActivity(context, fragment, bundle);
    }

    public static void navigateWithFadeAnimation(Context context, Fragment fragment) {
        Bundle bundle = new Bundle();

        bundle.putBoolean(Constants.ANIMATION_FADE, true);
        navigateToInNewActivity(context, fragment, bundle);
    }

    public static void navigateWithLongFadeAnimation(Context context, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.ANIMATION_LONG_FADE, true);
        navigateToInNewActivity(context, fragment, bundle);
    }

    public static void navigateToInNewActivityWithTranstion(Context context, View view, Fragment fragment, Bundle bundle) {
        Intent intent = new Intent(context, TransparentActivity.class);

        intent.putExtra(Constants.EXTRAS_CLASS_NAME, fragment.getClass()
                .getName());
        intent.putExtra(Constants.EXTRAS_BUNDLE, bundle);
//        context.startActivity(intent);

//        // 这里指定了共享的视图元素
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation((Activity) context, view, "image");
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }

    public static void navigateToInFullScreenActivity(Context context, Fragment fragment, Bundle bundle) {
        Intent intent = new Intent(context, FullScreenActivity.class);

        intent.putExtra(Constants.EXTRAS_CLASS_NAME, fragment.getClass()
                .getName());
        intent.putExtra(Constants.EXTRAS_BUNDLE, bundle);
        context.startActivity(intent);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activityAnimate(activity, bundle);
        }
    }


    public static void activityAnimate(Activity context, Bundle bundle) {

        boolean noAnimation = false;
        boolean isUpAnim = false;
        boolean isfade = false;
        boolean isLongFade = false;

        if (bundle != null) {
            noAnimation = bundle.getBoolean(Constants.NO_ANIMATION);
            isUpAnim = bundle.getBoolean(Constants.ANIMATION_UP);
            isfade = bundle.getBoolean(Constants.ANIMATION_FADE);
            isLongFade = bundle.getBoolean(Constants.ANIMATION_LONG_FADE);
        }

        if (noAnimation) {
            context.overridePendingTransition(R.anim.no_anim, R.anim.no_anim);
        } else if (isUpAnim) {
            context.overridePendingTransition(R.anim.playlist_down_in, R.anim.playlist_slide_out);
        } else if (isLongFade) {
            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (isfade) {
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            context.overridePendingTransition(R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit);
        }

    }

    public static void replaceFragment(int fragmentId, FragmentManager fragmentManager,
                                       Fragment fragment, Bundle bundle) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        fragmentTransaction.replace(fragmentId, fragment, fragment.getClass().getName());
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }
}
