package carmanagement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TextStorageService {
    private final Path dataDirectory;

    public TextStorageService(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void ensureDataDirectory() throws IOException {
        Files.createDirectories(dataDirectory);
    }

    public List<String> readRecords(String fileName) throws IOException {
        Path filePath = dataDirectory.resolve(fileName);
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }

    public void writeRecords(String fileName, List<String> records) throws IOException {
        ensureDataDirectory();
        Files.write(dataDirectory.resolve(fileName), records, StandardCharsets.UTF_8);
    }
}
