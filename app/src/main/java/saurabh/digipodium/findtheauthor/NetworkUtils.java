package saurabh.digipodium.findtheauthor;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class NetworkUtils {
    public static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BOOK_API_BASE = "https://www.googleapis.com/books/v1/volumes?";
    private static final String MAX_RESULTS = "maxResults";
    private static final String QUERY_PARAM = "q";
    private static final String PRINT_TYPE = "printType";
    private static final String YOUR_API_KEY = "";

    static String getBookInfo(String query){




        //to collect data from net or connect to server
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONData = null;
        try{
            Uri buildUri = Uri.parse(BOOK_API_BASE).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,query)
                    .appendQueryParameter(MAX_RESULTS,"10")
                    .appendQueryParameter(PRINT_TYPE,"books")
                    .build();

            URL requestUrl = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //to get data now from server as byte stream
            InputStream inp = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inp));
            StringBuilder builder = new StringBuilder(); // because the data we get from above is in bytes


            //convert data into readable format
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if(builder.length()==0){
                return null;
            }

            bookJSONData = builder.toString();

        }catch (IOException e){

            e.printStackTrace();

        }finally {
            //connection closing
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(reader!=null){
                try {
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

        }

        return bookJSONData;
    }

}
