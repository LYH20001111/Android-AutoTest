package com.hudou.autotest.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
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
import com.hudou.autotest.listener.MyOnClickListener;

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
        holder.llItemType.setOnClickListener(new MyOnClickListener() {
            @Override
            public void dealClick(View v) {
                enterOptionFragment(position);
            }
        });
//        holder.imgBtnDetail.setOnClickListener(v -> { if (!isFastClick()) enterOptionFragment(position);});
        //holder.imgBtnDetail.setOnClickListener(v -> holder.llItemType.performClick());// 两行代码相同的效果
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