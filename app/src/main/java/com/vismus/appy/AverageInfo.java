package com.vismus.appy;

public class AverageInfo {

    double _average;
    double _samplingFraction;

    public AverageInfo(double average, double samplingFraction){
        _average = average;
        _samplingFraction = samplingFraction;
    }

    public double getAverage(){
        return _average;
    }


    public double getSamplingFraction(){
        return _samplingFraction;
    }

}
