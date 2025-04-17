import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.MultipartConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import org.yinuo.hw4.bean.GetResponse;
import org.yinuo.hw4.bean.PostResponse;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB before written to disk
    maxFileSize = 1024 * 1024 * 10,      // 10MB max file size
    maxRequestSize = 1024 * 1024 * 50    // 50MB max request size
)
public class AlbumServlet extends HttpServlet {

  // Keep json file inside this folder for testing.
  // After deployment, change the file path.
  private final String JSON_FILE_PATH = "/usr/share/apache-tomcat-9.0.80/webapps/albums.json";

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    Gson gson = new Gson();

    // If there's no album ID in the url link, return response 400
    String urlPath = request.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      badRequestReminder(response, "Invalid request: Missing album ID");
      return;
    }

    String[] albumId = urlPath.split("/");
    if (albumId.length != 2 || !isNumeric(albumId[1])) {
      badRequestReminder(response, "Invalid request: Missing album ID");
      return;
    }

    // If the database file is not found, return response 404
    File file = new File(JSON_FILE_PATH);
    if (!file.exists()) {
      fileNotFoundReminder(response);
      return;
    }

    // Iterate over the database to see if the album exists
    String jsonData = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
    JsonArray albums = JsonParser.parseString(jsonData).getAsJsonArray();
    for (JsonElement element : albums) {
      JsonObject album = element.getAsJsonObject();

      // Return success message with album information
      if (album.get("albumID").getAsString().equals(albumId[1])) {
        response.setStatus(HttpServletResponse.SC_OK);
        String artist = album.get("artist").getAsString();
        String title = album.get("title").getAsString();
        int year = album.get("year").getAsInt();
        GetResponse jsonResponse = new GetResponse(artist, title, year);
        String responseString = gson.toJson(jsonResponse);
        PrintWriter out = response.getWriter();
        out.print(responseString);
        out.flush();
        return;
      }
    }

    // If album not found, return response 404
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    JsonObject errorResponse = new JsonObject();
    errorResponse.addProperty("msg", "Album not found");
    PrintWriter out = response.getWriter();
    out.print(new Gson().toJson(errorResponse));
    out.flush();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // If file not found, return response 404
    File file = new File(JSON_FILE_PATH);
    if (!file.exists()) {
      fileNotFoundReminder(response);
      return;
    }
    Gson gson = new Gson();

    // Process image information
    Part filePart = request.getPart("image");
    Part profilePart = request.getPart("profile");
    if (filePart == null && profilePart == null) {
      badRequestReminder(response, "Invalid request: Missing album image and profile");
      return;
    } else if (filePart == null) {
      badRequestReminder(response, "Invalid request: Missing album image file");
      return;
    } else if (profilePart == null) {
      badRequestReminder(response, "Invalid request: Missing album profile");
      return;
    }

    long imageSize = filePart.getSize();
    String imageType = filePart.getContentType();
    if (imageType.isEmpty() || imageSize == 0) {
      badRequestReminder(response, "Invalid request: Missing album image file");
      return;
    }
    String profileJson = new BufferedReader(new InputStreamReader(profilePart.getInputStream()))
        .lines().collect(Collectors.joining("\n"));
    JsonObject profile = gson.fromJson(profileJson, JsonObject.class);
    // If request does not contain necessary attributes, return error 400
    if (!profile.has("artist") || !profile.has("title") || !profile.has("year")) {
      badRequestReminder(response, "Invalid request: Missing required album attributes (artist, title, year)");
      return;
    }

    String artist = profile.get("artist").getAsString();
    String title = profile.get("title").getAsString();
    String year = profile.get("year").getAsString();

    String jsonData = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
    JsonArray albums = JsonParser.parseString(jsonData).getAsJsonArray();

    // If the posted album already exists, return error 400
    for (JsonElement element : albums) {
      JsonObject album = element.getAsJsonObject();
      if (album.get("artist").getAsString().equalsIgnoreCase(artist) &&
          album.get("title").getAsString().equalsIgnoreCase(title) &&
          album.get("year").getAsString().equals(year)) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("msg", "Album already exists");
        PrintWriter out = response.getWriter();
        out.print(new Gson().toJson(errorResponse));
        out.flush();
        return;
      }
    }

    // If not, add the new album
    JsonObject newAlbum = new JsonObject();
    newAlbum.addProperty("albumID", albums.size() + 1);
    newAlbum.addProperty("artist", artist);
    newAlbum.addProperty("title", title);
    newAlbum.addProperty("year", year);

    newAlbum.addProperty("imageSize", filePart.getSize());
    newAlbum.addProperty("imageType", filePart.getContentType());
    albums.add(newAlbum);
    try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
      gson.toJson(albums, writer);
    }

    // Send success message
    response.setStatus(HttpServletResponse.SC_OK);
    PostResponse jsonResponse = new PostResponse(albums.size(), imageSize);
    String responseString = gson.toJson(jsonResponse);
    PrintWriter out = response.getWriter();
    out.print(responseString);
    out.flush();
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

  private static void badRequestReminder(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    JsonObject errorResponse = new JsonObject();
    errorResponse.addProperty("msg", message);
    PrintWriter out = response.getWriter();
    out.print(new Gson().toJson(errorResponse));
    out.flush();
  }

  private static void fileNotFoundReminder(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    JsonObject errorResponse = new JsonObject();
    errorResponse.addProperty("msg", "File not found");
    PrintWriter out = response.getWriter();
    out.print(new Gson().toJson(errorResponse));
    out.flush();
  }
}