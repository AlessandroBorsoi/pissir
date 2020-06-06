package it.uniupo.disit.pissir.it;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CsvParser {

    public Stream<String> csvLines(File file) throws FileNotFoundException {
        InputStream is = new FileInputStream(file);
        return new BufferedReader(new InputStreamReader(is)).lines();
    }

    public Optional<Csv> parse(String line) {
        List<String> elements = Stream.of(line.split(",")).map(String::trim).collect(toList());
        if (elements.size() != 6) return Optional.empty();
        try {
            return Optional.of(
                    new Csv(
                            Long.parseLong(elements.get(0)),
                            elements.get(1),
                            Double.parseDouble(elements.get(2)),
                            Double.parseDouble(elements.get(3)),
                            Integer.parseInt(elements.get(4)),
                            Double.parseDouble(elements.get(5))
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
