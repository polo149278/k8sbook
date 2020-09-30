-- REGION
INSERT INTO region (region_name, creation_timestamp)
VALUES ('홋카이도', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('도호쿠', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('칸토', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('추부', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('긴키', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('추고쿠', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('시코쿠', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('큐슈', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('오키나와', current_timestamp);

-- LOCATION
INSERT INTO location (location_name, region_id, note)
VALUES ('추라우미 수족관', (SELECT region_id FROM region WHERE region_name = '오키나와'),
  '오키나와의 대표적인 수족관으로 고래 상어를 비롯하여 다양한 오키나와의 바다 생물을 볼 수 있다.');

INSERT INTO location (location_name, region_id, note)
VALUES ('슈리성', (SELECT region_id FROM region WHERE region_name = '오키마와'),
  '류큐 왕조의 왕성으로 세계유산 중 하나이다.');

-- BATCH_PROCESSING
INSERT INTO batch_processing (batch_name)
values ('SAMPLE_APP_BATCH');
