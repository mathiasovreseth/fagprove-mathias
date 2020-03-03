CREATE USER fagprove WITH PASSWORD 'mathias';
CREATE DATABASE fagprove
    WITH ENCODING='UTF8'
    OWNER=fagprove
    TEMPLATE=template0
    LC_COLLATE='en_US.UTF-8'
    LC_CTYPE='en_US.UTF-8'
    CONNECTION LIMIT=-1;
\c fagprove
ALTER SCHEMA public OWNER TO fagprove;
