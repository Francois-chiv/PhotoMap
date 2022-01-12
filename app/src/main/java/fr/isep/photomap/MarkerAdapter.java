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
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {
    private List titles = new ArrayList<String>();
    private List descriptions = new ArrayList<String>();
    private List ratings = new ArrayList<Float>();
    private List photos = new ArrayList<String>();
    private List geopoints = new ArrayList<GeoPoint>();


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final RatingBar ratingBar;
        private final ImageView imageView;

        public ViewHolder(View view){
            super(view);
            textViewTitle = (TextView) view.findViewById(R.id.text_view_title);
            textViewDescription = (TextView) view.findViewById(R.id.text_view_description);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            imageView = (ImageView) view.findViewById(R.id.image_view);
        }

        public TextView getTextViewTitle(){
            return textViewTitle;
        }
        public TextView getTextViewDescription(){
            return textViewDescription;
        }
        public RatingBar getRatingBar(){
            return ratingBar;
        }
        public ImageView getImageView(){ return imageView; }

    }

    public MarkerAdapter(List<Map<String, Object>> dataSet){
        for (int i = 0; i < dataSet.size(); i++){
            titles.add(dataSet.get(i).get("title"));
            descriptions.add(dataSet.get(i).get("description"));
            ratings.add(dataSet.get(i).get("rating"));
            photos.add(dataSet.get(i).get("photo"));
            geopoints.add(dataSet.get(i).get("position"));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.marker_recycler_view_row_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String title = (String) titles.get(position);
        String description = (String) descriptions.get(position);
        float rating = ((Number) ratings.get(position)).floatValue();
        String photo = (String) photos.get(position);
        String latitude = "" + ((GeoPoint) geopoints.get(position)).getLatitude();
        String longitude = "" + ((GeoPoint) geopoints.get(position)).getLongitude();

        viewHolder.getTextViewTitle().setText(title);
        viewHolder.getTextViewDescription().setText(description);
        viewHolder.getRatingBar().setRating(rating);
        viewHolder.getImageView().setImageBitmap(decodeImage(photo));
        viewHolder.getTextViewTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MarkerInfoActivity.class);
                intent.putExtra("Title", title);
                intent.putExtra("Description", description);
                intent.putExtra("Rating", rating);
                intent.putExtra("Photo", photo);
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    private Bitmap decodeImage(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}