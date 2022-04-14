package by.sofy.game_cheats_store.utils;

import by.sofy.game_cheats_store.entity.Product;
import by.sofy.game_cheats_store.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class FileUtil {

    @Value("${upload.path}")
    private String uploadPath;

    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("IN saveFile - Can't save empty file: {}.");
            throw new StorageException("Failed to store empty file.");
        }

        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String resultFileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
        log.info("IN saveFile - Save new file: {}.", resultFileName);
        try {
            file.transferTo(new File(uploadDir.getAbsolutePath(), resultFileName));
        } catch (IOException e) {
            throw new StorageException(e);
        }

        return resultFileName;
    }

    public void deleteFile(String path) {
        File fileDir = new File(new File(uploadPath), path);
        log.info("IN deleteFile - Delete file by path: {}.", path);
        fileDir.delete();
    }
}
