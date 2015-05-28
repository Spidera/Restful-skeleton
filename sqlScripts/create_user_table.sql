CREATE TABLE user (
  user_id       INT         NOT NULL AUTO_INCREMENT,
  given_name    VARCHAR(50) NOT NULL,
  family_name   VARCHAR(50),
  email_address VARCHAR(50) NOT NULL,
  password VARCHAR(10) NOT NULL,
  PRIMARY KEY (user_id)
)
