package wgl.example.com.googlemappath1;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;
//polyline에 사용할 노드들 좌표를 얻기위해 참고
//http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/

public class DirectionsJSONParser {

    Vector<Vector<Vector<LatLng>>> search=new Vector();

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public Vector<Vector<Vector<LatLng>>> parse(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            //google map wep service참고 ->경로:routes, 구간:legs, 스탭:step,가장 작은 단위
            //rout배열 얻음 ---배열geocoded_waypoints, 요소"status" 제외
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes : 단일 결과*/
            for(int i=0;i<jRoutes.length();i++){    //rout배열 객체 전체 확인
                //legs배열 얻음 --- 객체bounds, 요소"copyrights", 객체overview_polyline,
                //                 ,요소 summary:, "warnings", 배열waypoint_order 제외
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                Vector<Vector<LatLng>> search_leg= new Vector();

                /** Traversing all legs : 경유지 지정할 경우 legs는 2이상일수도 있음*/
                for(int j=0;j<jLegs.length();j++){  //leg배열 객체 전체 확인
                    //step배열  ---  객체distance, 객체duration, 객체end_location, 객체polyline, 객체start_location
                    //              ,요소"end_address", 요소"html_instructions", 요소"maneuver", 요소"travel_mode"
                    //              ,배열traffic_speed_entry, 배열via_waypoint 제외
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){ //step배열 객체 전체 확인
                        String polyline = "";
                        //( ( step[k]->k객체 )->poluline객체 )->point =>String형으로
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");

                        //decodepoly 제작 메서드 List<LatLng>반환
                        Vector<LatLng> list= decodePoly(polyline);

                        search_leg.add(list);

                    }
                    search.add(search_leg);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return search;
    }
    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    //polyline의 point에 있는 코드에서 LatLng,wgs84코드들을 빼옴
    private Vector<LatLng> decodePoly(String encoded) {
        Vector<LatLng> poly= new Vector<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
