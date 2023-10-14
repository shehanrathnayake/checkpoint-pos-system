CREATE TABLE IF NOT EXISTS user_role (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS user (
    user_id VARCHAR(10) PRIMARY KEY ,
    first_name VARCHAR(20) NOT NULL ,
    last_name VARCHAR(20) NOT NULL ,
    password VARCHAR(100) NOT NULL ,
    user_role_id INT NOT NULL ,
    CONSTRAINT fk_user_role FOREIGN KEY user (user_role_id) REFERENCES user_role (id)
);

CREATE TABLE IF NOT EXISTS user_profile_picture (
    user_id VARCHAR(10) PRIMARY KEY ,
    profile_picture BLOB(2500) NOT NULL ,
    CONSTRAINT fk_user_profile_picture FOREIGN KEY user_profile_picture (user_id) REFERENCES user (user_id)
);

