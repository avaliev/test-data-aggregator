package airat.valiev.testapp.dataagregator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class DataReader {

    private List<Operation> operations = new LinkedList<>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String fileName;

    public DataReader(String fileName) {
        this.fileName = fileName;
    }


    public void readData() throws IOException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> result = new LinkedList<>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String[] fields = line.split(" ");
            Operation operation = new Operation();
            operation.setId(Long.parseLong(fields[0]));
            LocalDateTime dateTime = LocalDateTime.from(formatter.parse(fields[1] + " " + fields[2]));
            operation.setDateTime(dateTime);
            operation.setPosCode(fields[3]);
            operation.setSum(Double.parseDouble(fields[4]));
            operations.add(operation);
        }
    }


    public List<Operation> getOperations() {
        return operations;
    }
}
