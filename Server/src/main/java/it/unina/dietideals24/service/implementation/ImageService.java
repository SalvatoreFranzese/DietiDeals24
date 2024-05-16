package it.unina.dietideals24.service.implementation;

import it.unina.dietideals24.service.interfaces.IImageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Service
@Qualifier("locallyStoreImageService")
public class ImageService implements IImageService {

    Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Saves an image locally, using the specified directory
     *
     * @param imageDirectory directory for the image
     * @param id             id of the item that has the image, used as name for the image
     * @param file           representation of the image
     * @throws IOException thrown when the image can't be saved due to "getBytes()" failing
     */
    @Override
    public void saveImage(String imageDirectory, Long id, MultipartFile file) throws IOException {
        String path = "images" + File.separatorChar + imageDirectory + File.separatorChar + id.toString() + ".jpeg";
        Path fileNameAndPath = Paths.get(path);
        Files.write(fileNameAndPath, file.getBytes());

        String message = "Image saved in: " + fileNameAndPath;
        logger.info(message);
    }

    @Override
    public byte[] getImage(String url) {
        try {
            return convertImageToBytes(url);
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private byte[] convertImageToBytes(String url) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("images" + File.separatorChar + url));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}
