package airat.valiev.testapp.dataagregator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataReader {

    private List<Operation> operations = new LinkedList<>();

    Logger logger = Logger.getLogger("Aggregate data task");

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String fileName;

    public DataReader(String fileName) {
        this.fileName = fileName;
    }


    public void readData() throws IOException {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            List<String> result = new LinkedList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    String[] fields = line.split(" ");
                    Operation operation = new Operation();
                    operation.setId(Long.parseLong(fields[0]));
                    LocalDateTime dateTime = LocalDateTime.from(formatter.parse(fields[1] + " " + fields[2]));
                    operation.setDateTime(dateTime);
                    operation.setPosCode(fields[3]);
                    operation.setSum(BigDecimal.valueOf(Double.parseDouble(fields[4])));
                    operations.add(operation);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "data in line " + line + " is incorrect", e);
                }
            }
        }
    }


    public List<Operation> getOperations() {
        return operations;
    }
}
