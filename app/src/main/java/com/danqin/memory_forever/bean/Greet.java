package com.danqin.memory_forever.bean;

import java.util.ArrayList;
import java.util.List;

public class Greet {

    private String classmateName;
    private String time;

    private List<Record> records = new ArrayList<>();

    private List<String> videoPaths = new ArrayList<>();

    public String getClassmateName() {
        return classmateName;
    }

    public void setClassmateName(String classmateName) {
        this.classmateName = classmateName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public List<String> getVideoPaths() {
        return videoPaths;
    }

    public void setVideoPaths(List<String> videoPaths) {
        this.videoPaths = videoPaths;
    }

    public static class Record{
        String recordPath;
        int duration;

        public String getRecordPath() {
            return recordPath;
        }

        public void setRecordPath(String recordPath) {
            this.recordPath = recordPath;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

}
