
create table nummers (
    nummer integer,
    class varchar(255) not null,
    primary key (class)
);

#commentaar
create table verzamelcamping (
    id integer not null,
    naam varchar(255),  #commentaar
    email varchar(255),  --nog meer commentaar
    fax varchar(25),
    straat varchar(255),
    postcode varchar(10),
    plaats varchar(255),
    land varchar(25),
    longitude double,
    latitude double,
    telefoon varchar(25),
    url varchar(255),
    primary key (id)
);

create table camping (
    id integer not null,
    hoortbij integer not null,
    bronid integer not null,
    naam varchar(255),
    email varchar(255),
    fax varchar(25),
    straat varchar(255),
    postcode varchar(255),
    plaats varchar(255),
    land varchar(255),
    longitude double,
    latitude double,
    telefoon varchar(25),
    url varchar(255),
    primary key (id)
);

create table bron (
    id integer not null,
    naam varchar(255),
    url varchar(255),
    primary key(id)
);

create table geolocatie (
    id integer not null,
    zoekstring varchar(255),
    latitude double,
    longitude double,
    primary key(id)
);

create index geozoek on geolocatie(zoekstring);
