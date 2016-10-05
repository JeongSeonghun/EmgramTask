package wgl.example.com.googlemappath1;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogSave {
    long now;
    Date date;
    SimpleDateFormat sim= new SimpleDateFormat("MM/dd HH:mm:ss.SSS");
    File txtFile;

    public LogSave(){}

    public void save(String content){
        now= System.currentTimeMillis();
        date= new Date(now);
        String timeLog=sim.format(date);
        String log="";
        log+=timeLog+":"+content;

        try {
            FileWriter write = new FileWriter(txtFile, true);
            write.append(log+"\n");
            write.close();
            //System.out.println("저장 완료.");
            //Toast.makeText(context,"저장 완료.", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFile(Context context, String folderName, String txtName){

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/"+folderName;
        File file = new File(path);
        try {
            //폴더 생성
            if(!file.exists()){
                file.mkdirs();
                //Toast.makeText(context,"폴더 생성", Toast.LENGTH_SHORT);
                //System.out.println("폴더 생성");
            }

            String txtPath=path+"/"+txtName+".txt";
            txtFile = new File(txtPath);
            //파일 생성
            if(!txtFile.exists()){
                txtFile.createNewFile();
                Toast.makeText(context, "디렉토리 및 파일생성 성공", Toast.LENGTH_SHORT).show();
            }
        } catch(IOException ie){
            Toast.makeText(context, "디렉토리 및 파일생성 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public String getAbsoutePath(){
        return txtFile.getAbsolutePath().toString();
    }

}
