package airat.valiev.testapp.dataagregator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DataAggregator {

    static Logger logger = Logger.getLogger("Aggregate data task");

    public static void main(String[] args) {

        try {
            DataReader dataReader = new DataReader("output.txt");
            dataReader.readData();

            aggregateByPosCode(dataReader.getOperations());
            aggregateByDate(dataReader.getOperations());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "data loading failed", e);
        }
    }

    public static void aggregateByPosCode(List<Operation> operations) {

        HashMap<Object, Double> aggregateMap = new HashMap<>();
        for (Operation operation : operations) {
            String name = operation.getPosCode();
            if (aggregateMap.containsKey(name)) {
                double sum = aggregateMap.get(name);
                aggregateMap.put(name, sum + operation.getSum());
            } else {
                aggregateMap.put(name, 0d);
            }
        }
        List<Map.Entry<Object, Double>> list = aggregateMap
                .entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())).collect(Collectors.toList());
        try {
            writeMapToFile(list, "sums-by-offices.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "aggregate by POS data writing failed ", e);
        }
    }

    public static void aggregateByDate(List<Operation> operations) {

        HashMap<Object, Double> aggregateMap = new HashMap<>();
        for (Operation operation : operations) {
            LocalDate key = operation.getDate();
            if (aggregateMap.containsKey(key)) {
                double sum = aggregateMap.get(key);
                aggregateMap.put(key, sum + operation.getSum());
            } else {
                aggregateMap.put(key, 0d);
            }
        }
        List<Map.Entry<Object, Double>> list = aggregateMap
                .entrySet().stream()
                .sorted(Comparator.comparing(e1 -> ((LocalDate) e1.getKey())))
                .collect(Collectors.toList());
        try {
            writeMapToFile(list, "sums-by-dates.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "aggregate by POS data writing failed ", e);
        }
    }


    public static void writeMapToFile(List<Map.Entry<Object, Double>> list, String outputfile) throws IOException {

        FileWriter fileWriter = new FileWriter(outputfile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        Iterator<Map.Entry<Object, Double>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Double> entry = iterator.next();
            bufferedWriter.write(entry.getKey() + " " + Math.round(entry.getValue()));
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }


}
