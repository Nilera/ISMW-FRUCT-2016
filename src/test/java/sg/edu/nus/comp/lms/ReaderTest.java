package sg.edu.nus.comp.lms;

import com.opencsv.CSVWriter;
import org.junit.Test;
import sg.edu.nus.comp.lms.reader.CSVReader;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReaderTest {

    private static final Random RANDOM = new Random();
    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 10;

    private static String value(Instance instance, int index) {
        if (instance.attribute(index).isNumeric()) {
            return String.format("%.3f", instance.value(index));
        } else {
            return instance.stringValue(index);
        }
    }

    @Test
    public void checkCSVReader() throws IOException {
        // generate random CSV file
        int featuresNumber = RANDOM.nextInt(MAX_VALUE) + MIN_VALUE;
        int itemNumber = RANDOM.nextInt(MAX_VALUE) + MIN_VALUE;
        String[] header = IntStream.range(0, featuresNumber)
                .mapToObj(i -> "_" + i)
                .toArray(String[]::new);
        header[0] = "_id";
        List<String[]> data = IntStream.range(0, itemNumber)
                .mapToObj(i ->
                        IntStream.range(0, featuresNumber)
                                .mapToDouble(j -> RANDOM.nextDouble())
                                .mapToObj(v -> String.format("%.3f", v))
                                .toArray(String[]::new)
                ).collect(Collectors.toList());
        for (int i = 0; i < data.size(); i++) {
            data.get(i)[0] = "_" + i;
        }

        // write it to file
        File file = File.createTempFile("tmp", null);
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(header);
            writer.writeAll(data);
        }

        // read instances
        Instances instances = new CSVReader(file).readAll();

        // check read data
        for (int i = 0; i < instances.numAttributes(); i++) {
            assertThat(instances.attribute(i).name(), equalTo(header[i]));
        }
        for (int i = 0; i < instances.numAttributes(); i++) {
            int attrIndex = instances.attribute(i).index();
            for (int j = 0; j < instances.size(); j++) {
                Instance instance = instances.get(j);
                String[] item = data.get(j);
                assertThat(value(instance, attrIndex), equalTo(item[attrIndex]));
            }
        }
    }
}
