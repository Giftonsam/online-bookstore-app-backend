-- Insert Categories
INSERT INTO categories (name, description, is_active, created_at) VALUES
('Fiction', 'Fiction books including novels and short stories', true, NOW()),
('Non-Fiction', 'Non-fiction books including biographies and self-help', true, NOW()),
('Technology', 'Books about programming, software development, and technology', true, NOW()),
('Science', 'Scientific books and research publications', true, NOW()),
('Business', 'Business and entrepreneurship books', true, NOW()),
('History', 'Historical books and documentaries', true, NOW()),
('Children', 'Children books and educational content', true, NOW()),
('Mystery', 'Mystery and thriller novels', true, NOW());

-- Insert Sample Books
INSERT INTO books (title, author, isbn, description, price, discount_price, stock_quantity, image_url, publication_date, publisher, pages, language, is_featured, is_active, created_at, updated_at, category_id) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'A classic American novel about the Jazz Age', 299.00, 249.00, 50, '/uploads/books/gatsby.jpg', '1925-04-10', 'Scribner', 180, 'English', true, true, NOW(), NOW(), 1),
('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'A novel about racial injustice and childhood innocence', 349.00, NULL, 30, '/uploads/books/mockingbird.jpg', '1960-07-11', 'J.B. Lippincott & Co.', 281, 'English', true, true, NOW(), NOW(), 1),
('Clean Code', 'Robert C. Martin', '9780132350884', 'A handbook of agile software craftsmanship', 599.00, 499.00, 25, '/uploads/books/cleancode.jpg', '2008-08-01', 'Prentice Hall', 464, 'English', true, true, NOW(), NOW(), 3),
('The Psychology of Money', 'Morgan Housel', '9780857197689', 'Timeless lessons on wealth, greed, and happiness', 399.00, 349.00, 40, '/uploads/books/psychology_money.jpg', '2020-09-08', 'Harriman House', 256, 'English', true, true, NOW(), NOW(), 5),
('Sapiens', 'Yuval Noah Harari', '9780062316097', 'A brief history of humankind', 449.00, 399.00, 35, '/uploads/books/sapiens.jpg', '2011-01-01', 'Harper', 443, 'English', false, true, NOW(), NOW(), 6),
('The Alchemist', 'Paulo Coelho', '9780062315007', 'A magical story about following your dreams', 299.00, 249.00, 60, '/uploads/books/alchemist.jpg', '1988-01-01', 'HarperOne', 163, 'English', true, true, NOW(), NOW(), 1),
('Atomic Habits', 'James Clear', '9780735211292', 'An easy and proven way to build good habits', 399.00, 349.00, 45, '/uploads/books/atomic_habits.jpg', '2018-10-16', 'Avery', 320, 'English', true, true, NOW(), NOW(), 2),
('The Da Vinci Code', 'Dan Brown', '9780307474278', 'A mystery thriller novel', 349.00, NULL, 20, '/uploads/books/davinci.jpg', '2003-03-18', 'Doubleday', 454, 'English', false, true, NOW(), NOW(), 8),
('Harry Potter and the Philosopher Stone', 'J.K. Rowling', '9780747532699', 'The first book in the Harry Potter series', 399.00, 349.00, 100, '/uploads/books/harry_potter_1.jpg', '1997-06-26', 'Bloomsbury', 223, 'English', true, true, NOW(), NOW(), 7),
('Introduction to Algorithms', 'Thomas H. Cormen', '9780262033848', 'Comprehensive introduction to algorithms', 899.00, 799.00, 15, '/uploads/books/algorithms.jpg', '2009-07-31', 'MIT Press', 1312, 'English', false, true, NOW(), NOW(), 3);

-- Insert Admin User
INSERT INTO users (first_name, last_name, email, password, phone, address, role, is_active, created_at) VALUES
('Admin', 'User', 'admin@bookstore.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', '9876543210', 'Admin Address', 'ADMIN', true, NOW());

-- Insert Sample User
INSERT INTO users (first_name, last_name, email, password, phone, address, role, is_active, created_at) VALUES
('John', 'Doe', 'john.doe@email.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', '9876543211', '123 Main Street, City, State', 'USER', true, NOW());

-- Note: Password for both users is "password123"