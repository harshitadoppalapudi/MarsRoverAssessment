package com.nasarover.util;

import com.nasarover.exception.ImageDownloadException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility class for HTTP operations.
 */
public class HttpUtil {
    
    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_REDIRECTS = 5;
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000; // 30 seconds
    
    /**
     * Downloads a file from a URL and returns it as a byte array.
     * 
     * @param fileUrl the URL of the file to download
     * @return byte array containing the file data
     * @throws ImageDownloadException if the download fails
     */
    public static byte[] downloadFile(String fileUrl) throws ImageDownloadException {
        int redirectCount = 0;
        String currentUrl = fileUrl;
        
        while (redirectCount < MAX_REDIRECTS) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(currentUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setInstanceFollowRedirects(false);
                
                int status = connection.getResponseCode();
                
                // Handle redirects
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || 
                    status == HttpURLConnection.HTTP_MOVED_PERM || 
                    status == HttpURLConnection.HTTP_SEE_OTHER) {
                    
                    redirectCount++;
                    String newUrl = connection.getHeaderField("Location");
                    connection.disconnect();
                    
                    if (newUrl == null) {
                        throw new ImageDownloadException("Redirect with no location header");
                    }
                    
                    // Handle relative redirects
                    if (!newUrl.startsWith("http")) {
                        URL base = new URL(currentUrl);
                        URL next = new URL(base, newUrl);
                        newUrl = next.toExternalForm();
                    }
                    
                    currentUrl = newUrl;
                    continue;
                }
                
                // Handle successful response
                if (status == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = connection.getInputStream();
                         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        
                        return outputStream.toByteArray();
                    }
                } else {
                    throw new ImageDownloadException("Download failed with status code: " + status);
                }
            } catch (IOException e) {
                throw new ImageDownloadException("Download failed: " + e.getMessage(), e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        
        throw new ImageDownloadException("Too many redirects");
    }
}
