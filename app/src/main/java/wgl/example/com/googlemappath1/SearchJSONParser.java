package wgl.example.com.googlemappath1;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;
//polyline에 사용할 노드들 좌표를 얻기위해 참고
//http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/

public class SearchJSONParser {

    Vector<Vector<Vector<LatLng>>> search=new Vector();

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public Vector<Vector<Vector<LatLng>>> parse(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            //routes는 출발지 목적지 단일 결과
            for(int i=0;i<jRoutes.length();i++){    //rout배열 객체 전체 확인

                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                Vector<Vector<LatLng>> search_leg= new Vector();

                /** Traversing all legs */
                //경유지 지정할 경우 legs는 2이상일수도 있음
                for(int j=0;j<jLegs.length();j++){  //leg배열 객체 전체 확인

                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    Vector<LatLng> list=new Vector();

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){ //step배열 객체 전체 확인
                        list.add(new LatLng(Double.valueOf(jSteps.getJSONObject(k).getString("lat")),
                                Double.valueOf(jSteps.getJSONObject(k).getString("lng"))));

                    }
                    search_leg.add(list);
                }
                search.add(search_leg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return search;

    }

}
