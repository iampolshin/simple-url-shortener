package iam.polshin.urlshortener;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "RedirectServlet", value = "/s/*")
public class RedirectServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String alias = null;
        String fullUrl = null;
        Pattern pattern = Pattern.compile("/s/([^,]*)");
        Matcher matcher = pattern.matcher(request.getRequestURL());
        if (matcher.find()) {
            alias = matcher.group().substring(3);
        }
        try (Connection connection = DatabaseConnection.initializeDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT full_url FROM url WHERE alias = ?");
            preparedStatement.setString(1, alias);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                fullUrl = resultSet.getString("full_url");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (fullUrl == null) {
            PrintWriter pw = response.getWriter();
            pw.println("<html>");
            pw.println("Incorrect link!<br>");
            pw.println("<a href=\"/\">Generate new short link</a>\n");
            pw.println("</html>");
        } else {
            response.sendRedirect(fullUrl);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullUrl = request.getParameter("full_url");
        String alias = RandomStringUtils.randomAlphanumeric(7);
        try (Connection connection = DatabaseConnection.initializeDatabase()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO url (alias, full_url) VALUES (?, ?)");
            preparedStatement.setString(1, alias);
            preparedStatement.setString(2, fullUrl);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/");

        StringBuilder shortUrl = new StringBuilder();
        shortUrl.append(request.getScheme()).append("://").append(request.getServerName()).append(":")
                .append(request.getServerPort()).append("/s/").append(alias);

        response.addHeader("short_url", String.valueOf(shortUrl));
        requestDispatcher.forward(request, response);
    }
}
