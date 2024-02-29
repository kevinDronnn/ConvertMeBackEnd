package com.example.convertmebackend.controllers;

import com.example.convertmebackend.entity.VideoFileInfo;
import it.sauronsoftware.jave.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@CrossOrigin("*")
@RequestMapping("/video")
public class VideoController {

    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    @PostMapping("/converter")
    public ResponseEntity<byte[]> returnConvertedVideo(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("original_extension") String original,
                                                       @RequestParam("future_extension") String future,
                                                       @RequestParam("video_codec") String videoCodec,
                                                       @RequestParam("audio_codec") String audioCodec,
                                                       @RequestParam("video_bitrate") Integer videoBitRate,
                                                       @RequestParam("audio_bitrate") Integer audioBitRate,
                                                       @RequestParam("video_framerate") Integer videoFrameRate,
                                                       @RequestParam("audio_channels") Integer channels,
                                                       @RequestParam("sampling_rate") Integer samplingRate,
                                                       @RequestParam("volume") Integer volume) {
        if (file.isEmpty() || file.getSize() == 0) {
            // Обработка случая, когда файл отсутствует или пуст
            return ResponseEntity.badRequest().build();
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith("." + original.toLowerCase())) {
            // Обработка случая, когда расширение файла не соответствует ожидаемому формату
            return ResponseEntity.badRequest().build();
        }

        try {
            // Создаем временный файл
            Path tempFile = Files.createTempFile("temp_video", "." + original);
            // Копируем данные из MultipartFile в временный файл
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Вызываем метод конвертации с временным файлом
            byte[] convertedData = videoConverter(tempFile.toFile(), future, videoCodec, audioCodec, videoBitRate, audioBitRate, videoFrameRate, channels, samplingRate, volume);

            logger.info("file converted");
            // Удаляем временный файл
            Files.delete(tempFile);

            // Получаем имя файла совместимое со всеми системами
            String fileName = file.getOriginalFilename();
            int startIndex = fileName.replaceAll("\\\\", "/").lastIndexOf("/");
            fileName = fileName.trim().replace(" ", "").substring(startIndex + 1, fileName.lastIndexOf(".") - 1);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=" + fileName + "." + future)
                    .body(convertedData);

        } catch (IOException | EncoderException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/getVideoInfo")
    public ResponseEntity<VideoFileInfo> returnVideoInfo(@RequestParam("file") MultipartFile file) throws EncoderException {
        // Создаем временный файл
        String fileName = file.getOriginalFilename();
        int startIndex = fileName.replaceAll("\\\\", "/").lastIndexOf("/");
        fileName = fileName.trim().replace(" ", "").substring(fileName.lastIndexOf("."), fileName.length() - 1);
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("temp_audioInfo", "." + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Копируем данные из MultipartFile в временный файл
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Encoder encoder = new Encoder();
        String name = file.getOriginalFilename().substring(startIndex + 1);

        double sizeInMb = (double) (file.getSize() / (1024 * 1024));

        double roundedSize = Math.round(sizeInMb * 100.0) / 100.0;

        VideoFileInfo videoFileInfo = new VideoFileInfo(
                name,
                roundedSize,
                encoder.getInfo(tempFile.toFile()).getVideo().getBitRate(),
                (int) encoder.getInfo(tempFile.toFile()).getVideo().getFrameRate(),
                encoder.getInfo(tempFile.toFile()).getAudio().getBitRate(),
                encoder.getInfo(tempFile.toFile()).getAudio().getSamplingRate()
        );

        logger.info("success get info about video");
        return ResponseEntity.ok().body(videoFileInfo);
    }

    private byte[] videoConverter(File source,
                                  String future,
                                  String videoCodec,
                                  String audioCodec,
                                  Integer videoBitRate,
                                  Integer audioBitRate,
                                  Integer videoFrameRate,
                                  Integer channels,
                                  Integer samplingRate,
                                  Integer volume) throws EncoderException, IOException {


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        File target = new File("target." + future);

        Integer resultVolume = (volume * 256) / 100;


        AudioAttributes audio = new AudioAttributes();
        audio.setCodec(audioCodec);
        audio.setBitRate(audioBitRate);
        audio.setChannels(channels);
        audio.setSamplingRate(samplingRate);
        audio.setVolume(resultVolume);


        VideoAttributes video = new VideoAttributes();
        video.setCodec(videoCodec);
        video.setBitRate(videoBitRate);
        video.setFrameRate(videoFrameRate);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat(future);
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);
        Encoder encoder = new Encoder();

        encoder.encode(source, target, attrs);

        // Чтение данных из target в массив байтов
        Files.copy(target.toPath(), outputStream);

        // Удаляем временный файл
        Files.delete(target.toPath());

        return outputStream.toByteArray();
    }
}
