import configs.TestConfig;
import io.github.cruciblemc.omniconfig.OmniconfigCore;
import io.github.cruciblemc.omniconfig.api.OmniconfigAPI;
import io.github.cruciblemc.omniconfig.api.lib.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

//TODO: Actual proper testing
public class TestAnnotation {
    @TempDir
    static File fakeMc;

    @BeforeAll
    static void setup() {
        OmniconfigCore.INSTANCE.init(fakeMc, Environment.DEDICATED_SERVER);
    }

    @Test
    void testConfig() throws IOException {
        OmniconfigAPI.registerAnnotationConfig(TestConfig.class);
        try (InputStream resource = TestAnnotation.class.getResourceAsStream("TestConfig.cfg")) {
            Assertions.assertNotNull(resource, "Missing required resources.");
            List<String> expected = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.toList());
            List<String> result = Files.readAllLines(Paths.get(fakeMc.getAbsolutePath(), "config", "TestConfig.cfg"));
            Assertions.assertEquals(expected, result, "Configuration content does not match expectations");
        }
    }
}
