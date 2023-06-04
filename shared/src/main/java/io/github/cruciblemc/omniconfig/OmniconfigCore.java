package io.github.cruciblemc.omniconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cruciblemc.omniconfig.api.lib.Environment;
import io.github.cruciblemc.omniconfig.backing.Configuration;
import io.github.cruciblemc.omniconfig.core.Omniconfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class OmniconfigCore {
    public static final Logger logger = LogManager.getLogger("Omniconfig");
    public static final String FILE_SEPARATOR = File.separator;

    /**
     * This must only ever true if we are in a client environment and
     * currently are logged in to non-local server.
     */
    public static boolean onRemoteServer = false;

    public static final OmniconfigCore INSTANCE = new OmniconfigCore();

    private File mcLocation;
    private File configFolder;
    private File dataFolder;
    private File defaultConfigsArchive;
    private File defaultConfigsJson;
    private Environment side;

    private OmniconfigCore() {
        // NO-OP
    }

    public void init(File mcLocation, Environment onSide) {
        logger.info("Initializing Omniconfig.");
        this.side = onSide;
        this.mcLocation = mcLocation;
        this.configFolder = new File(this.mcLocation, "config");
        this.dataFolder = new File(this.mcLocation, "mcdata");
        this.defaultConfigsArchive = new File(this.dataFolder, "defaultconfigs");
        this.defaultConfigsJson = new File(this.dataFolder, "defaultconfigs.json");

        this.configFolder.mkdirs();
        this.dataFolder.mkdirs();
    }

    public String sanitizeName(String configName) {
        String newName = configName.replace("/", FILE_SEPARATOR);

        if (newName.endsWith(".cfg")) {
            newName = newName.substring(0, newName.length() - 4);
        }

        return newName;
    }

    public void backUpDefaultCopy(Omniconfig cfg) {
        Configuration backingConfig = cfg.getBackingConfig();

        if (this.defaultConfigsArchive.exists() && this.defaultConfigsArchive.isFile()) {
            if (!this.compareMD5()) {
                logger.info("Deleting defaultconfigs archive...");
                this.defaultConfigsArchive.delete();
            }
        }

        if (!this.defaultConfigsArchive.exists() || !this.defaultConfigsArchive.isFile()) {
            try {
                ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(this.defaultConfigsArchive.toPath()));
                out.close();

                this.updateMemorizedMD5Digest();
                logger.info("Made new defaultconfigs archive: {}", this.defaultConfigsArchive.getCanonicalPath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            File trueConfig = cfg.getFile();
            File tempConfig = File.createTempFile(trueConfig.getName(), null);
            backingConfig.setFile(tempConfig);
            backingConfig.forceDefault(true);
            backingConfig.save();

            this.updateFileWithinArchive(this.defaultConfigsArchive, cfg.getFile(), cfg.getFileID().replace(OmniconfigCore.FILE_SEPARATOR, "/"));

            tempConfig.delete();
            backingConfig.setFile(trueConfig);
            backingConfig.forceDefault(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // logger.info("Updated default copy of omniconfig file: {}", cfg.getFileID());
    }

    private void updateFileWithinArchive(File zipFile, File fileToUpdate, String zipEntryName) throws IOException {
        File tempFile = File.createTempFile(zipFile.getName(), null);
        tempFile.delete();

        // For the time of writing, preserve old archive as temporary file
        try {
            Files.move(zipFile.toPath(), tempFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath(), e);
        }

        byte[] buf = new byte[1024];

        ZipInputStream zipInput = new ZipInputStream(Files.newInputStream(tempFile.toPath()));
        ZipOutputStream zipOutput = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()));

        // Copy all files from the old archive, besides that one we're trying to update
        ZipEntry entry = zipInput.getNextEntry();
        while (entry != null) {
            String entryName = entry.getName();

            if (zipEntryName.equals(entryName)) {
                entry = zipInput.getNextEntry();
                continue;
            }

            zipOutput.putNextEntry(new ZipEntry(entryName));

            int len;
            while ((len = zipInput.read(buf)) > 0) {
                zipOutput.write(buf, 0, len);
            }

            entry = zipInput.getNextEntry();
        }

        zipInput.close();

        // Now write whatever we're updating into the archive
        InputStream updateInput = Files.newInputStream(fileToUpdate.toPath());
        zipOutput.putNextEntry(new ZipEntry(zipEntryName));

        int len;
        while ((len = updateInput.read(buf)) > 0) {
            zipOutput.write(buf, 0, len);
        }

        zipOutput.closeEntry();
        updateInput.close();

        // Finalize
        zipOutput.close();
        tempFile.delete();

        this.updateMemorizedMD5Digest();
    }

    private boolean compareMD5() {
        String memorized = this.getMemorizedMD5Digest();
        String current = this.getArchiveMD5Digest();

        boolean equals = Objects.equals(memorized, current);
        return equals;
    }

    private String getArchiveMD5Digest() {
        return getMD5Digest(this.defaultConfigsArchive);
    }

    @Nullable
    private String getMemorizedMD5Digest() {
        try {
            if (this.defaultConfigsArchive.exists() && this.defaultConfigsArchive.isFile())
                if (this.defaultConfigsJson.exists() && this.defaultConfigsJson.isFile()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    FileInputStream fileInput = new FileInputStream(this.defaultConfigsJson);
                    InputStreamReader streamReader = new InputStreamReader(fileInput, StandardCharsets.UTF_8);

                    HashMap<String, String> map = gson.fromJson(streamReader, HashMap.class);
                    String hash = map.get("md5");

                    streamReader.close();
                    fileInput.close();

                    return hash;
                }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private void updateMemorizedMD5Digest() {
        this.updateMemorizedMD5Digest(this.getArchiveMD5Digest());
    }

    private void updateMemorizedMD5Digest(String hash) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(this.defaultConfigsJson);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutput, StandardCharsets.UTF_8);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            HashMap<String, String> map = new HashMap<>();
            map.put("md5", hash);

            gson.toJson(map, streamWriter);

            streamWriter.close();
            fileOutput.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public File extractDefaultCopy(String fileID) {
        try {
            if (this.defaultConfigsArchive.exists() && this.defaultConfigsArchive.isFile()) {
                if (this.compareMD5()) {
                    File defaultCopy = File.createTempFile(UUID.randomUUID().toString(), null);
                    defaultCopy.delete();

                    ZipFile archive = new ZipFile(this.defaultConfigsArchive);
                    ZipEntry entry = archive.getEntry(fileID.replace(OmniconfigCore.FILE_SEPARATOR, "/"));

                    if (entry != null) {
                        InputStream inputStream = archive.getInputStream(entry);
                        Files.copy(inputStream, defaultCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        inputStream.close();
                    }

                    archive.close();

                    return defaultCopy;
                } else {
                    logger.info("Deleting defaultconfigs archive...");
                    this.defaultConfigsArchive.delete();
                    return null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static String getMD5Digest(File file) {
        if (file.exists() && file.isFile()) {
            try (InputStream stream = Files.newInputStream(file.toPath())) {
                return md5Hex(stream);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to get MD5Digest from file " + file.getAbsolutePath(), ex);
            }
        }

        return null;
    }

    //==================================================================================================================
    // From https://stackoverflow.com/a/58118078 -- Apache v2 license
    //==================================================================================================================
    private static final char[] LOOKUP_TABLE_LOWER = new char[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66};
    private static final char[] LOOKUP_TABLE_UPPER = new char[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46};

    public static String encodeHex(byte[] byteArray, boolean upperCase, ByteOrder byteOrder) {

        // our output size will be exactly 2x byte-array length
        final char[] buffer = new char[byteArray.length * 2];

        // choose lower or uppercase lookup table
        final char[] lookup = upperCase ? LOOKUP_TABLE_UPPER : LOOKUP_TABLE_LOWER;

        int index;
        for (int i = 0; i < byteArray.length; i++) {
            // for little endian we count from last to first
            index = (byteOrder == ByteOrder.BIG_ENDIAN) ? i : byteArray.length - i - 1;

            // extract the upper 4 bit and look up char (0-A)
            buffer[i << 1] = lookup[(byteArray[index] >> 4) & 0xF];
            // extract the lower 4 bit and look up char (0-A)
            buffer[(i << 1) + 1] = lookup[(byteArray[index] & 0xF)];
        }
        return new String(buffer);
    }

    public static String encodeHex(byte[] byteArray) {
        return encodeHex(byteArray, false, ByteOrder.BIG_ENDIAN);
    }
    //==================================================================================================================

    public static String md5Hex(InputStream stream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        while (stream.available() > 0) {
            digest.update((byte) stream.read());
        }
        return encodeHex(digest.digest());
    }

    public Environment getEnvironment() {
        return side;
    }

    public static void executeInEnvironment(Environment side, Supplier<Runnable> supplier) {
        side.execute(supplier);
    }

    public File getConfigFolder() {
        return this.configFolder;
    }
}
