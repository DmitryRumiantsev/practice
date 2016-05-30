-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema chat
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `chat` ;

-- -----------------------------------------------------
-- Schema chat
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `chat` DEFAULT CHARACTER SET utf8 ;
USE `chat` ;

-- -----------------------------------------------------
-- Table `chat`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `chat`.`users` ;

CREATE TABLE IF NOT EXISTS `chat`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `chat`.`messages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `chat`.`messages` ;

CREATE TABLE IF NOT EXISTS `chat`.`messages` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(140) NULL,
  `date` DATETIME NOT NULL DEFAULT NOW( ),
  `user_id` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `user_id_idx` (`user_id` ASC),
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `chat`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `chat`.`users`
-- -----------------------------------------------------
START TRANSACTION;
USE `chat`;
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (1, 'user1');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (2, 'user2');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (3, 'user3');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (4, 'user4');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (5, 'user5');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (6, 'user6');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (7, 'user7');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (8, 'user8');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (9, 'user9');
INSERT INTO `chat`.`users` (`id`, `name`) VALUES (10, 'user10');

COMMIT;


-- -----------------------------------------------------
-- Data for table `chat`.`messages`
-- -----------------------------------------------------
START TRANSACTION;
USE `chat`;
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (1, 'message1', 2016-05-09, 1);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (2, 'message2', DEFAULT, 2);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (3, 'message3', DEFAULT, 1);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (4, 'meeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeessage4', DEFAULT, 5);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (5, 'message5', DEFAULT, 4);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (6, 'message6', DEFAULT, 7);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (7, 'message7', DEFAULT, 9);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (8, 'message8', DEFAULT, 3);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (9, 'message9', DEFAULT, 1);
INSERT INTO `chat`.`messages` (`id`, `text`, `date`, `user_id`) VALUES (10, 'message10', DEFAULT, 2);

COMMIT;

