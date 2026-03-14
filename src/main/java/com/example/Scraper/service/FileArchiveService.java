package com.example.Scraper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileArchiveService {

    private static final Logger log = LoggerFactory.getLogger(FileArchiveService.class);

    @Value("${screenshot.folder}")
    private String screenshotFolder;

    private static final DateTimeFormatter ARCHIVE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(cron = "59 59 23 * * ?")
    public void archiveDailyScreenshots() {
        log.info("Starting daily archiving of screenshots");

        File folder = new File(screenshotFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Screenshot folder does not exist: {}", screenshotFolder);
            return;
        }

        LocalDate today = LocalDate.now();

        String archiveName = today.format(ARCHIVE_DATE_FORMAT) + "_Flight_List.zip";
        File archiveFile = new File(folder, archiveName);

        File[] filesToZip = folder.listFiles(file -> {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    LocalDate fileDate = attrs.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return fileDate.equals(today);
                } catch (IOException e) {
                    log.warn("Could not read attributes for file: {}", file.getName(), e);
                    return false;
                }
            }
            return false;
        });

        if (filesToZip == null || filesToZip.length == 0) {
            log.info("No screenshots found for today, nothing to archive.");
            return;
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveFile))) {
            for (File file : filesToZip) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);
                    fis.transferTo(zos);
                    zos.closeEntry();
                }
                log.debug("Added to archive: {}", file.getName());
            }
            log.info("Archive created successfully: {}", archiveFile.getAbsolutePath());


            for (File file : filesToZip) {
                if (file.delete()) {
                    log.debug("Deleted original file: {}", file.getName());
                } else {
                    log.warn("Failed to delete file: {}", file.getName());
                }
            }

        } catch (IOException e) {
            log.error("Error while creating archive", e);
        }
    }
}