-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1
-- Время создания: Фев 04 2022 г., 01:03
-- Версия сервера: 10.4.22-MariaDB
-- Версия PHP: 8.1.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `parking`
--
CREATE DATABASE IF NOT EXISTS `parking` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `parking`;

-- --------------------------------------------------------

--
-- Структура таблицы `car`
--

DROP TABLE IF EXISTS `car`;
CREATE TABLE `car` (
  `regno` varchar(10) NOT NULL,
  `mark` varchar(50) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


--
-- Структура таблицы `parking_order`
--

DROP TABLE IF EXISTS `parking_order`;
CREATE TABLE `parking_order` (
  `id` int(11) NOT NULL,
  `car_id` varchar(10) NOT NULL,
  `start_time` varchar(20) NOT NULL,
  `finish_time` varchar(20) DEFAULT NULL,
  `space_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- --------------------------------------------------------

--
-- Структура таблицы `parking_space`
--

DROP TABLE IF EXISTS `parking_space`;
CREATE TABLE `parking_space` (
  `id` int(11) NOT NULL,
  `code` varchar(5) NOT NULL,
  `occupied` varchar(12) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Дамп данных таблицы `parking_space`
--

-- --------------------------------------------------------

--
-- Структура таблицы `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `login` varchar(15) NOT NULL,
  `password` varchar(20) NOT NULL,
  `likes` int(11) DEFAULT 0,
  `admin` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Дамп данных таблицы `user`
--

INSERT INTO `user` (`id`, `name`, `login`, `password`, `likes`, `admin`) VALUES
(1, 'Admin', 'admin', '123', 2, 1);

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `car`
--
ALTER TABLE `car`
  ADD PRIMARY KEY (`regno`),
  ADD KEY `car_user_id_fk` (`user_id`);

--
-- Индексы таблицы `parking_order`
--
ALTER TABLE `parking_order`
  ADD PRIMARY KEY (`id`),
  ADD KEY `parking_order_car_regno_fk` (`car_id`),
  ADD KEY `parking_order_parking_space_id_fk` (`space_id`);

--
-- Индексы таблицы `parking_space`
--
ALTER TABLE `parking_space`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `parking_order`
--
ALTER TABLE `parking_order`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT для таблицы `parking_space`
--
ALTER TABLE `parking_space`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT для таблицы `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `car`
--
ALTER TABLE `car`
  ADD CONSTRAINT `car_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Ограничения внешнего ключа таблицы `parking_order`
--
ALTER TABLE `parking_order`
  ADD CONSTRAINT `parking_order_car_regno_fk` FOREIGN KEY (`car_id`) REFERENCES `car` (`regno`),
  ADD CONSTRAINT `parking_order_parking_space_id_fk` FOREIGN KEY (`space_id`) REFERENCES `parking_space` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
