package com.example.convertmebackend.entity;

public class AudioFileInfo {
    private String nameAudio;
    private double sizeAudio;
    private int bitRateAudio;
    private int samplingRateAudio;

    public AudioFileInfo(String nameAudio, double sizeVideo, int bitRateAudio, int samplingRateAudio) {
        this.nameAudio = nameAudio;
        this.sizeAudio = sizeVideo;
        this.bitRateAudio = bitRateAudio;
        this.samplingRateAudio = samplingRateAudio;
    }

    public String getNameAudio() {
        return nameAudio;
    }

    public void setNameAudio(String nameAudio) {
        this.nameAudio = nameAudio;
    }

    public double getSizeAudio() {
        return sizeAudio;
    }

    public void setSizeAudio(double sizeAudio) {
        this.sizeAudio = sizeAudio;
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
        return "AudioFileInfo{" +
                "nameAudio='" + nameAudio + '\'' +
                ", sizeVideo=" + sizeAudio +
                ", bitRateAudio=" + bitRateAudio +
                ", samplingRateAudio=" + samplingRateAudio +
                '}';
    }
}
