package com.example.whatsappchatanalyser;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView  totalMessage,totalWords,totalMediaShared;
    String filename;
    String filePath;
    Path path;
    String theFile;
    PyObject pythObj;
    PyObject pyObjectGraph;
    ArrayList<String> listN = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialButton button = (MaterialButton) findViewById(R.id.button);


        BarChart barView = findViewById(R.id.bar_graph_view);


        totalMessage = (TextView) findViewById(R.id.total_message_tv);
        totalWords = (TextView) findViewById(R.id.total_words_tv);
        totalMediaShared = (TextView) findViewById(R.id.total_media_shared_tv);
        Spinner userListSpinner = (Spinner) findViewById(R.id.spinner);

        userListSpinner.setOnItemSelectedListener(this);

        {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();


        pythObj = py.getModule("app");


        InputStream inputStream = getResources().openRawResource(R.raw.he);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            // fileReader = new FileReader(myFile);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            theFile = stringBuilder.toString();
            pythObj.callAttr("upload_file", theFile);

            // unqiue user to list
            PyObject pyObj = pythObj.callAttr("user_list");
            List<PyObject> pyListObj = pyObj.asList();

            for (int i = 0; i < pyListObj.toArray().length; i++) {
                listN.add(i, pyListObj.get(i).toString());
            }

            //  PyObject sda = pythObj.callAttr("sum",3,4);

        }
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listN);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userListSpinner.setAdapter(aa);

    }


        {
            ArrayList<BarEntry> topBusyUsers = new ArrayList<>();
            pyObjectGraph = pythObj.callAttr("graph_points");
            List<PyObject> graphPointsList = pyObjectGraph.asList();
            int i=0;
            for (PyObject py :
                    graphPointsList) {
                topBusyUsers.add(new BarEntry(i++,py.toInt()));
            }
            BarDataSet barDataSet = new BarDataSet(topBusyUsers,"Most Busy User");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(16);

            BarData barData = new BarData(barDataSet);
            barView.setFitBars(true);
            barView.setData(barData);
            barView.getDescription().setText("");
            barView.animateY(1000);

        }
















    button.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick (View view) {
            Log.d("TAGGGG","");
         //   String nameFile = "/storage/self/primary/Download/WhatsApp Chat with TE-B 21-22 EVEN.txt ";
//            File myFile = new File(nameFile);

//



        }
    });

    }


    @Override
    public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) {
        PyObject showStats = pythObj.callAttr("selected_user", listN.get(i));



        Toast.makeText(getApplicationContext(), listN.get(i).toString(), Toast.LENGTH_SHORT).show();
        List<PyObject> a = showStats.asList();
        totalMessage.setText(a.get(0).toString());
        totalWords.setText(a.get(1).toString());
        totalMediaShared.setText(a.get(2).toString());

    }

    @Override
    public void onNothingSelected (AdapterView<?> adapterView) {

    }




    public void chooseTheFile(){
        Intent fileManager= new Intent(Intent.ACTION_GET_CONTENT);
        fileManager.setType("text/plain");
        someActivityResultLauncher.launch(fileManager);
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        filePath = data.getData().getPath();
                        // path = Paths.get(data.getData().getPath());
                        filename = filePath.substring(filePath.lastIndexOf("/")+1);

                        //  filePath =filePath.substring(filePath.lastIndexOf(":")+1);

//                        textView.setText(filename + " \n"+ filePath1 + "\n"+ path);
                    }
                }
            });
}