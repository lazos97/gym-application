-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Εξυπηρετητής: 127.0.0.1
-- Χρόνος δημιουργίας: 14 Μάη 2024 στις 16:34:01
-- Έκδοση διακομιστή: 10.4.32-MariaDB
-- Έκδοση PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Βάση δεδομένων: `gym`
--

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `fitness_programs`
--

CREATE TABLE `fitness_programs` (
  `id` int(11) NOT NULL,
  `room_name` varchar(100) NOT NULL,
  `date` varchar(100) NOT NULL,
  `start_hour` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `fitness_programs`
--

INSERT INTO `fitness_programs` (`id`, `room_name`, `date`, `start_hour`) VALUES
(14, 'Pilates', '15-05-2024', '09:00 AM'),
(16, 'Yoga', '22-05-2024', '09:30 AM'),
(17, 'CrossFit', '13-06-2024', '10:00 AM'),
(18, 'Heavy Weight', '29-06-2024', '10:30 AM'),
(19, 'Pilates', '11-06-2024', '09:00 AM'),
(20, 'Cardio', '06-06-2024', '09:30 AM'),
(21, 'Martial Arts', '11-06-2024', '10:00 AM'),
(22, 'Yoga', '10-06-2024', '10:30 AM'),
(23, 'Kangoo Jumps', '18-06-2024', '09:00 AM'),
(24, 'Cardio', '02-07-2024', '09:30 AM'),
(25, 'Tai Chi', '01-08-2024', '09:00 AM');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `reports`
--

CREATE TABLE `reports` (
  `id` int(11) NOT NULL,
  `content` varchar(100) NOT NULL,
  `user_id` int(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `reports`
--

INSERT INTO `reports` (`id`, `content`, `user_id`) VALUES
(9, 'My CrossFit workout was excellent!', 21),
(10, 'Tai Chi was perfect!', 22),
(11, 'Kangoo Jumbs was a little bit boring', 22);

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `program_id` int(100) NOT NULL,
  `user_id` int(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `reservations`
--

INSERT INTO `reservations` (`id`, `program_id`, `user_id`) VALUES
(40, 17, 21),
(41, 24, 21),
(42, 25, 22),
(43, 23, 22);

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `role` enum('admin','member') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `users`
--

INSERT INTO `users` (`id`, `email`, `username`, `password`, `address`, `role`) VALUES
(20, 'admin@admin.com', 'admin', '12345', 'Thessaloniki, Greece', 'admin'),
(21, 'lazaros@user.com', 'lazaros', '12345', 'Thessaloniki,Greece', 'member'),
(22, 'kar@user.com', 'karaventzas', '12345', 'Thessaloniki, Greece', 'member');

--
-- Ευρετήρια για άχρηστους πίνακες
--

--
-- Ευρετήρια για πίνακα `fitness_programs`
--
ALTER TABLE `fitness_programs`
  ADD PRIMARY KEY (`id`);

--
-- Ευρετήρια για πίνακα `reports`
--
ALTER TABLE `reports`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user_id`);

--
-- Ευρετήρια για πίνακα `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `program_id` (`program_id`);

--
-- Ευρετήρια για πίνακα `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT για άχρηστους πίνακες
--

--
-- AUTO_INCREMENT για πίνακα `fitness_programs`
--
ALTER TABLE `fitness_programs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT για πίνακα `reports`
--
ALTER TABLE `reports`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT για πίνακα `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT για πίνακα `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- Περιορισμοί για άχρηστους πίνακες
--

--
-- Περιορισμοί για πίνακα `reports`
--
ALTER TABLE `reports`
  ADD CONSTRAINT `user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Περιορισμοί για πίνακα `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `program_id` FOREIGN KEY (`program_id`) REFERENCES `fitness_programs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
