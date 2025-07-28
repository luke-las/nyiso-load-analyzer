package com.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LoadDataImporterTest {

    private Path tempDir;
    private LoadDataImporter importer;

    @BeforeEach
    void setUp() throws IOException {
        importer = new LoadDataImporter();
        tempDir = Files.createTempDirectory("testdata");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    @Test
    void testImportSkipsWhenNoDatesProvided() throws Exception {
        assertDoesNotThrow(() -> importer.importDirectory(tempDir, null, null));
    }
}
