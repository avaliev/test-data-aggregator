package airat.valiev.testapp.datagenerator;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestDataGenerator {

    static List<String> posList;

    static Logger logger = Logger.getLogger("Generate data task");

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static final char gap = ' ';

    public static void main(String[] args) throws IOException {
        String inputFileName = null;
        String outputFileName = null;
        long countOfOperations = 100000;
        if (args.length == 3) {
            inputFileName = args[0];
            countOfOperations = Long.parseLong(args[1]);
            outputFileName = args[2];
        } else {
            inputFileName = "input.txt";
            outputFileName = "output.txt";
        }

        try {
            posList = readPosList(inputFileName);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "reading list of POS failed", e);
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName))) {
            float minSum = 10_000f;
            float maxSum = 100_000f;

            Random random = new Random();
            TimeGenerator timeGenerator = new TimeGenerator(LocalDateTime.now().getYear());

            for (int i = 0; i <= countOfOperations; i++) {
                int rndPos = random.nextInt(posList.size());
                LocalDateTime time = timeGenerator.getRandomDateTime();
                float rndSum = minSum + random.nextFloat() * (maxSum - minSum);

                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append(i).append(gap);
                lineBuilder.append(formatter.format(time)).append(gap);
                lineBuilder.append(posList.get(rndPos)).append(gap);
                lineBuilder.append(String.format(Locale.US, "%.2f", rndSum)).append(gap);
                bufferedWriter.write(lineBuilder.toString());
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();
        }
    }


    public static List<String> readPosList(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> result = new LinkedList<>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            if (!line.isEmpty()) {
                result.add(line);
            }
        }
        return result;
    }

    public static class TimeGenerator {

        LocalDateTime dateTimeStart;
        LocalDateTime dateTimeEnd;
        Random random = new Random();

        public TimeGenerator(int targetYear) {
            dateTimeStart = LocalDateTime.of(targetYear - 1, 1, 1, 0, 0);
            dateTimeEnd = LocalDateTime.of(targetYear, 1, 1, 0, 0);
        }

        public LocalDateTime getRandomDateTime() {
            ZoneId zoneId = ZoneId.systemDefault();
            long from = dateTimeStart.atZone(zoneId).toEpochSecond();
            long to = dateTimeEnd.atZone(zoneId).toEpochSecond();
            long seconds = ThreadLocalRandom.current().nextLong(from, to);
            Instant instant = Instant.ofEpochSecond(seconds);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zoneId);
            return dateTime;
        }
    }
}
