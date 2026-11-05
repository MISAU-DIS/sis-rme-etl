create database connectors_test_src_db;

create database connectors_test_dst_db;

CREATE TABLE `etl_demo_with_query_dst_db`.`person_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(250) NOT NULL,
  `full_address` varchar(500) NULL,
  `person_creation_date` datetime,
  `address_creation_date` datetime,
  `person_uuid` char(38) NOT NULL,
  `address_uuid` char(38) NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,  
   PRIMARY KEY (`id`),
   UNIQUE KEY `person_data_uk_1` (`person_uuid`, `address_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

