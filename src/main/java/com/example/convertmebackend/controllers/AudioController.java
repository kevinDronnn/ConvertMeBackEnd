package com.example.convertmebackend.controllers;

import com.example.convertmebackend.entity.AudioFileInfo;
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
@RequestMapping("/audio")
public class AudioController {

    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);

    @PostMapping("/converter")
    public ResponseEntity<byte[]> returnConvertedAudio(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("original_extension") String original,
                                                       @RequestParam("future_extension") String future,
                                                       @RequestParam("codec") String codec,
                                                       @RequestParam("bit_Rate") Integer bitRate,
                                                       @RequestParam("channels") Integer channels,
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
            Path tempFile = Files.createTempFile("temp_audio", "." + original);
            // Копируем данные из MultipartFile в временный файл
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Вызываем метод конвертации с временным файлом
            byte[] convertedData = audioConverter(tempFile.toFile(), future, codec, bitRate, channels, samplingRate, volume);

            logger.info("file converted");

            // Удаляем временный файл
            Files.delete(tempFile);

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

    @PostMapping("/getAudioInfo")
    public ResponseEntity<AudioFileInfo> returnAudioInfo(@RequestParam("file") MultipartFile file) throws EncoderException {
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

        AudioFileInfo audioInfo = new AudioFileInfo(
                name,
                roundedSize,
                encoder.getInfo(tempFile.toFile()).getAudio().getBitRate() / 10,
                encoder.getInfo(tempFile.toFile()).getAudio().getSamplingRate());

        logger.info("success get info about audio");

        return ResponseEntity.ok().body(audioInfo);
    }

    private byte[] audioConverter(File source, String future, String codec, Integer bitRate,
                                  Integer channels, Integer samplingRate, Integer volume) throws EncoderException, IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        File target = new File("target." + future);

        Integer resultVolume = (volume * 256) / 100;

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec(codec);
        audio.setBitRate(bitRate);
        audio.setChannels(channels);
        audio.setSamplingRate(samplingRate);
        audio.setVolume(resultVolume);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat(future);
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        encoder.encode(source, target, attrs);

        // Чтение данных из target в массив байтов
        try (InputStream inputStream = new FileInputStream(target)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Удаляем временный файл
        Files.delete(target.toPath());

        return outputStream.toByteArray();
    }
}