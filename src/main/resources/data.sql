CREATE TABLE Admin (
                       email VARCHAR(255) NOT NULL PRIMARY KEY,
                       Uid INT NOT NULL UNIQUE,
                       name VARCHAR(255) NOT NULL,
                       phone VARCHAR(20)
);

CREATE TABLE Lecturer (
                          email VARCHAR(255) NOT NULL PRIMARY KEY,
                          uid INT NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL,
                          phone VARCHAR(20)
);

CREATE TABLE Room (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      roomName VARCHAR(255) NOT NULL,
                      capacity INT NOT NULL,
                      isBusy BOOLEAN NOT NULL DEFAULT FALSE,
                      location VARCHAR(255),
                      multimedia BOOLEAN NOT NULL DEFAULT FALSE,
                      projector BOOLEAN NOT NULL DEFAULT FALSE,
                      requireApproval BOOLEAN NOT NULL DEFAULT FALSE,
                      isRestricted BOOLEAN NOT NULL DEFAULT FALSE,
                      url VARCHAR(500)
);

CREATE TABLE Student (
                         email VARCHAR(255) NOT NULL PRIMARY KEY,
                         uid INT NOT NULL UNIQUE,
                         name VARCHAR(255) NOT NULL,
                         phone VARCHAR(20),
                         breakTimer INT DEFAULT 0
);

CREATE TABLE User (
                      uid INT NOT NULL PRIMARY KEY,
                      role ENUM('Admin', 'Lecturer', 'Student') NOT NULL,
);

CREATE TABLE Record (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        roomId INT NOT NULL,
                        userId INT ,
                        startTime DATETIME NOT NULL,
                        endTime DATETIME NOT NULL,
                        recordTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        hasCheckedIn BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE Message (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         Uid INT NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         text TEXT NOT NULL,
                         createTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         isRead BOOLEAN NOT NULL DEFAULT FALSE
                         
);