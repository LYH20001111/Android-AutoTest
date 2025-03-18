package com.hudou.autotest.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hudou.autotest.R;
import com.hudou.autotest.constant.Item;
import com.hudou.autotest.fragment.OptionsFragment;

import java.util.ArrayList;

public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.RecycleViewHolder>{
    private OptionsFragment testCaseFragment = null;
    private final ArrayList<Item> itemList;
    private final FragmentActivity activity;
    public MyRecycleAdapter(FragmentActivity activity, ArrayList<Item> itemList){
        this.activity = activity;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyRecycleAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_test_item_type, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecycleAdapter.RecycleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvItem.setText(itemList.get(position).getName());
        holder.tvDescription.setText(itemList.get(position).getDescription());

        holder.llItemType.setOnClickListener(v -> enterOptionFragment(position));
        holder.imgBtnDetail.setOnClickListener(v -> enterOptionFragment(position));
//        holder.llItemType.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    // 按下时改变颜色
//                    v.setBackgroundColor(Color.YELLOW);
//                    return true; // 消费事件
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    // 松开时恢复默认颜色
//                    v.setBackgroundColor(Color.parseColor("#cfcfcf"));
//                    if (v.performClick()){
//                        enterOptionFragment(position);
//                    }
//                    return true; // 消费事件
//                default:
//                    return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class RecycleViewHolder extends RecyclerView.ViewHolder{
        LinearLayout llItemType;
        TextView tvItem;
        TextView tvDescription;
        ImageButton imgBtnDetail;
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            llItemType = itemView.findViewById(R.id.ll_item_type);
            tvItem = itemView.findViewById(R.id.tv_item);
            tvDescription = itemView.findViewById(R.id.tv_description);
            imgBtnDetail =  itemView.findViewById(R.id.imgbtn_detail);
        }
    }

    private void enterOptionFragment(int position){
        testCaseFragment = new OptionsFragment(itemList.get(position).getClz());
        activity.runOnUiThread(() -> {
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
            supportFragmentManager.beginTransaction()
                    .replace(R.id.main_layout, testCaseFragment)
                    .addToBackStack(testCaseFragment.getClass().getSimpleName())
                    .commit();
            supportFragmentManager.executePendingTransactions();

        });
    }



}