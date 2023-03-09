package io.renren.utils;

import io.renren.config.MongoManager;
import io.renren.controller.SysGeneratorController;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器   工具类
 *
 * @author chengpilu
 */
public class GenUtilsCommon {


    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String[] tablePrefixArray) {
        if (null != tablePrefixArray && tablePrefixArray.length > 0) {
            for (String tablePrefix : tablePrefixArray) {
                if (tableName.startsWith(tablePrefix)) {
                    tableName = tableName.replaceFirst(tablePrefix, "");
                }
            }
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            Configuration config = new PropertiesConfiguration("generator.properties");
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(SysGeneratorController.packageName)) {
                config.setProperty("package", SysGeneratorController.packageName);
            }
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(SysGeneratorController.moduleName)) {
                config.setProperty("moduleName", SysGeneratorController.moduleName);
            }
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(SysGeneratorController.tablePrefix)) {
                config.setProperty("tablePrefix", SysGeneratorController.tablePrefix);
            }
            return config;
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }

    public static List<String> getTemplates(boolean isComplex, boolean isFirst) {
        List<String> templates = new ArrayList<String>();
        templates.add("template/Command.java.vm");
        templates.add("template/PageQuery.java.vm");
        templates.add("template/Controller.java.vm");
        templates.add("template/Convertor.java.vm");
        templates.add("template/DO.java.vm");
        templates.add("template/Dto.java.vm");
        templates.add("template/DtoConvertor.java.vm");
        templates.add("template/Entity.java.vm");
        templates.add("template/EntityId.java.vm");
        templates.add("template/Factory.java.vm");
        templates.add("template/Mapper.java.vm");
        templates.add("template/Mapper.xml.vm");
        templates.add("template/Repository.java.vm");
        templates.add("template/RepositoryImpl.java.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
//        templates.add("template/ServiceFacade.java.vm");
//        templates.add("template/ServiceFacadeImpl.java.vm");
        templates.add("template/ApplicationService.java.vm");
        templates.add("template/ApplicationServiceImpl.java.vm");
        templates.add("template/QueryService.java.vm");
        templates.add("template/QueryServiceImpl.java.vm");
        templates.add("template/UpdateSpecification.java.vm");
        templates.add("template/entity-select.vue.vm");
        if (isComplex) {
            if (isFirst) {
                templates.add("template/ServiceFacadeAll.java.vm");
                templates.add("template/indexAll.vue.vm");
                templates.add("template/add-or-update-all.vue.vm");
                //需要子表的数据
                templates.add("template/index-update.vue.vm");
                templates.add("template/CommandAll.java.vm");
                templates.add("template/ServiceFacadeImplAll.java.vm");
                templates.add("template/menu-all.sql.vm");
                templates.add("template/user-select.vue.vm");
            } else {
                templates.add("template/index-sub.vue.vm");
            }

        } else {
            templates.add("template/menu.sql.vm");
            templates.add("template/index.vue.vm");
            templates.add("template/add-or-update.vue.vm");
        }

        if (MongoManager.isMongo()) {
            // mongo不需要mapper、sql   实体类需要替换
            templates.remove(0);
            templates.remove(1);
            templates.remove(2);
            templates.add("templatebak/MongoEntity.java.vm");
        }
        return templates;
    }


    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName, String fatherName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }
        //com.ennew.boot.interfaces.sys.web.command;
        //com.enn.srm.supplier.client.dto.command
        if (template.contains("/Command.java.vm")) {
            return packagePath
                    + "client" + File.separator
                    //+ "sys" + File.separator
                    + "dto" + File.separator
                    + "command" + File.separator
                    + className + "Command.java";
        }
        //com.enn.srm.supplier.client.dto.query
        if (template.contains("/PageQuery.java.vm")) {
            return packagePath
                + "client" + File.separator
                //+ "sys" + File.separator
                + "dto" + File.separator
                + "query" + File.separator
                + className + "PageQuery.java";
        }
        //com.ennew.boot.interfaces.sys.web;
        if (template.contains("Controller.java.vm")) {
            return packagePath
                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "web" + File.separator
                    + className + "Controller.java";
        }
        //com.ennew.boot.infrastructure.persistence.mybatis.convertor
        if (template.contains("/Convertor.java.vm")) {
            return packagePath
                    + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "convertor" + File.separator
                    + className + "Convertor.java";
        }
        //com.ennew.boot.infrastructure.persistence.mybatis.entity
        if (template.contains("DO.java.vm")) {
            return packagePath
                    + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "entity" + File.separator
                    + className + "Do.java";
        }
        //com.ennew.boot.interfaces.sys.facade.dto
        if (template.contains("Dto.java.vm")) {
            return packagePath
//                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "client" + File.separator
                    + "dto" + File.separator
                    + className + "Dto.java";
        }
        //com.ennew.boot.interfaces.sys.facade.convertor
        //com.enn.srm.supplier.application.convertor.ProductCategoryDtoConvertor
        if (template.contains("/DtoConvertor.java.vm")) {
            return packagePath
//                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "application" + File.separator
                    + "convertor" + File.separator
                    + className + "DtoConvertor.java";
        }
        //com.ennew.boot.domain.model.user
        if (template.contains("Entity.java.vm")) {
            return packagePath
                    + "domain" + File.separator
                    + "model" + File.separator
                    + "entity" + File.separator
                    + className + ".java";
        }
        //com.ennew.boot.domain.model.user.types
        if (template.contains("EntityId.java.vm")) {
            return packagePath
                    + "domain" + File.separator
                    + "model" + File.separator
                    + "types" + File.separator
                    + className + "Id.java";
        }
        //com.ennew.boot.domain.factory
        if (template.contains("Factory.java.vm")) {
            return packagePath
                    + "domain" + File.separator
                    + "factory" + File.separator
                    + className + "Factory.java";
        }
        //com.ennew.boot.infrastructure.persistence.mybatis.mapper;
        if (template.contains("Mapper.java.vm")) {
            return packagePath
                    + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "mapper" + File.separator
                    + className + "Mapper.java";
        }
        //com.ennew.boot.infrastructure.persistence.mybatis.mapper.xml;
        if (template.contains("Mapper.xml.vm")) {
            return packagePath
                    + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "mapper" + File.separator
                    + "xml" + File.separator
                    + className + "Mapper.xml";
        }
        //com.ennew.boot.domain.repository;
        if (template.contains("Repository.java.vm")) {
            return packagePath
                    + "domain" + File.separator
                    + "repository" + File.separator
                    + className + "Repository.java";
        }
        //com.ennew.boot.infrastructure.persistence.mybatis.repository.impl
        if (template.contains("RepositoryImpl.java.vm")) {
            return packagePath
                    + "infrastructure" + File.separator
                    + "persistence" + File.separator
                    + "mybatis" + File.separator
                    + "repository" + File.separator
                    + "impl" + File.separator
                    + className + "RepositoryImpl.java";
        }

        //com.ennew.boot.interfaces.sys.facade
        if (template.contains("ServiceFacade.java.vm")) {
            return packagePath
                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "facade" + File.separator
                    + className + "ServiceFacade.java";
        }
        //com.ennew.boot.interfaces.sys.facade.impl
        if (template.contains("ServiceFacadeImpl.java.vm")) {
            return packagePath
                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "facade" + File.separator
                    + "impl" + File.separator
                    + className + "ServiceFacadeImpl.java";
        }
        //com.ennew.boot.application
        if (template.contains("/Service.java.vm")) {
            return packagePath
                + "application" + File.separator
                + className + "Service.java";
        }
        //com.ennew.boot.application.impl
        if (template.contains("/ServiceImpl.java.vm")) {
            return packagePath
                    + "application" + File.separator
                    + "impl" + File.separator
                    + className + "ServiceImpl.java";
        }

        //com.ennew.boot.application.service
        if (template.contains("ApplicationService.java.vm")) {
            return packagePath
                + "application" + File.separator
                + "service" + File.separator
                + className + "ApplicationService.java";
        }

        //com.ennew.boot.application.service.impl
        if (template.contains("ApplicationServiceImpl.java.vm")) {
            return packagePath
                + "application" + File.separator
                + "service" + File.separator
                + "impl" + File.separator
                + className + "ApplicationServiceImpl.java";
        }

        //com.ennew.boot.application.service
        if (template.contains("/QueryService.java.vm")) {
            return packagePath
                + "application" + File.separator
                + "service" + File.separator
                + className + "QueryService.java";
        }

        //com.ennew.boot.application.service.impl
        if (template.contains("/QueryServiceImpl.java.vm")) {
            return packagePath
                + "application" + File.separator
                + "service" + File.separator
                + "impl" + File.separator
                + className + "QueryServiceImpl.java";
        }

        //com.ennew.boot.domain.specification
        if (template.contains("UpdateSpecification.java.vm")) {
            return packagePath
                    + "domain" + File.separator
                    + "specification" + File.separator
                    + className + "UpdateSpecification.java";
        }

        if (template.contains("menu.sql.vm")) {
            return className.toLowerCase() + "_menu.sql";
        }

        if (template.contains("menu-all.sql.vm")) {
            return className.toLowerCase() + "_menu_all.sql";
        }

        if (template.contains("index.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + ".vue";
        }

        if (template.contains("add-or-update.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + "-add-or-update.vue";
        }

        //com.ennew.boot.interfaces.sys.facade
        if (template.contains("ServiceFacadeAll.java.vm")) {
            return packagePath
                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "facade" + File.separator
                    + className + "AllServiceFacade.java";
        }

        if (template.contains("template/indexAll.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + ".vue";
        }

        if (template.contains("add-or-update-all.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + "-add-or-update-all.vue";
        }

        if (template.contains("index-update.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + "-update.vue";
        }

        if (template.contains("index-sub.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + fatherName.toLowerCase() + "-" + className.toLowerCase() + ".vue";
        }

        //com.ennew.boot.interfaces.sys.web.command;
        if (template.contains("/CommandAll.java.vm")) {
            return packagePath
                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "web" + File.separator
                    + "command" + File.separator
                    + className + "AllCommand.java";
        }

        //com.ennew.boot.interfaces.sys.facade.impl
        if (template.contains("ServiceFacadeImplAll.java.vm")) {
            return packagePath
                    + "interfaces" + File.separator
                    //+ "sys" + File.separator
                    + "facade" + File.separator
                    + "impl" + File.separator
                    + className + "AllServiceFacadeImpl.java";
        }

        if (template.contains("user-select.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + "user" + File.separator + "user-select.vue";
        }

        if (template.contains("entity-select.vue.vm")) {
            return "views" + File.separator
                    + "modules" + File.separator
                    + moduleName + File.separator
                    + className.toLowerCase() + "-select.vue";
        }
        return null;
    }

    /**
     * 自动部署文件
     */
    public static void genetatorAuto(String fileName, StringWriter sw, boolean sqlAuto) throws Exception {
        String absolutePath = null;
        if (fileName.endsWith("vue")) {
            absolutePath = SysGeneratorController.frontUrl + "/src/" + fileName;
        }
        if (fileName.endsWith("java") || fileName.endsWith("xml")) {
//            ### DDD各模块生成的代码的base路径
//            SysGeneratorController.adminUrl = /Users/lichaoqiang/xinao/dev/tasks/scm
//            interfacesPath=/Users/lichaoqiang/xinao/dev/tasks/scm/scm_interfaces
//            applicationPath=/Users/lichaoqiang/xinao/dev/tasks/scm/scm_application
//            domainPath=/Users/lichaoqiang/xinao/dev/tasks/scm/scm_domain
//            infrastructurePath=/Users/lichaoqiang/xinao/dev/tasks/scm/scm_infrastructure
            if (fileName.indexOf("/interfaces/") >= 0 || fileName.indexOf("\\interfaces\\") >= 0 ) {
                absolutePath = SysGeneratorController.interfacesPath + "/src/" + fileName;
            } else if (fileName.indexOf("/application/") >= 0 || fileName.indexOf("\\application\\") >= 0  ) {
                absolutePath = SysGeneratorController.applicationPath + "/src/" + fileName;
            } else if (fileName.indexOf("/domain/") >= 0 || fileName.indexOf("\\domain\\") >= 0 ) {
                absolutePath = SysGeneratorController.domainPath + "/src/" + fileName;
            } else if (fileName.indexOf("/infrastructure/") >= 0 || fileName.indexOf("\\infrastructure\\") >= 0 ) {
                absolutePath = SysGeneratorController.infrastructurePath + "/src/" + fileName;
            }  else if (fileName.indexOf("/client/") >= 0 || fileName.indexOf("\\client\\") >= 0 ) {
                absolutePath = SysGeneratorController.clientPath + "/src/" + fileName;
            } else {
                absolutePath = SysGeneratorController.adminUrl + "/src/" + fileName;
            }

        }
        if (fileName.endsWith("sql")) {
            absolutePath = SysGeneratorController.adminUrl + "/sql/" + fileName;
        }

        File file = new File(absolutePath);
        // 1、代码清理
        if (file.exists()) {
            file.delete();
        }

        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            // 2、创建父目录
            parentFile.mkdirs();
        }
        file.createNewFile();

        // 3、代码生成
        OutputStream out = new FileOutputStream(file);
        IOUtils.write(sw.toString(), out, "UTF-8");
        IOUtils.closeQuietly(sw);
        IOUtils.closeQuietly(out);

        if (sqlAuto && fileName.endsWith("sql")) {
            List<String> temp = new ArrayList<>();
            temp.add(absolutePath);
            executeSqlFile(temp);
        }
    }

    /**
     * 执行sql文件
     */
    private static void executeSqlFile(List<String> absolutePathList) {
        DataSource dataSource = SpringContextHolder.getBean(DataSource.class);
        try {
            Connection connection = dataSource.getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            Resources.setCharset(Charset.forName("UTF-8")); //设置字符集,不然中文乱码插入错误
            // 4、执行sql
            for (String absolutePath : absolutePathList) {
                if (absolutePath.endsWith(".sql")) {
                    File file = new File(absolutePath);
                    if (file.exists()) {
                        FileReader reader = new FileReader(file);
                        runner.runScript(reader);
                        IOUtils.closeQuietly(reader);
                    }
                }
            }
            runner.closeConnection();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
