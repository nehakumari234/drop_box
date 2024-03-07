package com.project.dropbox.service;

import com.mongodb.WriteResult;
import com.mongodb.client.result.DeleteResult;
import com.project.dropbox.FileMetaData;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DropBoxService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Boolean saveMetaDataToDB(FileMetaData fileMetaData){
        mongoTemplate.save(fileMetaData, "fileMetaData");
        return true;
    }

    public FileMetaData fetchFileMetaData(String fileId){
        Query query = new Query();
        query.addCriteria(Criteria.where("fileId").is(fileId));
        return mongoTemplate.findOne(query, FileMetaData.class);
    }

    public WriteResult removeMetaData(String fileId){
        Query query = new Query();
        query.addCriteria(Criteria.where("fileId").is(fileId));
        return mongoTemplate.remove(query, FileMetaData.class);
    }

    public List<FileMetaData> fetchAllFileMetaData(){
        return mongoTemplate.find(new Query(), FileMetaData.class);
    }
}
