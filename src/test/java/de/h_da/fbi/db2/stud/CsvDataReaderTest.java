package de.h_da.fbi.db2.stud;

import de.h_da.fbi.db2.tools.CsvDataReader;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by a.hofmann on 03.10.2016.
 */
public class CsvDataReaderTest {
    @Test
    public void read() throws Exception {
        final List<String[]> list = CsvDataReader.read();
        assertTrue(list.size() == 201);
    }
}