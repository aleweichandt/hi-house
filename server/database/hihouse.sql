-- phpMyAdmin SQL Dump
-- version 4.1.12
-- http://www.phpmyadmin.net
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 06-10-2014 a las 16:09:11
-- Versión del servidor: 5.6.16
-- Versión de PHP: 5.5.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `hihouse`
--
CREATE DATABASE IF NOT EXISTS `hihouse` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `hihouse`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `dispositivos`
--

DROP TABLE IF EXISTS `dispositivos`;
CREATE TABLE IF NOT EXISTS `dispositivos` (
  `ID_Dispositivo` char(20) NOT NULL,
  `Tipo` char(2) NOT NULL,
  `Ambiente` varchar(20) NOT NULL,
  `Descripcion_Ejec_Voz` varchar(50) NOT NULL,
  `Estado` char(1) NOT NULL,
  `Pin1` int(11) DEFAULT NULL,
  `Pin2` int(11) DEFAULT NULL,
  `Pin3` int(11) DEFAULT NULL,
  `Param1` varchar(10) DEFAULT NULL,
  `Param2` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`ID_Dispositivo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `dispositivos`
--

INSERT INTO `dispositivos` (`ID_Dispositivo`, `Tipo`, `Ambiente`, `Descripcion_Ejec_Voz`, `Estado`, `Pin1`, `Pin2`, `Pin3`, `Param1`, `Param2`) VALUES
('test', '2', 'casa', 'test', '0', 13, -1, -1, NULL, NULL);

--
-- Disparadores `dispositivos`
--
DROP TRIGGER IF EXISTS `delete_device_trigger`;
DELIMITER //
CREATE TRIGGER `delete_device_trigger` BEFORE DELETE ON `dispositivos`
 FOR EACH ROW BEGIN
DELETE FROM perfil_dispositivo WHERE ID_Dispositivo=OLD.ID_Dispositivo;
DELETE FROM simulaciones WHERE ID_Dispositivo=OLD.ID_Dispositivo;
END
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `perfiles`
--

DROP TABLE IF EXISTS `perfiles`;
CREATE TABLE IF NOT EXISTS `perfiles` (
  `ID_Perfil` char(20) NOT NULL,
  `Ambiente` varchar(20) NOT NULL,
  `Descripcion` varchar(50) DEFAULT NULL,
  `ID_Simulador` varchar(20) NOT NULL,
  PRIMARY KEY (`ID_Perfil`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `perfiles`
--

INSERT INTO `perfiles` (`ID_Perfil`, `Ambiente`, `Descripcion`, `ID_Simulador`) VALUES
('default', 'none', NULL, '0');

--
-- Disparadores `perfiles`
--
DROP TRIGGER IF EXISTS `delete_profile_trigger`;
DELIMITER //
CREATE TRIGGER `delete_profile_trigger` BEFORE DELETE ON `perfiles`
 FOR EACH ROW BEGIN
DELETE FROM usuario_perfil WHERE ID_Perfil=OLD.ID_Perfil;
DELETE FROM perfil_dispositivo WHERE ID_Perfil=OLD.ID_Perfil;
DELETE FROM simulaciones WHERE ID_Perfil=OLD.ID_Perfil;
END
//
DELIMITER ;
DROP TRIGGER IF EXISTS `update_profile_trigger`;
DELIMITER //
CREATE TRIGGER `update_profile_trigger` BEFORE UPDATE ON `perfiles`
 FOR EACH ROW BEGIN
DELETE FROM perfil_dispositivo WHERE ID_Perfil=OLD.ID_Perfil;
END
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `perfil_dispositivo`
--

DROP TABLE IF EXISTS `perfil_dispositivo`;
CREATE TABLE IF NOT EXISTS `perfil_dispositivo` (
  `ID_Perfil` char(20) NOT NULL,
  `ID_Dispositivo` char(20) NOT NULL,
  PRIMARY KEY (`ID_Perfil`,`ID_Dispositivo`),
  KEY `remove_pd_device` (`ID_Dispositivo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `perfil_dispositivo`
--

INSERT INTO `perfil_dispositivo` (`ID_Perfil`, `ID_Dispositivo`) VALUES
('default', 'test');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `simulaciones`
--

DROP TABLE IF EXISTS `simulaciones`;
CREATE TABLE IF NOT EXISTS `simulaciones` (
  `ID_Perfil` char(20) NOT NULL,
  `ID_Dispositivo` char(20) NOT NULL,
  PRIMARY KEY (`ID_Perfil`,`ID_Dispositivo`),
  KEY `remove_pd_simulador_disp` (`ID_Dispositivo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `ID_Usuario` char(12) NOT NULL,
  `Nombre` varchar(50) NOT NULL,
  `Password` char(4) NOT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `Admin` tinyint(1) NOT NULL DEFAULT '0',
  `Receptor_Alerta` tinyint(1) NOT NULL DEFAULT '0',
  `ID_Notificacion` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID_Usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`ID_Usuario`, `Nombre`, `Password`, `Email`, `Admin`, `Receptor_Alerta`, `ID_Notificacion`) VALUES
('admin', 'administrador', '1234', NULL, 1, 0, NULL);

--
-- Disparadores `usuarios`
--
DROP TRIGGER IF EXISTS `delete_user_trigger`;
DELIMITER //
CREATE TRIGGER `delete_user_trigger` BEFORE DELETE ON `usuarios`
 FOR EACH ROW DELETE FROM usuario_perfil WHERE ID_Usuario=OLD.ID_Usuario
//
DELIMITER ;
DROP TRIGGER IF EXISTS `update_user_trigger`;
DELIMITER //
CREATE TRIGGER `update_user_trigger` BEFORE UPDATE ON `usuarios`
 FOR EACH ROW DELETE FROM usuario_perfil WHERE ID_Usuario=OLD.ID_Usuario
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_perfil`
--

DROP TABLE IF EXISTS `usuario_perfil`;
CREATE TABLE IF NOT EXISTS `usuario_perfil` (
  `ID_Usuario` char(12) NOT NULL,
  `ID_Perfil` char(20) NOT NULL,
  PRIMARY KEY (`ID_Usuario`,`ID_Perfil`),
  KEY `remove_up_profile` (`ID_Perfil`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuario_perfil`
--

INSERT INTO `usuario_perfil` (`ID_Usuario`, `ID_Perfil`) VALUES
('admin', 'default');

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `perfil_dispositivo`
--
ALTER TABLE `perfil_dispositivo`
  ADD CONSTRAINT `remove_pd_device` FOREIGN KEY (`ID_Dispositivo`) REFERENCES `dispositivos` (`ID_Dispositivo`),
  ADD CONSTRAINT `remove_pd_profile` FOREIGN KEY (`ID_Perfil`) REFERENCES `perfiles` (`ID_Perfil`);

--
-- Filtros para la tabla `simulaciones`
--
ALTER TABLE `simulaciones`
  ADD CONSTRAINT `remove_pd_simulador_disp` FOREIGN KEY (`ID_Dispositivo`) REFERENCES `dispositivos` (`ID_Dispositivo`),
  ADD CONSTRAINT `remove_pd_simulador_perf` FOREIGN KEY (`ID_Perfil`) REFERENCES `perfiles` (`ID_Perfil`);

--
-- Filtros para la tabla `usuario_perfil`
--
ALTER TABLE `usuario_perfil`
  ADD CONSTRAINT `remove_up_profile` FOREIGN KEY (`ID_Perfil`) REFERENCES `perfiles` (`ID_Perfil`),
  ADD CONSTRAINT `remove_up_user` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
