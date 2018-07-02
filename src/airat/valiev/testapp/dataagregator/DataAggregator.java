package airat.valiev.testapp.dataagregator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
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

        HashMap<Object, BigDecimal> aggregateMap = new HashMap<>();
        for (Operation operation : operations) {
            String name = operation.getPosCode();
            if (aggregateMap.containsKey(name)) {
                BigDecimal sum = aggregateMap.get(name);
                aggregateMap.put(name, sum.add(operation.getSum()));
            } else {
                aggregateMap.put(name, new BigDecimal(0));
            }
        }
        List<Map.Entry<Object, BigDecimal>> list = aggregateMap
                .entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue().doubleValue(), e1.getValue().doubleValue())).collect(Collectors.toList());
        try {
            writeMapToFile(list, "sums-by-offices.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "aggregate by POS data writing failed ", e);
        }
    }

    public static void aggregateByDate(List<Operation> operations) {

        HashMap<Object, BigDecimal> aggregateMap = new HashMap<>();
        for (Operation operation : operations) {
            LocalDate key = operation.getDate();
            if (aggregateMap.containsKey(key)) {
                BigDecimal sum = aggregateMap.get(key);
                aggregateMap.put(key, sum.add(operation.getSum()));
            } else {
                aggregateMap.put(key, new BigDecimal(0));
            }
        }
        List<Map.Entry<Object, BigDecimal>> list = aggregateMap
                .entrySet().stream()
                .sorted(Comparator.comparing(e1 -> ((LocalDate) e1.getKey())))
                .collect(Collectors.toList());
        try {
            writeMapToFile(list, "sums-by-dates.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "aggregate by POS data writing failed ", e);
        }
    }


    public static void writeMapToFile(List<Map.Entry<Object, BigDecimal>> list, String outputfile) throws IOException {

        FileWriter fileWriter = new FileWriter(outputfile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        Iterator<Map.Entry<Object, BigDecimal>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, BigDecimal> entry = iterator.next();
            bufferedWriter.write(entry.getKey() + " " + Math.round(entry.getValue().doubleValue()));
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }


}
