delete from users_roles;
delete from users;
delete from comments;
delete from posts;
delete from roles;
delete from categories;
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO users (id, name, username, email, password) VALUES (2, 'Pedro', 'ToRechulon', 'pedro@gmail.com', '1234');
insert into users (id,name,username,email,password) values (1, 'Juan', 'lacabra_7', 'juanillolacabra@gmail.com','lacabra');
insert into categories (id, name, description) values (1010, 'hola', 'holas a mundo');
insert into posts (id, title, description, content, category_id) values (1000, 'hola', 'hola mundo java', 'System.out.println("Hola Mundo")', 1010);

insert into comments (id,body,email,name,post_id) values (1,'bodyyyyyyyyyyyyyyy','angel@gmail.com','angel',1000);
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles (user_id, role_id) VALUES (2, 2);