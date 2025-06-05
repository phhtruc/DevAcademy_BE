ALTER TABLE khoahoc
    ADD thoi_han INT NULL;

CREATE TABLE danhmuc
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao    VARCHAR(255)          NULL,
    ngay_tao     datetime              NULL,
    ngay_sua     datetime              NULL,
    nguoi_sua    VARCHAR(255)          NULL,
    ten_danh_muc VARCHAR(255)          NULL,
    xoa          BIT(1)                NULL,
    CONSTRAINT pk_danhmuc PRIMARY KEY (id)
);

CREATE TABLE danhmuckhoahoc
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao   VARCHAR(255)          NULL,
    ngay_tao    datetime              NULL,
    ngay_sua    datetime              NULL,
    nguoi_sua   VARCHAR(255)          NULL,
    id_khoa_hoc BIGINT                NULL,
    id_danh_muc BIGINT                NULL,
    CONSTRAINT pk_danhmuckhoahoc PRIMARY KEY (id)
);

ALTER TABLE danhmuckhoahoc
    ADD CONSTRAINT FK_DANHMUCKHOAHOC_ON_IDDANHMUC FOREIGN KEY (id_danh_muc) REFERENCES danhmuc (id);

ALTER TABLE danhmuckhoahoc
    ADD CONSTRAINT FK_DANHMUCKHOAHOC_ON_IDKHOAHOC FOREIGN KEY (id_khoa_hoc) REFERENCES khoahoc (id);