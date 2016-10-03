package wgl.example.com.emtask4;

/**
 * Created by EMGRAM on 2016-09-09.
 */
public class TourSt {
    String alias, wgs_x, wgs_y;
    public TourSt(String alias, String wgs_x, String wgs_y){
        this.alias= alias;
        this.wgs_x= wgs_x;
        this.wgs_y= wgs_y;
    }

    public String getAlias(){
        return alias;
    }
    public double getWgsX(){
        return Double.valueOf(wgs_x);
    }
    public double getWgsY(){
        return Double.valueOf(wgs_y);
    }
}
