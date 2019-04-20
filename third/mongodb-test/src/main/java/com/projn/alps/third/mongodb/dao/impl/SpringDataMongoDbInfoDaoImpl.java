package com.projn.alps.third.mongodb.dao.impl;

import com.projn.alps.third.mongodb.dao.IMongoDbInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository("SpringDataMongoDbInfoDao")
public class SpringDataMongoDbInfoDaoImpl implements IMongoDbInfoDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDataMongoDbInfoDaoImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate = null;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <T>boolean saveObjInfo(T obj) {
        if(obj==null) {
            LOGGER.error("Invaild param!");
            return false;
        }
        try {
            mongoTemplate.insert(obj);
            return true;
        } catch (Exception e) {
            LOGGER.error("Save obj info error, error info("+ e.getMessage() +")!");
        }
        return false;
    }

    @Override
    public <T> T getObjInfo(String id, Class<T> cls) {
        if(StringUtils.isEmpty(id)) {
            LOGGER.error("Invaild param!");
            return null;
        }
        try {
            Query query = new Query(Criteria.where("id").is(id));
            T entityInfo = mongoTemplate.findOne(query, cls);
            return entityInfo;
        } catch (Exception e) {
            LOGGER.error("Get obj info error, error info("+ e.getMessage() +")!");
        }
        return null;
    }

    @Override
    public <T> Long getObjInfoCount(String itemName, Object item, Class<T> cls) {
        if(StringUtils.isEmpty(itemName) || itemName ==null) {
            LOGGER.error("Invaild param!");
            return null;
        }

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(itemName).is(item));
            return mongoTemplate.count(query, cls);
        } catch (Exception e) {
            LOGGER.error("Get obj info count error, error info("+ e.getMessage() +")!");
        }
        return null;
    }

    @Override
    public <T> List<T> getObjInfoList(String itemName, Object item, int beginIndex, int size, Class<T> cls) {
        if(StringUtils.isEmpty(itemName) || beginIndex<=0 || size<0) {
            LOGGER.error("Invaild param!");
            return null;
        }
        try {
            Query query = new Query();
            Criteria criteria = Criteria.where(itemName).is(item);
            query.addCriteria(criteria);
            query.skip(beginIndex);
            query.limit(size);
            return mongoTemplate.find(query, cls);
        } catch (Exception e) {
            LOGGER.error("Get obj info list error, error info("+ e.getMessage() +")!");
        }

        return null;
    }

    @Override
    public <T> boolean updateObjItemInfo(String id, String itemName, Object newItem, Class<T> cls) {
        if(StringUtils.isEmpty(id)) {
            LOGGER.error("Invaild param!");
            return false;
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(id));
            Update update = new Update();
            update.set(itemName, newItem);
            mongoTemplate.updateFirst(query, update, cls);
            return true;
        } catch (Exception e) {
            LOGGER.error("Update obj item info error, error info("+ e.getMessage() +")!");
        }
        return false;
    }

    @Override
    public <T> boolean deleteObjInfo(String id, Class<T> cls) {
        if(StringUtils.isEmpty(id)) {
            LOGGER.error("Invaild param!");
            return false;
        }
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(id));
            mongoTemplate.remove(query, cls);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete obj info error, error info("+ e.getMessage() +")!");
        }
        return false;
    }


}
