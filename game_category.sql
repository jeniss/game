/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50627
Source Host           : localhost:3306
Source Database       : game

Target Server Type    : MYSQL
Target Server Version : 50627
File Encoding         : 65001

Date: 2017-07-07 15:03:42
*/

SET FOREIGN_KEY_CHECKS=0;

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
