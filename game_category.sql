/*
 Navicat Premium Data Transfer

 Source Server         : my
 Source Server Type    : MySQL
 Source Server Version : 50710
 Source Host           : localhost
 Source Database       : game

 Target Server Type    : MySQL
 Target Server Version : 50710
 File Encoding         : utf-8

 Date: 07/06/2017 23:57:36 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `game_category`
-- ----------------------------
DROP TABLE IF EXISTS `game_category`;
CREATE TABLE `game_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) DEFAULT NULL,
  `type` varchar(24) DEFAULT NULL COMMENT 'key--关键字，item--商品类型',
  `game_id` int(11) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `code` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `game_category`
-- ----------------------------
BEGIN;
INSERT INTO `game_category` VALUES ('1', null, 'item', '1', '游戏币', '-3'), ('2', null, 'item', '1', '装备', '-2'), ('3', '2', 'key', '1', '艳阳宝玉', '1000'), ('4', '2', 'key', '1', '碧涛仙玉', '1'), ('5', '2', 'key', '1', '天龙魔血', '100'), ('6', '2', 'key', '1', '聚宝盆', '1000'), ('7', '2', 'key', '1', '飞天神符', '1000'), ('8', '2', 'key', '1', '仙豆', '1000'), ('9', '2', 'key', '1', '十铁碎片礼包', '1'), ('10', '2', 'key', '1', '金精铁玉+10级', '1'), ('11', '2', 'key', '1', '沐雨令', '1'), ('12', '2', 'key', '1', '抽抽', '100');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
