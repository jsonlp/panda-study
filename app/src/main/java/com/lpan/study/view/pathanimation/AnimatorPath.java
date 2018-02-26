package com.lpan.study.view.pathanimation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by liaopan on 2018/1/22 14:30.
 */

public class AnimatorPath {

    //一系列的轨迹记录动作
    private List<PathPoint> mPoints = new ArrayList<>();


    /**
     * 移动位置到:
     * @param x
     * @param y
     */
    public void moveTo(float x,float y){
        mPoints.add(PathPoint.moveTo(x,y));
    }

    /**
     * 直线移动
     * @param x
     * @param y
     */
    public void lineTo(float x,float y){
        mPoints.add(PathPoint.lineTo(x,y));
    }

    /**
     * 二阶贝塞尔曲线移动
     * @param c0X
     * @param c0Y
     * @param x
     * @param y
     */
    public void secondBesselCurveTo(float c0X, float c0Y,float x,float y){
        mPoints.add(PathPoint.secondBesselCurveTo(c0X,c0Y,x,y));
    }

    /**
     * 三阶贝塞尔曲线移动
     * @param c0X
     * @param c0Y
     * @param c1X
     * @param c1Y
     * @param x
     * @param y
     */
    public void thirdBesselCurveTo(float c0X, float c0Y, float c1X, float c1Y, float x, float y){
        mPoints.add(PathPoint.thirdBesselCurveTo(c0X,c0Y,c1X,c1Y,x,y));
    }

    public void paraboal(float x,float y){
        mPoints.add(PathPoint.parabola(x,y));
    }
    /**
     *
     * @return  返回移动动作集合
     */
    public Collection<PathPoint> getPoints(){
        return mPoints;
    }

}
