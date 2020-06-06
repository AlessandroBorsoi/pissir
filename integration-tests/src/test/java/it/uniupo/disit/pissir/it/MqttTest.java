package it.uniupo.disit.pissir.it;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class MqttTest {

    CsvParser parser = new CsvParser();
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void parseCsv() throws Exception {
        URL resource = classLoader.getResource("small.csv");
        File file = new File(resource.getFile());

        Stream<Csv> csvStream = parser.csvLines(file)
                .map(line -> parser.parse(line))
                .flatMap(Optional::stream);

        assertEquals(71, csvStream.count());
    }

}
