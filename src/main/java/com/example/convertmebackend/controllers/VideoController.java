package com.example.convertmebackend.controllers;

import it.sauronsoftware.jave.*;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/converter")
    public ResponseEntity<byte[]> returnConvertedAudio(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("original_extension") String original,
                                                       @RequestParam("future_extension") String future,
                                                       @RequestParam("video_codec") String videoCodec,
                                                       @RequestParam("audio_codec") String audioCodec,
                                                       @RequestParam("video_bit_rate") Integer videoBitRate,
                                                       @RequestParam("audio_bit_rate") Integer audioBitRate,
                                                       @RequestParam("video_frame_rate") Integer videoFrameRate,
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
            Path tempFile = Files.createTempFile("temp_video", "." + original);
            // Копируем данные из MultipartFile в временный файл
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Вызываем метод конвертации с временным файлом
            byte[] convertedData = videoConverter(tempFile.toFile(), future, videoCodec, audioCodec, videoBitRate, audioBitRate, videoFrameRate, channels, samplingRate, volume);

            // Удаляем временный файл
            Files.delete(tempFile);

            // Получаем имя файла совместимое со всеми системами
            String fileName = file.getOriginalFilename();
            int startIndex = fileName.replaceAll("\\\\", "/").lastIndexOf("/");
            fileName = fileName.trim().replace(" ","").substring(startIndex + 1,fileName.lastIndexOf(".")-1);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=" + fileName + "." + future)
                    .body(convertedData);

        } catch (IOException | EncoderException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private byte[] videoConverter( File source,
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
        System.out.println(encoder.getInfo(source));
        encoder.encode(source, target, attrs);

        // Чтение данных из target в массив байтов
        Files.copy(target.toPath(), outputStream);

        // Удаляем временный файл
        Files.delete(target.toPath());

        return outputStream.toByteArray();
    }
}
