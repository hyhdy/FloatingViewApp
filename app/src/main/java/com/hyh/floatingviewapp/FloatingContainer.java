package com.hyh.floatingviewapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * created by curdyhuang on 2019-11-11
 */
public class FloatingContainer extends RelativeLayout {
    public static final int RES_ID = R.layout.floating_layout;
    private View mRoot;
    private RecyclerView mRvList;
    private ImageView mIvLogo;
    boolean mShowMsg = true;
    private int mTouchSlop;
    private int mInitMotionX,mInitMotionY,mLastMotionX,mLastMotionY;
    private OnFloatingListener mOnFloatingListener;
    private int mWidgetW;
    private int mWidgetH;

    public FloatingContainer(@NonNull Context context) {
        this(context,null);
    }

    public FloatingContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获得touchslop
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();

        mRoot = inflate(context,RES_ID,this);
        init();
    }

    private void init(){
        mIvLogo = mRoot.findViewById(R.id.iv_logo);

        mIvLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mShowMsg){
                    mRvList.setVisibility(GONE);
                    mShowMsg = false;
                }else{
                    mRvList.setVisibility(VISIBLE);
                    mShowMsg = true;
                }
            }
        });

        mRvList = mRoot.findViewById(R.id.rv_list);
        MyAdapter adapter = new MyAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvList.setLayoutManager(layoutManager);
        mRvList.setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(oldw!=w||oldh!=h){
            mWidgetW = w;
            mWidgetH = h;
            if(mOnFloatingListener !=null){
                mOnFloatingListener.onSizeChanged(w,h);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitMotionX = mLastMotionX = (int) ev.getRawX();
                mInitMotionY = mLastMotionY =  (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int absDx = Math.abs(x - mInitMotionX);
                int absDy = Math.abs(y - mInitMotionY);
                if (absDx > mTouchSlop/4 || absDy > mTouchSlop/4) {
                    Log.d("hyh", "FloatingContainer: onInterceptTouchEvent: 拦截");
                    intercept = true;
                }
                break;
            default:
        }
        return intercept||super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{

            }
                break;
            case MotionEvent.ACTION_MOVE: {
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - mLastMotionX;
                int movedY = nowY - mLastMotionY;
                mLastMotionX = nowX;
                mLastMotionY = nowY;
                if (mOnFloatingListener != null) {
                    mOnFloatingListener.onMove(movedX, movedY, mWidgetW, mWidgetH);
                }
            }
                break;
            case MotionEvent.ACTION_UP:{

            }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnFloatingListener(OnFloatingListener onFloatingListener) {
        mOnFloatingListener = onFloatingListener;
    }

    public interface OnFloatingListener {
        /**
         * 移动悬浮窗
         * @param movedX x轴移动的像素
         * @param movedY y轴移动的像素
         * @param widgetW 悬浮窗宽度
         * @param widgetH 悬浮窗高度
         */
        void onMove(int movedX, int movedY, int widgetW,int widgetH);

        /**
         * 悬浮窗尺寸更新
         * @param widgetW 悬浮窗宽度
         * @param widgetH 悬浮窗高度
         */
        void onSizeChanged(int widgetW,int widgetH);
    }

    private static class MyAdapter extends RecyclerView.Adapter{
        private List<String> mDataList = new ArrayList<>();

        public MyAdapter() {
            mDataList.add("hahaha1");
            mDataList.add("hahaha2");
            mDataList.add("hahaha3");
            mDataList.add("hahaha4");
            mDataList.add("hahaha1");
            mDataList.add("hahaha2");
            mDataList.add("hahaha3");
            mDataList.add("hahaha4");
            mDataList.add("hahaha1");
            mDataList.add("hahaha2");
            mDataList.add("hahaha3");
            mDataList.add("hahaha4");
            mDataList.add("hahaha1");
            mDataList.add("hahaha2");
            mDataList.add("hahaha3");
            mDataList.add("hahaha4");
            mDataList.add("hahaha1");
            mDataList.add("hahaha2");
            mDataList.add("hahaha3");
            mDataList.add("hahaha4");
            mDataList.add("hahaha1");
            mDataList.add("hahaha2");
            mDataList.add("hahaha3");
            mDataList.add("hahaha4");
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            NormalViewHolder normalViewHolder = new NormalViewHolder(textView);
            return normalViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((NormalViewHolder)holder).setViewData(mDataList.get(position));
        }

        @Override
        public int getItemCount() {
            if(mDataList!=null){
                return mDataList.size();
            }
            return 0;
        }

        public class NormalViewHolder extends RecyclerView.ViewHolder{
            private TextView mTvText;

            public NormalViewHolder(@NonNull View itemView) {
                super(itemView);
                mTvText = (TextView) itemView;
            }

            public void setViewData(String text){
                mTvText.setText(text);
            }

        }
    }
}
