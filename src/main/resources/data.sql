CREATE TABLE Admin (
                       email VARCHAR(255) NOT NULL PRIMARY KEY,
                       uid INT NOT NULL UNIQUE,
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
                      roomType INT NOT NULL,
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
                      uid INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                      role ENUM('Admin', 'Lecturer', 'Student') NOT NULL
);

CREATE TABLE Record (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        roomId INT NOT NULL,
                        userId INT ,
                        startTime DATETIME NOT NULL,
                        endTime DATETIME NOT NULL,
                        recordTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        hasCheckedIn BOOLEAN NOT NULL DEFAULT FALSE,
                        isCancelled BOOLEAN NOT NULL DEFAULT FALSE,
                        statusId tinyint NOT NULL DEFAULT 1
);

CREATE TABLE Message (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         uid INT NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         text TEXT NOT NULL,
                         createTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         isRead BOOLEAN NOT NULL DEFAULT FALSE,
                         sender VARCHAR(255) NOT NULL
);

-- 插入 Admin 数据
INSERT INTO Admin (email, uid, name, phone) VALUES
                                                ('admin1@example.com', 1, 'Admin One', '1234567890'),
                                                ('admin2@example.com', 2, 'Admin Two', '0987654321');

-- 插入 Lecturer 数据
INSERT INTO Lecturer (email, uid, name, phone) VALUES
                                                   ('lecturer1@example.com', 3, 'Lecturer One', '1112223333'),
                                                   ('lecturer2@example.com', 4, 'Lecturer Two', '4445556666');

-- 插入 Room 数据
INSERT INTO Room (roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, roomType, url) VALUES
                                                                                                                        ('Meeting Room A', 20, FALSE, 'Foreign Language Network Building, 109, 1st floor', TRUE, TRUE, FALSE, FALSE, 'meeting room', 'https://706f-poppy-5gvizuof36ac74d2-1324175882.tcb.qcloud.la/booking%20system/R-C.jpg?sign=7387659e0e8855bcfd75834175f4dd00&t=1741172449'),
                                                                                                                        ('Meeting Room B', 10, TRUE, 'Foreign Language Network Building, 634, 6th floor', FALSE, FALSE, TRUE, FALSE, 'meeting room', 'https://706f-poppy-5gvizuof36ac74d2-1324175882.tcb.qcloud.la/booking%20system/R-C.jpg?sign=7387659e0e8855bcfd75834175f4dd00&t=1741172449'),
                                                                                                                        ('Activities Room A', 40, FALSE, 'Foreign Language Network Building, 635, 6th floor', TRUE, TRUE, FALSE, FALSE, 'activities room', 'https://706f-poppy-5gvizuof36ac74d2-1324175882.tcb.qcloud.la/booking%20system/R-C.jpg?sign=7387659e0e8855bcfd75834175f4dd00&t=1741172449'),
                                                                                                                        ('Activities Room B', 25, TRUE, 'Foreign Language Network Building, 623, 6th floor', TRUE, FALSE, TRUE, TRUE, 'activities room', 'https://706f-poppy-5gvizuof36ac74d2-1324175882.tcb.qcloud.la/booking%20system/R-C.jpg?sign=7387659e0e8855bcfd75834175f4dd00&t=1741172449'),
                                                                                                                        ('Classroom A', 15, FALSE, 'Foreign Language Network Building, 101, 1st floor', FALSE, TRUE, FALSE, FALSE, 'teaching room', 'https://706f-poppy-5gvizuof36ac74d2-1324175882.tcb.qcloud.la/booking%20system/R-C.jpg?sign=7387659e0e8855bcfd75834175f4dd00&t=1741172449'),
                                                                                                                        ('Classroom B', 12, TRUE, 'Foreign Language Network Building, 102, 1st floor', TRUE, TRUE, TRUE, TRUE, 'teaching room', 'https://706f-poppy-5gvizuof36ac74d2-1324175882.tcb.qcloud.la/booking%20system/R-C.jpg?sign=7387659e0e8855bcfd75834175f4dd00&t=1741172449');



-- 插入 Student 数据
INSERT INTO Student (email, uid, name, phone, breakTimer) VALUES
                                                              ('student1@example.com', 5, 'Student One', '7778889999', 10),
                                                              ('student2@example.com', 6, 'Student Two', '6665554444', 5);

-- 插入 User 数据
INSERT INTO User (uid, role) VALUES
                                 (1, 'Admin'),
                                 (3, 'Lecturer'),
                                 (5, 'Student');

-- 插入 Record 数据
INSERT INTO Record (roomId, userId, startTime, endTime, recordTime, hasCheckedIn, isCancelled, statusId) VALUES
                                                                                                             (1, 101, '2025-03-06 10:00:00', '2025-03-06 12:00:00', NOW(), TRUE, FALSE, 1),
                                                                                                             (2, 102, '2025-03-06 14:00:00', '2025-03-06 16:00:00', NOW(), FALSE, FALSE, 1),
                                                                                                             (3, 103, '2025-03-07 09:00:00', '2025-03-07 11:00:00', NOW(), TRUE, FALSE, 1),
                                                                                                             (4, 104, '2025-03-07 13:30:00', '2025-03-07 15:30:00', NOW(), FALSE, FALSE, 1),
                                                                                                             (5, 105, '2025-03-08 08:00:00', '2025-03-08 10:00:00', NOW(), TRUE, FALSE, 1);


-- 插入 Message 数据
INSERT INTO Message (uid, title, text, isRead, sender) VALUES
                                                           (5, 'Room Booking Confirmation', 'Your booking for Room A is confirmed.', FALSE, 'System'),
                                                           (3, 'Room Booking Rejected', 'Your request for Room B has been denied.', TRUE, 'Admin');
