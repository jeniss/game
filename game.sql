/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50627
Source Host           : localhost:3306
Source Database       : game

Target Server Type    : MYSQL
Target Server Version : 50627
File Encoding         : 65001

Date: 2017-07-10 10:19:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(32) DEFAULT NULL,
  `value` varchar(32) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES ('1', 'valid.ip', '172.104.93.45;127.0.0.1', '可用的ip');

-- ----------------------------
-- Table structure for game
-- ----------------------------
DROP TABLE IF EXISTS `game`;
CREATE TABLE `game` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `code` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of game
-- ----------------------------
INSERT INTO `game` VALUES ('1', '诛仙3', '61');

-- ----------------------------
-- Table structure for game_category
-- ----------------------------
DROP TABLE IF EXISTS `game_category`;
CREATE TABLE `game_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `type` varchar(24) DEFAULT NULL COMMENT 'key--关键字，item--商品类型',
  `game_id` int(11) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `code` varchar(32) DEFAULT NULL,
  `value` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of game_category
-- ----------------------------
INSERT INTO `game_category` VALUES ('1', null, 'item', '1', '游戏币', 'gameCoin', '-3');
INSERT INTO `game_category` VALUES ('2', null, 'item', '1', '装备', 'equipment', '-2');
INSERT INTO `game_category` VALUES ('3', '2', 'key', '1', '艳阳宝玉', null, '1000');
INSERT INTO `game_category` VALUES ('4', '2', 'key', '1', '碧涛仙玉', null, '1');
INSERT INTO `game_category` VALUES ('5', '2', 'key', '1', '天龙魔血', null, '100');
INSERT INTO `game_category` VALUES ('6', '2', 'key', '1', '聚宝盆', null, '1000');
INSERT INTO `game_category` VALUES ('7', '2', 'key', '1', '飞天神符', null, '1000');
INSERT INTO `game_category` VALUES ('8', '2', 'key', '1', '仙豆', null, '1000');
INSERT INTO `game_category` VALUES ('9', '2', 'key', '1', '十铁碎片礼包', null, '1');
INSERT INTO `game_category` VALUES ('10', '2', 'key', '1', '金精铁玉+10级', null, '1');
INSERT INTO `game_category` VALUES ('11', '2', 'key', '1', '沐雨令', null, '1');
INSERT INTO `game_category` VALUES ('12', '2', 'key', '1', '抽抽', null, '100');

-- ----------------------------
-- Table structure for server_area
-- ----------------------------
DROP TABLE IF EXISTS `server_area`;
CREATE TABLE `server_area` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `game_id` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `code` varchar(32) DEFAULT NULL,
  `active` varchar(1) DEFAULT NULL,
  `merger_id` int(11) DEFAULT NULL COMMENT '合并后的区服id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of server_area
-- ----------------------------
INSERT INTO `server_area` VALUES ('1', '1', null, '幽天战区', '25686', 'Y', null);
INSERT INTO `server_area` VALUES ('2', '1', '1', '星庐听雨', '25710', 'Y', null);
INSERT INTO `server_area` VALUES ('3', '1', '1', '寻梦青云', '25921', 'Y', null);
INSERT INTO `server_area` VALUES ('4', '1', '1', '似梦仙缘', '29809', 'Y', null);
INSERT INTO `server_area` VALUES ('5', '1', '1', '碧海明月', '29808', 'Y', null);
INSERT INTO `server_area` VALUES ('6', '1', null, '阳天战区', '2196', 'Y', null);
INSERT INTO `server_area` VALUES ('7', '1', '6', '一念乾坤', '26808', 'Y', null);
INSERT INTO `server_area` VALUES ('8', '1', '6', '周星两仪', '26809', 'Y', null);
INSERT INTO `server_area` VALUES ('9', '1', '6', '西荒巫地', '27058', 'Y', null);
INSERT INTO `server_area` VALUES ('10', '1', '6', '辞旧迎新', '27717', 'Y', null);
INSERT INTO `server_area` VALUES ('11', '1', '6', '吉祥如意', '27862', 'Y', null);
INSERT INTO `server_area` VALUES ('12', '1', '6', '月耀星河', '28137', 'Y', null);
INSERT INTO `server_area` VALUES ('13', '1', '6', '十里桃花', '28190', 'Y', null);
INSERT INTO `server_area` VALUES ('14', '1', '6', '三生七世', '28478', 'Y', null);
INSERT INTO `server_area` VALUES ('15', '1', '6', '竹峰春雨', '28733', 'Y', null);
INSERT INTO `server_area` VALUES ('16', '1', '6', '春城飞花', '29600', 'Y', null);
INSERT INTO `server_area` VALUES ('17', '1', '6', '逆天改命', '29689', 'Y', null);
INSERT INTO `server_area` VALUES ('18', '1', null, '钧天战区', '2799', 'Y', null);
INSERT INTO `server_area` VALUES ('19', '1', '18', '一剑诛仙', '29944', 'Y', null);
INSERT INTO `server_area` VALUES ('20', '1', '18', '情系十年', '29945', 'Y', null);

-- ----------------------------
-- Table structure for trade_flow
-- ----------------------------
DROP TABLE IF EXISTS `trade_flow`;
CREATE TABLE `trade_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `game_id` int(11) DEFAULT NULL,
  `server_area_id` int(11) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `name` varchar(256) DEFAULT NULL,
  `price` decimal(10,0) DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `total_price` decimal(10,0) DEFAULT NULL,
  `unit_price` decimal(10,0) DEFAULT NULL,
  `unit_price_desc` varchar(256) DEFAULT NULL,
  `trade_status` varchar(16) DEFAULT NULL COMMENT 'finished －－ 交易完成；selling －－ 立即购买',
  `entry_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of trade_flow
-- ----------------------------
