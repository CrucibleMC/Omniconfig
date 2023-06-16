package configs;

import io.github.cruciblemc.omniconfig.api.annotation.AnnotationConfig;
import io.github.cruciblemc.omniconfig.api.annotation.properties.*;
import io.github.cruciblemc.omniconfig.api.lib.ClassSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AnnotationConfig
public class TestConfig {

    @ConfigBoolean(name = "Boolean Config")
    public static boolean booleanConfig = true;

    @ConfigClassSet(name = "ClassSet Config")
    public static ClassSet<?> classSetConfig = new ClassSet<>(Object.class);

    @ConfigDouble(name = "Double Config")
    public static double doubleConfig = Math.PI;

    @ConfigEnum(name = "Enum Config")
    public static TestEnum enumConfig = TestEnum.VALUE3;

    @ConfigFloat(name = "Float Config")
    public static float floatConfig = 0.132F;

    @ConfigInt(name = "Int Config", max = Integer.MAX_VALUE)
    public static int intConfig = Integer.MAX_VALUE;

    @ConfigString(name = "String Config")
    public static String stringConfig = "µ";

    @ConfigStringCollection(name = "String Collection Config")
    public static List<String> stringCollectionConfig = new ArrayList<>(Arrays.asList("ABC", "123", "'", "\"", "←↓→"));

    enum TestEnum {
        VALUE1,
        VALUE2,
        VALUE3
    }
}
