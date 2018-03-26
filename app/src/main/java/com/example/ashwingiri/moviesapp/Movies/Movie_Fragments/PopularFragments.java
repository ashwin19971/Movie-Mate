package com.example.ashwingiri.moviesapp.Movies.Movie_Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ashwingiri.moviesapp.Movies.Description;
import com.example.ashwingiri.moviesapp.Movies.Movie;
import com.example.ashwingiri.moviesapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PopularFragments extends Fragment  {

    ArrayList<Movie> movie=new ArrayList<>();
    ListView lvMovie;
    MovieAdapter adapter;
    URL url;
    String data;

    public static final String POSTER_PATH="poster_path";
    public static final String OVERVIEW="overview";
    public static final String RELEASE_DATE="release_date";
    public static final String ID="id";
    public static final String TITLE="title";
    public static final String BACKDROP_PATH="backdrop_path";
    public static final String VOTE_AVERAGE= "vote_average";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this activity_movie_fragment
        View rootView= inflater.inflate(R.layout.activity_movie_fragment, container, false);
        lvMovie= rootView.findViewById(R.id.lvInTheaters);
        adapter=new MovieAdapter();
        lvMovie.setAdapter(adapter);
//        lvMovie.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new DownloadMovieTask().execute();

    }

//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        ArrayList<String> temp=new ArrayList<>();
//        temp.add(data);
//        temp.add(String.valueOf(i));
//        startActivity(new Intent(getContext(),Description.class).putExtra("description",temp));
//
//    }

    public class DownloadMovieTask extends AsyncTask<Void,Void,ArrayList<Movie>> {

        ArrayList<Movie> movieAsync=new ArrayList<>();

        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            try {
                url=new URL("https://api.themoviedb.org/3/movie/popular?api_key=3b4c65c3780fc1ef44ec5500b186d833&language=en-US&page=1");
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String buf=br.readLine();
                while (buf!=null){
                    sb.append(buf);
                    buf=br.readLine();
                }
                data=sb.toString();
                JSONObject jsonResponse=new JSONObject(data);
                JSONArray movieJsonArray=jsonResponse.optJSONArray("results");
                for (int i = 0; i < movieJsonArray.length(); i++) {
                    JSONObject movieJsonObject = movieJsonArray.getJSONObject(i);
                    Movie movies = new Movie();
                    movies.setBackdrop_path(movieJsonObject.getString(BACKDROP_PATH));
                    movies.setId(movieJsonObject.getString(ID));
                    movies.setOverview(movieJsonObject.getString(OVERVIEW));
                    movies.setPoster_path(movieJsonObject.getString(POSTER_PATH));
                    movies.setTitle(movieJsonObject.getString(TITLE));
                    movies.setRelease_date(movieJsonObject.getString(RELEASE_DATE));
                    movies.setVote_average(movieJsonObject.getString(VOTE_AVERAGE));
                    movieAsync.add(movies);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return movieAsync;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            movie.clear();
            movie.addAll(movies);
            adapter.notifyDataSetChanged();
        }

    }
    private class MovieAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return movie.size();
        }

        @Override
        public Movie getItem(int i) {
            return movie.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView==null) {
                convertView=getLayoutInflater().inflate(R.layout.movie_row,viewGroup,false);
            }
            final Movie temp_movie = getItem(i);
            ((TextView) convertView.findViewById(R.id.tvTitle)).setText(temp_movie.getTitle());
            ((TextView) convertView.findViewById(R.id.tvRating)).setText(String.format("Rating: %s", temp_movie.getVote_average()));
            ((TextView) convertView.findViewById(R.id.tvReleased)).setText(String.format("Released: %s", temp_movie.getRelease_date()));

            if(temp_movie.getPoster_path().length() < 5)
            {
                (convertView.findViewById(R.id.ivImage)).setVisibility(View.GONE);
            }else{
                Picasso.with(getContext())
                        .load("http://image.tmdb.org/t/p/w400/"+temp_movie.getPoster_path())
                        .resize(400, 400)
                        .into((ImageView) convertView.findViewById(R.id.ivImage));
            }

            convertView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getContext(),Description.class).putExtra("description",temp_movie));
                        }
                    }
            );
            return convertView;
        }
    }

}