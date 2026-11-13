create database connectors_test_dst_db;

CREATE TABLE `office` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `office_uk` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `person` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(250) NOT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `uuid` char(38) NOT NULL,
  `office_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `person_uk` (`uuid`),
  KEY `person_fk1` (`office_id`),
  CONSTRAINT `person_fk1` FOREIGN KEY (`office_id`) REFERENCES `office` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `person_id` int NOT NULL,
  `full_address` varchar(500) NOT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `address_uk` (`uuid`),
  KEY `fk1` (`person_id`),
  CONSTRAINT `fk1` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `person_data` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(250) NOT NULL,
  `full_address` varchar(500) DEFAULT NULL,
  `person_creation_date` datetime DEFAULT NULL,
  `address_creation_date` datetime DEFAULT NULL,
  `person_uuid` char(38) NOT NULL,
  `address_uuid` char(38) DEFAULT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `person_data_uk_1` (`person_uuid`,`address_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb3;