USE dalattravel_db;
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM categories;

INSERT INTO categories (category_id, category_name) VALUES
(1, 'Khách sạn'),
(2, 'Nhà hàng/Quán ăn'),
(3, 'Địa điểm du lịch');

UPDATE tourist_places SET category_id = 3 WHERE category_id IS NULL OR category_id NOT IN (1, 2, 3);

SET FOREIGN_KEY_CHECKS = 1;
