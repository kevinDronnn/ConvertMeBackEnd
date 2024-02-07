package com.example.convertmebackend.entity;

public class VideoFileInfo {
    private String nameVideo;
    private double sizeVideo;
    private int bitRateVideo;
    private int frameRateVideo;
    private int bitRateAudio;
    private int samplingRateAudio;

    public VideoFileInfo(String nameVideo, double sizeVideo, int bitRateVideo, int frameRateVideo, int bitRateAudio, int samplingRateAudio) {
        this.nameVideo = nameVideo;
        this.sizeVideo = sizeVideo;
        this.bitRateVideo = bitRateVideo;
        this.frameRateVideo = frameRateVideo;
        this.bitRateAudio = bitRateAudio;
        this.samplingRateAudio = samplingRateAudio;
    }

    public String getNameVideo() {
        return nameVideo;
    }

    public void setNameVideo(String nameVideo) {
        this.nameVideo = nameVideo;
    }

    public double getSizeVideo() {
        return sizeVideo;
    }

    public void setSizeVideo(double sizeVideo) {
        this.sizeVideo = sizeVideo;
    }

    public int getBitRateVideo() {
        return bitRateVideo;
    }

    public void setBitRateVideo(int bitRateVideo) {
        this.bitRateVideo = bitRateVideo;
    }

    public int getFrameRateVideo() {
        return frameRateVideo;
    }

    public void setFrameRateVideo(int frameRateVideo) {
        this.frameRateVideo = frameRateVideo;
    }

    public int getBitRateAudio() {
        return bitRateAudio;
    }

    public void setBitRateAudio(int bitRateAudio) {
        this.bitRateAudio = bitRateAudio;
    }

    public int getSamplingRateAudio() {
        return samplingRateAudio;
    }

    public void setSamplingRateAudio(int samplingRateAudio) {
        this.samplingRateAudio = samplingRateAudio;
    }

    @Override
    public String toString() {
        return "VideoFileInfo{" +
                "nameVideo='" + nameVideo + '\'' +
                ", sizeVideo=" + sizeVideo +
                ", bitRateVideo=" + bitRateVideo +
                ", frameRateVideo=" + frameRateVideo +
                ", bitRateAudio=" + bitRateAudio +
                ", samplingRateAudio=" + samplingRateAudio +
                '}';
    }
}
