package controller;

import java.net.Socket;
import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tracking/*")
public class TrackingServlet extends HttpServlet {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8081;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String[] parts = pathInfo.split("/");
        if (parts.length < 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }
        String orderId = parts[1];
        String action = parts[2];
        String message = request.getReader().readLine();

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(orderId);
            out.println(action.toUpperCase() + "|" + message);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Tracking update sent.");
        }
    }
}
