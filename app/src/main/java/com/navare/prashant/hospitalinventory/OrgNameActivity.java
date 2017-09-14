package com.navare.prashant.hospitalinventory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

public class OrgNameActivity extends AppCompatActivity {

    private EditText    mOrgNameET;
    private Button      mNextButton;
    private Context     mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_name);

        mContext = this;

        mOrgNameET = (EditText) findViewById(R.id.orgNameET);
        mNextButton = (Button) findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orgName = mOrgNameET.getText().toString();
                if (orgName.isEmpty()) {
                    Toast toast = Toast.makeText(mContext, "Please enter the organization name.", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                HospitalInventoryApp.setOrgName(orgName);
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        });

    }
}
