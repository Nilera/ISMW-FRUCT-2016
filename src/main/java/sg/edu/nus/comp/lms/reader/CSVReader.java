package sg.edu.nus.comp.lms.reader;

import sg.edu.nus.comp.lms.entity.SocialNetwork;
import sg.edu.nus.comp.lms.weka.operation.IntersectInstances;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader extends DataReader {

    public CSVReader(File file) {
        super(file);
    }

    public CSVReader(File... files) {
        super(files);
    }

    public CSVReader(SocialNetwork socialNetwork, File directory) {
        this(socialNetwork.findFiles(directory)
                .toArray(new File[socialNetwork.getFiles().length]));
    }

    public static List<Instances> readAllSources(String folder) {
        List<Instances> sources = new ArrayList<>();
        sources.add(new CSVReader(SocialNetwork.TWITTER, new File(folder)).readAll());
        sources.add(new CSVReader(SocialNetwork.FOURSQUARE, new File(folder)).readAll());
        sources.add(new CSVReader(SocialNetwork.INSTAGRAM, new File(folder)).readAll());
        return sources;
    }

    public static List<Instances> readAllIntersectedSources(String folder) {
        List<Instances> sources = readAllSources(folder);
        for (int i = 0; i < sources.size(); i++) {
            for (int j = i + 1; j < sources.size(); j++) {
                new IntersectInstances(sources.get(i), sources.get(j)).eval();
            }
        }
        return sources;
    }

    @Override
    protected Instances read(File file) {
        try {
            CSVLoader loader = new CSVLoader();
            loader.setSource(file);
            return loader.getDataSet();
        } catch (IOException e) {
            throw new IllegalStateException("Incorrect file format or file path: " + file.getName(), e);
        }
    }
}
