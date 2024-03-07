package com.project.dropbox;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@Document(collection="FileMetaData")
public class FileMetaData {

    public static final String FILE_PATH = "/Users/Neha.Kumari1/Downloads/dropbox/src/main/file_uploads";

    private String fileName;
    private LocalDateTime createdAt;
    private long size;
    private String fileType;

    public FileMetaData(String fileName, long size, String fileType){
        this.fileName=fileName;
        this.size=size;
        this.fileType=fileType;
    }
}
