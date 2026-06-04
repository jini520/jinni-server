-- V2: categories 테이블에 name_en(영문명) 추가
-- 기존 행은 name 값으로 임시 backfill 후 NOT NULL 제약 적용

ALTER TABLE categories ADD COLUMN name_en VARCHAR(100);

UPDATE categories SET name_en = name WHERE name_en IS NULL;

ALTER TABLE categories ALTER COLUMN name_en SET NOT NULL;
