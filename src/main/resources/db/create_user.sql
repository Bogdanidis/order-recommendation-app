DROP
USER if exists 'nikos'@'localhost';

CREATE
USER 'nikos'@'localhost' IDENTIFIED BY 'nikos';

GRANT ALL PRIVILEGES ON  *.* TO
'nikos'@'localhost';