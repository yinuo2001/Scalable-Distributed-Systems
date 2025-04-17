import java.io.BufferedReader;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {
    private static final int EXPECTED_URL_PARTS = 8;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // Check if the URL exists
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Missing parameters");
            return;
        }

        // Check if the URL is valid
        String[] urlParts = urlPath.split("/");
        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            // Process the request for future use, if needed
            // int resortID = Integer.parseInt(urlParts[1]);
            // int seasonID = Integer.parseInt(urlParts[3]);
            // int dayID = Integer.parseInt(urlParts[5]);
            // int skierID = Integer.parseInt(urlParts[7]);

            res.getWriter().write("It works!");
        }
    }

    // Validate the URL
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    private boolean isUrlValid(String[] urlParts) {
        if (urlParts.length != EXPECTED_URL_PARTS) {
            return false;
        }
        if (!urlParts[2].equals("seasons") || !urlParts[4].equals("day") || !urlParts[6].equals("skier")) {
            return false;
        }
        if (!isNumeric(urlParts[1]) || !isNumeric(urlParts[3]) || !isNumeric(urlParts[5]) || !isNumeric(urlParts[7])) {
            return false;
        }
        int dayID = Integer.parseInt(urlParts[5]);
        return dayID >= 1 && dayID <= 365;
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");

        // Check if the URL is valid
        String urlPath = req.getPathInfo();
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Missing parameters");
            return;
        }
        String[] urlParts = urlPath.split("/");
        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            // Check if request body is valid
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                if (line.contains("time") || line.contains("liftID")) {
                    try {
                        Integer.parseInt(line.split(":")[1].split(",")[0].trim());
                    } catch (NumberFormatException e) {
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        res.getWriter().write("Invalid time");
                        return;
                    }
                }
            }
            String requestBody = sb.toString();



            if (!requestBody.contains("time") || !requestBody.contains("liftID")) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Invalid request body");
            } else {
                res.setStatus(HttpServletResponse.SC_CREATED);
                res.getWriter().write("It works!");
            }
        }
    }
}