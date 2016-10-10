package wgl.example.com.googlemappath1;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class Main3Activity extends AppCompatActivity {

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                  // 3
            CalendarContract.Calendars.NAME
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_NAME_INDEX = 4;

    boolean operate;

    Spinner spinner;

    Vector al=new Vector();
    TextView txtview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        System.out.println("test000_0:start");
        System.out.println("test000_0:" + CalendarContract.Calendars.CONTENT_URI);
        txtview=(TextView)findViewById(R.id.calTxt);
        /*
        // Run query
        Cursor cur = null;
        //Cursor c = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"shun6889@gmail.com", "com.google",
                "shun6889@gmail.com"};
// Submit the query and get a Cursor object back.
*/
        /*
        final String CONTENT_URI = "content://com.android.calendar";
        Uri uri = Uri.parse(CONTENT_URI + "/calendars");
        */

        //System.out.println("test000_1:"+ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CALENDAR));
        //System.out.println("test000_1:"+PackageManager.PERMISSION_GRANTED);

        checkDangerousPermissions();

        //System.out.println("test000_0:"+uri);
        //cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        /*
        try{
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            //cur = cr.query(uri, null, null, null, null);
            System.out.println("test000_0:"+cur.toString());


            // Use the cursor to step through the returned records
            while (cur.moveToNext()) {
                long calID = 0;
                String displayName = null;
                String accountName = null;
                String ownerName = null;
                String name= null;

                // Get the field values
                calID = cur.getLong(PROJECTION_ID_INDEX);
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                name= cur.getString(PROJECTION_NAME_INDEX);

                System.out.println("test000:"+calID);
                System.out.println("test000:"+displayName);
                System.out.println("test000:"+accountName);
                System.out.println("test000:"+ownerName);
                System.out.println("test000:"+name);

                // Do something with the values...

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        */

        //selection = "(" + CalendarContract.Events.ORGANIZER + " = ?)";
        //selectionArgs = new String[]{"shun6889@gmail.com"};
        //출력 colum
        /*
        String[] eventStr = new String[]{CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.EVENT_TIMEZONE,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_END_TIMEZONE,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.ORGANIZER
        };

        //조건 where, 조건(colum=?)
        String selection = "(" + CalendarContract.Events.ORGANIZER + " = ?)";
        String[] selectionArgs = new String[]{"shun6889@gmail.com"};
        //캘린더 uri
        Uri uri = CalendarContract.Events.CONTENT_URI;
        Cursor c=null;
        try{
            c = getContentResolver().query(
                    uri,
                    eventStr,
                    selection, selectionArgs,
                    CalendarContract.Events.DTSTART + " DESC");
        }catch (Exception e){
            e.printStackTrace();
        }

        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    do {
                        for(int i=0;i<eventStr.length;i++){
                            Log.i("test001_0:",eventStr[i]);
                        }
                        Log.v("test",String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events._ID))));
                        System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.ORGANIZER)));
                        System.out.println("test001:"+c.getLong(c.getColumnIndex(CalendarContract.Events._ID)));
                        System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.TITLE)));
                        System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                        System.out.println("test001:"+c.getLong(c.getColumnIndex(CalendarContract.Events.DTSTART)));
                        System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
                        System.out.println("test001:"+c.getLong(c.getColumnIndex(CalendarContract.Events.DTEND)));
                        System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
                    } while (c.moveToNext());
                }

            } finally {
                c.close();
            }

        }
        */


        Button bt= (Button)findViewById(R.id.infoCal);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=CalendarContract.Calendars.CONTENT_URI;
                String[] calenProj= new String[]{CalendarContract.Calendars.ACCOUNT_NAME};
                Cursor c=null;
                try{
                    if(operate)
                        c=getContentResolver().query(uri,calenProj,null,null,null);

                    if(c!=null){
                        if(c.moveToFirst()){
                            do{
                                al.add(c.getString(c.getColumnIndex(calenProj[0])));
                            }while(c.moveToNext());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                AlertDialog.Builder alertDialog= new AlertDialog.Builder(getApplicationContext());
                alertDialog.setTitle("Account");
                alertDialog.setSingleChoiceItems(new AccountAdapter(getApplicationContext(), R.layout.layout, al), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        CalenderInfo calenInfo = new CalenderInfo();
                        if(operate)
                            calenInfo.execute((String)al.get(i));

                    }
                });
                /*
                alertDialog.setSingleChoiceItems(c, 0, calenProj[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CalenderInfo calenInfo = new CalenderInfo();
                        if(operate)
                            calenInfo.execute((String)al.get(i));
                    }
                });
                */
                alertDialog.show();
            }
        });


        spinner=(Spinner)findViewById(R.id.spinner);

        Uri uri=CalendarContract.Calendars.CONTENT_URI;
        String[] calenProj= new String[]{CalendarContract.Calendars.ACCOUNT_NAME};
        Cursor c=null;
        try{
            if(operate)
                c=getContentResolver().query(uri,calenProj,null,null,null);

            if(c!=null){
                if(c.moveToFirst()){
                    do{
                        al.add(c.getString(c.getColumnIndex(calenProj[0])));
                    }while(c.moveToNext());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        for(int i=0; i<al.size();i++)
        System.out.println("test001:"+i+":"+(String)al.get(i));
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,
                al);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CalenderInfo calenderInfo=new CalenderInfo();

                calenderInfo.execute((String)al.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private class CalenderInfo extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {

            ContentResolver cr = getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
            String selection = "(" + CalendarContract.Events.ORGANIZER + " = ?)";
            String[] selectionArgs = new String[1];
            String[] eventProjection= new String[] {
                    CalendarContract.Events._ID,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DESCRIPTION,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.EVENT_TIMEZONE,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.EVENT_END_TIMEZONE,
                    CalendarContract.Events.ALL_DAY,
                    CalendarContract.Events.ORGANIZER,
                    CalendarContract.Events.ACCOUNT_NAME
            };
            Cursor c=null;
            selectionArgs[0]=strings[0];
            String txtV="";

            System.out.println("test001:"+CalendarContract.Events.ACCOUNT_NAME);

            try{
                /*
                c = getContentResolver().query(
                        uri,
                        eventProjection,
                        null,null,null);
                c.moveToFirst();
                String[] args2=new String[]{c.getString(c.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME))};
                System.out.println("test001:"+args2[0]);
                */

                if(selectionArgs[0].equals("My calendar")){
                    c = getContentResolver().query(
                            uri,
                            eventProjection,
                            //selection,args2,
                            null, null, null);
                }else{
                    c = getContentResolver().query(
                            uri,
                            eventProjection,
                            //selection,args2,
                            selection,selectionArgs,
                            CalendarContract.Events.DTSTART + " DESC");
                }

                if (c != null) {
                    try {
                        if (c.moveToFirst()) {
                            do {
                                for(int i=0;i<eventProjection.length;i++){
                                    Log.i("title",eventProjection[i]);
                                }

                                Log.v(eventProjection[0],String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events._ID))));
                                Log.v(eventProjection[1],c.getString(c.getColumnIndex(CalendarContract.Events.TITLE)));
                                Log.v(eventProjection[2],c.getString(c.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                                Log.v(eventProjection[3],String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events.DTSTART))));
                                Log.v(eventProjection[4],c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
                                Log.v(eventProjection[5],String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events.DTEND))));
                                Log.v(eventProjection[6],String.valueOf(c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE))));
                                Log.v(eventProjection[7],c.getString(c.getColumnIndex(CalendarContract.Events.ALL_DAY)));
                                Log.v(eventProjection[8],c.getString(c.getColumnIndex(CalendarContract.Events.ORGANIZER)));
                                //Log.v(eventProjection[9],c.getString(c.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME)));

                                txtV+=String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events._ID)))+" / ";
                                txtV+=c.getString(c.getColumnIndex(CalendarContract.Events.TITLE))+" / ";
                                txtV+=String.valueOf(c.getLong(c.getColumnIndex(CalendarContract.Events.DTSTART)))+" / ";
                                txtV+=c.getString(c.getColumnIndex(CalendarContract.Events.ORGANIZER))+"\n";
/*
                                System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.ORGANIZER)));
                                System.out.println("test001:"+c.getLong(c.getColumnIndex(CalendarContract.Events._ID)));
                                System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.TITLE)));
                                System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                                System.out.println("test001:"+c.getLong(c.getColumnIndex(CalendarContract.Events.DTSTART)));
                                System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
                                System.out.println("test001:"+c.getLong(c.getColumnIndex(CalendarContract.Events.DTEND)));
                                System.out.println("test001:"+c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
*/
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


    /*
    public static ArrayList<CalendarBean> getCalendarList(Context context) {

        ArrayList<CalendarBean> list = new ArrayList<>();



        // 캘린더 프로바이더로 접근한다

        Cursor c = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);

        if (c != null) {

            try {

                if (c.moveToFirst()) {

                    do {

                        // 값을 한 개씩 반복해서 구한다

                        CalendarBean b = new CalendarBean();

                        b.setId(c.getLong(c.getColumnIndex(CalendarContract.Calendars._ID));

                        b.setName(c.getString(c.getColumnIndex(CalendarContract.Calendars.NAME));

                        list.add(b);

                    } while (c.moveToNext());

                }

            } finally {

                c.close();

            }

        }

        return list;

    }
    */
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

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "log text 저장.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
