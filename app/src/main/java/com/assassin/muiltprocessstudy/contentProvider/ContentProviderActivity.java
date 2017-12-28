package com.assassin.muiltprocessstudy.contentProvider;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.assassin.muiltprocessstudy.R;
/**
 *contentProvider的原理探析
 */
public class ContentProviderActivity extends AppCompatActivity 
{

    private ContentResolver contentResolver;
    

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);

        contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.assassin.shay.provider");
        contentResolver.query(uri, null, null, null, null);
        contentResolver.query(uri, null, null, null, null);
        contentResolver.query(uri, null, null, null, null);
        
    }
}
