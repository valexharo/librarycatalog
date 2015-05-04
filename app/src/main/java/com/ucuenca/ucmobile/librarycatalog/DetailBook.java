package com.ucuenca.ucmobile.librarycatalog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DetailBook extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_book);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        protected String detailsBook;
        public TextView txtTitle,txtAuthor,txtLugar, txtDate, txtDimension, txtDescription, txtPages;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail_book, container, false);
            detailsBook = new String();
            txtTitle = (TextView) rootView.findViewById(R.id.detail_text_book);
            txtAuthor = (TextView) rootView.findViewById(R.id.item_autor);
            txtLugar = (TextView) rootView.findViewById(R.id.item_lugarp);
            txtDate = (TextView) rootView.findViewById(R.id.item_fechap);
            txtDimension = (TextView) rootView.findViewById(R.id.item_dimensiones);
            txtPages = (TextView) rootView.findViewById(R.id.item_paginas);
            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String idLibro = intent.getStringExtra(Intent.EXTRA_TEXT);
               // ((TextView) rootView.findViewById(R.id.detail_text_book))
               //         .setText(idLibro);

                DetailBookTask booksTask = new DetailBookTask();
                booksTask.execute(idLibro);
               // String data = booksTask.getListDetailJsonStr();
               // String resultadoServidor = booksTask.getDetailsBook();
               /* ((TextView) rootView.findViewById(R.id.detail_text_book))
                        .setText(detailsBook);*/
            }


            return rootView;
        }


        //CLASS TO CONSUME THE WEB SERVICE TO READ DATA
        public class DetailBookTask extends AsyncTask<String, Void, String> {
            private String listDetailJsonStr;
            private Exception exception;

            // private String detailsBook;


            public String getListDetailJsonStr() {
                return listDetailJsonStr;
            }

            public void setListDetailJsonStr(String listDetailJsonStr) {
                this.listDetailJsonStr = listDetailJsonStr;
            }

            protected String doInBackground(String... params) {

                String idLibro = params[0];

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

// Will contain the raw JSON response as a string.
                // String listBookJsonStr = null;
                final String format = "json";
                String query = "select * where {<" + idLibro + "> ?property ?data}";


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
                        listDetailJsonStr = null;
                    }
                    //listDetailJsonStr = new String[10];

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    int i=0;

                    final String RESULT = "results";
                    final String BINDING = "bindings";

                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                        // listDetailJsonStr = listDetailJsonStr + line;
                        i++;
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        listDetailJsonStr = null;
                    }
                    listDetailJsonStr = buffer.toString();
                    detailsBook = listDetailJsonStr;
                    return listDetailJsonStr;
                    //   listDetailJsonStr = buffer.toString();
                    // Log.v("CARGO LOS DATOS : " , listDetailJsonStr );
                } catch (IOException e) {
                    //   Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    Log.e("IOException" , "Error ");
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("Error", "Error closing stream", e);
                        }
                    }
                }
                return null;

            }


            protected void onPostExecute(String result) {
                // TODO: check this.exception
                // TODO: do something with the feed
                //detailsBook
                detailsBook = result;
                String propiedad , dato;
                try {
                    JSONObject bookJson = new JSONObject(result);
                    JSONObject resultArray = bookJson.getJSONObject("results");
                    JSONArray bindingArray = resultArray.getJSONArray("bindings");
                    for (int i = 0; i < bindingArray.length(); i++) {
                        // Get the JSON object representing the day
                        JSONObject bookItem = bindingArray.getJSONObject(i);
                        propiedad = bookItem.getJSONObject("property").getString("value");
                        dato = bookItem.getJSONObject("data").getString("value");

                        if (propiedad.equals("http://iflastandards.info/ns/fr/frbr/frbrer/P3020")) {
                            txtTitle.setText(dato);
                        }
                        if (propiedad.equals("http://iflastandards.info/ns/fr/frbr/frbrer/P3021")) {
                            txtAuthor.setText(dato);
                        }
                        if (propiedad.equals("http://iflastandards.info/ns/isbd/elements/P1016")) {
                            txtLugar.setText(dato);
                        }
                        if (propiedad.equals("http://iflastandards.info/ns/isbd/elements/P1018")) {
                            txtDate.setText(dato);
                        }
                        if (propiedad.equals("http://iflastandards.info/ns/isbd/elements/P1024")) {
                            txtDimension.setText(dato);
                        }
                        if (propiedad.equals("http://iflastandards.info/ns/isbd/elements/P1040")) {
                            txtDescription.setText(dato);
                        }
                        if (propiedad.equals("http://iflastandards.info/ns/isbd/elements/P1054")) {
                            txtPages.setText(dato);
                        }

                        //resultStrs[i] = title + " - " + autor;
                    }



                    //  TextView display = (TextView) findViewById(R.id.textView1);
                    //  display.setText(values[0]);
                }catch (Exception e){

                }
            }

        }


    }



}



