-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Dec 20, 2025 at 04:22 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `LibManSys`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `account_id` int(11) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(25) NOT NULL,
  `age` int(10) NOT NULL,
  `sex` enum('MALE','FEMALE') NOT NULL,
  `contact_number` varchar(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `role` enum('reader','librarian') NOT NULL,
  `password` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`account_id`, `first_name`, `last_name`, `age`, `sex`, `contact_number`, `email`, `address`, `role`, `password`) VALUES
(1, 'CJay', 'Acopra', 25, 'MALE', '09123456789', 'cjay@email.com', 'Legazpi City', 'librarian', 'abc_123'),
(2, 'Zoe', 'Dela Torre', 25, 'FEMALE', '09123456789', 'zoe@email.com', '123 St. Daraga, Albay', 'reader', 'abc_123'),
(3, 'Emily', 'Davis', 46, 'MALE', '09149012791', 'emily.davis78@example.com', '112 Main St, Phoenix', 'reader', 'password98'),
(4, 'Emily', 'Brown', 55, 'FEMALE', '09344852901', 'emily.brown80@example.com', '473 Main St, Los Angeles', 'reader', 'password38'),
(5, 'Peter', 'Brown', 33, 'FEMALE', '09929910361', 'peter.brown3@example.com', '884 Main St, San Antonio', 'reader', 'password19'),
(6, 'David', 'Brown', 19, 'FEMALE', '09970775767', 'david.brown80@example.com', '412 Main St, Los Angeles', 'reader', 'password73'),
(7, 'Susan', 'Brown', 37, 'MALE', '09611356358', 'susan.brown64@example.com', '111 Main St, San Diego', 'librarian', 'password73'),
(8, 'Sarah', 'Smith', 34, 'MALE', '09152683205', 'sarah.smith7@example.com', '480 Main St, San Antonio', 'reader', 'password17'),
(9, 'Chris', 'Jones', 19, 'FEMALE', '09888076180', 'chris.jones70@example.com', '966 Main St, Phoenix', 'reader', 'password17'),
(10, 'David', 'Williams', 52, 'FEMALE', '09441795556', 'david.williams62@example.com', '888 Main St, San Antonio', 'reader', 'password0'),
(11, 'Sarah', 'Jones', 68, 'MALE', '09905591872', 'sarah.jones18@example.com', '852 Main St, New York', 'reader', 'password43'),
(12, 'Jane', 'Rodriguez', 60, 'FEMALE', '09757932847', 'jane.rodriguez28@example.com', '933 Main St, San Diego', 'reader', 'password64'),
(13, 'Emily', 'Martinez', 47, 'MALE', '09831636046', 'emily.martinez14@example.com', '247 Main St, San Antonio', 'reader', 'password6'),
(14, 'John', 'Brown', 65, 'FEMALE', '09321993305', 'john.brown44@example.com', '778 Main St, Phoenix', 'reader', 'password49'),
(15, 'Chris', 'Smith', 62, 'MALE', '09421533692', 'chris.smith99@example.com', '536 Main St, Philadelphia', 'reader', 'password96'),
(18, 'Emily', 'Rodriguez', 52, 'FEMALE', '09402358581', 'emily.rodriguez62@example.com', '139 Main St, Philadelphia', 'librarian', 'password12'),
(19, 'Peter', 'Rodriguez', 34, 'MALE', '09746751157', 'peter.rodriguez45@example.com', '38 Main St, Phoenix', 'librarian', 'password73'),
(20, 'Peterrererere', 'Smithhey', 36, 'FEMALE', '09186312842', 'peter.smith86@example.com', '995 Main St, Phoenix', 'librarian', 'password53'),
(21, 'Cresencio', 'Acopra', 48, 'MALE', '0909123456', 'cresencio@email.com', 'Bagacay, Legazpi City', 'reader', 'abc_123'),
(22, 'Another', 'Librarian', 99, 'FEMALE', '0909123456', 'AnLib@email.com', 'Somewhere in the city', 'librarian', 'asdkjhgasdjhgasd');

-- --------------------------------------------------------

--
-- Table structure for table `books`
--

CREATE TABLE `books` (
  `book_id` int(10) NOT NULL,
  `book_name` varchar(100) NOT NULL,
  `book_author` varchar(25) NOT NULL,
  `issue_date` date NOT NULL,
  `book_category` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `books`
--

INSERT INTO `books` (`book_id`, `book_name`, `book_author`, `issue_date`, `book_category`) VALUES
(1, 'Atomic Habits', 'James Clear', '2025-12-07', 'Self Improvement'),
(3, 'A Great Legacy of Dragons', 'Michael Smith', '2001-02-12', 'Mystery'),
(4, 'The Incredible History on the Moon', 'Jane Williams', '2021-11-20', 'Biography'),
(5, 'Some Fantastic World of the Future', 'David Garcia', '2015-07-20', 'Thriller'),
(6, 'A Mysterious History of the Past', 'John Williams', '2019-06-10', 'Mystery'),
(7, 'My Wonderful World on the Moon', 'David Jones', '2023-12-21', 'History'),
(8, 'An Lost Journey of the Future', 'Michael Jones', '2017-06-10', 'Mystery'),
(9, 'A Mysterious History of the Ancients', 'Sarah Jones', '2010-01-09', 'Science Fiction'),
(10, 'My Mysterious Legacy on the Moon', 'Michael Johnson', '2005-12-06', 'Thriller'),
(11, 'A Amazing Destiny of the Ancients', 'Jane Davis', '2014-10-13', 'Mystery'),
(12, 'A Amazing Adventures on the Moon', 'Peter Johnson', '2000-08-17', 'Biography'),
(13, 'The Wonderful Adventures on the Moon', 'Jane Davis', '2006-03-02', 'History'),
(14, 'An Great World of the Past', 'Sarah Brown', '2018-05-16', 'History'),
(15, 'The Forgotten Adventures of the Past', 'Peter Miller', '2004-05-25', 'Biography'),
(16, 'An Great Journey of Dragons', 'Michael Jones', '2006-05-16', 'Science Fiction'),
(17, 'An Great Secret of the Future', 'John Williams', '2020-04-28', 'Mystery'),
(18, 'A Fantastic Secret of the Ancients', 'Susan Johnson', '2025-10-20', 'Mystery'),
(19, 'The Mysterious Secret of the Ancients', 'John Rodriguez', '2001-06-01', 'Science Fiction'),
(20, 'An Great Adventures of the Past', 'Chris Davis', '2005-01-31', 'Romance'),
(21, 'An Lost Chronicles in Space', 'David Smith', '2017-05-03', 'Thriller'),
(22, 'The Lost Journey of the Past', 'David Davis', '2014-02-22', 'Romance'),
(23, 'A Wonderful Adventures of the Past', 'Jessica Rodriguez', '2017-06-14', 'Biography'),
(24, 'Some Wonderful History on the Moon', 'John Williams', '2016-11-13', 'History'),
(25, 'A Forgotten Adventures of Dragons', 'Sarah Garcia', '2019-08-05', 'Biography'),
(26, 'My Forgotten Destiny of Dragons', 'Emily Davis', '2001-07-13', 'History'),
(27, 'The Fantastic Chronicles of the Future', 'Sarah Miller', '2009-10-17', 'History'),
(28, 'The Incredible Secret of the Ancients', 'John Johnson', '2015-01-02', 'History'),
(29, 'An Wonderful Destiny on the Moon', 'John Miller', '2005-05-14', 'Science Fiction'),
(30, 'The Incredible Secret of the Future', 'Sarah Garcia', '2003-09-06', 'Biography'),
(31, 'An Wonderful Destiny in Space', 'Jane Smith', '2012-02-22', 'Fantasy'),
(32, 'A Wonderful Legacy in Space', 'Chris Miller', '2012-07-06', 'History'),
(33, 'An Lost Journey of the Future', 'Jessica Brown', '2005-07-21', 'Mystery'),
(34, 'A Great Secret of Dragons', 'Jane Garcia', '2011-09-13', 'History'),
(35, 'Some Wonderful Journey of the Past', 'Chris Garcia', '2011-09-24', 'Biography'),
(36, 'Some Wonderful Adventures of the Past', 'John Williams', '2019-05-09', 'Science Fiction'),
(37, 'An Wonderful Legacy on the Moon', 'John Brown', '2017-04-15', 'Mystery'),
(38, 'The Wonderful Journey on the Moon', 'Emily Jones', '2019-05-13', 'History'),
(39, 'Some Incredible Adventures of the Ancients', 'Emily Miller', '2009-01-01', 'Romance'),
(40, 'The Amazing Chronicles of the Future', 'Jane Brown', '2003-11-26', 'Fantasy'),
(41, 'An Forgotten Destiny of the Future', 'Emily Smith', '2000-02-12', 'Mystery'),
(42, 'An Forgotten Chronicles of the Future', 'Chris Williams', '2020-08-08', 'Fantasy'),
(43, 'The Fantastic History of the Future', 'Michael Garcia', '2020-01-29', 'Biography'),
(44, 'An Incredible Destiny of Dragons', 'Peter Miller', '2020-06-15', 'Fantasy'),
(45, 'My Amazing History of the Future', 'Peter Brown', '2014-04-25', 'Fantasy'),
(46, 'Some Amazing Legacy of the Past', 'Susan Jones', '2010-08-13', 'Romance'),
(47, 'My Great Journey on the Moon', 'Sarah Smith', '2025-10-09', 'Fantasy'),
(48, 'The Lost Secret of the Future', 'Michael Williams', '2013-09-27', 'Mystery');

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `transaction_id` int(11) NOT NULL,
  `transaction_type` enum('borrow','return') NOT NULL,
  `date` date NOT NULL,
  `book_id` int(11) NOT NULL,
  `book_name` varchar(255) NOT NULL,
  `account_id` int(11) NOT NULL,
  `contact_number` varchar(11) NOT NULL,
  `email` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`transaction_id`, `transaction_type`, `date`, `book_id`, `book_name`, `account_id`, `contact_number`, `email`) VALUES
(1, 'borrow', '0000-00-00', 1, 'Atomic Habits', 2, '09123456789', 'zoe@email.com'),
(2, 'return', '0000-00-00', 1, 'Atomic Habits', 2, '09123456789', 'zoe@email.com'),
(3, 'borrow', '2025-12-15', 3, 'A Great Legacy of Dragons', 2, '09123456789', 'zoe@email.com');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`account_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`book_id`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `fk_book_id` (`book_id`),
  ADD KEY `fk_account_id` (`account_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
  MODIFY `account_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `books`
--
ALTER TABLE `books`
  MODIFY `book_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
