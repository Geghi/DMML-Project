-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Feb 22, 2020 alle 17:12
-- Versione del server: 10.4.8-MariaDB
-- Versione PHP: 7.1.32

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tweetquake`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `earthquake`
--

CREATE TABLE `earthquake` (
  `Timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `Position` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `earthquake`
--

INSERT INTO `earthquake` (`Timestamp`, `Position`) VALUES
('2020-02-22 15:52:29', 'NOT FOUND'),
('2020-02-22 15:53:20', 'NOTFOUND');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `earthquake`
--
ALTER TABLE `earthquake`
  ADD PRIMARY KEY (`Timestamp`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
