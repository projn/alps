package com.sct.mongodbtest.dao.impl;

import com.sct.mongodbtest.dao.IMongoDbInfoDao;
import com.sct.mongodbtest.entity.EntityInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository("MongoDbInfoDao")
public class MongoDbInfoDaoImpl implements IMongoDbInfoDao {
    private static final Logger logger = Logger.getLogger(MongoDbInfoDaoImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate = null;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean saveObjInfo(EntityInfo obj) {
        if(obj==null) {
            logger.error("Invaild param!");
            return false;
        }
        try {
            mongoTemplate.insert(obj);
            return true;
        } catch (Exception e) {
            logger.error("Save obj info error, error info("+ e.getMessage() +")!");
        }
        return false;
    }

    @Override
    public EntityInfo getObjInfo(String id) {
        if(StringUtils.isEmpty(id)) {
            logger.error("Invaild param!");
            return null;
        }
        try {
            Query query = new Query(Criteria.where("id").is(id));
            EntityInfo entityInfo = mongoTemplate.findOne(query, EntityInfo.class);
            return entityInfo;
        } catch (Exception e) {
            logger.error("Get obj info error, error info("+ e.getMessage() +")!");
        }
        return null;
    }

    @Override
    public Long getObjInfoCount(String itemName, Object item) {
        if(StringUtils.isEmpty(itemName) || itemName ==null) {
            logger.error("Invaild param!");
            return null;
        }

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(itemName).is(item));
            return mongoTemplate.count(query, EntityInfo.class);
        } catch (Exception e) {
            logger.error("Get obj info count error, error info("+ e.getMessage() +")!");
        }
        return null;
    }

    @Override
    public List<EntityInfo> getObjInfoList(String itemName, Object item, int beginIndex, int size) {
        if(StringUtils.isEmpty(itemName) || beginIndex<=0 || size<0) {
            logger.error("Invaild param!");
            return null;
        }
        try {
            Query query = new Query();
            query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createDate")));
            Criteria criteria = Criteria.where(itemName).is(item);
            query.addCriteria(criteria);
            query.skip(beginIndex);
            query.limit(size);
            return mongoTemplate.find(query, EntityInfo.class);
        } catch (Exception e) {
            logger.error("Get obj info list error, error info("+ e.getMessage() +")!");
        }

        return null;
    }

    @Override
    public boolean updateObjItemInfo(String itemName, Object item, Object newItem) {
        if(StringUtils.isEmpty(itemName)) {
            logger.error("Invaild param!");
            return false;
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(itemName).is(item));
            Update update = new Update();
            update.set(itemName, newItem);
            mongoTemplate.updateFirst(query, update, EntityInfo.class);
            return true;
        } catch (Exception e) {
            logger.error("Update obj item info error, error info("+ e.getMessage() +")!");
        }
        return false;
    }

    @Override
    public boolean deleteObjInfo(String id) {
        if(StringUtils.isEmpty(id)) {
            logger.error("Invaild param!");
            return false;
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(id));
            mongoTemplate.remove(query, EntityInfo.class);
            return true;
        } catch (Exception e) {
            logger.error("Delete obj info error, error info("+ e.getMessage() +")!");
        }
        return false;
    }


}
