package com.apsu.joshua.victoryroyalestatsandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Receive username/platform info from mainActivity
        final String user = getIntent().getStringExtra("USERNAME_KEY");
        String plat = getIntent().getStringExtra("PLATFORM_KEY");
        //change string from full platform name to abbreviation
        if (plat.equals("Playstation 4")) {
            plat = "psn";
        } else if (plat.equals("Xbox")) {
            plat = "xbl";
        } else {
            plat = "pc";
        }
        final String platform = plat;
        //Run the query to load page
        new QueryStatsTask(user, platform).execute();
    }

    class QueryStatsTask extends AsyncTask<Void, Void, String> {
        // Set up all the initial variables and Views.
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView2);
        ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar2);
        TextView usernameText = (TextView) findViewById(R.id.userText);
        TextView platformView = (TextView) findViewById(R.id.platformText);
        TextView overallKillsView = (TextView) findViewById(R.id.overallKillsText);
        TextView overallWinsView = (TextView) findViewById(R.id.overallWinsText);
        TextView overallKDView = (TextView) findViewById(R.id.overallKDText);
        TextView overallRatioView = (TextView) findViewById(R.id.overallPercentText);
        TextView overallMatchesView = (TextView) findViewById(R.id.overallMatchesText);
        TextView overallScoreView = (TextView) findViewById(R.id.overallScoreText);
        TextView soloKillsView = (TextView) findViewById(R.id.soloKillsText);
        TextView soloWinsView = (TextView) findViewById(R.id.soloWinsText);
        TextView soloKDView = (TextView) findViewById(R.id.soloKDText);
        TextView soloRatioView = (TextView) findViewById(R.id.soloPercentText);
        TextView soloMatchesView = (TextView) findViewById(R.id.soloMatchesText);
        TextView soloScoreView = (TextView) findViewById(R.id.soloScoreText);
        TextView duoKillsView = (TextView) findViewById(R.id.duoKillsText);
        TextView duoWinsView = (TextView) findViewById(R.id.duoWinsText);
        TextView duoKDView = (TextView) findViewById(R.id.duoKDText);
        TextView duoRatioView = (TextView) findViewById(R.id.duoPercentText);
        TextView duoMatchesView = (TextView) findViewById(R.id.duoMatchesText);
        TextView duoScoreView = (TextView) findViewById(R.id.duoScoreText);
        TextView squadKillsView = (TextView) findViewById(R.id.squadKillsText);
        TextView squadWinsView = (TextView) findViewById(R.id.squadWinsText);
        TextView squadKDView = (TextView) findViewById(R.id.squadKDText);
        TextView squadRatioView = (TextView) findViewById(R.id.squadPercentText);
        TextView squadMatchesView = (TextView) findViewById(R.id.squadMatchesText);
        TextView squadScoreView = (TextView) findViewById(R.id.squadScoreText);
        TextView responseView = (TextView) findViewById(R.id.responseView);
        Boolean playerFound = true;
        String API_URL = "https://api.fortnitetracker.com/v1/profile/";
        String API_KEY = "insert key here";
        String usernameInput, systemInput, username, system, overallScore = "0", overallWins = "0",
                overallKills  = "0", overallKD = "0", overallRatio = "0", overallMatches = "0",
                soloScore = "0", soloWins = "0", soloKills = "0", soloKD = "0",
                soloRatio = "0", soloMatches = "0", duoScore = "0", duoWins = "0",
                duoKills = "0", duoKD = "0", duoRatio = "0", duoMatches = "0",
                squadScore = "0", squadWins = "0", squadKills = "0", squadKD = "0",
                squadRatio = "0", squadMatches = "0";

        //Constructor to receive inputted username and platform
        QueryStatsTask(String username, String platform) {
            this.usernameInput = username;
            this.systemInput = platform;
        }

        //Show progress bar while process is loading
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
        }

        //Retrieve information from server in the background thread
        protected String doInBackground(Void... urls) {

            try {
                //Set up url to include the typed in username and platform
                URL fortniteAPI = new URL(API_URL + systemInput + "/" + usernameInput);
                //Open a connection to the server
                HttpsURLConnection urlConnection = (HttpsURLConnection) fortniteAPI.openConnection();
                //Send headers with the request. One to identifiy our app and another to provide our
                // unique API-KEY
                urlConnection.setRequestProperty("User-Agent", "tracker-companion-app");
                urlConnection.setRequestProperty("TRN-Api-Key", API_KEY);
                try {
                    //Read the information into a string
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            JSONObject jsonDataStart = null;
            try {
                //Put string into JSON objects and strings
                jsonDataStart = new JSONObject(response);
                system = jsonDataStart.getString("platformNameLong");
                platformView.setText("Platform:     " + system);
                username = jsonDataStart.getString("epicUserHandle");
                usernameText.setText("Username:     " + username);

                JSONObject jsonData = jsonDataStart.getJSONObject("stats");

                JSONArray totalStats = jsonDataStart.getJSONArray("lifeTimeStats");
                JSONObject currentObj = null;
                for (int i = 0; i < totalStats.length(); i++){
                    currentObj = totalStats.getJSONObject(i);
                    if (currentObj.getString("key").equals("Score")) {
                        overallScore = currentObj.getString("value");
                    } else if (currentObj.getString("key").equals("Matches Played")) {
                        overallMatches = currentObj.getString("value");
                    } else if (currentObj.getString("key").equals("Wins")) {
                        overallWins = currentObj.getString("value");
                    } else if (currentObj.getString("key").equals("Win%")) {
                        overallRatio = currentObj.getString("value");
                    } else if (currentObj.getString("key").equals("Kills")) {
                        overallKills = currentObj.getString("value");
                    } else if (currentObj.getString("key").equals("K/d")) {
                        overallKD = currentObj.getString("value");
                    }
                }

                // Put all the Solo stats into strings.
                JSONObject soloJson = jsonData.getJSONObject("p2");
                JSONObject soloScoreJson = soloJson.getJSONObject("score");
                soloScore = soloScoreJson.getString("displayValue");
                JSONObject soloWinsJson = soloJson.getJSONObject("top1");
                soloWins = soloWinsJson.getString("displayValue");
                JSONObject soloKDJson = soloJson.getJSONObject("kd");
                soloKD = soloKDJson.getString("displayValue");
                JSONObject soloRatioJson = soloJson.getJSONObject("winRatio");
                soloRatio = soloRatioJson.getString("displayValue");
                JSONObject soloMatchesJson = soloJson.getJSONObject("matches");
                soloMatches = soloMatchesJson.getString("displayValue");
                JSONObject soloKillsJson = soloJson.getJSONObject("kills");
                soloKills = soloKillsJson.getString("displayValue");

                // Put all the Duo stats into strings.
                JSONObject duoJson = jsonData.getJSONObject("p10");
                JSONObject duoScoreJson = duoJson.getJSONObject("score");
                duoScore = duoScoreJson.getString("displayValue");
                JSONObject duoWinsJson = duoJson.getJSONObject("top1");
                duoWins = duoWinsJson.getString("displayValue");
                JSONObject duoKDJson = duoJson.getJSONObject("kd");
                duoKD = duoKDJson.getString("displayValue");
                JSONObject duoRatioJson = duoJson.getJSONObject("winRatio");
                duoRatio = duoRatioJson.getString("displayValue");
                JSONObject duoMatchesJson = duoJson.getJSONObject("matches");
                duoMatches = duoMatchesJson.getString("displayValue");
                JSONObject duoKillsJson = duoJson.getJSONObject("kills");
                duoKills = duoKillsJson.getString("displayValue");

                // Put all the Squad stats into strings.
                JSONObject squadJson = jsonData.getJSONObject("p9");
                JSONObject squadScoreJson = squadJson.getJSONObject("score");
                squadScore = squadScoreJson.getString("displayValue");
                JSONObject squadWinsJson = squadJson.getJSONObject("top1");
                squadWins = squadWinsJson.getString("displayValue");
                JSONObject squadKDJson = squadJson.getJSONObject("kd");
                squadKD = squadKDJson.getString("displayValue");
                JSONObject squadRatioJson = squadJson.getJSONObject("winRatio");
                squadRatio = squadRatioJson.getString("displayValue");
                JSONObject squadMatchesJson = squadJson.getJSONObject("matches");
                squadMatches = squadMatchesJson.getString("displayValue");
                JSONObject squadKillsJson = squadJson.getJSONObject("kills");
                squadKills = squadKillsJson.getString("displayValue");
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    //If no player is found, show error message
                    jsonDataStart = new JSONObject(response);
                    loading.setVisibility(View.GONE);
                    responseView.setText(jsonDataStart.getString("error"));
                    playerFound = false;
                    responseView.setVisibility(View.VISIBLE);
                } catch (JSONException e1) {
                    //If no JSON object at all is found (network error) then display network error
                    e1.printStackTrace();
                    loading.setVisibility(View.GONE);
                    if (response.equals("THERE WAS AN ERROR")) {
                        responseView.setText("Network error!\nPlease check your connection and try again.");
                        responseView.setVisibility(View.VISIBLE);
                        playerFound = false;
                    }
                }
            }
            // If player handle is found, make stats visible.
            if (playerFound) {
                // Set all the TextView values and make it visible:
                loading.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                overallKillsView.setText("Kills:   " + overallKills);
                overallKDView.setText("K/d:   " + overallKD);
                overallMatchesView.setText("Matches:   " + overallMatches);
                overallRatioView.setText("Win%:   " + overallRatio);
                overallWinsView.setText("Wins:   " + overallWins);
                overallScoreView.setText("Score:   " + overallScore);
                soloKillsView.setText("Kills:   " + soloKills);
                soloKDView.setText("K/d:   " + soloKD);
                soloMatchesView.setText("Matches:   " + soloMatches);
                soloRatioView.setText("Win%:   " + soloRatio + "%");
                soloWinsView.setText("Wins:   " + soloWins);
                soloScoreView.setText("Score:   " + soloScore);
                duoKillsView.setText("Kills:   " + duoKills);
                duoKDView.setText("K/d:   " + duoKD);
                duoMatchesView.setText("Matches:   " + duoMatches);
                duoRatioView.setText("Win%:   " + duoRatio + "%");
                duoWinsView.setText("Wins:   " + duoWins);
                duoScoreView.setText("Score:   " + duoScore);
                squadKillsView.setText("Kills:   " + squadKills);
                squadKDView.setText("K/d:   " + squadKD);
                squadMatchesView.setText("Matches:   " + squadMatches);
                squadRatioView.setText("Win%:   " + squadRatio + "%");
                squadWinsView.setText("Wins:   " + squadWins);
                squadScoreView.setText("Score:   " + squadScore);
            }
        }
    }
}
