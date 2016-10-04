package wgl.example.com.googlemappath1;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class LogActivity extends AppCompatActivity {
    TextView logContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        logContent= (TextView)findViewById(R.id.log_content);

        roadLog();
    }

    public void roadLog(){
        if (!checkExternalStorage()) return;
        // 외부메모리를 사용하지 못하면 끝냄

        try {
            StringBuffer data = new StringBuffer();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File f = new File(path+"/MyDir", "log_vector.txt");

            BufferedReader buffer = new BufferedReader
                    (new FileReader(f));
            String str = buffer.readLine();
            while (str!=null) {
                data.append(str+"\n");
                str = buffer.readLine();
            }
            logContent.setText(data);
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean checkExternalStorage() {
        String state;
        state = Environment.getExternalStorageState();

        String path= Environment.getExternalStorageDirectory().toString();
        String dirPath = getFilesDir().getAbsolutePath();
        System.out.println("test000_1: "+path);
        System.out.println("test000_1: "+dirPath);


        // 외부메모리 상태
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 읽기 쓰기 모두 가능
            Log.d("test0", "외부메모리 읽기 쓰기 모두 가능");
            System.out.println("test000: 외부메모리 읽기 쓰기 모두 가능");
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            //읽기전용
            Log.d("test0", "외부메모리 읽기만 가능");
            System.out.println("test000: 외부메모리 읽기만 가능");
            return false;
        } else {
            // 읽기쓰기 모두 안됨
            Log.d("test0", "외부메모리 읽기쓰기 모두 안됨 : "+ state);
            System.out.println("test000: 외부메모리 읽기쓰기 모두 안됨: "+state);

            return false;
        }
    }

}
