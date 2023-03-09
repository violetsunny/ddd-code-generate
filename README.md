**项目说明** 

- ddd-generator是源自人人开源项目的代码生成器的改造升级的DDD分层代码生成器，可在线生成entity、xml、dao、service、html、js、sql代码，减少70%以上的开发任务
<br>

 **本地部署**

- 通过git下载源码
- 修改application.yml，更新MySQL账号和密码、数据库名称
- Eclipse、IDEA运行GeneratorApplication.java，则可启动项目
- 项目访问路径：http://localhost

**DDD代码生成说明**

本代码生成是基于人人的代码生成修改的，修改的生成框架是：

- 前端：xtoon-boot-element
- 后端：xtoon-boot-develop

使用本代码生成有以下规则必须遵守，不然生成的代码无法运行：

- （1）表的主键必须命名为id且为varchar类型：例子 `id` varchar(32) NOT NULL
- （2）有7个属性字段必须包含其中包含tenant_id、status、del_flag、created_by、created_time、updated_by、updated_time

除了以上的8个字段属性之外自己可以根据实际的情况来添加，建表实例：
```sql
    DROP TABLE IF EXISTS `sys_tb_goods`;
    CREATE TABLE `sys_tb_goods` (
    `id` varchar(64) NOT NULL,
    `tenant_id`    varchar(32)    DEFAULT NULL COMMENT '所属租户',
    `state`        tinyint(4)     DEFAULT '0' COMMENT '状态',
    `is_deleted`   tinyint(1)     DEFAULT '0' COMMENT '删除标识',
    `create_by`   varchar(32)    DEFAULT NULL COMMENT '创建人',
    `create_time` datetime       DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(32)    DEFAULT NULL COMMENT '更新人',
    `update_time` datetime       DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品';
```

**DDD主子表代码生成说明**

使用主子表代码生成有以下规则必须遵守，不然生成的代码无法运行：

- （1）主子表生成的时候必须选择两个以上的表来进行生成
- （2）要在选择的表中选定一个主表
- （3）需要设置子表对于主表的外键属性名称，注意界面中的关联属性要和SQL中的外键属性一致
- （4）每一个子表中都必须包含该外键属性

具体的建表例子请参考sql/complex.sql[一个主表，五个子表的例子]
