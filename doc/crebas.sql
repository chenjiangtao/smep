/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2013/12/19 17:00:29                          */
/*==============================================================*/
drop table if exists smep_sys_engine;

drop table if exists smep_sys_service;

drop table if exists smep_sys_param;

drop table if exists smep_adc_corp_info;

drop table if exists smep_adc_corp_param;

drop table if exists smep_adc_staff_info;

drop table if exists smep_adc_staff_param;

drop table if exists smep_msg_mo_log;

drop table if exists smep_msg_mt_log;

drop table if exists smep_msg_report_log;

drop table if exists smep_mms_mo_log;

drop table if exists smep_mms_mt_log;

drop table if exists smep_mms_report_log;

/*==============================================================*/
/* Table: smep_sys_engine                                       */
/*==============================================================*/
create table smep_sys_engine
(
   EngineName           varchar(50) not null primary key comment '引擎名',
   ClassName            varchar(200) not null comment '类路径',
   Description          varchar(200) comment '描述',
   State                int(1) not null comment '状态: 1启用 0禁用'
)
ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO smep_sys_engine VALUES ('cmpp20Engine','com.aesirteam.smep.sms.engine.CMPP20Engine','中国移动CMPP2.0短信收发引擎',1),('cmpp30Engine','com.aesirteam.smep.sms.engine.CMPP30Engine','中国移动CMPP3.0短信收发引擎',0),('jedisFactory','com.aesirteam.smep.resources.JedisFactory','Redis连接工厂类',1),('mm7Engine','com.aesirteam.smep.mms.engine.MM7Engine','中国移动MM7彩信收发引擎',1),('mmsMoDbEngine','com.aesirteam.smep.mms.engine.MmsMoDbEngine','彩信上行日志入库引擎',1),('mmsMtDbEngine','com.aesirteam.smep.mms.engine.MmsMtDbEngine','彩信下行日志入库引擎',1),('mmsPendingEngine','com.aesirteam.smep.mms.engine.MmsPendingEngine','彩信号码单条拆分引擎',1),('mmsReportDbEngine','com.aesirteam.smep.mms.engine.MmsReportDbEngine','彩信状态报告入库引擎',1),('sgip12Engine','com.aesirteam.smep.sms.engine.SGIP12Engine','中国联通SGIP1.2短信收发引擎',0),('smgp30Engine','com.aesirteam.smep.sms.engine.SMGP30Engine','中国电信SMGP3.0短信收发引擎',0),('smsMoDbEngine','com.aesirteam.smep.sms.engine.SmsMoDbEngine','短信上行日志入库引擎',1),('smsMtDbEngine','com.aesirteam.smep.sms.engine.SmsMtDbEngine','短信下行日志入库引擎',1),('smsPendingEngine','com.aesirteam.smep.sms.engine.SmsPendingEngine','短信号码单条拆分引擎',1),('smsReportDbEngine','com.aesirteam.smep.sms.engine.SmsReportDbEngine','短信状态报告入库引擎',1),('sysParams','com.aesirteam.smep.resources.SysParams','系统全局参数',1);

/*==============================================================*/
/* Table: smep_sys_servic                                       */
/*==============================================================*/
create table smep_sys_service (
  ServiceName varchar(50) not null primary key comment '服务名',
  ClassName   varchar(200) not null comment '类路径',
  RelationEngine varchar(50) not null comment '关联引擎',
  LastStartTime datetime comment '最后启动时间',
  LastStopTime datetime comment '最后停止时间',
  CurrState int(1) comment '状态：1 运行中 0 已停止'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

alter table smep_sys_service add constraint FK_SYS_REL_ENGINE foreign key (RelationEngine)
      references smep_sys_engine (EngineName);

INSERT INTO smep_sys_service VALUES ('cmpp20MtService','com.aesirteam.smep.sms.services.CMPP20MtService','cmpp20Engine',NULL,NULL,0),('cmpp30MtService','com.aesirteam.smep.sms.services.CMPP30MtService','cmpp30Engine',NULL,NULL,0),('mm7MtService','com.aesirteam.smep.mms.services.MM7MtService','mm7Engine',NULL,NULL,0),('mmsMoToDbService','com.aesirteam.smep.mms.services.MmsMoToDbService','mmsMoDbEngine',NULL,NULL,0),('mmsMtToDbService','com.aesirteam.smep.mms.services.MmsMtToDbService','mmsMtDbEngine',NULL,NULL,0),('mmsPendingService','com.aesirteam.smep.mms.services.MmsPendingService','mmsPendingEngine',NULL,NULL,0),('mmsReportToDbService','com.aesirteam.smep.mms.services.MmsReportToDbService','mmsReportDbEngine',NULL,NULL,0),('sgip12MtService','com.aesirteam.smep.sms.services.SGIP12MtService','sgip12Engine',NULL,NULL,0),('smgp30MtService','com.aesirteam.smep.sms.services.SMGP30MtService','smgp30Engine',NULL,NULL,0),('smsMoToDbService','com.aesirteam.smep.sms.services.SmsMoToDbService','smsMoDbEngine',NULL,NULL,0),('smsMtToDbService','com.aesirteam.smep.sms.services.SmsMtToDbService','smsMtDbEngine',NULL,NULL,0),('smsPendingService','com.aesirteam.smep.sms.services.SmsPendingService','smsPendingEngine',NULL,NULL,0),('smsReportToDbService','com.aesirteam.smep.sms.services.SmsReportToDbService','smsReportDbEngine',NULL,NULL,0);

/*==============================================================*/
/* Table: smep_sys_param                                        */
/*==============================================================*/
create table smep_sys_param (
  Id int(3) not null auto_increment primary key,
  param_group varchar(50) not null,
  param_name varchar(50) not null,
  param_value varchar(300) not null,
  param_desc varchar(100)
) ENGINE=MyISAM AUTO_INCREMENT=153 DEFAULT CHARSET=utf8;

INSERT INTO smep_sys_param VALUES (1,'cmpp20Engine','host','10.201.250.108','短信网关IP地址'),(2,'cmpp20Engine','port','7890','短信网关端口'),(3,'cmpp20Engine','source-addr','924185','接入帐号'),(4,'cmpp20Engine','shared-secret','924185','鉴权密码'),(5,'cmpp20Engine','heartbeat-interval','10','心跳间隔，单位:秒'),(6,'cmpp20Engine','reconnect-interval','5','重连间隔，单位:秒'),(7,'cmpp20Engine','heartbeat-noresponseout','5','心跳无应答时间，单位:秒'),(8,'cmpp20Engine','transaction-timeout','10','网络传输超时，单位:秒'),(9,'cmpp20Engine','read-timeout','5000','网络读取超时，单位:毫秒'),(10,'cmpp20Engine','version','1','版本号'),(11,'cmpp20Engine','serviceId','880003','业务代码'),(12,'cmpp20Engine','feeType','01','默认资费类型'),(13,'cmpp20Engine','feeCode','00','默认资费代码，单位:分(人民币)'),(14,'cmpp20Engine','msgSrc','924185','内容来源'),(15,'cmpp20Engine','cacheCapacity','20000','缓存队列配额上限'),(16,'cmpp20Engine','batchSize','100','队列批量提取大小值'),(17,'cmpp30Engine','host','10.201.250.108','短信网关IP地址'),(18,'cmpp30Engine','port','7891','短信网关端口'),(19,'cmpp30Engine','source-addr','924185','接入帐号'),(20,'cmpp30Engine','shared-secret','924185','鉴权密码'),(21,'cmpp30Engine','heartbeat-interval','10','心跳间隔，单位:秒'),(22,'cmpp30Engine','reconnect-interval','5','重连间隔，单位:秒'),(23,'cmpp30Engine','heartbeat-noresponseout','5','心跳无应答时间，单位:秒'),(24,'cmpp30Engine','transaction-timeout','10','网络传输超时，单位:秒'),(25,'cmpp30Engine','read-timeout','5000','网络读取超时，单位:毫秒'),(26,'cmpp30Engine','version','1','版本号'),(27,'cmpp30Engine','serviceId','880003','业务代码'),(28,'cmpp30Engine','feeType','01','默认资费类型'),(29,'cmpp30Engine','feeCode','00','默认资费代码，单位:分(人民币)'),(30,'cmpp30Engine','msgSrc','924185','内容来源'),(31,'cmpp30Engine','cacheCapacity','20000','缓存队列配额上限'),(32,'cmpp30Engine','batchSize','100','队列批量提取大小值'),(33,'smgp30Engine','host','10.201.250.108','短信网关IP地址'),(34,'smgp30Engine','port','9890','短信网关端口'),(35,'smgp30Engine','source-addr','924185','接入帐号'),(36,'smgp30Engine','shared-secret','924185','鉴权密码'),(37,'smgp30Engine','heartbeat-interval','10','心跳间隔，单位:秒'),(38,'smgp30Engine','reconnect-interval','5','重连间隔，单位:秒'),(39,'smgp30Engine','heartbeat-noresponseout','5','心跳无应答时间，单位:秒'),(40,'smgp30Engine','transaction-timeout','10','网络传输超时，单位:秒'),(41,'smgp30Engine','read-timeout','5000','网络读取超时，单位:毫秒'),(42,'smgp30Engine','version','48','版本号'),(43,'smgp30Engine','loginmode','2','0-send mode 1-receive mode 2-transmit mode'),(44,'smgp30Engine','serviceId','880003','业务代码'),(45,'smgp30Engine','feeType','01','默认资费类型'),(46,'smgp30Engine','feeCode','00','默认资费代码，单位:分(人民币)'),(47,'smgp30Engine','cacheCapacity','20000','缓存队列配额上限'),(48,'smgp30Engine','batchSize','100','队列批量提取大小值'),(49,'sgip12Engine','host','10.201.250.108','短信网关IP地址'),(50,'sgip12Engine','port','8001','短信网关端口'),(51,'sgip12Engine','listener-host','127.0.0.1','本地监听地址'),(52,'sgip12Engine','listener-port','8801','本地监听端口'),(53,'sgip12Engine','source-addr','924185','接入帐号'),(54,'sgip12Engine','shared-secret','924185','鉴权密码'),(55,'sgip12Engine','src-nodeid','3085199999','源节点编号'),(56,'sgip12Engine','heartbeat-interval','10','心跳间隔，单位:秒'),(57,'sgip12Engine','reconnect-interval','5','重连间隔，单位:秒'),(58,'sgip12Engine','heartbeat-noresponseout','5','心跳无应答时间，单位:秒'),(59,'sgip12Engine','transaction-timeout','10','网络传输超时，单位:秒'),(60,'sgip12Engine','read-timeout','5000','网络读取超时，单位:毫秒'),(61,'sgip12Engine','version','1','版本号'),(62,'sgip12Engine','serviceId','880003','业务代码'),(63,'sgip12Engine','feeType','01','默认资费类型'),(64,'sgip12Engine','feeCode','00','默认资费代码，单位:分(人民币)'),(65,'sgip12Engine','cacheCapacity','20000','缓存队列配额上限'),(66,'sgip12Engine','batchSize','100','队列批量提取大小值'),(67,'smsPendingEngine','cacheCapacity','10000','缓存队列配额上限'),(68,'smsPendingEngine','batchSize','100','队列批量提取大小值'),(69,'smsReportDbEngine','cacheCapacity','20000','缓存队列配额上限'),(70,'smsReportDbEngine','batchSize','100','队列批量提取大小值'),(71,'smsMtDbEngine','cacheCapacity','20000','缓存队列配额上限'),(72,'smsMtDbEngine','batchSize','100','队列批量提取大小值'),(73,'smsMoDbEngine','cacheCapacity','10000','缓存队列配额上限'),(74,'smsMoDbEngine','batchSize','100','队列批量提取大小值'),(101,'mm7Engine','authMode','0','鉴权模式: 0-不鉴权 1-基本鉴权 2-摘要鉴权'),(102,'mm7Engine','userName','zxme','登陆用户名'),(103,'mm7Engine','password','zxme','鉴权密码'),(104,'mm7Engine','maxMsgSize','100000','最大消息长度'),(105,'mm7Engine','charSet','GBK','彩信默认编码类型\r\n'),(106,'mm7Engine','mmscURL','/vas','彩信提交URL地址'),(107,'mm7Engine','mmscIP','10.201.250.108:8088','彩信网关IP地址及端口'),(108,'mm7Engine','mmscID','927001','彩信网关编码'),(109,'mm7Engine','useSSL','true','使用SSL传输'),(110,'mm7Engine','listenIP','0.0.0.0','本地监听地址'),(111,'mm7Engine','listenPort','8089','本地监听端口'),(112,'mm7Engine','timeout','5000','网络超时，单位:毫秒'),(113,'mm7Engine','keepAlive','on','长连接'),(114,'mm7Engine','maxKeepAliveRequests','100','最大连接数'),(115,'mm7Engine','serverMaxKeepAlive','100','服务最大活动连接数'),(116,'mm7Engine','minKeepAliveRequests','1','最小连接数'),(117,'mm7Engine','keepAliveTimeout','30','长连接网络超时，单位:秒'),(118,'mm7Engine','step','1','连接池增长的步长'),(119,'mm7Engine','vaspId','924185','SP代码'),(120,'mm7Engine','vasId','10657021','服务代码'),(121,'mm7Engine','serviceId','880003','业务代码'),(122,'mm7Engine','cacheCapacity','20000','缓存队列配额上限'),(123,'mm7Engine','batchSize','100','队列批量提取大小值'),(124,'mmsPendingEngine','cacheCapacity','10000','缓存队列配额上限'),(125,'mmsPendingEngine','batchSize','100','队列批量提取大小值'),(126,'mmsMtDbEngine','cacheCapacity','20000','缓存队列配额上限'),(127,'mmsMtDbEngine','batchSize','100','队列批量提取大小值'),(129,'mmsReportDbEngine','cacheCapacity','20000','缓存队列配额上限'),(130,'mmsReportDbEngine','batchSize','100','队列批量提取大小值'),(131,'mmsMoDbEngine','cacheCapacity','10000','缓存队列配额上限'),(132,'mmsMoDbEngine','batchSize','100','队列批量提取大小值'),(133,'jedisFactory','host','10.201.250.109','Redis服务器地址'),(134,'jedisFactory','port','6379','Redis服务器端口'),(135,'jedisFactory','database','0','Redis数据库模式'),(136,'jedisFactory','timeout','0','网络超时，单位:毫秒'),(137,'jedisFactory','maxactive','201','连接池最大连接数'),(138,'jedisFactory','maxidle','50','连接池最大空闲数'),(139,'jedisFactory','maxwait','3000','最大的等待时间'),(140,'sysParams','sms.queueName','q:sms:mt:tosend','待发送短信队列键名'),(141,'sysParams','sms.queueMaxLength','100000','待发送短信队列最大容量'),(142,'sysParams','sms.submitQueuePrefix','run:sms_owner','短信发送状态队列键名前缀'),(143,'sysParams','sms.bZip','true','消息压缩标志'),(144,'sysParams','mms.queueName','q:mms:mt:tosend','待发送彩信队列键名'),(145,'sysParams','mms.queueMaxLength','100000','待发送彩信队列最大容量'),(146,'sysParams','mms.submitQueuePrefix','run:mms_owner','彩信发送状态队列键名前缀'),(147,'sysParams','mms.bZip','true','消息压缩标志'),(148,'sysParams','mms.content.root.path','/home','彩信媒体文件存放根路径'),(149,'sysParams','mobileSplitStr',',','号码分割标识符'),(150,'sysParams','mobileSegNum','^([+]?86)?((135|136|137|138|139|147|150|151|152|157|158|159|182|183|184|187|188)\\d{8}|(1340|1341|1342|1343|1344|1345|1346|1347|1348)\\d{7})$','移动号段匹配表达式'),(151,'sysParams','unicomSegNum','^([+]?86)?(130|131|132|145|155|156|185|186)\\d{8}$','联通号段匹配表达式'),(152,'sysParams','telecomSegNum','^([+]?86)?((133|153|180|181|189)\\d{8}|1349\\d{7})$','电信号段匹配表达式');

/*==============================================================*/
/* Table: smep_adc_corp_info                                    */
/*==============================================================*/
create table smep_adc_corp_info
(
   CORPACCOUNT          varchar(20) not null primary key comment '企业编号',
   LICENSE              varchar(10) comment '业务license',
   OPTYPE               int comment '订购状态：1 : 订购；2 : 暂停服务；3 : 恢复服务；4 : 取消；5 : 订购关系变更；6 : 基本信息变更',
   OPNOTE               varchar(200) comment '订购描述',
   POINT                varchar(50) comment '业务功能点标识',
   STDATE               datetime comment '状态时间',
   CREATEOR             int comment '创建者',
   CORP_NAME            varchar(100) comment '企业名称',
   CORP_SHORTNAME       varchar(100) comment '企业简称',
   CORP_LINKMAN         varchar(100) comment '企业联系人',
   CORP_LINKPHONE       varchar(100) comment '联系人座机号',
   CORP_LINKMOBILE      varchar(100) comment '联系人手机号'
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='企业信息表';

/*==============================================================*/
/* Table: smep_adc_corp_param                                   */
/*==============================================================*/
create table smep_adc_corp_param
(
   CPID                 int not null auto_increment primary key,
   CORPACCOUNT          varchar(20) comment '企业编号',
   PARAM_TYPE           varchar(50) comment '参数类型',
   PARAM_NAME           varchar(100) comment '参数名',
   PARAM_VALUE          varchar(100) comment '参数值'
)
ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='企业信息参数表';

/*==============================================================*/
/* Table: smep_adc_staff_info                                   */
/*==============================================================*/
create table smep_adc_staff_info
(
   UFID                 varchar(50) not null primary key comment '用户编号',
   CORPACCOUNT          varchar(20) comment '企业编号',
   USERTYPE             int comment '用户类型：0：管理员；1：用户；2：企业通讯录',
   OPTYPE               int comment '订购状态：0-增加；1-删除；2-修改',
   OPNOTE               varchar(100) comment '订购描述',
   STDATE               datetime comment '状态时间',
   CREATEOR             int comment '创建者',
   STAFF_NAME           varchar(100) comment '用户姓名',
   STAFF_MOBILE         varchar(100) comment '用户手机号码',
   STAFF_SEX            varchar(10) comment '用户性别：0：女；1：男',
   STAFF_DEPTID         varchar(50) comment '用户归属部门编号'
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='用户订购表';

/*==============================================================*/
/* Table: smep_adc_staff_param                                  */
/*==============================================================*/
create table smep_adc_staff_param
(
   SPID                 int not null auto_increment primary key,
   UFID                 varchar(50) comment '用户编号',
   PARAM_NAME           varchar(100),
   PARAM_VALUE          varchar(100)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='用户信息参数表';

alter table smep_adc_corp_param add constraint FK_CP_CORPACCOUNT foreign key (CORPACCOUNT)
      references smep_adc_corp_info (CORPACCOUNT) on delete restrict on update restrict;

alter table smep_adc_staff_info add constraint FK_SI_CORPACCOUNT foreign key (CORPACCOUNT)
      references smep_adc_corp_info (CORPACCOUNT) on delete restrict on update restrict;

alter table smep_adc_staff_param add constraint FK_SP_UFID foreign key (UFID)
      references smep_adc_staff_info (UFID) on delete restrict on update restrict;
      
/*==============================================================*/
/* Table: smep_msg_mo_log                                       */
/*==============================================================*/
create table smep_msg_mo_log
(
   LogId                int not null auto_increment primary key,
   MsgId                varchar(30) not null  comment '网关消息编号',
   DestId               varchar(21) comment '目的号码(SP)',
   Service_Id           varchar(10) comment '业务类型',
   Tp_Pid               int,
   Tp_Udhi              int,
   Msg_Fmt              int comment '信息格式 0：ASCII串 3：短信写卡操作 4：二进制信息 8：UCS2编码 15：含GB汉字',
   Src_Terminal_Id      varchar(21) comment '源终端MSISDN号码',
   Msg_Length           int comment '消息长度',
   Msg_Content          varchar(160) comment 'MO内容',
   Src_terminal_type    int comment 'cmpp3.0',
   LinkID               varchar(20) comment 'cmpp3.0',
   Createor             varchar(50),
   CreateTime           datetime,
   Protocol             varchar(6)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='短信上行日志';
      
/*==============================================================*/
/* Table: smep_msg_mt_log                                       */
/*==============================================================*/
create table smep_msg_mt_log
(
   LogId                int not null auto_increment primary key,
   MsgId                varchar(30) not null comment '网关消息编号',
   SeqNo                varchar(32) comment '消息编号',
   CorpNo               varchar(20) comment '企业编号(同CORPACCOUNT)',
   Pk_Total             int,
   Pk_Number            int,
   Registered_Delivery  int comment '是否要求返回状态确认报告：0：不需要 1：需要',
   Msg_Level            int comment '信息级别',
   Service_Id           varchar(10) comment '业务类型',
   Fee_UserType         int comment '计费用户类型字段 0：对目的终端MSISDN计费；1：对源终端MSISDN计费；2：对SP计费; 3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。',
   Fee_Terminal_Id      varchar(21),
   Tp_Pid               int,
   Tp_Udhi              int,
   Msg_Fmt              int comment '信息格式 0：ASCII串 3：短信写卡操作 4：二进制信息 8：UCS2编码 15：含GB汉字',
   Msg_Src              varchar(10) comment '信息内容来源(SP_Id)',
   Fee_Type             varchar(2) comment '资费类别 01：免费 02：按条计费',
   Fee_Code             varchar(6) comment '资费代码（以分为单位）',
   Valid_Time           datetime comment '存活有效期',
   At_Time              datetime comment '定时发送时间',
   Src_Terminal_Id      varchar(21) comment '源号码(SP)',
   Dest_Terminal_Id     varchar(21) comment '接收短信的MSISDN号码',
   Msg_Content          varchar(160) comment '信息内容',
   Reserve              varchar(10) comment '保留',
   Fee_Terminal_Type    int comment 'cmpp3.0 被计费用户的号码类型',
   Dest_Terminal_Type   int comment 'cmpp3.0 接收短信的MSISDN号码类型',
   LinkId               varchar(20) comment 'cmpp3.0 用于点播业务',
   Createor             varchar(50),
   CreateTime           datetime,
   Protocol             varchar(6)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='短信下行日志';

/*==============================================================*/
/* Table: smep_msg_report_log                                   */
/*==============================================================*/
create table smep_msg_report_log
(
   LogId                int not null auto_increment primary key,
   MsgId                varchar(30) not null comment '网关消息编号 （同MSG_MT_LOG）',
   DestId               varchar(21) comment '目的号码(SP)',
   Src_Terminal_Id      varchar(21) comment '源终端MSISDN号码',
   Stat                 varchar(7) comment '状态报告:结果',
   Submit_time          datetime comment '状态报告:消息发送时间',
   Done_time            datetime comment '状态报告:消息接收时间',
   Dest_terminal_Id     varchar(21) comment '状态报告:目的终端MSISDN号码',
   Smsc_sequence        int,
   Createor             varchar(50),
   CreateTime           datetime,
   Protocol             varchar(6)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='短信状态报告日志';

/*==============================================================*/
/* Table: smep_mms_mt_log                                       */
/*==============================================================*/
create table smep_mms_mt_log
(
   LogId                int not null auto_increment primary key,
   TransactionId        varchar(30) not null comment '消息流水号',
   SeqNo                varchar(32) comment '消息编号',
   CorpNo               varchar(20) comment '企业编号(同CORPACCOUNT)',
   VASPId               varchar(20) comment 'SP代码',
   VASId                varchar(20) comment '服务代码',
   MMSVersion           varchar(10) comment '接口版本号',
   MMSSubject           varchar(100) comment '彩信标题',
   Src_Terminal_Id      varchar(21) comment '源号码(SP)',
   Dest_Terminal_Id     varchar(21) comment '接收彩信的MSISDN号码',
   Service_Id           varchar(10) comment '业务类型',
   Registered_Delivery  int comment '是否要求返回状态确认报告：0：不需要 1：需要',
   Msg_Level            int comment '信息级别',
   Mms_BodyType         int comment '彩信消息体类型 0：文件名方式 ；1：文件流方式',
   Valid_Time           datetime comment '存活有效期',
   At_Time              datetime comment '定时发送时间',
   Mms_File             longtext comment '文件列表',
   Createor             varchar(50),
   CreateTime           datetime,
   Protocol             varchar(6),
   MsgId                varchar(30) comment '网关返回消息编号',
   StatusCode           int comment '网关返回提交状态',
   StatusText           varchar(30) comment '网关返回提交描述'
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='彩信下行日志';

/*==============================================================*/
/* Table: smep_mms_report_log                                   */
/*==============================================================*/
create table smep_mms_report_log
(
   LogId                int not null auto_increment primary key,
   TransactionId        varchar(30) not null comment '消息流水号',
   MMSVersion           varchar(10) comment '接口版本号',
   MMSRelayServerID     varchar(10) comment 'MMSRelayServer的标识符',
   MessageID     				varchar(30) comment '原始MM的标识',
   Dest_terminal_Id     varchar(21) comment '目的号码MSISDN号码',
   Src_Terminal_Id      varchar(21) comment '源号码(SP)',
   MMTimeStamp          datetime comment '处理MM的日期和时间',
   MMStatus             int comment '状态报告:结果',
   StatusText           varchar(30) comment '状态报告:描述',
   Createor             varchar(50),
   CreateTime           datetime,
   Protocol             varchar(6)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='彩信状态报告日志';

/*==============================================================*/
/* Table: smep_mms_mo_log                                   */
/*==============================================================*/
create table smep_mms_mo_log
(
   LogId                int not null auto_increment primary key,
   TransactionId        varchar(30) not null comment '消息流水号',
   MMSVersion           varchar(10) comment '接口版本号',
   MMSRelayServerID     varchar(10) comment 'MMSRelayServer的标识符',
   LinkID     				  varchar(30) comment '已链接消息的消息ID',
   Dest_terminal_Id     varchar(21) comment '目的号码(SP)',
   DestCc     				  text comment '抄送',
   DestBcc              text comment '密送',
   Src_Terminal_Id      varchar(21) comment '源号码MSISDN号码',
   MMTimeStamp          datetime comment '处理MM的日期和时间',
   ReplyChargingID      varchar(30),
   Priority             int comment '优先级',
   MMSSubject           varchar(100) comment '彩信标题',
   IsMultipart					int,
   MMSContext           longtext comment '彩信内容',
   Createor             varchar(50),
   CreateTime           datetime,
   Protocol             varchar(6)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='彩信上行日志';

