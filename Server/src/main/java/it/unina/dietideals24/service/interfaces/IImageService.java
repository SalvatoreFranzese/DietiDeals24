package it.unina.dietideals24.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageService {
    void saveImage(String profilePicDirectory, Long id, MultipartFile file) throws IOException;

    byte[] getImage(String url);
}
