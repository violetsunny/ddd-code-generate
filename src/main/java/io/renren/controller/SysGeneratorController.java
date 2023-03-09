/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import com.mysql.cj.util.StringUtils;
import io.renren.service.SysGeneratorService;
import io.renren.utils.GenUtilsCommon;
import io.renren.utils.PageUtils;
import io.renren.utils.Query;
import io.renren.utils.R;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {
    @Autowired
    private SysGeneratorService sysGeneratorService;

    public static String packageName = null;
    public static String moduleName = null;
    public static String tablePrefix = null;
    public static String adminUrl = null;
    public static String frontUrl = null;
    public static String interfacesPath = null;
    public static String applicationPath = null;
    public static String domainPath = null;
    public static String infrastructurePath = null;
    public static String clientPath = null;

    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils pageUtil = sysGeneratorService.queryList(new Query(params));

        return R.ok().put("page", pageUtil);
    }

    /**
     * 生成代码
     */
    @RequestMapping("/code")
    public void code(String tables,
                     String packageName,
                     String moduleName,
                     String tablePrefix,
                     boolean isAuto,
                     String interfacesPath,
                     String applicationPath,
                     String domainPath,
                     String infrastructurePath,
                     String clientPath,
                     String adminUrl,
                     String frontUrl,
                     boolean frontCheck,
                     boolean sqlAuto,
                     HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix,  interfacesPath, applicationPath, domainPath, infrastructurePath, clientPath, adminUrl, frontUrl);
//        System.out.println("tables"+tables);
        //tables="tb_supplier_aduit,tb_supplier_base,tb_supplier_bank,tb_supplier_certification,tb_supplier_validate,tb_supplier_purchaser,tb_supplier_scope";
        byte[] data = sysGeneratorService.generatorCode(tables.split(","), isAuto, frontCheck, sqlAuto);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    /**
     * 生成代码
     */
    @RequestMapping("/code2")
    public void code2(String tables,
                      String key,
                      String first,
                      String packageName,
                      String moduleName,
                      String tablePrefix,
                      boolean isAuto,
                      String interfacesPath,
                      String applicationPath,
                      String domainPath,
                      String infrastructurePath,
                      String clientPath,
                      String adminUrl,
                      String frontUrl,
                      boolean frontCheck,
                      boolean sqlAuto,
                      HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix,  interfacesPath, applicationPath, domainPath, infrastructurePath, clientPath, adminUrl, frontUrl);
        System.out.println("tables"+tables);
       // tables="tb_supplier_aduit,tb_supplier_base,tb_supplier_bank,tb_supplier_certification,tb_supplier_validate,tb_supplier_purchaser,tb_supplier_scope";
        byte[] data = sysGeneratorService.generatorCode2(tables.split(","), key, first, isAuto, frontCheck, sqlAuto);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }


    /**
     * 获取配置
     */
    @ResponseBody
    @RequestMapping("/config")
    public R config() {
        //配置信息
        Configuration config = GenUtilsCommon.getConfig();
        return R.ok().put("package", config.getString("package"))
                .put("moduleName", config.getString("moduleName"))
                .put("tablePrefix", config.getString("tablePrefix"));
    }


    /**
     * 生成代码
     */
    @ResponseBody
    @RequestMapping("/1code")
    public R acode(String tables,
                   String packageName,
                   String moduleName,
                   String tablePrefix,
                   boolean isAuto,
                   String interfacesPath,
                   String applicationPath,
                   String domainPath,
                   String infrastructurePath,
                   String clientPath,
                   String adminUrl,
                   String frontUrl,
                   boolean frontCheck,
                   boolean sqlAuto,
                   HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix,  interfacesPath, applicationPath, domainPath, infrastructurePath, clientPath, adminUrl, frontUrl);
        sysGeneratorService.generatorCode(tables.split(","), isAuto, frontCheck, sqlAuto);
        return R.ok();
    }

    /**
     * 生成代码
     */
    @ResponseBody
    @RequestMapping("/1code2")
    public R acode2(String tables,
                    String key,
                    String first,
                    String packageName,
                    String moduleName,
                    String tablePrefix,
                    boolean isAuto,
                    String interfacesPath,
                    String applicationPath,
                    String domainPath,
                    String infrastructurePath,
                    String clientPath,
                    String adminUrl,
                    String frontUrl,
                    boolean frontCheck,
                    boolean sqlAuto,
                    HttpServletResponse response) throws IOException {
        handStaticData(packageName, moduleName, tablePrefix, interfacesPath, applicationPath, domainPath, infrastructurePath, clientPath, adminUrl, frontUrl);
        sysGeneratorService.generatorCode2(tables.split(","), key, first, isAuto, frontCheck, sqlAuto);
        return R.ok();
    }


    private void handStaticData(String packageName,
                                String moduleName,
                                String tablePrefix,
                                String interfacesPath,
                                String applicationPath,
                                String domainPath,
                                String infrastructurePath,
                                String clientPath,
                                String adminUrl,
                                String frontUrl) {
        if (!StringUtils.isNullOrEmpty(packageName)) {
            SysGeneratorController.packageName = packageName;
        }
        if (!StringUtils.isNullOrEmpty(moduleName)) {
            SysGeneratorController.moduleName = moduleName;
        }
        if (!StringUtils.isNullOrEmpty(tablePrefix)) {
            SysGeneratorController.tablePrefix = tablePrefix;
        }
        if (!StringUtils.isNullOrEmpty(adminUrl)) {
            SysGeneratorController.adminUrl = adminUrl;
        }
        if (!StringUtils.isNullOrEmpty(interfacesPath)) {
            SysGeneratorController.interfacesPath = interfacesPath;
        }
        if (!StringUtils.isNullOrEmpty(applicationPath)) {
            SysGeneratorController.applicationPath = applicationPath;
        }
        if (!StringUtils.isNullOrEmpty(domainPath)) {
            SysGeneratorController.domainPath = domainPath;
        }
        if (!StringUtils.isNullOrEmpty(infrastructurePath)) {
            SysGeneratorController.infrastructurePath = infrastructurePath;
        }
        if (!StringUtils.isNullOrEmpty(clientPath)) {
            SysGeneratorController.clientPath = clientPath;
        }
        if (!StringUtils.isNullOrEmpty(frontUrl)) {
            SysGeneratorController.frontUrl = frontUrl;
        }
    }

}
