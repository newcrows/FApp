package com.crowsnet.fappexample.agenda;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crowsnet.fappexample.R;
import com.crowsnet.fappexample.Utils;

import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.Holder> {

    public static final String TAG = "AgendaAdapter";

    private List<AgendaItem> items;
    private View.OnClickListener clickListener;

    public AgendaAdapter(ItemClickListener itemClickListener) {
        this.clickListener = makeClickListener(itemClickListener);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_agenda, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final AgendaItem item = items.get(position);

        int color = item.col;
        Utils.setTextAndColor(holder.dateTV, item.date, color);
        Utils.setTextAndColor(holder.timeTV, item.time, color);
        Utils.setTextAndColor(holder.labelTV, item.label, color);
        Utils.setTextAndColor(holder.durationTV, item.duration, color);

        View itemView = holder.itemView;
        itemView.setBackgroundColor(item.bk);
        itemView.setTag(position);
        itemView.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void setItems(List<AgendaItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private View.OnClickListener makeClickListener(final ItemClickListener itemClickListener) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    AgendaItem clickedItem = items.get((Integer) v.getTag());
                    itemClickListener.onItemClick(clickedItem);
                }
            }
        };
    }

    public interface ItemClickListener {

        void onItemClick(AgendaItem item);
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private TextView dateTV, timeTV, labelTV, durationTV;

        public Holder(View itemView) {
            super(itemView);

            dateTV = itemView.findViewById(R.id.dateTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            labelTV = itemView.findViewById(R.id.labelTV);
            durationTV = itemView.findViewById(R.id.durationTV);
        }
    }
}
