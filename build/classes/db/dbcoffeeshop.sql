create database coffeeshop;
use coffeeshop;

create table clientes(
	IdCliente int primary key auto_increment,
	NombreC varchar(30) not null,
	ApellidoPaternoC varchar(30) not null,
	ApellidoMaternoC varchar(30) not null,
	CorreoE varchar(50) not null,
	PasswordC varchar(16) not null,
	Estado boolean not null
);

create table empleados(
	IdEmpleado int primary key auto_increment,
	NombreE varchar(30) not null,
	ApellidoPaternoE varchar(30) not null,
	ApellidoMaternoE varchar(30) not null,
	CorreoE varchar(50) not null,
	PasswordE varchar(16) not null,
	Estado boolean not null
);

create table pedidos(
	CodigoPedido int primary key auto_increment,
	Fecha date not null,
	Hora time not null,
	Pendiente boolean not null,
	Cancelado boolean not null,
	IdCliente int not null,
	foreign key (IdCliente) references clientes(IdCliente)
);

create table productos(
	IdProducto int primary key auto_increment,
	NombreP varchar(30) not null,
	Descripcion varchar(50) not null,
	Precio float not null,
	Estado boolean not null
);

create table carrito(
	IdCliente int not null,
	IdProducto int not null,
	Detalles varchar(50) not null,
	Cantidad int not null,
	Total float not null,
	foreign key (IdCliente) references clientes (IdCliente),
	foreign key (IdProducto) references productos (IdProducto)
);

create table detallepedidos(
	CodigoPedido int not null,
	IdProducto int not null,
	Cantidad int not null,
	Importe float not null,
	Detalles varchar(50),
	foreign key (CodigoPedido) references pedidos (CodigoPedido),
	foreign key (IdProducto) references productos(IdProducto)
);

create table estadoPedidos(
	CodigoPedido int not null,
    IdEmpleado int not null,
    Estado varchar(20),
    foreign key (CodigoPedido) references pedidos(CodigoPedido),
    foreign key (IdEmpleado) references empleados (IdEmpleado)
);