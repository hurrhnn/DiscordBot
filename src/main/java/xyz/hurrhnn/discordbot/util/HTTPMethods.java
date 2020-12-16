package xyz.hurrhnn.discordbot.util;

import org.json.JSONObject;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HTTPMethods {
    public static String GET(String URL, @Nullable Map<String, String> headers) {
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(URL).openConnection();
            httpsURLConnection.setRequestMethod("GET");
            HTTPSConnector(headers, httpsURLConnection);

            return getResponseResultString(httpsURLConnection, "GET");
        } catch (IOException e) {
            errHandler(e, "GET");
        }
        return "ERROR";
    }

    public static String POST(String URL, @Nullable Map<String, String> headers, JSONObject data) {
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(URL).openConnection();
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoOutput(true);
            HTTPSConnector(headers, httpsURLConnection);

            if (data.toString().length() != 0) {
                OutputStream httpsOutputStream = httpsURLConnection.getOutputStream();
                PrintWriter writer = new PrintWriter(httpsOutputStream);
                writer.write(data.toString());
                writer.flush();
            }

            return getResponseResultString(httpsURLConnection, "POST");
        } catch (IOException e) {
            errHandler(e, "POST");
        }
        return "ERROR";
    }

    private static void HTTPSConnector(@Nullable Map<String, String> headers, HttpsURLConnection httpsURLConnection) throws IOException {
        if (headers != null)
            headers.forEach(httpsURLConnection::setRequestProperty);

        httpsURLConnection.connect();
    }

    private static String getResponseResultString(HttpsURLConnection httpsURLConnection, String method) throws IOException {
        InputStream httpsInputStream = httpsURLConnection.getInputStream();

        byte[] b = new byte[4096];
        StringBuilder responseStringBuilder = new StringBuilder();
        for (int n; (n = httpsInputStream.read(b)) != -1;) {
            responseStringBuilder.append(new String(b, 0, n));
        }

        InputStream httpsErrorStream = httpsURLConnection.getErrorStream();
        if (httpsErrorStream != null) {
            responseStringBuilder = new StringBuilder();
            b = new byte[4096];
            for (int n; (n = httpsErrorStream.read(b)) != -1;) {
                responseStringBuilder.append(new String(b, 0, n));
            }

            if (responseStringBuilder.length() != 0) {
                System.out.println("REQUEST " + method + " ERROR: " + responseStringBuilder.toString());
                return "ERROR, " + responseStringBuilder.toString();
            }
        }
        return responseStringBuilder.toString().length() == 0? "OK" : ("OK, " + responseStringBuilder.toString());
    }

    public static void errHandler(Exception e, String method) {
        PrintStream errPrintStream = null;
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try {
            errPrintStream = new PrintStream(err, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        e.printStackTrace(errPrintStream);
        System.out.println("REQUEST " + method + " ERROR: " + err.toString().split("\n")[0]);
    }
}
