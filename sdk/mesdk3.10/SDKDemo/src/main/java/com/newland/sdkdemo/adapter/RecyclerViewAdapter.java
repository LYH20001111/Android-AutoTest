package com.newland.sdkdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newland.sdkdemo.R;
import com.newland.sdkdemo.annotation.MethodGridBean;

import java.util.List;

/**
 * Author by bxy, Date on 2019/5/9 0009.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<MethodGridBean> mGridList;

    public enum RecyclerViewType {
        ROW3_SMALL, ROW3_MEDIUM, ROW3_BIG
    }

    private RecyclerViewType itemType;

    public RecyclerViewAdapter(List<MethodGridBean> list, RecyclerViewType itemType) {
        this.mGridList = list;
        this.itemType = itemType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resouceid = R.layout.recyclerview_item3s;
        if (itemType == RecyclerViewType.ROW3_SMALL) {
            resouceid = R.layout.recyclerview_item3s;
        } else if (itemType == RecyclerViewType.ROW3_BIG) {
            resouceid = R.layout.recyclerview_item3b;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(resouceid, null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MethodGridBean bean = mGridList.get(position);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickLitener != null) {
                    mOnItemClickLitener.onItemClick(position);
                }
            }
        });
        try {
            if (itemType == RecyclerViewType.ROW3_SMALL) {
                if (bean.divtipid == -1) {
                    holder.btnLayOut.setVisibility(View.VISIBLE);
                    holder.tvTip.setVisibility(View.GONE);
                } else if (bean.divtipid == 0) {
                    holder.btnLayOut.setVisibility(View.GONE);
                    holder.tvTip.setVisibility(View.VISIBLE);
                    holder.tvTip.setText("");
                } else {
                    holder.btnLayOut.setVisibility(View.GONE);
                    holder.tvTip.setVisibility(View.VISIBLE);
                    holder.tvTip.setText(bean.divtipid);
                }
                if (bean.nameid != -1 || !bean.name.equals("")) {
                    holder.number.setVisibility(View.VISIBLE);
                    if(bean.imageid != -1){
                        holder.number.setText(bean.imageid + "");
                    }else{
                        holder.number.setText(bean.functionid + "");
                    }

                } else {
                    holder.number.setVisibility(View.INVISIBLE);
                }
            } else if (itemType == RecyclerViewType.ROW3_BIG) {
                holder.imageView.setImageResource(bean.imageid);
            }
            if (bean.nameid != -1) {
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(bean.nameid);
            }else if (!bean.name.equals("")){
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(bean.name+"");
            }else{
                holder.name.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mGridList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout parentLayout;
        TextView number;
        TextView name;
        ImageView imageView;
        RelativeLayout btnLayOut;
        TextView tvTip;

        public ViewHolder(View view) {
            super(view);
            parentLayout = (RelativeLayout) view.findViewById(R.id.id_recycleviewitem);
            name = (TextView) view.findViewById(R.id.id_name);
            if (itemType == RecyclerViewType.ROW3_SMALL) {
                number = (TextView) view.findViewById(R.id.id_number);
            } else if (itemType == RecyclerViewType.ROW3_BIG) {
                imageView = (ImageView) view.findViewById(R.id.id_imageview);
            }
            btnLayOut = (RelativeLayout) view.findViewById(R.id.id_btnlayout);
            tvTip = (TextView) view.findViewById(R.id.id_tip);
        }
    }

    private OnItemClickLitener mOnItemClickLitener;

    public interface OnItemClickLitener {
        void onItemClick(int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}