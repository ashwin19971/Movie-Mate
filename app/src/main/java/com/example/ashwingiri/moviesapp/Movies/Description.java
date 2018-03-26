package com.example.ashwingiri.moviesapp.Movies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ashwingiri.moviesapp.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Description extends YouTubeBaseActivity {

    private YouTubePlayerView youTubeView;
    public static final String KEY="key";
    Movie temp_movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_description);
        temp_movie= (Movie) getIntent().getSerializableExtra("description");
        if(temp_movie.getBackdrop_path().length() < 5)
        {
            (findViewById(R.id.ivPosterImage)).setVisibility(View.GONE);
        }else{
            Picasso.with(getApplicationContext())
                    .load("http://image.tmdb.org/t/p/w300/"+ temp_movie.getBackdrop_path())
                    .into((ImageView) findViewById(R.id.ivPosterImage));
        }

        TextView tvTitle,tvRating,tvOverview,tvRelease,tvTitleHeader;

        tvTitleHeader=findViewById(R.id.tvTitleHeader);
        tvTitleHeader.setText(temp_movie.getTitle());


        tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText(temp_movie.getTitle());

        tvRelease=findViewById(R.id.tvReleased);
        tvRelease.setText(String.format("Release Date: %s", temp_movie.getRelease_date()));

        tvRating=findViewById(R.id.tvRating);
        tvRating.setText(String.format("Rating: %s", temp_movie.getVote_average()));

        tvOverview=findViewById(R.id.tvOverView);
        tvOverview.setText(temp_movie.getOverview());

        new DownloadMovieTask().execute();
    }

    public class DownloadMovieTask extends AsyncTask<Void,Void,Void> {

        String s="";

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL(
                        "https://api.themoviedb.org/3/movie/"+temp_movie.getId()+"/videos?api_key=3b4c65c3780fc1ef44ec5500b186d833&language=en-US"
                );
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String buf=br.readLine();
                while (buf!=null){
                    sb.append(buf);
                    buf=br.readLine();
                }
                String data=sb.toString();
                JSONObject jsonResponse=new JSONObject(data);
                JSONArray movieJsonArray=jsonResponse.optJSONArray("results");
                final JSONObject movieJsonObject = movieJsonArray.getJSONObject(0);
                s=movieJsonObject.getString(KEY);
                Log.i("vgggggggg", "onCreate: "+s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            youTubeView = findViewById(R.id.youtube_view);
            youTubeView.initialize("AIzaSyD5Bz0W7prVXh7uuqc9tMXvFVHXQEjmf3U",
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                            try {
                                Log.i("vgggggggg", "onCreate: "+s);

                                youTubePlayer.cueVideo(s);

                             //               youTubePlayer.cueVideo("NW0lOo9_8Bw");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                        }
                    }
            );
        }

    }
}