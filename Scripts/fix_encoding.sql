USE dalattravel_db;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Recreate categories with correct UTF-8 encoding
DELETE FROM categories;
INSERT INTO categories (category_id, category_name) VALUES
(1, 'Khách sạn'),
(2, 'Nhà hàng/Quán ăn'),
(3, 'Địa điểm du lịch');

-- Verify tourist_places category assignment
UPDATE tourist_places SET category_id = 3 WHERE category_id IS NULL OR category_id NOT IN (1, 2, 3);

SET FOREIGN_KEY_CHECKS = 1;
