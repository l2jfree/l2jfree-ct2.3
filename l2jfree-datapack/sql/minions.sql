DROP TABLE IF EXISTS `minions`;
CREATE TABLE `minions` (
  `boss_id` INT(11) NOT NULL DEFAULT 0,
  `minion_id` INT(11) NOT NULL DEFAULT 0,
  `amount_min` INT(4) NOT NULL DEFAULT 0,
  `amount_max` INT(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`boss_id`,`minion_id`)
) DEFAULT CHARSET=utf8;

INSERT INTO `minions` VALUES
(20117,20118,1,3),
(20376,20377,1,2),
(20398,20399,1,2),
(20520,20445,3,5),
(20522,20524,2,4),
(20738,20739,3,5),
(20745,20746,1,2),
(20747,20748,1,2),
(20749,20750,1,2),
(20751,20752,3,3),
(20753,21040,4,4),
(20758,20759,1,1),
(20758,20760,1,1),
(20761,20762,2,3),
(20763,20764,1,1),
(20763,20765,1,1),
(20763,20766,1,1),
(20771,20772,1,3),
(20773,20774,2,4),
(20779,20750,1,3),
(20936,20937,1,1),
(20936,20938,1,1),
(20936,20939,1,1),
(20941,20940,3,3),
(20944,20942,1,1),
(20944,20943,2,2),
(20947,20945,1,2),
(20947,20946,1,2),
(20950,20948,1,2),
(20950,20949,1,2),
(20953,20951,1,2),
(20953,20952,1,2),
(20956,20954,1,2),
(20956,20955,1,2),
(20959,20957,1,2),
(20959,20958,1,2),
(20963,20960,1,1),
(20963,20961,1,1),
(20963,20962,1,1),
(20966,20964,1,2),
(20974,20975,1,2),
(20974,20976,1,2),
(20977,20978,1,1),
(20977,20979,1,1),
(20980,20981,1,1),
(20980,20982,1,1),
(20986,20987,1,2),
(20986,20988,1,2),
(20989,20990,1,1),
(20991,20992,1,2),
(20991,20993,1,2),
(20994,20995,3,4);

INSERT INTO `minions` VALUES 
(21058,21059,1,2),
(21058,21060,1,2),
(21075,21076,1,1),
(21075,21077,1,2),
(21078,21079,1,1),
(21078,21080,1,2),
(21081,21082,1,1),
(21081,21083,1,3),
(21090,21091,1,1),
(21090,21092,1,1),
(21312,21313,2,2),
(21343,21344,2,2),
(21345,21346,2,2),
(21347,21348,1,1),
(21347,21349,1,1),
(21369,21370,2,2),
(21371,21372,2,2),
(21373,21374,1,1),
(21373,21375,1,1),
(21596,21597,1,1),
(21596,21598,1,1),
(21599,21600,1,1),
(21599,21601,1,1),
(22028,22027,3,4),
(22080,22079,3,3),
(22084,22083,3,3),
(22088,22087,3,3),
(22092,22091,3,3),
(22094,22093,6,6),
(22096,22095,3,3),
(22100,22099,8,8),
(22102,22101,8,8),
(22104,22103,8,8),
(22123,22122,2,3),
(22135,22130,1,1),
(22135,22131,1,1);

INSERT INTO `minions` VALUES 
(27021,20492,6,8),
(27022,20367,1,3),
(27036,27037,2,3),
(27110,27111,3,5),
(27113,27111,3,6);

-- raid bosses
INSERT INTO `minions` VALUES 
(25001,25002,3,3),
(25001,25003,1,1),
(25004,25005,3,3),
(25004,25006,2,2),
(25007,25008,2,2),
(25007,25009,2,2),
(25010,25011,3,3),
(25010,25012,2,2),
(25013,25014,1,1),
(25013,25015,4,4),
(25016,25017,2,2),
(25016,25018,2,2),
(25020,25021,2,2),
(25020,25022,2,2),
(25023,25024,1,1),
(25023,25025,4,4),
(25026,25027,3,3),
(25026,25028,2,2),
(25029,25030,2,2),
(25029,25031,2,2),
(25032,25033,3,3),
(25032,25034,1,1),
(25035,25036,3,3),
(25035,25037,2,2),
(25038,25039,1,1),
(25038,25040,4,4),
(25041,25042,3,3),
(25041,25043,2,2),
(25044,25045,2,2),
(25044,25046,2,2),
(25047,25048,1,1),
(25047,25049,4,4),
(25051,25052,2,2),
(25051,25053,2,2),
(25054,25055,1,1),
(25054,25056,4,4),
(25057,25058,3,3),
(25057,25059,2,2),
(25060,25061,3,3),
(25060,25062,1,1),
(25064,25065,1,1),
(25064,25066,4,4),
(25067,25068,3,3),
(25067,25069,2,2),
(25070,25071,3,3),
(25070,25072,1,1),
(25073,25074,2,2),
(25073,25075,2,2),
(25076,25077,1,1),
(25076,25078,4,4),
(25079,25080,2,2),
(25079,25081,2,2),
(25082,25083,3,3),
(25082,25084,1,1),
(25085,25086,3,3),
(25085,25087,2,2),
(25089,25091,3,3),
(25089,25090,1,1),
(25092,25093,1,1),
(25092,25094,4,4),
(25095,25096,3,3),
(25095,25097,1,1),
(25099,25100,2,2),
(25099,25101,2,2),
(25103,25104,1,1),
(25103,25105,4,4),
(25106,25107,3,3),
(25106,25108,2,2),
(25109,25110,3,3),
(25109,25111,1,1),
(25112,25113,3,3),
(25112,25114,1,1),
(25115,25116,3,3),
(25115,25117,2,2),
(25119,25120,3,3),
(25119,25121,1,1),
(25122,25123,1,1),
(25122,25124,4,4),
(25128,25129,3,3),
(25128,25130,1,1),
(25131,25132,3,3),
(25131,25133,2,2),
(25134,25135,2,2),
(25134,25136,2,2),
(25137,25138,3,3),
(25137,25139,1,1),
(25140,25141,1,1),
(25140,25142,4,4),
(25143,25144,2,2),
(25143,25145,2,2),
(25146,25147,3,3),
(25146,25148,2,2),
(25149,25150,3,3),
(25149,25151,2,2),
(25152,25153,1,1),
(25152,25154,4,4),
(25155,25156,3,3),
(25155,25157,1,1),
(25159,25160,2,2),
(25159,25161,2,2),
(25166,25167,2,2),
(25166,25168,2,2),
(25170,25171,3,3),
(25170,25172,2,2),
(25173,25174,2,2),
(25173,25175,2,2),
(25176,25177,1,1),
(25176,25178,4,4),
(25179,25180,3,3),
(25179,25181,1,1),
(25182,25183,2,2),
(25182,25184,2,2),
(25185,25186,3,3),
(25185,25187,2,2),
(25189,25190,3,3),
(25189,25191,1,1),
(25192,25193,1,1),
(25192,25194,4,4),
(25199,25200,3,3),
(25199,25201,1,1),
(25202,25203,2,2),
(25202,25204,2,2),
(25205,25206,1,1),
(25205,25207,4,4),
(25208,25209,3,3),
(25208,25210,1,1),
(25211,25212,3,3),
(25211,25213,2,2),
(25214,25215,3,3),
(25214,25216,1,1),
(25217,25218,3,3),
(25217,25219,2,2),
(25220,25221,3,3),
(25220,25222,1,1),
(25223,25224,3,3),
(25223,25225,1,1),
(25226,25227,2,2),
(25226,25228,2,2),
(25230,25231,1,1),
(25230,25232,4,4),
(25235,25236,2,2),
(25235,25237,2,2),
(25238,25239,2,2),
(25238,25240,2,2),
(25241,25242,3,3),
(25241,25243,2,2),
(25245,25246,1,1),
(25245,25247,4,4),
(25249,25250,3,3),
(25249,25251,1,1),
(25252,25253,3,3),
(25252,25254,1,1),
(25256,25257,3,3),
(25256,25258,2,2),
(25260,25261,3,3),
(25260,25262,2,2),
(25263,25264,3,3),
(25263,25265,1,1),
(25266,25267,4,4),
(25266,25268,1,1),
(25269,25270,3,3),
(25269,25271,2,2),
(25273,25274,3,3),
(25273,25275,1,1),
(25277,25278,2,2),
(25277,25279,2,2),
(25283,25284,4,4),
(25283,25285,2,2),
(25286,25287,3,3),
(25286,25288,2,2),
(25286,25289,2,2),
(25290,25291,3,3),
(25290,25292,1,1),
(25293,25294,1,1),
(25293,25295,4,4),
(25296,25297,3,3),
(25296,25298,1,1),
(25299,25300,3,3),
(25299,25301,2,2),
(25302,25303,3,3),
(25302,25304,1,1),
(25306,25307,1,1),
(25306,25308,4,4),
(25309,25310,1,1),
(25309,25311,4,4),
(25312,25313,2,2),
(25312,25314,2,2),
(25316,25317,1,1),
(25316,25318,4,4),
(25319,25320,3,3),
(25319,25321,1,1),
(25322,25323,3,3),
(25322,25324,1,1),
(25325,25326,1,1),
(25325,25327,4,4),
(25328,25329,1,1),
(25328,25330,1,1),
(25328,25331,1,1),
(25328,25332,1,1),
(25339,25340,2,2),
(25339,25341,2,2),
(25342,25343,1,1),
(25342,25344,1,1),
(25342,25345,2,2),
(25346,25347,3,3),
(25346,25348,1,1),
(25349,25350,3,3),
(25349,25351,1,1),
(25352,25353,3,3),
(25354,25355,4,4),
(25354,25356,1,1),
(25357,25358,2,2),
(25357,25359,1,1),
(25360,25361,3,3),
(25362,25363,2,2),
(25362,25364,3,3),
(25366,25367,2,2),
(25366,25368,1,1),
(25369,25370,1,1),
(25369,25371,4,4),
(25373,25374,3,3),
(25375,25376,2,2),
(25375,25377,1,1),
(25378,25379,3,3),
(25380,25381,1,1),
(25380,25382,4,4),
(25383,25384,3,3),
(25385,25386,1,1),
(25385,25387,4,4),
(25388,25389,3,3),
(25388,25390,2,2),
(25392,25393,3,3),
(25395,25396,1,1),
(25395,25397,2,2),
(25398,25399,3,3),
(25398,25400,1,1),
(25401,25402,1,1),
(25401,25403,3,3),
(25404,25405,2,2),
(25404,25406,1,1),
(25407,25408,1,1),
(25407,25409,2,2),
(25410,25411,3,3),
(25412,25413,3,3),
(25412,25414,2,2),
(25415,25416,3,3),
(25415,25417,1,1),
(25418,25419,3,3),
(25420,25421,3,3),
(25420,25422,1,1),
(25423,25424,4,4),
(25423,25425,1,1),
(25426,25427,2,2),
(25426,25428,1,1),
(25429,25430,3,3),
(25431,25432,2,2),
(25431,25433,2,2),
(25434,25435,1,1),
(25434,25436,3,3),
(25438,25439,1,1),
(25438,25440,3,3),
(25441,25442,4,4),
(25441,25443,1,1),
(25444,25445,3,3),
(25444,25446,2,2),
(25447,25448,2,2),
(25447,25449,2,2),
(25450,25451,3,3),
(25450,25452,1,1),
(25453,25454,3,3),
(25453,25455,1,1),
(25456,25457,1,1),
(25456,25458,2,2),
(25456,25459,1,1),
(25460,25461,2,2),
(25460,25462,1,1),
(25463,25464,1,1),
(25463,25465,1,1),
(25463,25466,1,1),
(25467,25468,4,4),
(25467,25469,1,1),
(25470,25471,3,3),
(25470,25472,1,1),
(25473,25474,3,3),
(25475,25476,3,3),
(25475,25477,2,2),
(25478,25479,3,3),
(25478,25480,1,1),
(25481,25482,2,2),
(25481,25483,1,1),
(25484,25485,1,1),
(25484,25486,2,2),
(25487,25488,3,3),
(25487,25489,1,1),
(25490,25491,3,3),
(25490,25492,2,2),
(25493,25494,1,1),
(25493,25495,4,4),
(25496,25497,3,3),
(25498,25499,1,1),
(25498,25500,4,4),
(25501,25502,3,3),
(25501,25503,2,2),
(25504,25505,3,3),
(25506,25507,1,1),
(25506,25508,3,3),
(25509,25510,1,1),
(25509,25511,3,3),
(25514,25515,1,1),
(25514,25516,3,3),
(25623,25633,4,6),
(25625,25629,3,3),
(25625,25630,3,3),
(27108,27109,1,1),
(29096,29097,2,2),
(29096,29098,3,4),
(25524,25525,2,2),
(25524,25526,2,2),
(29056,29057,2,2),
(29056,29058,2,2);

-- Primeval Isle temp mob groups.
INSERT INTO `minions` VALUES
(22196,22197,0,1),
(22196,22198,0,1),
(22196,22218,0,1),
(22196,22223,0,1),
(22200,22201,0,1),
(22200,22202,0,1),
(22200,22219,0,1),
(22200,22224,0,1),
(22203,22204,0,1),
(22203,22205,0,1),
(22203,22220,0,1),
(22203,22225,0,1),
(22208,22209,0,1),
(22208,22210,0,1),
(22208,22221,0,1),
(22208,22226,0,1),
(22211,22212,0,1),
(22211,22213,0,1),
(22211,22222,0,1),
(22211,22227,0,1);


-- grand bosses
INSERT INTO `minions` VALUES
(29001,29003,5,8),
(29001,29004,6,9),
(29006,29007,10,10),
(29006,29008,3,3),
(29006,29011,4,4),
(29014,29015,6,8),
(29014,29016,4,7),
(29014,29017,6,8),
(29014,29018,4,7);

-- ToI - Binder group
INSERT INTO `minions` VALUES 
(20983,20984,1,1),
(20983,20985,1,1),
(20983,21074,1,1);

-- VoS - Judge of Splendor group
INSERT INTO `minions` VALUES
(21544,21545,1,1),
(21544,21546,1,1);

-- Original Sin Wardens
INSERT INTO `minions` VALUES
(22423,22424,1,1),
(22423,22425,1,1),
(22423,22426,1,1),
(22423,22427,1,1),
(22423,22428,1,1),
(22423,22429,1,1),
(22423,22430,1,1),
(22431,22432,1,1),
(22431,22433,1,1),
(22431,22434,1,1),
(22431,22435,1,1),
(22431,22436,1,1),
(22431,22437,1,1),
(22431,22438,1,1);

-- L2JFree addon from forced_updates
DELETE FROM minions WHERE boss_id IN ( 29062,22188,22191 );
INSERT INTO minions VALUES
(29062,29063,1,1),
(29062,29064,3,3),
(22188,22189,4,4),
(22188,22190,1,1),
(22191,22192,1,1),
(22191,22193,1,1),
(22113,22112,1,1),
(22118,22120,1,1);

DELETE FROM minions WHERE boss_id = 18633;
INSERT INTO minions VALUES
(18633,18634,6,6);

# Gracia flying bosses (savormix)
DELETE FROM minions WHERE boss_id BETWEEN 25623 AND 25626;
INSERT INTO minions VALUES
-- Valdstone
(25623,25633,5,5),
-- Rok
(25624,25627,3,3),
(25624,25628,3,3),
-- Enira
(25625,25629,3,3),
(25625,25630,3,3),
-- Dius
(25626,25631,3,3),
(25626,25632,3,3);
