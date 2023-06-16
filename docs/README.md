[![Release](https://jitpack.io/v/CrucibleMC/Omniconfig.svg?style=flat-square)](https://jitpack.io/#CrucibleMC/Omniconfig)
# Omniconfig

Version-independent mod configuration API. Previously embedded in [Grimoire](https://github.com/Aizistral-Studios/Grimoire), 
now offered as a standalone API.

### Supported MC versions
* Forge 1.7.10
* Forge 1.12.2


## Getting Started
To get started with Omniconfig, add the Omniconfig dependency to your project:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // Only the API
    implementation 'com.github.CrucibleMC:Omniconfig:<version>'
    // Alternatively, if you want to run in a minecraft development environment
    //implementation 'com.github.CrucibleMC:Omniconfig:<version>:<mc_version>-dev'
}
```

Omniconfig has two ways to declare a configuration file, either by `IOmniconfigBuilder` or `@AnnotationConfig` 

### IOmniconfigBuilder
With `IOmniconfigBuilder`, you can programmatically define your configuration file. 
Refer to the [Javadoc](https://javadoc.jitpack.io/com/github/CrucibleMC/Omniconfig/latest/javadoc/index.html) for
more information.

### @AnnotationConfig
With `@AnnotationConfig`, you can annotate your configuration properties directly in your code, simplifying 
the configuration process. You can use annotations such as `@ConfigString`, `@ConfigInt`, `@ConfigBoolean`,
and `@ConfigEnum` to mark your fields as configuration properties. 
Refer to the [Javadoc](https://javadoc.jitpack.io/com/github/CrucibleMC/Omniconfig/latest/javadoc/index.html) for
more information.

#### Examples

To declare an annotated configuration, create a dedicated configuration class
```java
@AnnotationConfig(name = "ExampleModConfig", version = "1.0.0", terminateNonInvokedKeys = true,
        reloadable = true, policy = VersioningPolicy.DISMISSIVE, sided = SidedConfigType.COMMON)
public class ExampleConfig {

    @ConfigBoolean(name = "Boolean Config", comment = "Stores a boolean value", sync = true, category = "Java Primitives")
    public static boolean booleanConfig = true;
    
    @ConfigClassSet(name = "ClassSet Config")
    public static ClassSet<?> classSetConfig = new ClassSet<>(Object.class);

    @ConfigDouble(name = "Double Config", category = "Java Primitives")
    public static double doubleConfig = Math.PI;

    @ConfigEnum(name = "Enum Config")
    public static TestEnum enumConfig = TestEnum.VALUE3;

    @ConfigFloat(name = "Float Config", category = "Java Primitives")
    public static float floatConfig = 0.132F;

    @ConfigInt(name = "Int Config", min = -1, max = Integer.MAX_VALUE, category = "Java Primitives")
    public static int intConfig = Integer.MAX_VALUE;

    @ConfigString(name = "String Config")
    public static String stringConfig = "µ";

    @ConfigStringCollection(name = "String Collection Config")
    public static List<String> stringCollectionConfig = new ArrayList<>(Arrays.asList("ABC", "123", "'", "/?°", "←↓→"));

    enum TestEnum {
        VALUE1,
        VALUE2,
        VALUE3
    }
}
```
Then all you need to do is call `OmniconfigAPI#registerAnnotationConfig(Class)` as early as possible in your mod. 
Keep in mind however, if you are using Omniconfig with a coremod, you must load after the Omniconfig's coremod.
