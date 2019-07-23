package com.example.linkagerecyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linkagerecyclerview.recyclerview.BaseRecyclerHolder;
import com.example.linkagerecyclerview.recyclerview.BaseRecyclerViewAdater;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinkBean linkBean;
    private LAdapter lAdapter;
    private RAdapter rAdapter;

    private void initData() {
        linkBean = new LinkBean();
        linkBean.itemLS = new ArrayList<>();
        linkBean.itemS = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LinkBean.ItemL itemL = new LinkBean.ItemL();
            itemL.setTitle("分类"+i);
            linkBean.itemLS.add(itemL);

            for (int j = 0; j < 16; j++) {
                if (i % 2 == 0 && j % 2 == 0){
                }else {
                    LinkBean.Item item = new LinkBean.Item();
                    item.setTitle("分类"+i);
                    item.setName("名称"+j);
                    item.setPrice("￥:"+(2+i+j)*3);
                    linkBean.itemS.add(item);
                }
            }
        }
        Log.i("ccb", linkBean.itemS.size()+"initData: "+linkBean.itemLS.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initListener();
    }

    private RecyclerView rvL, rvR;
    private TextView tv_head;

    private void initView() {
        tv_head = findViewById(R.id.tv_header);
        tv_head.setText(linkBean.itemLS.get(0).getTitle());
        rvL = findViewById(R.id.rv1);
        rvR = findViewById(R.id.rv2);
        rvL.setLayoutManager(new LinearLayoutManager(this));
        rvR.setLayoutManager(new LinearLayoutManager(this));
        lAdapter = new LAdapter(this,R.layout.item,linkBean.itemLS);
        lAdapter.bindToRecyclerView(rvL);
        rvL.setAdapter(lAdapter);
        rAdapter = new RAdapter(this,R.layout.item_goods,linkBean.itemS);
        rvR.setAdapter(rAdapter);
    }

    private boolean moveToTop = false;
    private int index;
    private void initListener() {
        lAdapter.setOnItemClickListener(new BaseRecyclerViewAdater.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (rvR.getScrollState() != RecyclerView.SCROLL_STATE_IDLE)return;
                lAdapter.fromClick = true;
                lAdapter.setChecked(position);
                String tag = lAdapter.getmData().get(position).getTitle();
                for (int i = 0; i < rAdapter.getmData().size(); i++) {
                    //根据左边选中的条目获取到右面此条目Title相同的位置索引；
                    if (TextUtils.equals(tag,rAdapter.getmData().get(i).getTitle())){
                        index = i;
                        moveToPosition_R(index);
                        return;
                    }
                }
            }
        });

        rvR.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) rvR.getLayoutManager();
                if (moveToTop){ //向下滑动时，只会把改条目显示出来；我们还需要让该条目滑动到顶部；
                    moveToTop = false;
                    int m = index - layoutManager.findFirstVisibleItemPosition();
                    if (m >= 0 && m <= layoutManager.getChildCount()){
                        int top = layoutManager.getChildAt(m).getTop();
                        rvR.smoothScrollBy(0,top);
                    }
                }else {
                    int index = layoutManager.findFirstVisibleItemPosition();
                    tv_head.setText(rAdapter.getmData().get(index).getTitle());
                    lAdapter.setToPosition(rAdapter.getmData().get(index).getTitle());
                }
            }
        });

        rvR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                lAdapter.fromClick = false;
                return false;
            }
        });
    }


    private void moveToPosition_R(int index) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvR.getLayoutManager();
        int f = layoutManager.findFirstVisibleItemPosition();
        int l = layoutManager.findLastVisibleItemPosition();
        if (index <= f){ //向上移动时
            layoutManager.scrollToPosition(index);
        }else if (index <= l){ //已经再屏幕上面显示时
            int m = index - f;
            if (0 <= m && m <= layoutManager.getChildCount()) {
                int top = layoutManager.getChildAt(m).getTop();
                rvR.smoothScrollBy(0, top);
            }
        }else { //向下移动时
            moveToTop = true;
            layoutManager.scrollToPosition(index);
        }
    }


    class LAdapter extends BaseRecyclerViewAdater<LinkBean.ItemL>{


        public LAdapter(Context context, int resLayout, List<LinkBean.ItemL> data) {
            super(context, resLayout, data);
        }

        @Override
        public void convert(BaseRecyclerHolder holder, final int position) {
            TextView tv = ((TextView)holder.getView(R.id.tv));
            tv.setText(getmData().get(position).getTitle());
            if (checked == position){
                tv.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
                tv.setBackgroundResource(R.color.colorfff);
            }else {
                tv.setTextColor(ContextCompat.getColor(context,R.color.color666));
                tv.setBackgroundResource(R.color.color16333333);
            }

        }

        private int checked; //当前选中项
        public boolean fromClick; //是否是自己点击的

        public void setChecked(int checked) {
            this.checked = checked;
            notifyDataSetChanged();
        }

        //让左边的额条目选中
        public void setToPosition(String title){
            if (fromClick)return;
            if (TextUtils.equals(title,getmData().get(checked).getTitle()))return;
            if (TextUtils.isEmpty(title))return;
            for (int i = 0; i < getmData().size(); i++) {
                if (TextUtils.equals(getmData().get(i).getTitle(),title)){
                    setChecked(i);
                    moveToPosition(i);
                    return;
                }
            }

        }

        private void moveToPosition(int index){
            //如果选中的条目不在显示范围内，要滑动条目让该条目显示出来
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getRecyclerView().getLayoutManager();
            int f = linearLayoutManager.findFirstVisibleItemPosition();
            int l = linearLayoutManager.findLastVisibleItemPosition();
            if (index<=f || index >= l){
                linearLayoutManager.scrollToPosition(index);
            }

        }

    }

    class RAdapter extends BaseRecyclerViewAdater<LinkBean.Item>{


        public RAdapter(Context context, int resLayout, List<LinkBean.Item> data) {
            super(context, resLayout, data);
        }

        @Override
        public void convert(BaseRecyclerHolder holder, final int position) {
            ((TextView)holder.getView(R.id.tvName)).setText(getmData().get(position).getTitle()+getmData().get(position).getName());
            ((TextView)holder.getView(R.id.tvPrice)).setText(getmData().get(position).getPrice());

            //悬停的标题头
            FrameLayout headLayout = holder.getView(R.id.stick_header);
            TextView tvHead = holder.getView(R.id.tv_header);
            if (position == 0){
               headLayout.setVisibility(View.VISIBLE);
               tvHead.setText(getmData().get(position).getTitle());
            }else {
                if (TextUtils.equals(getmData().get(position).getTitle(),getmData().get(position-1).getTitle())){
                    headLayout.setVisibility(View.GONE);
                }else {
                    headLayout.setVisibility(View.VISIBLE);
                    tvHead.setText(getmData().get(position).getTitle());
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Toast.makeText(MainActivity.this,getmData().get(position).getTitle()+getmData().get(position).getName(),Toast.LENGTH_SHORT).show();
                 }
             });
        }
    }

}