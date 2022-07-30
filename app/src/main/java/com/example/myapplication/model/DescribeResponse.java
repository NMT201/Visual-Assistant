package com.example.myapplication.model;

import java.util.HashMap;
import java.util.List;

public class DescribeResponse {

    private ResultDescribe result;
    private String focus_region;
    private Float time_process;
    private Float time_detect;
    private Float time_segment;
    private Float time_depthmap;

    public DescribeResponse(ResultDescribe result, String focus_region, Float time_process, Float time_detect, Float time_segment, Float time_depthmap) {
        this.result = result;
        this.focus_region = focus_region;
        this.time_process = time_process;
        this.time_detect = time_detect;
        this.time_segment = time_segment;
        this.time_depthmap = time_depthmap;
    }

    public ResultDescribe getResult() {
        return result;
    }

    public void setResult(ResultDescribe result) {
        this.result = result;
    }

    public String getFocus_region() {
        return focus_region;
    }

    public void setFocus_region(String focus_region) {
        this.focus_region = focus_region;
    }

    public Float getTime_process() {
        return time_process;
    }

    public void setTime_process(Float time_process) {
        this.time_process = time_process;
    }

    @Override
    public String toString() {

        //Tên -> hướng -> khoảng cách
        //['bike', 'car', 'truck', 'bus', 'crosswalk_sign', 'crosswalk_marking', 'bicycle', 'person', 'traffic_light']
        HashMap<String, String> objectNameMap = new HashMap<>();
        objectNameMap.put("bike", " xe máy ");
        objectNameMap.put("car", " ô tô ");
        objectNameMap.put("truck", " xe tải ");
        objectNameMap.put("bus", " xe buýt ");
        objectNameMap.put("crosswalk_sign", " biển báo qua đường ");
        objectNameMap.put("crosswalk_marking", " vạch qua đường ");
        objectNameMap.put("bicycle", " xe đạp ");
        objectNameMap.put("person", " người ");
        objectNameMap.put("traffic_light", " đèn giao thông ");

        //["Road", "Sidewalk", "Left", "Right", "Front", "All", "Near", "Far"]
        HashMap<String, String> focusRegion = new HashMap<>();
        focusRegion.put("Road", " dưới đường ");
        focusRegion.put("Sidewalk", " trên lề ");
        focusRegion.put("Left", " bên trái ");
        focusRegion.put("Right", " bên phải ");
        focusRegion.put("Front", " phía trước ");
        focusRegion.put("All", " Có ");
        focusRegion.put("Near", " Ở gần ");
        focusRegion.put("Far", " phía xa ");


        String message = focusRegion.get(focus_region) + ", ";

        HashMap<String, String> messageMap = new HashMap<>();

        for (int i = 0 ; i < result.getOrientation().size() ; i++){
            message += objectNameMap.get(result.getObject_name().get(i)) + ", " +
                    Integer.toString(result.getOrientation().get(i)) + " giờ, " +
                    Integer.toString(Math.round(result.getDistance().get(i))) + " mét .";
        }

        return message;
    }


}
