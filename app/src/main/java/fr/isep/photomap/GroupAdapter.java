package fr.isep.photomap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List names = new ArrayList<String>();
    private List descriptions = new ArrayList<String>();
    private List members = new ArrayList<List<String>>();

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textViewGroupName;
        private final TextView textViewGroupDescription;

        public ViewHolder(View view){
            super(view);
            textViewGroupName = (TextView) view.findViewById(R.id.text_view_group_name);
            textViewGroupDescription = (TextView) view.findViewById(R.id.text_view_group_description);
        }

        public TextView getTextViewGroupName(){
            return textViewGroupName;
        }
        public TextView getTextViewGroupDescription(){
            return textViewGroupDescription;
        }

    }

    public GroupAdapter(List<Map<String, Object>> dataSet){
        for (int i = 0; i < dataSet.size(); i++){
            names.add(dataSet.get(i).get("name"));
            descriptions.add(dataSet.get(i).get("description"));
            members.add(dataSet.get(i).get("members"));
        }
        Log.d("Members", members.size() + "");
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.group_recycler_view_row_item, viewGroup, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder viewHolder, final int position) {
        String name = (String) names.get(position);
        String description = (String) descriptions.get(position);
        String membersString = "";
        List<String> itemMembers = (List<String>) this.members.get(position);
        for (int i = 0 ; i < itemMembers.size() ; i ++){
            membersString += itemMembers.get(i) + "\n";
        }

        viewHolder.getTextViewGroupName().setText(name);
        viewHolder.getTextViewGroupDescription().setText(description);
        String finalMembersString = membersString;
        viewHolder.getTextViewGroupName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GroupInfoActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Description", description);
                intent.putExtra("Members", finalMembersString);
                v.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

}
