CREATE DATABASE  IF NOT EXISTS `proyecto_2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `proyecto_2`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: proyecto_2
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `detalle_actividad`
--

DROP TABLE IF EXISTS `detalle_actividad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_actividad` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `descripcion` varchar(500) NOT NULL,
  `horas` decimal(5,2) NOT NULL,
  `modulo` varchar(100) NOT NULL,
  `hoja_tiempo_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_detalle_fecha_modulo` (`fecha`,`modulo`,`hoja_tiempo_id`),
  KEY `fk_detalle_hoja` (`hoja_tiempo_id`),
  CONSTRAINT `fk_detalle_hoja` FOREIGN KEY (`hoja_tiempo_id`) REFERENCES `hoja_tiempo` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ck_detalle_horas` CHECK (((`horas` > 0) and (`horas` <= 24)))
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_actividad`
--

LOCK TABLES `detalle_actividad` WRITE;
/*!40000 ALTER TABLE `detalle_actividad` DISABLE KEYS */;
INSERT INTO `detalle_actividad` VALUES (1,'2026-08-24','Análisis de requerimientos',6.00,'Gestión de proyectos',1),(2,'2026-08-24','Diseño de pantalla de inicio',8.00,'Login',2),(3,'2026-08-25','Validación de usuario',8.00,'Login',3),(4,'2026-08-25','Pruebas Sistema',8.00,'Modulo 1',7),(5,'2026-08-03','Desarrollo modulo login',4.00,'Backend',8),(6,'2026-08-04','Testing',2.00,'Testing',8),(7,'2026-09-01','creacionBDD',6.00,'3',10),(11,'2026-08-04','Desarrollo prueba Ana 1',5.00,'Modulo A',15),(12,'2026-08-11','Desarrollo prueba Ana 2',5.00,'Modulo B',16),(13,'2026-08-05','Desarrollo Yapu 1',6.00,'Modulo C',17),(14,'2026-08-18','Desarrollo Yapu 2',9.00,'Modulo D',18),(15,'2026-08-06','Desarrollo Leiton 1',12.00,'Modulo E',19),(16,'2026-08-25','Desarrollo Leiton 2',8.00,'Modulo F',20),(17,'2026-08-06','Desarrollo Gabriela 1',10.00,'Modulo G',21),(18,'2026-08-12','Desarrollo Gabriela 2',8.00,'Modulo H',22),(19,'2026-09-07','Arquitectura de catalogo de productos',8.00,'Backend',23),(20,'2026-09-08','Desarrollo de API de carrito',7.50,'Backend',23),(21,'2026-09-09','Diseno de tabla de pedidos',6.00,'Database',23),(22,'2026-09-10','Implementacion de pasarela de pago',8.00,'Backend',23),(23,'2026-09-11','Testing de checkout completo',5.50,'Testing',23),(24,'2026-09-12','Configuracion de ambiente de produccion',4.00,'DevOps',23),(25,'2026-09-13','Documentacion de endpoints',3.50,'Backend',23),(26,'2026-09-07','Diseno de modulo de empleados',7.00,'Backend',25),(27,'2026-09-08','Desarrollo de calculo de sueldos',8.00,'Backend',25),(28,'2026-09-09','Implementacion de deducciones',6.50,'Backend',25),(29,'2026-09-10','Tabla de feriados y ausencias',5.00,'Database',25),(30,'2026-09-11','Testing de calculo de aportes',6.00,'Testing',25),(31,'2026-09-12','Generacion de rol de pagos',4.50,'Reporting',25),(32,'2026-09-13','Revision de reglas de negocio',3.00,'Backend',25),(33,'2026-10-05','Arquitectura de app de delivery',8.00,'Backend',27),(34,'2026-10-06','Desarrollo de seguimiento en tiempo real',7.50,'Backend',27),(35,'2026-10-07','Diseno de interfaz de repartidor',6.00,'Frontend',27),(36,'2026-10-08','Integracion con mapas',7.00,'Backend',27),(37,'2026-10-09','Testing de notificaciones push',5.00,'Testing',27),(38,'2026-10-10','Optimizacion de rutas',8.00,'Backend',27),(39,'2026-10-05','Maquetacion de pantalla principal',6.00,'Frontend',28),(40,'2026-10-06','Desarrollo de modulo de categorias',7.00,'Frontend',28),(41,'2026-10-07','Integracion con API de restaurantes',8.00,'Backend',28),(42,'2026-10-08','Implementacion de busqueda',5.50,'Backend',28),(43,'2026-10-09','Pruebas en multiples dispositivos',6.00,'Testing',28),(44,'2026-09-07','Diseno de arquitectura de catalogo',8.00,'Backend',30),(45,'2026-09-08','Desarrollo de modulo de productos',7.50,'Backend',30),(46,'2026-09-09','Configuracion de pasarela de pago',6.00,'Backend',30),(47,'2026-09-10','Implementacion de carrito de compras',8.00,'Frontend',30),(48,'2026-09-11','Testing de flujo de checkout',5.50,'Testing',30),(49,'2026-09-12','Optimizacion de consultas de inventario',4.00,'Database',30),(50,'2026-09-13','Documentacion de endpoints',3.50,'Backend',30),(51,'2026-09-07','Dise?o de prototipos de la tienda',6.00,'Frontend',29),(52,'2026-09-08','Maquetado de pagina principal',7.50,'Frontend',29),(53,'2026-09-09','Diseno de carrito de compras',6.00,'Frontend',29),(54,'2026-09-10','Testeo de usabilidad',5.00,'Testing',29),(55,'2026-09-11','Correccion de ajustes de UI',4.50,'Frontend',29),(56,'2026-09-12','Documentacion del diseno',3.00,'Frontend',29),(57,'2026-09-13','Revision final de interfaces',4.00,'Frontend',29),(58,'2026-09-07','Configuracion de base de datos',7.00,'Database',31),(59,'2026-09-08','Desarrollo de API de pedidos',8.00,'Backend',31),(60,'2026-09-09','Implementacion de usuarios',6.50,'Backend',31),(61,'2026-09-10','Integracion con pasarela de pago',7.00,'Backend',31),(62,'2026-09-11','Optimizacion de consultas',5.00,'Database',31),(63,'2026-09-12','Testing de API',6.00,'Testing',31),(64,'2026-09-13','Deploy a ambiente de pruebas',3.50,'DevOps',31),(65,'2026-09-15','Arquitectura de modulo de nomina',8.00,'Backend',32),(66,'2026-09-16','Desarrollo de calculo de salarios',7.50,'Backend',32),(67,'2026-09-17','Implementacion de roles y permisos',6.00,'Backend',32),(68,'2026-09-18','Integracion con sistema de RRHH',7.00,'Backend',32),(69,'2026-09-19','Testing de calculos de impuestos',5.50,'Testing',32),(70,'2026-09-20','Generacion de reportes de n?mina',4.50,'Reporting',32),(71,'2026-09-21','Documentacion del modulo',3.00,'Backend',32),(72,'2026-09-07','Configuracion de entorno de desarrollo',6.00,'DevOps',35),(73,'2026-09-08','Desarrollo de modulo de seguridad',7.50,'Backend',35),(74,'2026-09-09','Implementacion de registro de horas',8.00,'Backend',35),(75,'2026-09-10','Integracion con base de datos',6.50,'Database',35),(76,'2026-09-11','Testing del flujo completo',5.00,'Testing',35),(77,'2026-09-12','Optimizacion de consultas',4.50,'Database',35),(78,'2026-09-13','Documentacion del sistema',3.50,'Backend',35);
/*!40000 ALTER TABLE `detalle_actividad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hoja_tiempo`
--

DROP TABLE IF EXISTS `hoja_tiempo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hoja_tiempo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `proyecto_id` int NOT NULL,
  `recurso_id` int NOT NULL,
  `periodo` varchar(50) NOT NULL,
  `estado` enum('Borrador','Enviada','Aprobada','Rechazada','Inactiva') NOT NULL DEFAULT 'Borrador',
  PRIMARY KEY (`id`),
  KEY `fk_hoja_proyecto` (`proyecto_id`),
  KEY `fk_hoja_recurso` (`recurso_id`),
  CONSTRAINT `fk_hoja_proyecto` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`),
  CONSTRAINT `fk_hoja_recurso` FOREIGN KEY (`recurso_id`) REFERENCES `recurso` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoja_tiempo`
--

LOCK TABLES `hoja_tiempo` WRITE;
/*!40000 ALTER TABLE `hoja_tiempo` DISABLE KEYS */;
INSERT INTO `hoja_tiempo` VALUES (1,1,1,'2026-08-24 / 2026-08-30','Aprobada'),(2,1,4,'2026-08-24 / 2026-08-28','Enviada'),(3,1,4,'2026-08-24 / 2026-08-28','Enviada'),(4,1,4,'2026-08-24 / 2026-08-28','Enviada'),(5,1,4,'2026-08-24 / 2026-08-28','Inactiva'),(6,1,4,'2026-08-24 / 2026-08-28','Inactiva'),(7,1,4,'2026-08-24 / 2026-08-28','Enviada'),(8,1,4,'2026-08-03 / 2026-08-09','Enviada'),(9,1,5,'2026-08-31 / 2026-09-04','Inactiva'),(10,1,5,'2026-08-31 / 2026-09-04','Borrador'),(13,2,1,'2026-08-03 / 2026-08-09','Aprobada'),(14,2,1,'2026-08-03 / 2026-08-09','Aprobada'),(15,2,1,'2026-08-03 / 2026-08-09','Aprobada'),(16,2,1,'2026-08-10 / 2026-08-16','Aprobada'),(17,2,4,'2026-08-03 / 2026-08-09','Aprobada'),(18,2,4,'2026-08-17 / 2026-08-23','Enviada'),(19,2,5,'2026-08-03 / 2026-08-09','Enviada'),(20,2,5,'2026-08-24 / 2026-08-30','Aprobada'),(21,2,6,'2026-08-03 / 2026-08-09','Aprobada'),(22,2,6,'2026-08-10 / 2026-08-16','Aprobada'),(23,3,7,'2026-09-07 / 2026-09-13','Aprobada'),(24,3,7,'2026-09-14 / 2026-09-20','Enviada'),(25,4,8,'2026-09-07 / 2026-09-13','Aprobada'),(26,4,8,'2026-09-14 / 2026-09-20','Borrador'),(27,5,9,'2026-10-05 / 2026-10-11','Enviada'),(28,5,10,'2026-10-05 / 2026-10-11','Aprobada'),(29,3,1,'2026-09-07 / 2026-09-13','Aprobada'),(30,3,6,'2026-09-07 / 2026-09-13','Enviada'),(31,3,4,'2026-09-07 / 2026-09-13','Borrador'),(32,4,5,'2026-09-07 / 2026-09-13','Aprobada'),(33,1,7,'2026-09-01 / 2026-09-07','Enviada'),(34,2,8,'2026-09-01 / 2026-09-07','Borrador'),(35,1,3,'2026-09-07 / 2026-09-13','Aprobada');
/*!40000 ALTER TABLE `hoja_tiempo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proyecto`
--

DROP TABLE IF EXISTS `proyecto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proyecto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `cliente` varchar(150) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin_estimada` date NOT NULL,
  `estado` enum('Activo','Inactivo','Finalizado') NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_proyecto_codigo` (`codigo`),
  CONSTRAINT `ck_proyecto_fechas` CHECK ((`fecha_inicio` <= `fecha_fin_estimada`))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proyecto`
--

LOCK TABLES `proyecto` WRITE;
/*!40000 ALTER TABLE `proyecto` DISABLE KEYS */;
INSERT INTO `proyecto` VALUES (1,'PRY-001','Sistema de Control de Horas','Empresa VEX','2026-08-24','2026-09-30','Activo'),(2,'2','Proyecto Factura','Anita','2026-01-15','2027-01-15','Activo'),(3,'PRY-003','Plataforma E-commerce','Tienda Online SA','2026-09-01','2027-03-31','Activo'),(4,'PRY-004','Sistema de Nomina','Grupo Empresarial','2026-09-15','2027-04-30','Activo'),(5,'PRY-005','App de Delivery','FastFood Express','2026-10-01','2027-05-31','Activo');
/*!40000 ALTER TABLE `proyecto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proyecto_recurso`
--

DROP TABLE IF EXISTS `proyecto_recurso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proyecto_recurso` (
  `proyecto_id` int NOT NULL,
  `recurso_id` int NOT NULL,
  PRIMARY KEY (`proyecto_id`,`recurso_id`),
  KEY `fk_pr_recurso` (`recurso_id`),
  CONSTRAINT `fk_pr_proyecto` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`),
  CONSTRAINT `fk_pr_recurso` FOREIGN KEY (`recurso_id`) REFERENCES `recurso` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proyecto_recurso`
--

LOCK TABLES `proyecto_recurso` WRITE;
/*!40000 ALTER TABLE `proyecto_recurso` DISABLE KEYS */;
INSERT INTO `proyecto_recurso` VALUES (1,1),(2,1),(3,1),(1,3),(1,4),(2,4),(3,4),(1,5),(2,5),(4,5),(2,6),(3,6),(3,7),(4,8),(5,9),(5,10),(3,11);
/*!40000 ALTER TABLE `proyecto_recurso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recurso`
--

DROP TABLE IF EXISTS `recurso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recurso` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cedula` int DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `correo` varchar(150) NOT NULL,
  `rol` varchar(50) NOT NULL,
  `tarifa_base` decimal(10,2) NOT NULL,
  `tipo` enum('Junior','Senior') NOT NULL,
  `estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_recurso_correo` (`correo`),
  UNIQUE KEY `cedula` (`cedula`),
  CONSTRAINT `ck_recurso_tarifa` CHECK ((`tarifa_base` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recurso`
--

LOCK TABLES `recurso` WRITE;
/*!40000 ALTER TABLE `recurso` DISABLE KEYS */;
INSERT INTO `recurso` VALUES (1,NULL,'Ana López','ana.lopez@ejemplo.com','Desarrollador',25.00,'Junior','Activo'),(2,NULL,'Carlos Pérez','carlos.perez@ejemplo.com','Desarrollador',60.00,'Senior','Inactivo'),(3,NULL,'Lesly','desarrollador@proyecto.com','Desarrollador',25.00,'Junior','Activo'),(4,NULL,'Kevin Yapu','kevinY23@gmail.com','Desarrollador',25.00,'Junior','Activo'),(5,1005212772,'Kevin Leiton','leitin@gmail.com','Desarrollador',60.00,'Senior','Activo'),(6,1003297791,'Gabriela Valladares','gaby@gmail.com','Desarrollador',60.00,'Senior','Activo'),(7,912345678,'Sofia Torres','sofia.torres@empresa.com','Desarrollador',60.00,'Senior','Activo'),(8,1723456789,'Diego Herrera','diego.herrera@empresa.com','Desarrollador',25.00,'Junior','Activo'),(9,1876543219,'Valentina Rios','valentina.rios@empresa.com','Desarrollador',60.00,'Senior','Activo'),(10,543210987,'Mateo Castillo','mateo.castillo@empresa.com','Desarrollador',25.00,'Junior','Activo'),(11,601234567,'Daniel Prueba','daniel.prueba@empresa.com','Desarrollador',25.00,'Junior','Activo');
/*!40000 ALTER TABLE `recurso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reporte_estadistico`
--

DROP TABLE IF EXISTS `reporte_estadistico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reporte_estadistico` (
  `id` int NOT NULL AUTO_INCREMENT,
  `proyecto_id` int NOT NULL,
  `desarrollador` varchar(100) NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `total_horas` decimal(10,2) NOT NULL,
  `fecha_generacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_reporte_est_proyecto` (`proyecto_id`),
  CONSTRAINT `fk_reporte_est_proyecto` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`),
  CONSTRAINT `ck_reporte_dias` CHECK ((`fecha_inicio` <= `fecha_fin`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reporte_estadistico`
--

LOCK TABLES `reporte_estadistico` WRITE;
/*!40000 ALTER TABLE `reporte_estadistico` DISABLE KEYS */;
/*!40000 ALTER TABLE `reporte_estadistico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `rol` enum('Administrador','Desarrollador') NOT NULL,
  `tipo` enum('Gestor','Recurso') NOT NULL,
  `recurso_id` int DEFAULT NULL,
  `estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_nombre` (`usuario`),
  KEY `fk_usuario_recurso` (`recurso_id`),
  CONSTRAINT `fk_usuario_recurso` FOREIGN KEY (`recurso_id`) REFERENCES `recurso` (`id`),
  CONSTRAINT `ck_usuario_tipo_recurso` CHECK ((((`tipo` = _utf8mb4'Recurso') and (`recurso_id` is not null)) or ((`tipo` = _utf8mb4'Gestor') and (`recurso_id` is null))))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'admin','admin123','Administrador','Gestor',NULL,'Activo'),(2,'desarrollador','dev123','Desarrollador','Recurso',3,'Activo'),(3,'KevinY','keviny123','Desarrollador','Recurso',4,'Activo'),(4,'Leiton','leiton123','Desarrollador','Recurso',5,'Activo'),(5,'gg','123a','Desarrollador','Recurso',6,'Activo'),(6,'sofia','sofia123','Desarrollador','Recurso',7,'Activo'),(7,'diego','diego123','Desarrollador','Recurso',8,'Activo'),(8,'valentina','valentina123','Desarrollador','Recurso',9,'Activo'),(9,'mateo','mateo123','Desarrollador','Recurso',10,'Activo'),(10,'daniel','dev123','Desarrollador','Recurso',11,'Activo');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-01 22:21:52
