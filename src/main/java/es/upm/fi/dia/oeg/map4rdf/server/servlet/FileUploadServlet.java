/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.servlet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Jonathan Gonzalez (jonathan@jonbaraq.eu)
 */
public class FileUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "/tmp/uploads/";
    private static final String HREF_SYNTAX = "<a href=\"";
    private static final String SHAPE_FILE_CONFIGURATION_FILE =
            "shpoptions.properties";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {  
        // Process only multipart requests
        if (ServletFileUpload.isMultipartContent(req)) {
            processFileUpload(req, resp);
        } else {
            String paramValue = req.getParameter("urlShapeFile");
            if (paramValue != null && !paramValue.isEmpty()) {
                processUrl(paramValue, resp);
            } else {
                resp.getWriter().print("URL introduced is empty.");
                resp.flushBuffer();
            }
        }
    }
    
    private void processUrl(String url, HttpServletResponse resp)
            throws ServletException, IOException {
        String uploadDirectory = createUploadDirectory();
        String configurationPath = "";
        boolean configurationFound = false;
        
        Map<String, String> filesToDownloadMap = getFilesToDownload(url);
        
        File directory = new File(
                uploadDirectory + "/" + getDirectoryName(filesToDownloadMap));
        if (directory == null) {
             throw new IOException("The files cannot be downloaded."
                     + " Files on the repository don't have the right naming.");
        }
        
        // Create directories needed to upload the files.
        directory.mkdirs();
        
        for (String key : filesToDownloadMap.keySet()) {
            if (filesToDownloadMap.get(key).endsWith(SHAPE_FILE_CONFIGURATION_FILE)) {
                configurationFound = true;
                configurationPath = uploadDirectory + "/"
                        + filesToDownloadMap.get(key);
                BufferedWriter bw = new BufferedWriter(
                        new FileWriter(new File(configurationPath)));
                // Download the file from the repository.
                String line;
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new URL(key).openStream()));
        
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            } else {
                try {
                    // Download the shape files.
                    URL u = new URL(key);
                    URLConnection uc = u.openConnection();
                    int contentLength = uc.getContentLength();

                    InputStream raw = uc.getInputStream();
                    InputStream in = new BufferedInputStream(raw);
                    byte[] data = new byte[contentLength];
                    int bytesRead;
                    int offset = 0;
                    while (offset < contentLength) {
                        bytesRead = in.read(data, offset, data.length - offset);
                        if (bytesRead == -1) {
                            break;
                        }
                        offset += bytesRead;
                    }
                    in.close();

                    if (offset != contentLength) {
                        throw new IOException(
                                "Only read " + offset + " bytes; Expected "
                                + contentLength + " bytes");
                    }

                    FileOutputStream out = new FileOutputStream(
                            directory.getAbsolutePath() + "/"
                            + filesToDownloadMap.get(key));
                    out.write(data);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    // Nothing to be done.
                }
            }                
        }

        resp.setStatus(HttpServletResponse.SC_CREATED);
        if (!configurationFound) {
            resp.getWriter().print("Configuration file not found.");
            resp.flushBuffer();
            return;
        }
        
        resp.getWriter().print("The files were created successfully: "
                + configurationPath);
        resp.flushBuffer();
    }
    
    /**
     * Method that expects a URL with all the files to be downloaded from an
     * Apache server. 
     */
    private void processFileUpload(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Create a factory for disk-based file items
        FileItemFactory factory = new DiskFileItemFactory();

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request.
        try { 
            List<FileItem> items = upload.parseRequest(req);          
            for (FileItem fileItem : items) {
                // Process only file upload.
                if (fileItem.isFormField()) {
                    continue;
                }

                String fileName = fileItem.getName();
                // Get only the file name not whole path.
                if (fileName != null) {
                    fileName = FilenameUtils.getName(fileName);
                    if (!fileName.endsWith(".zip")) {
                        resp.getWriter().print("The file uploaded should be "
                                + "a zip file with all the files needed to "
                                + "build the model.");
                        resp.flushBuffer();
                        return;
                    }
                }

                String uploadDirectory = createUploadDirectory();
                File uploadedFile = new File(uploadDirectory, fileName);
                if (uploadedFile.createNewFile()) {
                    fileItem.write(uploadedFile);
                    // Unzip the file.
                    String configurationFile =
                            unzipFile(uploadDirectory, fileName);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    if (configurationFile.isEmpty()) {
                        resp.getWriter().print(
                                "Configuration file not found.");
                        resp.flushBuffer();
                        return;
                    }
                    resp.getWriter().print(
                            "The files were created successfully: "
                            + configurationFile);
                    resp.flushBuffer();
                } else {
                    throw new IOException(
                            "The file already exists in repository.");
                }
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while creating the file : "
                    + e.getMessage());
            resp.flushBuffer();
        }
   
    }
    
    private String unzipFile(String uploadDirectory, String fileName) {
        Enumeration entries;
        ZipFile zipFile;
        String configurationFile = "";

        try {
            zipFile = new ZipFile(uploadDirectory + "/" + fileName);
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String filePath = uploadDirectory + "/" + entry.getName();
                if (entry.isDirectory()) {
                    (new File(filePath)).mkdir();
                    continue;
                }
                copyInputStream(zipFile.getInputStream(entry), filePath);
                if (entry.getName().endsWith(SHAPE_FILE_CONFIGURATION_FILE)) {
                    configurationFile = filePath;
                }
            }
            zipFile.close();
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
        }
        
        return configurationFile;
    }
    
    private void copyInputStream(InputStream in, String path)
            throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        
        byte[] b = new byte[1024];
        int numberBytes = 0;
        
        while((numberBytes = in.read(b)) != -1) {
            out.write(b, 0, numberBytes);
        }
        
        in.close();
        out.flush();
        out.close();
    }
    
    private String createUploadDirectory() {
        File file = null;
        do {
            Random random = new Random();
            int nextRandom = random.nextInt();
            if (nextRandom < 0) { 
                nextRandom = nextRandom * -1;
            }
            file = new File(UPLOAD_DIRECTORY + "/" + nextRandom);
        } while (file.exists());
        file.mkdirs();
        
        return file.getAbsolutePath();
    }
    
    private Map<String, String> getFilesToDownload(String url)
            throws MalformedURLException, IOException {
        Map<String, String> filesToDownloadMap = new HashMap<String, String>();
        boolean startToAddFiles = false;
            
        // Generate the index.html file that contains all the files to be
        // downloaded that form the shapefile model.
        String line;
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new URL(url).openStream()));
        
        while ((line = br.readLine()) != null) {
            if (line.contains(HREF_SYNTAX)) {
                line = line.substring(line.indexOf(HREF_SYNTAX)).split("\"")[1];
                String link = getLinkFromHref(new URL(url), line);
                String filename = getFilenameFromLink(link);
                // If the link ends with / we don't save the link as it will redirect
                // to the parent directory.
                if (link.endsWith("/")) {
                    continue;
                }
                filesToDownloadMap.put(link, filename);
            }
        }
        
        return filesToDownloadMap;       
    }
    
    private String getLinkFromHref(URL url, String line) throws MalformedURLException {
        String link = line;
        if (!link.startsWith("http://")) {
            URL newUrl = new URL(url, link);
            link = newUrl.toString();
        }
        return link;
    }
    
    private String getFilenameFromLink(String link) {
        String[] fragments = link.split("/");
        return fragments[fragments.length - 1];
    }
    
    private String getDirectoryName(Map<String, String> map) {
        for (String key : map.keySet()) {
            if (!map.get(key).equals(SHAPE_FILE_CONFIGURATION_FILE)) {
                // Return the name without the extension of the file.
                return map.get(key).substring(0, map.get(key).length() - 4);
            }
        }
        return null;
    }
    
}