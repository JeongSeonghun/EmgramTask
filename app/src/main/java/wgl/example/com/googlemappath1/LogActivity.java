package wgl.example.com.googlemappath1;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class LogActivity extends AppCompatActivity {
    TextView logContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        logContent = (TextView) findViewById(R.id.log_content);


        roadLog();
    }

    //log 읽어오기용
    public void roadLog() {

        try {
            StringBuffer data = new StringBuffer();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File f = new File(path + "/MyDir", "log_hashmap.txt");
            BufferedReader buffer = new BufferedReader
                    (new FileReader(f));
            String str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();


            }
            logContent.setText(data);
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}