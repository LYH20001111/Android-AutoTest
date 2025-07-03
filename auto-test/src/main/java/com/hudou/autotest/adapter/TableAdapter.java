package com.hudou.autotest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hudou.autotest.R;
import com.hudou.autotest.constant.TableItem;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    private List<TableItem> items;

    public TableAdapter(List<TableItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_test_table_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableItem item = items.get(position);
        holder.caseItem.setText(item.getCaseItem());
        holder.failCount.setText(String.valueOf(item.getFailCount()));
        holder.totalCount.setText(String.valueOf(item.getTotalCount()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView caseItem;
        TextView failCount;
        TextView totalCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            caseItem = itemView.findViewById(R.id.caseItem);
            failCount = itemView.findViewById(R.id.failCount);
            totalCount = itemView.findViewById(R.id.totalCount);
        }
    }
}
