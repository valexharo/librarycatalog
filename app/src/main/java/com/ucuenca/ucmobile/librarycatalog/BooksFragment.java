package com.ucuenca.ucmobile.librarycatalog;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Usuario on 27/04/15.
 */
public class BooksFragment extends Fragment {

    private ItemBookAdapter booksAdapter;

    public BooksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        booksAdapter =
                new ItemBookAdapter(
                        getActivity(), // The current context (this activity)
                        new ArrayList<ItemBook>());


        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.lista_libros);
        listView.setAdapter(booksAdapter);

        // control click on item in ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ItemBook item = (ItemBook)booksAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailBook.class)
                        .putExtra(Intent.EXTRA_TEXT, item.getUri());
                startActivity(intent);
            }
        });



        //Click on Search
        Button mButton = (Button) rootView.findViewById(R.id.search_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText text = (EditText)rootView.findViewById(R.id.editText);
                String dataSearch = text.getText().toString();
                FetchBooksTask booksTask = new FetchBooksTask();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                boolean repo_ucuenca = prefs.getBoolean(getString(R.string.repo_ucuenca_key), true);
                String num_results = prefs.getString(getString(R.string.num_results_key),"20");
               // String text_search = prefs.getString(getString(R.string.search_default_key),getString(R.string.pref_search_default));

                String[] passing = new String[6];
                passing[0] = repo_ucuenca+"";
                passing[1] = "false"; // check universidad particular de loja
                passing[2] = "false"; // check escuela politectica nacional
                passing[3] = "false" ; // check cedia
                passing[4] = num_results ;
                passing[5] = dataSearch;

               // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
             /*   SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.pref_search_default), dataSearch);
                editor.commit();*/

                booksTask.execute(passing);

               // prefs.getStringSet("search_default_key", dataSearch);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.pref_search_default), dataSearch);
                editor.commit();
            }
        });


        return rootView;

    }



    @Override
    public void onStart(){
        super.onStart();
        updateListBook();
    }

    private void updateListBook() {
        FetchBooksTask booksTask = new FetchBooksTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean repo_ucuenca = prefs.getBoolean(getString(R.string.repo_ucuenca_key), true);
        String num_results = prefs.getString(getString(R.string.num_results_key),"20");
        String text_search = prefs.getString(getString(R.string.search_default_key),getString(R.string.pref_search_default));

        String[] passing = new String[6];
        passing[0] = repo_ucuenca+"";
        passing[1] = "false"; // check universidad particular de loja
        passing[2] = "false"; // check escuela politectica nacional
        passing[3] = "false" ; // check cedia
        passing[4] = num_results ;
        passing[5] = text_search;

        booksTask.execute(passing);
    }

    public class FetchBooksTask extends AsyncTask<String, Void, ArrayList<ItemBook>> {

        private final String LOG_TAG = FetchBooksTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<ItemBook> getBookDataFromJson(String booksJsonStr, int numRows)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULT = "results";
            final String BINDING = "bindings";

            JSONObject bookJson = new JSONObject(booksJsonStr);
            JSONObject resultArray = bookJson.getJSONObject(RESULT);
            JSONArray bindingArray = resultArray.getJSONArray(BINDING);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.
            ArrayList<ItemBook> resultStrs = new ArrayList<ItemBook>();


           // String[] resultStrs;

            //Si no ha habido resultados en la busqueda
            if (bindingArray.length() == 0) {
                // Toast.makeText(getActivity(),"NO SE HA ENCONTRADO RESULTADOS  " ,Toast.LENGTH_SHORT).show();
               // resultStrs = new String[1];
               // resultStrs[0] = "NO HAY RESULTADOS ";
                resultStrs.add(new ItemBook(1, "", "NO HAY RESULTADOS", "" ));
            }
            else{

           // resultStrs = new String[numRows];
            for (int i = 0; i < bindingArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String title;
                String uri;
                String autor;
                //String highAndLow;

                // Get the JSON object representing the day
                JSONObject bookItem = bindingArray.getJSONObject(i);
                uri = bookItem.getJSONObject("item").getString("value");
                title = bookItem.getJSONObject("title").getString("value");
                autor = bookItem.getJSONObject("autor").getString("value");

                resultStrs.add(new ItemBook(i, uri, title, autor ));
                //resultStrs[i] = title + " - " + autor;
            }
        }
            return resultStrs;

        }
        @Override
        protected ArrayList<ItemBook> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            String text_search = params[5];
            String num_limit = params[4];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String listBookJsonStr = null;
            final String format = "json";
            String query = "select *" +
                    "where {?item <http://iflastandards.info/ns/fr/frbr/frbrer/P3020> ?title" +
                    ". FILTER regex(?title, \"" + text_search + "\")" +
                    ". ?item <http://iflastandards.info/ns/fr/frbr/frbrer/P3021> ?autor}" +
                    "limit " + num_limit;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String baseURL="http://190.15.141.102:8890/sparql";
            final String query_param="query";
            final String format_param="format";

            Uri buildUrl = Uri.parse(baseURL).buildUpon()
                    //      .appendQueryParameter(query_param, params[0])
                    .appendQueryParameter(format_param,format)
                    .appendQueryParameter(query_param,query)
                    .appendQueryParameter("timeout",Integer.toString(0))
                            //    .appendQueryParameter(days_param, Integer.toString(numDays))
                    .build();

            Log.v("builder URI: ", buildUrl.toString());

            URL uri = new URL(buildUrl.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                listBookJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                listBookJsonStr = null;
            }
            listBookJsonStr = buffer.toString();
            Log.v("CARGO LOS DATOS : " , listBookJsonStr );
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                int limit = Integer.parseInt(num_limit);
                return getBookDataFromJson(listBookJsonStr, limit);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemBook> result) {
            if (result != null) {
                booksAdapter.clear();
                for(ItemBook itemBookStr : result) {
                    booksAdapter.add(itemBookStr);
                }
                // New data is back from the server.  Hooray!
               // listView.setAdapter(booksAdapter);
               // booksAdapter = new ItemBookAdapter(getActivity(), result);
                //booksAdapter = adapter;//.set.setAdapter(adapter);
            }
        }
    }

}