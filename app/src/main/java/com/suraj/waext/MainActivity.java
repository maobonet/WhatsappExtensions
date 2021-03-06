package com.suraj.waext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String PACKAGE_NAME = "com.suraj.waext";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("myprefs", 1);
        editor = sharedPreferences.edit();

        setupLockUI();
        setupReminderUI();
        setupHighlightUI();
        setupPrivacyUI();
        setUpLayoutUI();


    }

    private void setUpLayoutUI() {
        CheckBox checkBoxHideCamera = (CheckBox) findViewById(R.id.chkboxhidecamera);
        CheckBox checkBoxHideTabs = (CheckBox) findViewById(R.id.chkboxhidetabs);
        CheckBox checkBoxReplaceCallButton = (CheckBox) findViewById(R.id.chkboxreplacecallbtn);
        CheckBox checkBoxBlackTicks = (CheckBox) findViewById(R.id.chkboxblackticks);

        setUpCheckBox(checkBoxHideCamera, "hideCamera", false, "", false, "");
        setUpCheckBox(checkBoxHideTabs, "hideTabs", true, getApplicationContext().getString(R.string.req_restart), true, getApplicationContext().getString(R.string.req_restart));
        setUpCheckBox(checkBoxReplaceCallButton, "replaceCallButton", false, "", false, "");
        setUpCheckBox(checkBoxBlackTicks, "showBlackTicks", true, getApplicationContext().getString(R.string.req_restart), true, getApplicationContext().getString(R.string.req_restart));

        Spinner spinSingleClickActions = (Spinner) findViewById(R.id.spinsingleclickactions);
        spinSingleClickActions.setSelection(sharedPreferences.getInt("oneClickAction", 3));

        spinSingleClickActions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("oneClickAction", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    private void setupPrivacyUI() {
        final CheckBox checkBoxSeen = (CheckBox) findViewById(R.id.chkboxseen);
        final CheckBox checkBoxReadReports = (CheckBox) findViewById(R.id.chkboxreadreceipts);
        final CheckBox checkBoxDeliveryReports = (CheckBox) findViewById(R.id.chkboxdeliveryreports);

        setUpCheckBox(checkBoxSeen, "hideSeen", false, "", true, getApplicationContext().getString(R.string.restore_prefs));
        setUpCheckBox(checkBoxReadReports, "hideReadReceipts", false, "", false, "");
        setUpCheckBox(checkBoxDeliveryReports, "hideDeliveryReports", false, "", false, "");
        setUpCheckBox((CheckBox)findViewById(R.id.chkboxalwaysonline),"alwaysOnline",true,getApplicationContext().getString(R.string.last_seen_hidden),false,"");


        findViewById(R.id.imgbtnreceiptsetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WhiteListActivity.class));
            }
        });

    }

    private void setupLockUI() {
        Spinner spinner = (Spinner) (findViewById(R.id.spinminutes));

        spinner.setSelection(sharedPreferences.getInt("lockAfter", 2));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("lockAfter", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        (findViewById(R.id.btnchangepass)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                    }
                }

        );

        setUpCheckBox((CheckBox)findViewById(R.id.chkboxHideNotifs),"hideNotifs",false,"",false,"");
        setUpCheckBox((CheckBox)findViewById(R.id.chkboxLockWAWeb),"lockWAWeb",false,"",false,"");

    }


    private void setupReminderUI() {
        final CheckBox checkBox = (CheckBox) (findViewById(R.id.chkboxservicestate));

        if (ReminderService.isRunning) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    startService(new Intent(MainActivity.this, ReminderService.class));
                } else {
                    Intent stopIntent = new Intent(MainActivity.this, ReminderService.class);
                    stopIntent.putExtra("stopService", true);
                    startService(stopIntent);
                }
            }
        });
    }


    private void setupHighlightUI() {
        final Button btnchooser = (Button) findViewById(R.id.btncolorchooser);

        btnchooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ColorChooserActivity.class));

            }
        });

        btnchooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ColorChooserActivity.class);
                intent.putExtra("groupOrIndividual", "highlightColor");
                startActivity(intent);

            }
        });


        findViewById(R.id.btnindividualcolorchooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ColorChooserActivity.class);
                intent.putExtra("groupOrIndividual", "individualHighlightColor");
                startActivity(intent);
            }
        });

        final CheckBox checkBox = (CheckBox) (findViewById(R.id.chkboxhighlight));

        setUpCheckBox(checkBox, "enableHighlight", false, "", false, "");

    }

    private void setUpCheckBox(final CheckBox checkBox, final String prefname, final boolean onToast, final String onMessage, final boolean offToast, final String offMessage) {


        if (sharedPreferences.getBoolean(prefname, false))
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long t = SystemClock.elapsedRealtime();

                /*if (t < 2 * 60 * 1000) {
                    checkBox.setChecked(!checkBox.isChecked());
                    Toast.makeText(getApplicationContext(), R.string.wait_message, Toast.LENGTH_SHORT).show();
                    return;
                }*/

                if (checkBox.isChecked()) {
                    editor.putBoolean(prefname, true);

                    if (onToast) {
                        Toast.makeText(MainActivity.this, onMessage, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    editor.putBoolean(prefname, false);

                    if (offToast) {
                        Toast.makeText(MainActivity.this, offMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                editor.apply();

            }
        });
    }

}
