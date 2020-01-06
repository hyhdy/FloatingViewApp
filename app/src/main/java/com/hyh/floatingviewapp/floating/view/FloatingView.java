package com.hyh.floatingviewapp.floating.view;

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

import com.hyh.floatingviewapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * created by curdyhuang on 2019-11-23
 */
public class FloatingView extends RelativeLayout {
    public static final int RES_ID = R.layout.floating_layout;
    private View mRoot;
    private RecyclerView mRvList;
    private ImageView mIvLogo;
    boolean mShowMsg = true;

    public FloatingView(@NonNull Context context) {
        this(context,null);
    }

    public FloatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
            MyAdapter.NormalViewHolder normalViewHolder = new MyAdapter.NormalViewHolder(textView);
            return normalViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((MyAdapter.NormalViewHolder)holder).setViewData(mDataList.get(position));
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

    public boolean isShowMsg() {
        return mShowMsg;
    }
}
