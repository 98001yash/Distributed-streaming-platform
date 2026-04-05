package com.distributed_streaming_platform.video_processing_service.service;



import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class FFmpegService {

    public File transcode(File inputFile, String resolution) throws IOException, InterruptedException {

        String outputPath = inputFile.getParent() + "\\" + resolution + ".mp4";

        ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\ffmpeg\\bin\\ffmpeg.exe",
                "-i", inputFile.getAbsolutePath(),
                "-vf", "scale=" + getResolution(resolution),
                "-preset", "fast",
                "-crf", "23",
                outputPath
        );

        processBuilder.inheritIO();

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed for " + resolution);
        }

        return new File(outputPath);
    }

    private String getResolution(String quality) {
        return switch (quality) {
            case "P240" -> "426:240";
            case "P480" -> "854:480";
            case "P720" -> "1280:720";
            case "P1080" -> "1920:1080";
            default -> throw new IllegalArgumentException("Invalid quality");
        };
    }
}