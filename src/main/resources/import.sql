-- PW:test
INSERT INTO user_info (email, password, auth) VALUES ('test1@email.com', '$2a$10$7wW..ukoGMSahucTLVRF4Ok3QeBebsLBu.KEahlp5idXO016y5Srm', 'ROLE_USER');
INSERT INTO user_info (email, password, auth) VALUES ('test2@email.com', '$2a$10$7wW..ukoGMSahucTLVRF4Ok3QeBebsLBu.KEahlp5idXO016y5Srm', 'ROLE_USER');
INSERT INTO user_info (email, password, auth) VALUES ('test3@email.com', '$2a$10$7wW..ukoGMSahucTLVRF4Ok3QeBebsLBu.KEahlp5idXO016y5Srm', 'ROLE_USER');

--
INSERT INTO news_provider (name) VALUES ('naver'), ('daum');

--
INSERT INTO keyword (name) VALUES ('선거'), ('코로나'), ('대선'), ('오미크론');

--
INSERT INTO user_keyword (user_code, keyword_code, provider_code) values (1, 1, 1), (1, 2, 1), (1, 2, 2);
INSERT INTO user_keyword (user_code, keyword_code, provider_code) values (2, 1, 1), (2, 2, 1);
INSERT INTO user_keyword (user_code, keyword_code, provider_code) values (3, 2, 1), (2, 3, 1);
