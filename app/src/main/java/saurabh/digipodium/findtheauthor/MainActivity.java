package saurabh.digipodium.findtheauthor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch;
    private TextView textBookName;
    private TextView textBookName1;
    private TextView textAuthor;
    private EditText editBookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnSearch);
        textBookName = findViewById(R.id.textBookName);
        textAuthor = findViewById(R.id.textAuthorName);
        editBookName = findViewById(R.id.editBookName);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to hide keyboard
                InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(methodManager != null){
                    methodManager.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                // till above; the code for hiding

                //to check for internet connection establishment
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if(connMgr!=null){
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if(networkInfo!=null && networkInfo.isConnected()){
                        searchBooks();
                        textBookName.setText("");
                        textAuthor.setText("Loading..");
                    } else{
                        textBookName.setText("No network");
                        textAuthor.setText("");
                    }
                }

            }
        });
    }

    public void searchBooks(){

            String queryString = editBookName.getText().toString();
//            Context context;

            if(queryString.length() > 2){
                new FetchBook(textAuthor,textBookName).execute(queryString);
            } else{
                editBookName.setError("enter a book name");
                editBookName.requestFocus();
            }

    }


    //create a Asynctask class

    public class FetchBook extends AsyncTask<String, Void, String> {


        private WeakReference<TextView>  textTitle;   //referring to an object which can be destroyed
        private WeakReference<TextView>  textAuthor;   //referring to an object which can be destroyed


        //constructor

        public FetchBook(TextView textTitle, TextView textAuthor){
            this.textTitle = new WeakReference<>(textTitle);
            this.textAuthor= new WeakReference<>(textAuthor);
        }

        @Override
        protected String doInBackground(String... query) {
            //make a request to API server
            return NetworkUtils.getBookInfo(query[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            //parse the JSON data
            super.onPostExecute(result);
            try {
                //1. change raw string to json object
                JSONObject jsonObject = new JSONObject(result);

                //2. get the book data array
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                //3. loop variables
                int i=0;
                String title = null;
                String authors = null;

                //4. run loop
                while (i<itemsArray.length() && (authors==null && title == null)){

                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volume = book.getJSONObject("volumeInfo");
                    try {
                        title = volume.getString("title");
                        authors = volume.getString("authors");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    i++;
                }

                if(title!=null && authors !=null){
                    textTitle.get().setText(title);
                    textAuthor.get().setText(authors);
                }else{
                    textAuthor.get().setText("no result");
                    textTitle.get().setText("unknown");
                }

            }catch (Exception e){
                e.printStackTrace();
                textAuthor.get().setText("API error");
                textTitle.get().setText("Please check logcat");
            }
        }
    }



    //create NetworkUtills logs and create an URI. The networkutils file

    //intent permission setup: android manifest file

}