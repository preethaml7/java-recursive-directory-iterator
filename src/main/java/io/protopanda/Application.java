package io.protopanda;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
@Slf4j
public class Application implements ApplicationRunner {

    int unsupportedFileCount = 0;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {

        int fileCount = 0;

        try {

            File directoryPath = new File("/Users/preethaml7/Desktop");
            Collection<File> files = FileUtils.listFiles(directoryPath, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
            Object[] fileArray = files.toArray();
            fileCount = fileArray.length;

            log.info("Total files in the directory: " + fileCount);
            for (Object file : fileArray) {

                File filePath = new File(file.toString());
                ArrayList<Tag> tagsList = this.getMediaMetadata(filePath);

                log.info("FileName: " + file + " --> " + "MetaData: " + Arrays.toString(tagsList.toArray()));
            }
        } catch (Exception ex) {
            log.error("Exception in processing directories at provided path: " + ex.getClass().getSimpleName() + " ---> " + ex.getMessage());
        }

        log.info("Total File Count: " + fileCount);
        log.info("Unsupported File Count: " + unsupportedFileCount);
    }

    public ArrayList<Tag> getMediaMetadata(File filePath) {
        ArrayList<Tag> tagsList = new ArrayList<>();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(filePath);
            for (Directory directory : metadata.getDirectories()) {
                tagsList.addAll(directory.getTags());
            }
        } catch (ImageProcessingException ex) {
            this.handleUnprocessableFile(filePath);
            log.error("Exception in analyzing file metadata: " + ex.getClass().getSimpleName() + " ---> " + ex.getMessage());
        } catch (IOException ex) {
            log.error("Exception in analyzing file metadata: " + ex.getClass().getSimpleName() + " ---> " + ex.getMessage());
        }
        return tagsList;
    }

    public void handleUnprocessableFile(File filePath) {

        log.error("Cannot handle file: " + filePath + " and file type: " + FilenameUtils.getExtension(filePath.getName()));
        unsupportedFileCount++;

    }
}