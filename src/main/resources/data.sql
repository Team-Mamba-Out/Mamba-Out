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
                      uid INT NOT NULL PRIMARY KEY,
                      role ENUM('Admin', 'Lecturer', 'Student') NOT NULL
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
                                                                                                                                 ('Conference Room A', 20, FALSE, 'Building 1 - Floor 2', TRUE, TRUE, FALSE, FALSE, 1, 'http://example.com/roomA'),
                                                                                                                                 ('Lecture Hall 101', 100, TRUE, 'Building 2 - Floor 1', TRUE, FALSE, TRUE, FALSE, 2, 'http://example.com/lecture101'),
                                                                                                                                 ('Small Meeting Room', 8, FALSE, 'Building 3 - Floor 1', FALSE, FALSE, FALSE, FALSE, 3, 'http://example.com/meetingRoom'),
                                                                                                                                 ('Computer Lab', 30, TRUE, 'Building 4 - Floor 3', TRUE, TRUE, FALSE, FALSE, 4, 'http://example.com/computerLab'),
                                                                                                                                 ('VIP Lounge', 150, FALSE, 'Building 5 - Floor 2', TRUE, TRUE, TRUE, FALSE, 5, 'http://example.com/vipLounge');

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
INSERT INTO Record (roomId, userId, startTime, endTime, recordTime, hasCheckedIn) VALUES
                                                                                      (1, 101, '2025-03-06 10:00:00', '2025-03-06 12:00:00', NOW(), TRUE),
                                                                                      (2, 102, '2025-03-06 14:00:00', '2025-03-06 16:00:00', NOW(), FALSE),
                                                                                      (3, 103, '2025-03-07 09:00:00', '2025-03-07 11:00:00', NOW(), TRUE),
                                                                                      (4, 104, '2025-03-07 13:30:00', '2025-03-07 15:30:00', NOW(), FALSE),
                                                                                      (5, 105, '2025-03-08 08:00:00', '2025-03-08 10:00:00', NOW(), TRUE);

-- 插入 Message 数据
INSERT INTO Message (uid, title, text, isRead, sender) VALUES
                                                           (5, 'Room Booking Confirmation', 'Your booking for Room A is confirmed.', FALSE, 'System'),
                                                           (3, 'Room Booking Rejected', 'Your request for Room B has been denied.', TRUE, 'Admin');
