package wgl.example.com.emtask4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AddrActivity extends AppCompatActivity {

    TextView name_t, addr_t;
    String name_s, addr_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addr);

        name_t=(TextView)findViewById(R.id.add_name);
        addr_t=(TextView)findViewById(R.id.add_addr);

        Intent intent=getIntent();
        name_s = intent.getExtras().getString("name");
        addr_s = intent.getExtras().getString("addr");

        name_t.setText(name_s);
        addr_t.setText(addr_s);
    }
}
