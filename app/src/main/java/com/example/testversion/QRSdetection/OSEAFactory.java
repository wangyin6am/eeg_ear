
package com.example.testversion.QRSdetection;

import com.example.testversion.QRSdetection.detection.QRSDetector;
import com.example.testversion.QRSdetection.detection.QRSDetector2;
import com.example.testversion.QRSdetection.detection.QRSDetectorParameters;
import com.example.testversion.QRSdetection.detection.QRSFilterer;


public class OSEAFactory 
	{
	
	
/*	public static BeatDetectionAndClassification createBDAC(int sampleRate, int beatSampleRate)
		{
		BDACParameters bdacParameters = new BDACParameters(beatSampleRate) ;
		QRSDetectorParameters qrsDetectorParameters = new QRSDetectorParameters(sampleRate) ;
		
		BeatAnalyzer beatAnalyzer = new BeatAnalyzer(bdacParameters) ;
		Classifier classifier = new Classifier(bdacParameters, qrsDetectorParameters) ;
		Matcher matcher = new Matcher(bdacParameters, qrsDetectorParameters) ;
		NoiseChecker noiseChecker = new NoiseChecker(qrsDetectorParameters) ;
		PostClassifier postClassifier = new PostClassifier(bdacParameters) ;
		QRSDetector2 qrsDetector = createQRSDetector2(sampleRate) ;
		RythmChecker rythmChecker = new RythmChecker(qrsDetectorParameters) ;
		BeatDetectionAndClassification bdac 
			= new BeatDetectionAndClassification(bdacParameters, qrsDetectorParameters) ;

		classifier.setObjects(matcher, rythmChecker, postClassifier, beatAnalyzer) ;
		matcher.setObjects(postClassifier, beatAnalyzer, classifier) ;
		postClassifier.setObjects(matcher) ;
		bdac.setObjects(qrsDetector, noiseChecker, matcher, classifier) ;
		
		return bdac;
		}*/

	public static QRSDetector createQRSDetector(int sampleRate)
		{
		QRSDetectorParameters qrsDetectorParameters = new QRSDetectorParameters(sampleRate) ;
		
		QRSDetector qrsDetector = new QRSDetector(qrsDetectorParameters) ;
		QRSFilterer qrsFilterer = new QRSFilterer(qrsDetectorParameters) ;
		
		qrsDetector.setObjects(qrsFilterer) ;
		return qrsDetector ;
		}
	
	public static QRSDetector2 createQRSDetector2(int sampleRate) 
		{
		QRSDetectorParameters qrsDetectorParameters = new QRSDetectorParameters(sampleRate) ;
		
		QRSDetector2 qrsDetector2 = new QRSDetector2(qrsDetectorParameters) ;
		QRSFilterer qrsFilterer = new QRSFilterer(qrsDetectorParameters) ;
		
		qrsDetector2.setObjects(qrsFilterer) ;
		return qrsDetector2 ;
		}
	}
