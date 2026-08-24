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
  `descripcion` text NOT NULL,
  `horas` decimal(5,2) NOT NULL,
  `modulo` varchar(100) NOT NULL,
  `hoja_tiempo_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `hoja_tiempo_id` (`hoja_tiempo_id`),
  CONSTRAINT `detalle_actividad_ibfk_1` FOREIGN KEY (`hoja_tiempo_id`) REFERENCES `hoja_tiempo` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_actividad`
--

LOCK TABLES `detalle_actividad` WRITE;
/*!40000 ALTER TABLE `detalle_actividad` DISABLE KEYS */;
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
  `proyecto` varchar(100) NOT NULL,
  `recurso_id` int NOT NULL,
  `periodo` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `recurso_id` (`recurso_id`),
  CONSTRAINT `hoja_tiempo_ibfk_1` FOREIGN KEY (`recurso_id`) REFERENCES `recurso` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoja_tiempo`
--

LOCK TABLES `hoja_tiempo` WRITE;
/*!40000 ALTER TABLE `hoja_tiempo` DISABLE KEYS */;
/*!40000 ALTER TABLE `hoja_tiempo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recurso`
--

DROP TABLE IF EXISTS `recurso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recurso` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `identificacion` varchar(50) NOT NULL,
  `tarifa_base` decimal(10,2) NOT NULL,
  `tipo` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recurso`
--

LOCK TABLES `recurso` WRITE;
/*!40000 ALTER TABLE `recurso` DISABLE KEYS */;
/*!40000 ALTER TABLE `recurso` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-23 21:24:43
