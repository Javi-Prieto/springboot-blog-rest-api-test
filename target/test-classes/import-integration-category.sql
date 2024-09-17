delete from users_roles;
delete from users;
delete from comments;
delete from posts;
delete from roles;
delete from categories;

INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO users (id, name, username, email, password) VALUES (2, 'User', 'UserUsername', 'user@gmail.com', '1234');
INSERT INTO users (id,name,username,email,password) VALUES (1, 'Admin', 'AdminUsername', 'admin@gmail.com','admin');
INSERT INTO categories (id, name, description) VALUES (1000, 'Category', 'Category description');
INSERT INTO categories (id, name, description) VALUES (1001, 'Category2', 'Category2 description');
INSERT INTO posts (id, title, description, content, category_id) VALUES (1000, 'Post title', 'Post Description', 'Post content', 1000);
INSERT INTO posts (id, title, description, content, category_id) VALUES (1001, 'Post title2', 'Post Description2', 'Post content2', 1001);
INSERT INTO users_roles (user_id,role_id) VALUES (1,1);
INSERT INTO users_roles (user_id,role_id) VALUES (2,2);