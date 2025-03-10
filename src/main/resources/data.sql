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
                      url VARCHAR(500) DEFAULT NULL
);

CREATE TABLE Room_User (
                           room_id INT NOT NULL,
                           uid INT NOT NULL,
                           PRIMARY KEY (room_id, uid),
                           FOREIGN KEY (room_id) REFERENCES Room(id) ON DELETE CASCADE,
                           FOREIGN KEY (uid) REFERENCES User(uid) ON DELETE CASCADE
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

# -- 插入用户数据
# INSERT INTO User (uid, role) VALUES (1, 'Admin'), (2, 'Lecturer'), (3, 'Student');
#
# -- 插入管理员数据
# INSERT INTO Admin (email, uid, name, phone) VALUES ('admin@example.com', 1, 'Admin User', '1234567890');
#
# -- 插入讲师数据
# INSERT INTO Lecturer (email, uid, name, phone) VALUES ('lecturer@example.com', 2, 'Lecturer User', '0987654321');
#
# -- 插入学生数据
# INSERT INTO Student (email, uid, name, phone, breakTimer) VALUES ('student@example.com', 3, 'Student User', '1122334455', 0);
#
# -- 插入房间数据
# INSERT INTO Room (roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, roomType, url, permissionType)
# VALUES ('Room 101', 30, FALSE, 'Building A', TRUE, TRUE, FALSE, FALSE, 1, null, 0),
#        ('Room 102', 20, FALSE, 'Building B', TRUE, FALSE, FALSE, FALSE, 1, null, 1),
#        ('Room 103', 25, FALSE, 'Building C', FALSE, TRUE, FALSE, FALSE, 1, null, 2),
#        ('Room 104', 15, FALSE, 'Building D', FALSE, FALSE, FALSE, FALSE, 1, null, 3);
#
# -- 插入特定用户权限数据
# INSERT INTO Room_User (room_id, uid) VALUES (4, 1), (4, 2);
