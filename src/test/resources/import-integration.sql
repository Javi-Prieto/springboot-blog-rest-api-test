delete from users;
delete from comments;
delete from posts;
delete from roles;
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO users (id, name, username, email, password) VALUES (301, 'Pedro', 'ToRechulon', 'pedro@gmail.com', '$2b$12$FV5uUswMRP9NMgZGDeMm6ejlQ37SIAe6biliXr5Dc1dEL4sQLB6Yq');
insert into users (id,name,username,email,password) values (300, 'Juan', 'lacabra_7', 'juanillolacabra@gmail.com','lacabra');
