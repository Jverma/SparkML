package DataUtils;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.mllib.regression.LabeledPoint;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zrm22 on 10/23/15.
 */
public class TSVWriterUtils {

    public static void exportRDDtoTSV(JavaRDD<LabeledPoint> dataSet, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            dataSet.foreach(new VoidFunction<LabeledPoint>() {
                @Override
                public void call(LabeledPoint labeledPoint) throws Exception {
                    writer.write(labeledPoint.toString());
                    writer.newLine();
                }
            });

            writer.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

}
