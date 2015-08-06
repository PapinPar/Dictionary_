package com.test.papin.dictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity
{
    final String LOG_TAG = "myLogs";
    final Uri dictionary_CONTENT_URI = Uri.parse("content://papin.test.com.MyCP/dictionary");
    final String d_word = "word";
    final String d_trans = "translate";
    String TRANSLATION,realTranslation,prevWord,forShow,FromList;
    public static final String Endpoint = "http://api.mymemory.translated.net";
    DialogFragment dialog_add;
    TextView forTrnas,s1,s2;
    Button butt;
    Dialog dialog;
    MenuItem miActionProgressItem;
    ArrayList<String> LikeList = new ArrayList<String>();
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forTrnas = (TextView)findViewById(R.id.textForTrans);
        forTrnas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getLikeThis();
            }

            @Override
            public void afterTextChanged(Editable s) {
                miActionProgressItem.setVisible(false);
                butt.setClickable(true);

            }
        });
        s2 = (TextView)findViewById(R.id.inviz2);
        s2.addTextChangedListener(new TextWatcher() {
            @Override  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {       }

            @Override
            public void afterTextChanged(Editable s)
            {

                forTrnas.setText("");
                if (s2.getText().toString().equals("OKEY"))
                {
                    add();
                }
            }
        });

        butt = (Button)findViewById(R.id.button);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog_add = new DIalogAdd();
        start();

    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Приложение Dictionary для перевода с русского на английский и наоборот.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void start() {
        LikeList.clear();
        Cursor cursor = getContentResolver().query(dictionary_CONTENT_URI,null,null,null,null);
        String W;
        if(cursor.moveToFirst())
        {
            int name = cursor.getColumnIndex("word");
            do
            {
                W = cursor.getString(name);
                LikeList.add(W);

            }while (cursor.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, LikeList);
        ListView lvContact = (ListView) findViewById(R.id.lvContact);
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                FromList =LikeList.get(position);
                FromList = returnWandT(FromList);
                showDialog(1);
            }
        });
    }


    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button:
                miActionProgressItem.setVisible(true);
                requestData();
                start();

                break;
        }

    }

    private void getLikeThis()
    {
        LikeList.clear();
        Cursor cursor = getContentResolver().query(dictionary_CONTENT_URI,null,null, null,null);
        String W;
        if(cursor.moveToFirst())
        {
            int name = cursor.getColumnIndex("word");
            do
            {
                W = cursor.getString(name);
                if (W.contains(forTrnas.getText().toString()))
                {
                    W = cursor.getString(name);
                    LikeList.add(W);
                }
            }while (cursor.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, LikeList);
        ListView lvContact = (ListView) findViewById(R.id.lvContact);
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                FromList =LikeList.get(position);
                FromList = returnWandT(FromList);
                showDialog(1);
            }
        });
    }

    private String returnWandT(String fromList)
    {
        String selection = "word = ?";
        String[] selectionArgs = new String[] { fromList };
        Cursor cursor = getContentResolver().query(dictionary_CONTENT_URI,null,selection, selectionArgs,null);
        String T,W;
        if(cursor.moveToFirst())
        {

            int name = cursor.getColumnIndex("word");
            int trans = cursor.getColumnIndex("translate");
            do
            {
                T = cursor.getString(trans);
                W = cursor.getString(name);
                fromList = W+" - "+T;
            }while (cursor.moveToNext());
        }

        return fromList;
    }

    public void add()
    {
        String[] s = s1.getText().toString().split("-");
        int serch = SerchLike(s[0]);
        if(serch==0)
        {
            ContentValues cv = new ContentValues();
            cv.clear();
            cv.put(d_word, s[0]);
            cv.put(d_trans, s[1]);
            Uri newUri = getContentResolver().insert(dictionary_CONTENT_URI, cv);
            Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
            cv.clear();
            start();
        }
        else
            Toast.makeText(getBaseContext(), "Такое Слово Уже Переведено", Toast.LENGTH_SHORT).show();
    }

    private int SerchLike(String s)
    {
        int tmp=0;
        String selection = "word = ?";
        String[] selectionArgs = new String[] { s };
        Cursor cursor = getContentResolver().query(dictionary_CONTENT_URI,null,selection, selectionArgs,null);
        String W;
        if(cursor.moveToFirst())
        {

            int name = cursor.getColumnIndex("word");
            int trans = cursor.getColumnIndex("translate");
            do
            {
                W = cursor.getString(name);
                if(W.equals(s))
                    tmp++;
            }while (cursor.moveToNext());
        }

        return tmp;
    }

    private void getTranslation(String translation)
    {
        String[] tmp = translation.split("\"");
        Log.d("Success", tmp[5]);
        realTranslation = tmp[5];
    }

    private void requestData()
    {
        butt.setClickable(false);
        forTrnas = (TextView)findViewById(R.id.textForTrans);
        s1 = (TextView)findViewById(R.id.inviz1);
        int  proverka=0;
        final String lang;
        final String[] d = new String[100];
        RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Endpoint)
                .build();
        MyApi api = adapter.create(MyApi.class);

        proverka= getLang(forTrnas.getText().toString());
        if(proverka>0)
            lang = "en|ru";
        else
            lang = "ru|en";
        api.getWord(forTrnas.getText().toString(), lang, new Callback<NewsArchive>() {
            public void success(NewsArchive newsArchive, Response response) {
                prevWord = forTrnas.getText().toString();
                //Log.d("Success", "Translation :" + newsArchive.getResponseData());
                TRANSLATION = stringFromResponse(response);
                getTranslation(TRANSLATION);
                if (lang.equals("en|ru"))
                    realTranslation = decode(realTranslation);
                forTrnas.setText(realTranslation);

                forShow = prevWord + " - " + forTrnas.getText().toString();
                s1.setText(forShow);
                dialog_add.show(getSupportFragmentManager(), "Help");


            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getBaseContext(), "Проверьте пожалуйста подключение к интернету", Toast.LENGTH_SHORT).show();
            }
        });




    }

    protected Dialog onCreateDialog(int id)
    {
        if(id==1)
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Перевод Слова");
            adb.setMessage(FromList);
            adb.setPositiveButton("Ok",null);
            dialog = adb.show();
            return dialog;
        }

        return super.onCreateDialog(id);
    }

    public void onPrepareDialog(int id, Dialog dialog)
    {
        switch(id) {
            case (1) :
                AlertDialog timeDialog = (AlertDialog)dialog;
                timeDialog.setMessage(FromList);
                break;
        }
    }

    private String decode(String realTranslation)
    {
        String test = realTranslation;
        test = test.replace(" ","\\u0020");
       // test = test.replace("\\W","qqqq");
        test = test.replace(",","u002C");
        test = test.replace(".","u002E");
        test = test.replace(":","u003A");
        test = test.replace("?","u003F");
        test = test.replace("!","u0021");

        String str =test.split(" ")[0];
        str = str.replace("\\", "");
        String[] arr = str.split("u");
        String text = "";

        for(int i = 1; i < arr.length; i++)
        {
            int hexVal = Integer.parseInt(arr[i], 16);
            text += (char)hexVal;
        }
        return text;
    }

    private int getLang(String text)
    {
        text = text.toLowerCase();
        int z=0;
        for(char i = 'a';i<='z';i++)
        {
            if(text.contains(String.valueOf(i)))
            {
                z++;
            }
        }
        return z;
    }

    String stringFromResponse (Response response)
    {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(
                    response.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = sb.toString();
        return result;
    }


}