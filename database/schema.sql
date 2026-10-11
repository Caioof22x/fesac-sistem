create table usuario (
    id serial primary key,
    nome varchar(100) not null,
    email varchar(150) not null unique,
    senha varchar(255) not null  
);

create table sala (
    id serial primary key,
    nome varchar(100) not null,
    turma varchar(50) not null
);

create table criterio (
    id serial primary key,
    nome varchar(100) not null,
    peso_maximo numeric(5,2) not null
);

create table nota (
    id serial primary key,
    valor numeric(5,2) not null,
    fk_sala integer not null references sala(id),
    fk_criterio integer not null references criterio(id),
    fk_usuario integer not null references usuario(id) 
);

alter table usuario enable row level security;
alter table sala enable row level security;
alter table criterio enable row level security;
alter table nota enable row level security;
