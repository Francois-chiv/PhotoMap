package fr.isep.photomap;

import android.content.Intent;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {
    private List titles = new ArrayList<String>();
    private List descriptions = new ArrayList<String>();
    private List ratings = new ArrayList<Float>();


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;

        public ViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.text_view);
        }

        public TextView getTextView(){
            return textView;
        }

    }

    public MarkerAdapter(List<Map<String, Object>> dataSet){
        for (int i = 0; i < dataSet.size(); i++){
            titles.add(dataSet.get(i).get("title"));
            descriptions.add(dataSet.get(i).get("description"));
            ratings.add(dataSet.get(i).get("rating"));
        }
        Log.d("Titles size", "" + titles.size());
        Log.d("Descriptions size", "" + descriptions.size());
        Log.d("Ratings size", "" + ratings.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_row_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText((String) titles.get(position) + " " + descriptions.get(position) + " " + ratings.get(position));
        viewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String selectedBand = ""+((TextView) v).getText();
                Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                intent.putExtra("SelectedBand", selectedBand);
                v.getContext().startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}