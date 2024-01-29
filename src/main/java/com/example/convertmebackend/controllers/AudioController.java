package com.example.convertmebackend.controllers;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/converter")
    public ResponseEntity<byte[]> returnConvertedAudio(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("original_extension") String original,
                                                      @RequestParam("future_extension") String future,
                                                      @RequestParam("codec") String codec,
                                                      @RequestParam("bit_Rate") Integer bitRate,
                                                      @RequestParam("channels") Integer channels,
                                                      @RequestParam("sampling_rate") Integer samplingRate,
                                                      @RequestParam("volume") Integer volume) {
        try {
            // Создаем временный файл
            Path tempFile = Files.createTempFile("temp_audio", "." + original);
            // Копируем данные из MultipartFile в временный файл
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
//            System.out.println(tempFile.getFileName()+"\n"+
//                                original+"\n"+
//                                future+"\n"+
//                                codec+"\n"+
//                                bitRate+"\n"+
//                                channels+"\n"+
//                                samplingRate+"\n"+
//                                volume);
            // Вызываем метод конвертации с временным файлом
            byte[] convertedData = audioConverter(tempFile.toFile(), future, codec, bitRate, channels, samplingRate, volume);

            // Удаляем временный файл
            Files.delete(tempFile);

            String fileName = file.getOriginalFilename();
            int startIndex = fileName.replaceAll("\\\\", "/").lastIndexOf("/");
            fileName = fileName.trim().replace(" ","").substring(startIndex + 1,fileName.lastIndexOf(".")-1);

//            System.out.println(fileName);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=" + fileName + "." + future)
                    .body(convertedData);

        } catch (IOException | EncoderException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
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

        // Чтение данных из target.mp3 в массив байтов
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