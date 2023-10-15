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
    gender VARCHAR(10) NOT NULL ,
    CONSTRAINT fk_user_role FOREIGN KEY user (user_role_id) REFERENCES user_role (id)
);

CREATE TABLE IF NOT EXISTS user_profile_picture (
    user_id VARCHAR(10) PRIMARY KEY ,
    profile_picture BLOB(2500) NOT NULL ,
    CONSTRAINT fk_user_profile_picture FOREIGN KEY user_profile_picture (user_id) REFERENCES user (user_id)
);

CREATE TABLE IF NOT EXISTS customer (
    customer_id VARCHAR(10) PRIMARY KEY ,
    name VARCHAR(100) NOT NULL ,
    address VARCHAR(300) NOT NULL ,
    phone VARCHAR(15) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS item (
    item_code VARCHAR(30) PRIMARY KEY ,
    description VARCHAR(200) NOT NULL ,
    qty INT NOT NULL ,
    unit_price DECIMAL(8,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS "order" (
    order_id VARCHAR(10) PRIMARY KEY ,
    date DATE NOT NULL ,
    user_id VARCHAR(10) NOT NULL ,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user (user_id)
);

CREATE TABLE IF NOT EXISTS customer_order (
    customer_id VARCHAR(10) ,
    order_id VARCHAR(10) ,
    CONSTRAINT pk_customer_order PRIMARY KEY (customer_id,order_id) ,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES "order" (order_id)
);

CREATE TABLE IF NOT EXISTS order_item (
    item_code VARCHAR(30) ,
    order_id VARCHAR(10) ,
    qty INT NOT NULL ,
    unit_price DECIMAL(8,2) NOT NULL ,
    CONSTRAINT pk_order_item PRIMARY KEY (item_code, order_id) ,
    CONSTRAINT fk_item_code FOREIGN KEY (item_code) REFERENCES item (item_code) ,
    CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES "order" (order_id)
);