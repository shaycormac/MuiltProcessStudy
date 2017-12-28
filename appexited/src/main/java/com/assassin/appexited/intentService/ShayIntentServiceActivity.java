package com.assassin.appexited.intentService;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.assassin.appexited.R;

public class ShayIntentServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shay_intent_service);
        Button btn_start_service = findViewById(R.id.btn_start_service);
        btn_start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {

                ShayIntentService2.startActionFoo(ShayIntentServiceActivity.this, "ShayPatrickCormac", "Assassin");
                ShayIntentService2.startActionFoo(ShayIntentServiceActivity.this, "ShayPatrickCormac1", "Assassin");
                ShayIntentService2.startActionFoo(ShayIntentServiceActivity.this, "ShayPatrickCormac2", "Assassin");
                ShayIntentService2.startActionFoo(ShayIntentServiceActivity.this, "ShayPatrickCormac3", "Assassin");
                
            }
        });
      
    }
}
