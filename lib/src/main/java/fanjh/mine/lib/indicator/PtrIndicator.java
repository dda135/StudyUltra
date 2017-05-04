package fanjh.mine.lib.indicator;

import android.graphics.PointF;

/**
 * 手势处理的代理，主要是记录一些参数和处理数据判断
 */
public class PtrIndicator {

    public final static int POS_START = 0;
    protected int mOffsetToRefresh = 0;//刷新偏移量
    //记录上次触摸事件时候的手指坐标
    private PointF mPtLastMove = new PointF();
    private float mOffsetX;//当前move事件的x偏移量
    private float mOffsetY;//当前move事件的y偏移量，经过mResistance处理
    private int mCurrentPos = 0;//这个可以理解为当前头部位置
    private int mLastPos = 0;//在更新位置之前会记录之前的mCurrentPos
    private int mHeaderHeight;//头部的高度
    private int mPressedPos = 0;//当前按下时的头部位置，实际上就是DOWN事件时的mCurrentPos

    private float mRatioOfHeaderHeightToRefresh = 1.2f;//手动刷新时候的比例，基于mHeaderHeight
    private float mResistance = 1.7f;
    private boolean mIsUnderTouch = false;//当前是否在触摸状态下
    private int mOffsetToKeepHeaderWhileLoading = -1;//当刷新的时候保持头部位置的偏移量，这个需要手动设置，结合自动刷新使用
    // record the refresh complete position
    private int mRefreshCompleteY = 0;

    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    /**
     * 标示当前没有处于触摸事件
     */
    public void onRelease() {
        mIsUnderTouch = false;
    }

    /**
     * 记录刷新事件完成的位置
     */
    public void onUIRefreshComplete() {
        mRefreshCompleteY = mCurrentPos;
    }

    /**
     * 是否
     * @return
     */
    public boolean goDownCrossFinishPosition() {
        return mCurrentPos >= mRefreshCompleteY;
    }

    /**
     * 处理滑动时的偏移量
     * @param offsetX 两次事件的x坐标偏移量
     * @param offsetY 两次事件的y坐标偏移量，注意到这里有一个系数
     */
    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY / mResistance);
    }

    /**
     * 设置刷新系数，并且重新计算当前有效刷新偏移量
     * @param ratio
     */
    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mRatioOfHeaderHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * ratio);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mRatioOfHeaderHeightToRefresh;
    }

    /**
     * 获得刷新偏移量
     * @return
     */
    public int getOffsetToRefresh() {
        return mOffsetToRefresh;
    }

    /**
     * 手动设置刷新的偏移量
     * @param offset 设置的偏移量
     */
    public void setOffsetToRefresh(int offset) {
        mRatioOfHeaderHeightToRefresh = mHeaderHeight * 1f / offset;
        mOffsetToRefresh = offset;
    }

    /**
     * 处理按压事件
     * @param x 按压时的x坐标
     * @param y 按压时的y坐标
     */
    public void onPressDown(float x, float y) {
        mIsUnderTouch = true;//标记当前处于按压下
        mPressedPos = mCurrentPos;//记录按压时候的偏移量
        mPtLastMove.set(x, y);//记录当前事件的坐标
    }

    /**
     * 当前手势为滑动
     * @param x 当前x坐标
     * @param y 当前y坐标
     */
    public final void onMove(float x, float y) {
        //减去上次的坐标，获得两次事件的偏移量
        float offsetX = x - mPtLastMove.x;
        float offsetY = (y - mPtLastMove.y);
        //处理偏移量并保存
        processOnMove(x, y, offsetX, offsetY);
        //其中offsetY经过系数处理
        mPtLastMove.set(x, y);
    }

    protected void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public int getLastPosY() {
        return mLastPos;
    }

    public int getCurrentPosY() {
        return mCurrentPos;
    }

    /**
     * 设置当前偏移量
     * Update current position before update the UI
     */
    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPos;//记录上次的偏移量
        mCurrentPos = current;//修改偏移量
        onUpdatePos(current, mLastPos);//回调
    }

    /**
     * 偏移量变化的回调
     * @param current 当前偏移量
     * @param last 之前的偏移量
     */
    protected void onUpdatePos(int current, int last) {
        //默认空实现
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setHeaderHeight(int height) {
        mHeaderHeight = height;
        updateHeight();
    }

    /**
     * 修改头部高度之后要修改刷新偏移量，主要是有比例存在
     */
    protected void updateHeight() {
        mOffsetToRefresh = (int) (mRatioOfHeaderHeightToRefresh * mHeaderHeight);
    }

    public void convertFrom(PtrIndicator ptrSlider) {
        mCurrentPos = ptrSlider.mCurrentPos;
        mLastPos = ptrSlider.mLastPos;
        mHeaderHeight = ptrSlider.mHeaderHeight;
    }

    /**
     * 当前头部位置是否超出起始位置
     * @return true表示已经超出，此时基本可以认为头部可见
     */
    public boolean hasLeftStartPosition() {
        return mCurrentPos > POS_START;
    }

    /**
     * 是否从初始位置开始进行了移动
     * @return true是
     */
    public boolean hasJustLeftStartPosition() {
        return mLastPos == POS_START && hasLeftStartPosition();
    }

    /**
     * 当前是否刚好回到初始位置
     * @return true是
     */
    public boolean hasJustBackToStartPosition() {
        return mLastPos != POS_START && isInStartPosition();
    }

    /**
     * 当前是否超出刷新的偏移量
     * @return true表示已经超出
     */
    public boolean isOverOffsetToRefresh() {
        return mCurrentPos >= getOffsetToRefresh();
    }

    /**
     * 相对按压的时候是否有移动
     * @return true表示有移动
     */
    public boolean hasMovedAfterPressedDown() {
        return mCurrentPos != mPressedPos;
    }

    /**
     * 当前是否在初始位置
     * @return true在
     */
    public boolean isInStartPosition() {
        return mCurrentPos == POS_START;
    }

    /**
     * 是否刚刚从上到下穿越刷新的位置
     * @return true表示刚好穿过刷新的位置
     */
    public boolean crossRefreshLineFromTopToBottom() {
        return mLastPos < getOffsetToRefresh() && mCurrentPos >= getOffsetToRefresh();
    }

    /**
     * 是否刚刚从上到下穿越头部高度的偏移量位置
     * 自动刷新的时候有用
     */
    public boolean hasJustReachedHeaderHeightFromTopToBottom() {
        return mLastPos < mHeaderHeight && mCurrentPos >= mHeaderHeight;
    }

    /**
     * 当前位置是否超过保持头部位置刷新的偏移量
     * 一般来说这个是用于刷新后回弹部分高度用的
     * @return true表示满足条件
     */
    public boolean isOverOffsetToKeepHeaderWhileLoading() {
        return mCurrentPos > getOffsetToKeepHeaderWhileLoading();
    }

    /**
     * 手动设置满足的刷新时头部位置的偏移量，注意一下这个是偏移量
     * @param offset 需要设置的偏移量，要>=0才有效
     */
    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        mOffsetToKeepHeaderWhileLoading = offset;
    }

    /**
     * 获得刷新时头部位置的偏移量，如果没有手动设置，默认使用头部的高度
     * @return 偏移量，这个和mCurrentPos的意义一致
     */
    public int getOffsetToKeepHeaderWhileLoading() {
        return mOffsetToKeepHeaderWhileLoading >= 0 ? mOffsetToKeepHeaderWhileLoading : mHeaderHeight;
    }

    public boolean isAlreadyHere(int to) {
        return mCurrentPos == to;
    }

    public float getLastPercent() {
        final float oldPercent = mHeaderHeight == 0 ? 0 : mLastPos * 1f / mHeaderHeight;
        return oldPercent;
    }

    public float getCurrentPercent() {
        final float currentPercent = mHeaderHeight == 0 ? 0 : mCurrentPos * 1f / mHeaderHeight;
        return currentPercent;
    }

    /**
     * 当前偏移量是否小于最小值
     * @param to  当前偏移量
     * @return true小于最小值
     */
    public boolean willOverTop(int to) {
        return to < POS_START;
    }
}
