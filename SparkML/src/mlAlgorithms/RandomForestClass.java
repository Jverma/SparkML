package mlAlgorithms;

import DataUtils.TSVReaderUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.rdd.RDD;
import scala.Tuple2;

import java.io.File;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

/**
 * Created by zrm22 on 10/21/15.
 */
public class RandomForestClass implements java.io.Serializable{
    File trainFile, testFile;
    JavaRDD<LabeledPoint> trainData, testData;
    HashMap<String,String> params;
   // RandomForestModel model;
     JavaSparkContext sc;

    public RandomForestClass() {
        SparkConf sparkConf = new SparkConf().setAppName("JavaRandomForestHackTest").setMaster("local[*]");
        final JavaSparkContext sc2 = new JavaSparkContext(sparkConf);
        //sc = sc2;
        //sc2.close();
    }

    public RandomForestClass(int labelIndex, File trainingFile) {
        this.trainFile = trainingFile;
        this.testFile = trainingFile;
        SparkConf sparkConf = new SparkConf().setAppName("JavaRandomForestHackTest").setMaster("local[*]");
        final JavaSparkContext sc2 = new JavaSparkContext(sparkConf);
        //sc = sc2;
        trainData = TSVReaderUtils.createRDD(sc2, trainFile,labelIndex);
        JavaRDD<LabeledPoint>[] splits = trainData.randomSplit(new double[]{0.70, 0.30});
        trainData = splits[0];
        testData = splits[1];

        params = new HashMap<>();
        params.put("numClasses","2");
        params.put("numTrees","10"); // Use more in practice.
        params.put("featureSubsetStrategy" , "auto"); // Let the algorithm choose.
        params.put("impurity", "gini");
        params.put("maxDepth", "5");
        params.put("maxBins" , "32");
        params.put("seed" , "12345");
        //sc2.close();
    }

    public RandomForestClass(int labelIndex, File trainingFile, File testingFile) {
        this.trainFile = trainingFile;
        this.testFile = testingFile;
        SparkConf sparkConf = new SparkConf().setAppName("JavaRandomForestHackTest").setMaster("local[*]");
        final JavaSparkContext sc2 = new JavaSparkContext(sparkConf);
        //sc = sc2;
        trainData = TSVReaderUtils.createRDD(sc2, trainFile,labelIndex);
        testData = TSVReaderUtils.createRDD(sc2, testFile,labelIndex);


        params = new HashMap<>();
        params.put("numClasses","2");
        params.put("numTrees","10"); // Use more in practice.
        params.put("featureSubsetStrategy" , "auto"); // Let the algorithm choose.
        params.put("impurity", "gini");
        params.put("maxDepth", "5");
        params.put("maxBins" , "32");
        params.put("seed" , "12345");

        //sc2.close();
    }
    public RandomForestClass(int labelIndex, File trainingFile, File testingFile, HashMap<String,String> params) {
        this.trainFile = trainingFile;
        this.testFile = testingFile;
        SparkConf sparkConf = new SparkConf().setAppName("JavaRandomForestHackTest").setMaster("local[*]");
        final JavaSparkContext sc2 = new JavaSparkContext(sparkConf);
        sc = sc2;
        trainData = TSVReaderUtils.createRDD(sc2, trainFile,labelIndex);
        testData = TSVReaderUtils.createRDD(sc2, testFile,labelIndex);

        this.params = params;
    }


    public RandomForestModel trainModel() {
        HashMap<Integer, Integer> categoricalFeaturesInfo = new HashMap<Integer, Integer>();
        final RandomForestModel model = RandomForest.trainClassifier(trainData, Integer.parseInt(params.get("numClasses")),
                categoricalFeaturesInfo,Integer.parseInt(params.get("numTrees")), params.get("featureSubsetStrategy"), params.get("impurity"),
                Integer.parseInt(params.get("maxDepth")), Integer.parseInt(params.get("maxBins")),Integer.parseInt(params.get("seed")));
        //System.out.println(model.toDebugString());
        return model;
    }

    public JavaRDD<Tuple2<Object,Object>> computePredictionPairs(RandomForestModel model) {
        JavaRDD<Tuple2<Object, Object>> scoreAndLabels = testData.map(
                new Function<LabeledPoint, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(LabeledPoint p) {
                        Double score = model.predict(p.features());
                        return new Tuple2<Object, Object>(score, p.label());
                    }
                }
        );
        return scoreAndLabels;
    }

    public void getConfusionMatrix(JavaRDD<Tuple2<Object,Object>> predictionPairs) {
        MulticlassMetrics metrics = new MulticlassMetrics(JavaRDD.toRDD(predictionPairs));
        System.out.println(metrics.confusionMatrix().toString());
    }
    public Matrix getConfusionMatrix(RandomForestModel model) {
        JavaRDD<Tuple2<Object, Object>> scoreAndLabels = testData.map(
                new Function<LabeledPoint, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(LabeledPoint p) {
                        Double score = model.predict(p.features());
                        return new Tuple2<Object, Object>(score, p.label());
                    }
                }
        );
        MulticlassMetrics metrics = new MulticlassMetrics(JavaRDD.toRDD(scoreAndLabels));
        System.out.println(metrics.confusionMatrix().toString());
        return metrics.confusionMatrix();
    }

    public double getClassificationError(RandomForestModel model) {
        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
                    //@Override
                    public Tuple2<Double, Double> call(LabeledPoint p) {
                        return new Tuple2<Double, Double>(model.predict(p.features()), p.label());
                    }
                });


        Double testErr =
                1.0 * predictionAndLabel.filter(new Function<Tuple2<Double, Double>, Boolean>() {
                    //@Override
                    public Boolean call(Tuple2<Double, Double> pl) {
                        return !pl._1().equals(pl._2());
                    }
                }).count() / testData.count();
        System.out.println("Test Error: " + testErr);
        getConfusionMatrix(computePredictionPairs(model));
        return testErr;
    }

    public void closeContext() {
        testData.context().stop();
        //sc.close();
    }

}
