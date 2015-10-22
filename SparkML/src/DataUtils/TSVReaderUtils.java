package DataUtils;

import javafx.scene.control.Labeled;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.regression.LabeledPoint;
import scala.Tuple3;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by zrm22 on 10/21/15.
 */
public class TSVReaderUtils {

    public static JavaRDD<LabeledPoint> createRDD(JavaSparkContext sc, File inputFile, int labelIndex) {

       /*
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(inputFile));
            String currentLine = fileReader.readLine();
            String[] headers = currentLine.split("\t");
            ArrayList<LabeledPoint> points =
            while((currentLine = fileReader.readLine())!=null) {

            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
*/


        JavaRDD<String> data = sc.textFile(inputFile.getAbsolutePath());
        String header = data.first();
        data = data.filter(new Function<String, Boolean>() {
            @Override
            public Boolean call(String s) throws Exception {
                if(!s.equals(header)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        final int labelIndexFinal = labelIndex;
        JavaRDD<LabeledPoint> parsedData = data.map(
            new Function<String, LabeledPoint>() {
                public LabeledPoint call(String line) {
                    String[] rowSplit = line.split("\t");
                    ArrayList<Double> rowDouble = Arrays.stream(rowSplit).map((val) -> {
                        return Double.parseDouble(val);
                    }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                    double labelVal = rowDouble.get(labelIndexFinal);
                    rowDouble.remove(labelIndexFinal);

                    double[] array = rowDouble.parallelStream().mapToDouble(Double::doubleValue).toArray();
                    DenseVector features = new DenseVector(array);
                    return new LabeledPoint(labelVal,features);
                }
            }
        );

       return parsedData;
    }

    //ZRM Oct22
    public static HashMap<String,Integer> getHeadersFromFile(File inputFile) {
        HashMap<String,Integer> headerMap = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String headerLine = reader.readLine();
            String[] headerLineSplit = headerLine.split("\t");
            for(int i = 0; i<headerLineSplit.length; i++) {
                headerMap.put(headerLineSplit[i],i);
            }

            reader.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
        return headerMap;
    }

}
