/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.renren.config.MongoManager;
import io.renren.dao.GeneratorDao;
import io.renren.dao.MongoDBGeneratorDao;
import io.renren.factory.MongoDBCollectionFactory;
import io.renren.utils.GenUtils;
import io.renren.utils.GenUtils2;
import io.renren.utils.PageUtils;
import io.renren.utils.Query;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysGeneratorService {

    @Autowired
    private GeneratorDao generatorDao;


    public PageUtils queryList(Query query) {
        Page<?> page = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String, Object>> list = generatorDao.queryList(query);
        int total = (int) page.getTotal();
        if (generatorDao instanceof MongoDBGeneratorDao) {
            total = MongoDBCollectionFactory.getCollectionTotal(query);
        }
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    public Map<String, String> queryTable(String tableName) {
        return generatorDao.queryTable(tableName);
    }

    public List<Map<String, String>> queryColumns(String tableName) {
        return generatorDao.queryColumns(tableName);
    }


    public byte[] generatorCode(String[] tableNames,
                                boolean isAuto,
                                boolean frontCheck,
                                boolean sqlAuto) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GenUtils.generatorCode(table, isAuto, frontCheck, sqlAuto, columns, zip);
        }

        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }


    public byte[] generatorCode2(String[] tableNames,
                                 String key,
                                 String first,
                                 boolean isAuto,
                                 boolean frontCheck,
                                 boolean sqlAuto) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        List subList = new ArrayList<Map<String, Object>>();
        for (String tableName : tableNames) {
            if (!tableName.equals(first)) {
                //查询表信息
                Map<String, String> table = queryTable(tableName);
                //查询列信息
                List<Map<String, String>> columns = queryColumns(tableName);
                //生成代码
                Map<String, Object> map = GenUtils2.generatorCode(table, columns, zip, key, first, isAuto, frontCheck, sqlAuto, null);
                subList.add(map);
            }
        }

        //查询表信息
        Map<String, String> table = queryTable(first);
        //查询列信息
        List<Map<String, String>> columns = queryColumns(first);
        //生成代码
        GenUtils2.generatorCode(table, columns, zip, key, first, isAuto, frontCheck, sqlAuto, subList);

        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}
