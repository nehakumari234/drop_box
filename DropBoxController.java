package com.project.dropbox.controller;

import com.project.dropbox.FileMetaData;
import com.project.dropbox.service.DropBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("dropbox/service")
public class DropBoxController {

    @Autowired
    private DropBoxService dropBoxService;

    @RequestMapping(value="files/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            String fileId = UUID.randomUUID().toString();
            String filePath = FileMetaData.FILE_PATH + fileId + "_" + fileName;

            // Save the file to local storage
            byte[] bytes = file.getBytes();
            Path path = Paths.get(filePath);
            Files.write(path, bytes);


            //save the file metadata to db storage
            FileMetaData fileMetaData = new FileMetaData(fileName, file.getSize(),file.getContentType());
            dropBoxService.saveMetaDataToDB(fileMetaData);


            // Return the unique file identifier
            fileMetaData.setCreatedAt(LocalDateTime.now());
            return ResponseEntity.ok().body(fileId);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @RequestMapping(value="files/get/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable String fileId) {
        FileMetaData fileMetaData = dropBoxService.fetchFileMetaData(fileId);
        String filePath = FileMetaData.FILE_PATH + fileId + "_" + fileMetaData.getFileName();// Adjust this path according to your file storage

        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            return ResponseEntity.ok().contentLength(fileBytes.length).body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value="files/update/{fileId}", method = RequestMethod.PUT)
    public String updateFile (@PathVariable String fileId,
                             @RequestParam(value = "createdAt", required = false) LocalDateTime date,
                             @RequestParam(value = "fileName", required = false) String fileName) {


        try {
            FileMetaData fileMetaData = dropBoxService.fetchFileMetaData(fileId);
            fileMetaData.setCreatedAt(date);
            fileMetaData.setFileName(fileName);
            dropBoxService.saveMetaDataToDB(fileMetaData);
            return "File updated successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "File updation failed due to server error";
    }

    @RequestMapping(value="files/delete/{fileId}", method = RequestMethod.DELETE)
    public String deleteFile(@PathVariable String fileId) throws IOException {
        try{
            dropBoxService.removeMetaData(fileId);
            return "File deleted successfully";
        }
        catch (Exception e){
            return "File deletion failed";
        }
    }

    @RequestMapping(value="files/get/all", method = RequestMethod.GET)
    public List<FileMetaData> listFiles() {
        List<FileMetaData> list = dropBoxService.fetchAllFileMetaData();
        return list;
    }

}
