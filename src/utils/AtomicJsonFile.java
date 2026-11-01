package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Writes beside the destination before replacing it, preserving existing data on serialization failure. */
public final class AtomicJsonFile {
    private AtomicJsonFile() { }

    public static void write(ObjectMapper mapper, File destination, Object value) throws IOException {
        Path target = destination.toPath().toAbsolutePath();
        Path temporary = Files.createTempFile(target.getParent(), ".pet-adoption-", ".json");
        try {
            mapper.writeValue(temporary.toFile(), value);
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
