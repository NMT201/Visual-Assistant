package com.example.myapplication.model;

import java.util.List;

public class StreamingResponse {

    private List<Boolean> obstacle;
    private Boolean on_road;
    private Float time_process;
    private Float time_segment;
    private Float time_depthmap;

    public Float getTime_segment() {
        return time_segment;
    }

    public void setTime_segment(Float time_segment) {
        this.time_segment = time_segment;
    }

    public Float getTime_depthmap() {
        return time_depthmap;
    }

    public void setTime_depthmap(Float time_depthmap) {
        this.time_depthmap = time_depthmap;
    }

    public StreamingResponse(Float time_segment, Float time_depthmap) {
        this.time_segment = time_segment;
        this.time_depthmap = time_depthmap;
    }

    public StreamingResponse(List<Boolean> obstacle, Boolean on_road, Float time_process) {
        this.obstacle = obstacle;
        this.on_road = on_road;
        this.time_process = time_process;
    }

    public List<Boolean> getObstacle() {
        return obstacle;
    }

    public void setObstacle(List<Boolean> obstacle) {
        this.obstacle = obstacle;
    }

    public Boolean getOn_road() {
        return on_road;
    }

    public void setOn_road(Boolean on_road) {
        this.on_road = on_road;
    }

    public Float getTime_process() {
        return time_process;
    }

    public void setTime_process(Float time_process) {
        this.time_process = time_process;
    }

    public String warnObstacle() {
        String message = "";

        /*[x, x, x,
           x, x, x
           x, x, x]*/

        /*[x, x,      x,
           x, true,   x
           x, x,      x     ]*/
        if (obstacle.get(4)) {
            message += "có vật cản phía trước,";

            if (!obstacle.get(5) || !obstacle.get(3)) {

                /*[x , x,    x,
                   x , true, false
                   x , x,    x      ]*/
                if (!obstacle.get(5)) {

                    /*[x , x,    false,
                       x , true, false
                       x , x,    false]*/
                    if (!obstacle.get(2) && !obstacle.get(8)) {
                        message += " hãy đi về bên phải.";
                        return message;
                        }
                    /*[false , x,    x,
                       false , true, x
                       false , x,    x  ]*/

                    if (!obstacle.get(0) && !obstacle.get(6)) {
                        message += " hãy đi về bên trái.";
                        return message;

                        }
                    }
                }

            return message;
            }
        if (obstacle.get(4)) {
            message += "có vật cản phía dưới,";

            if (!obstacle.get(5) || !obstacle.get(3)) {

                /*[x , x,    x,
                   x , true, false
                   x , x,    x      ]*/
                if (!obstacle.get(5)) {

                    /*[x , x,    false,
                       x , true, false
                       x , x,    false]*/
                    if (!obstacle.get(2) && !obstacle.get(8)) {
                        message += " hãy đi về bên phải.";
                        return message;
                    }
                    /*[false , x,    x,
                       false , true, x
                       false , x,    x  ]*/

                    if (!obstacle.get(0) && !obstacle.get(6)) {
                        message += " hãy đi về bên trái.";
                        return message;

                    }
                }
            }

            return message;
        }

        if (obstacle.get(4)) {
            message += "có vật cản phía trên,";

            if (!obstacle.get(5) || !obstacle.get(3)) {

                /*[x , x,    x,
                   x , true, false
                   x , x,    x      ]*/
                if (!obstacle.get(5)) {

                    /*[x , x,    false,
                       x , true, false
                       x , x,    false]*/
                    if (!obstacle.get(2) && !obstacle.get(8)) {
                        message += " hãy đi về bên phải.";
                        return message;
                    }
                    /*[false , x,    x,
                       false , true, x
                       false , x,    x  ]*/

                    if (!obstacle.get(0) && !obstacle.get(6)) {
                        message += " hãy đi về bên trái.";
                        return message;

                    }
                }
            }

            return message;
        }



        return message;
    }

    public String warnOnRoad() {
        if (on_road) {
            return "Bạn đang hướng ra đường";
        }
        else {
            return "";
        }


    }

    @Override
    public String toString() {
        return "StreamingResponse{" +
                "obstacle=" + obstacle +
                ", on_road=" + on_road +
                ", time_process=" + time_process +
                ", time_segment=" + time_segment +
                ", time_depthmap=" + time_depthmap +
                '}';
    }
}
