package android.android_5.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import android.android_5.R;
import android.android_5.pojo.NavigationList;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {

    private LayoutInflater inflater;

    private Context context;

    private List<NavigationList> data = Collections.emptyList();

    public NavigationAdapter(Context context, List<NavigationList> data) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        NavigationViewHolder navigationViewHolder = new NavigationViewHolder(view);
        return navigationViewHolder;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        NavigationList current = data.get(position);

        holder.title.setText(current.getTitle());
        holder.icon.setImageResource(current.getId());
    }

    class NavigationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public NavigationViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView_rowName);
            icon = (ImageView) itemView.findViewById(R.id.imageView_row);
        }
    }


}
