package wgl.example.com.googlemappath1;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by EMGRAM on 2016-10-04.
 */
public class TestNode {
    int rout, setp, leg;
    LatLng point;
    public TestNode(int rout, int step, int leg, LatLng point){
        this.rout=rout;
        this.setp=step;
        this.leg=leg;
        this.point=point;
    }

    public int getRout(){
        return rout;
    }
    public int getLeg(){
        return leg;
    }
    public int getSetp(){
        return setp;
    }
    public LatLng getPoint(){
        return point;
    }
}
