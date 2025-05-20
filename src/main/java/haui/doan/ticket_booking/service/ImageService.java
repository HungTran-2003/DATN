package haui.doan.ticket_booking.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.io.IOException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static final String UPLOADS_DIR = "uploads";
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w500"; // URL gốc của ảnh


    public String saveImage(String imageUrl, String fileName) throws Exception {

        String fullImageUrl = IMAGE_URL + imageUrl; // Tạo URL đầy đủ của ảnh

        // Tạo thư mục uploads nếu chưa tồn tại
        File uploadDir = new File(UPLOADS_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Tải ảnh từ URL
        URL url = new URL(fullImageUrl);
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(new File(uploadDir, fileName))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // Trả về đường dẫn file đã lưu
        return UPLOADS_DIR + File.separator + fileName;
    }

    public String saveFile(MultipartFile file, String fileName) throws Exception {
        Path path = Paths.get("uploads/" + fileName);
        Files.write(path, file.getBytes());
        return path.toString();
    }

    public boolean deleteImage(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}