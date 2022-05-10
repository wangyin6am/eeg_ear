package com.example.testversion.QRSdetection;


import com.example.testversion.QRSdetection.detection.QRSDetector2;

import java.util.ArrayList;

public class QRSDetection {

    public static double[] QRSdetection(int[] ecgSamples, int sampleRate) {
//        QRSDetector2 qrsDetector = OSEAFactory.createQRSDetector2(sampleRate);
        QRSDetector2 qrsDetector = OSEAFactory.createQRSDetector2(sampleRate);
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < ecgSamples.length; i++) {
            int result = qrsDetector.QRSDet(ecgSamples[i]);
            if (result != 0) {
                list.add(i-result);
//                System.out.println("A QRS-Complex was detected at sample: " + (i-result));
            }
        }

        double[] RpeakArray = new double[list.size()-1];
        for (int i = 0; i < list.size()-1; i++) {
            double RR = list.get(i+1) - list.get(i);
            RpeakArray[i] = RR / sampleRate;
//            System.out.println("RR Interval: "+ RR);
        }

        return RpeakArray;

    }
}
