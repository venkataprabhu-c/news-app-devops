package com.vikas.news;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.json.*;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.Properties;

public class NewsServlet extends HttpServlet {

    private String apiKey;
    private String baseUrl;

    @Override
    public void init() throws ServletException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) throw new ServletException("config.properties missing");
            Properties p = new Properties();
            p.load(in);

            apiKey = p.getProperty("NEWS_API_KEY");
            baseUrl = p.getProperty("NEWS_URL");

            if (apiKey == null || apiKey.isEmpty()) throw new ServletException("API key missing");
            if (baseUrl == null || baseUrl.isEmpty())
                baseUrl = "https://newsapi.org/v2/everything?";
        } catch (IOException e) {
            throw new ServletException("Failed to load config", e);
        }
    }

    private JSONObject callNewsAPI(String query, int page, int pageSize) throws IOException {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        String urlStr = baseUrl
                + "q=" + URLEncoder.encode(query, "UTF-8")
                + "&from=" + weekAgo
                + "&to=" + today
                + "&sortBy=publishedAt"
                + "&page=" + page
                + "&pageSize=" + pageSize
                + "&apiKey=" + apiKey;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        return new JSONObject(sb.toString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String path = req.getServletPath();

        if ("/news-data".equals(path)) {
            handleAjax(req, resp);
            return;
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private void handleAjax(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("q");
        if (query == null || query.isEmpty()) query = "india";

        int page = Integer.parseInt(req.getParameter("page") == null ? "1" : req.getParameter("page"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize") == null ? "10" : req.getParameter("pageSize"));

        try {
            JSONObject apiResponse = callNewsAPI(query, page, pageSize);

            JSONObject out = new JSONObject();
            out.put("status", apiResponse.optString("status"));
            out.put("totalResults", apiResponse.optInt("totalResults"));
            out.put("articles", apiResponse.optJSONArray("articles"));

            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(out.toString());

        } catch (Exception e) {
            JSONObject out = new JSONObject();
            out.put("status", "error");
            out.put("message", e.getMessage());
            out.put("articles", new JSONArray());

            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(out.toString());
        }
    }
}
