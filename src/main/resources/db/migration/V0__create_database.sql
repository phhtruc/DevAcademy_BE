CREATE DATABASE IF NOT EXISTS DevAcademy;

CREATE TABLE nguoidung
(
    id           BINARY(16)   NOT NULL,
    nguoi_tao    VARCHAR(255) NULL,
    ngay_tao     datetime NULL,
    ngay_sua     datetime NULL,
    nguoi_sua    VARCHAR(255) NULL,
    ho_ten       VARCHAR(255) NULL,
    email        VARCHAR(255) NULL,
    mat_khau     VARCHAR(255) NULL,
    trang_thai   VARCHAR(255) NULL,
    anh_dai_dien VARCHAR(255) NULL,
    xoa          BIT(1) NULL,
    CONSTRAINT pk_nguoidung PRIMARY KEY (id)
);

CREATE TABLE vaitro
(
    id          INT AUTO_INCREMENT NOT NULL,
    nguoi_tao   VARCHAR(255) NULL,
    ngay_tao    datetime NULL,
    ngay_sua    datetime NULL,
    nguoi_sua   VARCHAR(255) NULL,
    ten_vai_tro VARCHAR(255) NULL,
    xoa         BIT(1) NULL,
    CONSTRAINT pk_vaitro PRIMARY KEY (id)
);

CREATE TABLE khoahoc
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao    VARCHAR(255) NULL,
    ngay_tao     datetime NULL,
    ngay_sua     datetime NULL,
    nguoi_sua    VARCHAR(255) NULL,
    ten_khoa_hoc VARCHAR(255) NULL,
    gia          DECIMAL NULL,
    anh_mo_ta    VARCHAR(255) NULL,
    mo_ta        LONGTEXT NULL,
    cong_khai    BIT(1) NULL,
    xoa          BIT(1) NULL,
    CONSTRAINT pk_khoahoc PRIMARY KEY (id)
);

CREATE TABLE congnghe
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    ten_cong_nghe VARCHAR(255) NULL,
    xoa           BIT(1) NULL,
    CONSTRAINT pk_congnghe PRIMARY KEY (id)
);

CREATE TABLE chuong
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    ten_chuong    VARCHAR(255) NULL,
    thu_tu_chuong INT NULL,
    cong_khai     BIT(1) NULL,
    xoa           BIT(1) NULL,
    id_khoa_hoc   BIGINT NULL,
    CONSTRAINT pk_chuong PRIMARY KEY (id)
);

ALTER TABLE chuong
    ADD CONSTRAINT FK_CHUONG_ON_IDKHOAHOC FOREIGN KEY (id_khoa_hoc) REFERENCES khoahoc (id);

CREATE TABLE baihoc
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao      VARCHAR(255) NULL,
    ngay_tao       datetime NULL,
    ngay_sua       datetime NULL,
    nguoi_sua      VARCHAR(255) NULL,
    ten_bai_hoc    VARCHAR(255) NULL,
    loai_bai       VARCHAR(255) NULL,
    thu_tu_bai_hoc INT NULL,
    noi_dung       LONGTEXT NULL,
    noi_dung_phu   LONGTEXT NULL,
    video_url      VARCHAR(255) NULL,
    xoa            BIT(1) NULL,
    id_chuong      BIGINT NULL,
    CONSTRAINT pk_baihoc PRIMARY KEY (id)
);

ALTER TABLE baihoc
    ADD CONSTRAINT FK_BAIHOC_ON_IDCHUONG FOREIGN KEY (id_chuong) REFERENCES chuong (id);

CREATE TABLE cauhinhprompt
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao   VARCHAR(255) NULL,
    ngay_tao    datetime NULL,
    ngay_sua    datetime NULL,
    nguoi_sua   VARCHAR(255) NULL,
    cau_truc    LONGTEXT NULL,
    trang_thai  BIT(1) NULL,
    xoa         BIT(1) NULL,
    id_khoa_hoc BIGINT NULL,
    CONSTRAINT pk_cauhinhprompt PRIMARY KEY (id)
);

ALTER TABLE cauhinhprompt
    ADD CONSTRAINT FK_CAUHINHPROMPT_ON_IDKHOAHOC FOREIGN KEY (id_khoa_hoc) REFERENCES khoahoc (id);

CREATE TABLE congnghesudung
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao    VARCHAR(255) NULL,
    ngay_tao     datetime NULL,
    ngay_sua     datetime NULL,
    nguoi_sua    VARCHAR(255) NULL,
    id_khoa_hoc  BIGINT NULL,
    id_cong_nghe BIGINT NULL,
    CONSTRAINT pk_congnghesudung PRIMARY KEY (id)
);

ALTER TABLE congnghesudung
    ADD CONSTRAINT FK_CONGNGHESUDUNG_ON_IDCONGNGHE FOREIGN KEY (id_cong_nghe) REFERENCES congnghe (id);

ALTER TABLE congnghesudung
    ADD CONSTRAINT FK_CONGNGHESUDUNG_ON_IDKHOAHOC FOREIGN KEY (id_khoa_hoc) REFERENCES khoahoc (id);

CREATE TABLE thanhtoan
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    so_tien       DECIMAL NULL,
    phuong_thuc   VARCHAR(255) NULL,
    trang_thai    VARCHAR(255) NULL,
    xoa           BIT(1) NULL,
    id_khoa_hoc   BIGINT NULL,
    id_nguoi_dung BINARY(16)            NULL,
    CONSTRAINT pk_thanhtoan PRIMARY KEY (id)
);

ALTER TABLE thanhtoan
    ADD CONSTRAINT FK_THANHTOAN_ON_IDKHOAHOC FOREIGN KEY (id_khoa_hoc) REFERENCES khoahoc (id);

ALTER TABLE thanhtoan
    ADD CONSTRAINT FK_THANHTOAN_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE tiendo_hoc
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    trang_thai    VARCHAR(255) NULL,
    xoa           BIT(1) NULL,
    id_nguoi_dung BINARY(16)            NULL,
    id_bai_hoc    BIGINT NULL,
    CONSTRAINT pk_tiendo_hoc PRIMARY KEY (id)
);

ALTER TABLE tiendo_hoc
    ADD CONSTRAINT FK_TIENDO_HOC_ON_IDBAIHOC FOREIGN KEY (id_bai_hoc) REFERENCES baihoc (id);

ALTER TABLE tiendo_hoc
    ADD CONSTRAINT FK_TIENDO_HOC_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE lichsu_chat_ai
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    nguoi_gui     VARCHAR(255) NULL,
    noi_dung      LONGTEXT NULL,
    thoi_gian     datetime NULL,
    prompt_goc    LONGTEXT NULL,
    xoa           BIT(1) NULL,
    id_nguoi_dung BINARY(16)            NULL,
    id_bai_hoc    BIGINT NULL,
    CONSTRAINT pk_lichsu_chat_ai PRIMARY KEY (id)
);

ALTER TABLE lichsu_chat_ai
    ADD CONSTRAINT FK_LICHSU_CHAT_AI_ON_IDBAIHOC FOREIGN KEY (id_bai_hoc) REFERENCES baihoc (id);

ALTER TABLE lichsu_chat_ai
    ADD CONSTRAINT FK_LICHSU_CHAT_AI_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE token
(
    id            BINARY(16)   NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    token         VARCHAR(255) NULL,
    token_type    VARCHAR(255) NULL,
    da_het_han    BIT(1) NULL,
    bi_thu_hoi    BIT(1) NULL,
    xoa           BIT(1) NULL,
    id_nguoi_dung BINARY(16)   NULL,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

ALTER TABLE token
    ADD CONSTRAINT FK_TOKEN_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE dangkykhoahoc
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    loai_dang_ky  VARCHAR(255) NULL,
    tinh_trang    VARCHAR(255) NULL,
    xoa           BIT(1) NULL,
    id_khoa_hoc   BIGINT NULL,
    id_nguoi_dung BINARY(16)            NULL,
    CONSTRAINT pk_dangkykhoahoc PRIMARY KEY (id)
);

ALTER TABLE dangkykhoahoc
    ADD CONSTRAINT FK_DANGKYKHOAHOC_ON_IDKHOAHOC FOREIGN KEY (id_khoa_hoc) REFERENCES khoahoc (id);

ALTER TABLE dangkykhoahoc
    ADD CONSTRAINT FK_DANGKYKHOAHOC_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE bainop
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao         VARCHAR(255) NULL,
    ngay_tao          datetime NULL,
    ngay_sua          datetime NULL,
    nguoi_sua         VARCHAR(255) NULL,
    status            VARCHAR(255) NULL,
    nhan_xet          LONGTEXT NULL,
    nhan_xet_thu_cong LONGTEXT NULL,
    link_bai_nop      VARCHAR(255) NULL,
    file_nop          LONGTEXT NULL,
    noi_dung_bai_lam  LONGTEXT NULL,
    xoa               BIT(1) NULL,
    id_nguoi_dung     BINARY(16)            NULL,
    id_bai_hoc        BIGINT NULL,
    CONSTRAINT pk_bainop PRIMARY KEY (id)
);

ALTER TABLE bainop
    ADD CONSTRAINT FK_BAINOP_ON_IDBAIHOC FOREIGN KEY (id_bai_hoc) REFERENCES baihoc (id);

ALTER TABLE bainop
    ADD CONSTRAINT FK_BAINOP_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE binhluan
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao        VARCHAR(255) NULL,
    ngay_tao         datetime NULL,
    ngay_sua         datetime NULL,
    nguoi_sua        VARCHAR(255) NULL,
    noi_dung         LONGTEXT NULL,
    id_binh_luan_goc INT NULL,
    xoa              BIT(1) NULL,
    id_nguoi_dung    BINARY(16)            NULL,
    id_bai_hoc       BIGINT NULL,
    CONSTRAINT pk_binhluan PRIMARY KEY (id)
);

ALTER TABLE binhluan
    ADD CONSTRAINT FK_BINHLUAN_ON_IDBAIHOC FOREIGN KEY (id_bai_hoc) REFERENCES baihoc (id);

ALTER TABLE binhluan
    ADD CONSTRAINT FK_BINHLUAN_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE luotthichbinhluan
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nguoi_tao     VARCHAR(255) NULL,
    ngay_tao      datetime NULL,
    ngay_sua      datetime NULL,
    nguoi_sua     VARCHAR(255) NULL,
    xoa           BIT(1) NULL,
    id_nguoi_dung BINARY(16)            NULL,
    id_binh_luan  BIGINT NULL,
    CONSTRAINT pk_luotthichbinhluan PRIMARY KEY (id)
);

ALTER TABLE luotthichbinhluan
    ADD CONSTRAINT FK_LUOTTHICHBINHLUAN_ON_IDBINHLUAN FOREIGN KEY (id_binh_luan) REFERENCES binhluan (id);

ALTER TABLE luotthichbinhluan
    ADD CONSTRAINT FK_LUOTTHICHBINHLUAN_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

CREATE TABLE vaitro_nguoidung
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    id_nguoi_dung BINARY(16)            NULL,
    id_vai_tro    INT NULL,
    CONSTRAINT pk_vaitro_nguoidung PRIMARY KEY (id)
);

ALTER TABLE vaitro_nguoidung
    ADD CONSTRAINT FK_VAITRO_NGUOIDUNG_ON_IDNGUOIDUNG FOREIGN KEY (id_nguoi_dung) REFERENCES nguoidung (id);

ALTER TABLE vaitro_nguoidung
    ADD CONSTRAINT FK_VAITRO_NGUOIDUNG_ON_IDVAITRO FOREIGN KEY (id_vai_tro) REFERENCES vaitro (id);

