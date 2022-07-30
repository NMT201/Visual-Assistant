package com.example.myapplication.model;

public class DescribePost {
    private String data;
    private String ques;

    public DescribePost(String data, String ques) {
        this.data = data;
        this.ques = ques;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }
}
