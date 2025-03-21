package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/uploads/*") // Permet d'accéder aux images via http://localhost:8080/uploads/nom_image.png
public class ImageServletkansawbfiha extends HttpServlet {
    private static final String IMAGE_DIR = "src/main/resources/uploads/"; // Chemin local des images

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filename = req.getPathInfo().substring(1); // Récupère le nom du fichier
        File file = new File(IMAGE_DIR, filename);

        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Image non trouvée !");
            return;
        }

        resp.setContentType(getServletContext().getMimeType(file.getName()));
        resp.setContentLength((int) file.length());

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = resp.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
