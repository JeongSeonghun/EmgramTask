package wgl.example.com.googlemappath1;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Vector;

//https://developer.android.com/reference/android/provider/CalendarContract.CalendarColumns.html#CALENDAR_COLOR


public class MainActivity extends AppCompatActivity {

    boolean operate;

    Spinner spinner;

    Vector calendarName=new Vector();
    TextView txtview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtview=(TextView)findViewById(R.id.calTxt);

        spinner=(Spinner)findViewById(R.id.spinner);
        checkDangerousPermissions();

        //calendas테이블 uri
        Uri uri= CalendarContract.Calendars.CONTENT_URI;
        //collum name
        String[] calenProj= new String[]{
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars._SYNC_ID,    //동기화용
                CalendarContract.Calendars.VISIBLE, //evnent 표시 여부
                CalendarContract.Calendars.NAME,    //calendar명
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   //표시될 calendar명
                CalendarContract.Calendars.CALENDAR_COLOR
        };
        Cursor c=null;
        try{
            if(operate)
                c=getContentResolver().query(uri,calenProj,null,null,null);

            if(c!=null){
                if(c.moveToFirst()){
                    calendarName.add("total");
                    do{
                        if(!calendarName.contains(c.getString(c.getColumnIndex(calenProj[0]))))
                            calendarName.add(c.getString(c.getColumnIndex(calenProj[0])));
                        /*
                        for(int i=0; i<calenProj.length; i++){
                            Log.i(calenProj[i],c.getString(c.getColumnIndex(calenProj[i])));
                        }
                        */
                        //column에 따른 값 표시
                        System.out.println(calenProj[0]+":"+c.getString(c.getColumnIndex(calenProj[0])));
                        System.out.println(calenProj[1]+":"+c.getString(c.getColumnIndex(calenProj[1])));
                        System.out.println(calenProj[2]+":"+c.getString(c.getColumnIndex(calenProj[2])));
                        System.out.println(calenProj[3]+":"+c.getString(c.getColumnIndex(calenProj[3])));
                        System.out.println(calenProj[4]+":"+c.getString(c.getColumnIndex(calenProj[4])));
                        System.out.println(calenProj[5]+":"+c.getString(c.getColumnIndex(calenProj[5])));
                        System.out.println(calenProj[6]+":"+c.getString(c.getColumnIndex(calenProj[6])));
                        System.out.println(calenProj[7]+":"+c.getString(c.getColumnIndex(calenProj[7])));


                    }while(c.moveToNext());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        /*
        for(int i=0; i<al.size();i++)
            System.out.println("test001:"+i+":"+(String)al.get(i));
            */
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,
                calendarName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CalenderInfo calenderInfo=new CalenderInfo();

                if(i==0){
                    calenderInfo.execute("total");
                }else{
                    calenderInfo.execute((String)calendarName.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    //캘린더 데이터 확인
    private class CalenderInfo extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            //calendar envents 테이블 uri
            Uri uri = CalendarContract.Events.CONTENT_URI;
            //where 조건문
            String selection = "(" + CalendarContract.Events.ORGANIZER + " = ?)";
            //where 조건문 ?에 해당하는 value
            String[] selectionArgs = new String[1];
            //찾아볼 column
            String[] eventProjection= new String[] {
                    CalendarContract.Events._ID,
                    CalendarContract.Events.TITLE,  //calendar event명(일정 제목)
                    CalendarContract.Events.DESCRIPTION,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.EVENT_TIMEZONE,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.EVENT_END_TIMEZONE,
                    CalendarContract.Events.ALL_DAY,    //하루종일인지 표시
                    CalendarContract.Events.ORGANIZER,
                    CalendarContract.Events.ACCOUNT_NAME
            };
            Cursor c=null;
            selectionArgs[0]=strings[0];
            String txtV="";
            for(int i=0;i<calendarName.size();i++){
                txtV+=(String)calendarName.get(i)+"\n";
            }

            try{

                //전체 표시용
                if(selectionArgs[0].equals("total")){
                    c = getContentResolver().query(
                            uri,
                            eventProjection,
                            null, null, null);
                }else{  //조건 account에 따른 calendar events
                    c = getContentResolver().query(
                            uri,
                            eventProjection,
                            selection,selectionArgs,
                            CalendarContract.Events.DTSTART + " DESC");
                }

                if (c != null) {
                    try {
                        if (c.moveToFirst()) {
                            for(int i=0;i<eventProjection.length;i++){
                                Log.i("title",eventProjection[i]);
                            }

                            do {

                                Log.v(eventProjection[0],String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events._ID))));
                                Log.v(eventProjection[1],c.getString(c.getColumnIndex(CalendarContract.Events.TITLE)));
                                Log.v(eventProjection[2],c.getString(c.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                                Log.v(eventProjection[3],String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events.DTSTART))));
                                Log.v(eventProjection[4],c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
                                Log.v(eventProjection[5],String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events.DTEND))));
                                Log.v(eventProjection[6],String.valueOf(c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE))));
                                Log.v(eventProjection[7],c.getString(c.getColumnIndex(CalendarContract.Events.ALL_DAY)));
                                Log.v(eventProjection[8],c.getString(c.getColumnIndex(CalendarContract.Events.ORGANIZER)));
                                Log.v(eventProjection[9],c.getString(c.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME)));


                                txtV+=String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events._ID)))+" / ";
                                txtV+=c.getString(c.getColumnIndex(CalendarContract.Events.TITLE))+" / ";
                                txtV+=String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events.DTSTART)))+" / ";
                                txtV+=c.getString(c.getColumnIndex(CalendarContract.Events.ORGANIZER))+"\n";


                            } while (c.moveToNext());
                        }
                    } finally {
                        c.close();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return txtV;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            txtview.setText(s);
        }
    }

    //권한 확인
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR
        };

        //권한 확인
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            operate=true;

            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            operate=false;
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            //한번이라도 거부한 경우 상세히 설명을 위하여, 최초 false, 이전에 거부시 true
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "캘린더 접근.", Toast.LENGTH_LONG).show();
            } else {    //퍼미션 요청 대화창 표시
                ActivityCompat.requestPermissions(this, permissions, 1);//activity, permissions[], requestCode
            }
        }


    }
    //퍼미셔 요청에 따른 결과 값 call back, 사용자가 새 process 팝업에서 권한 수락시
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
