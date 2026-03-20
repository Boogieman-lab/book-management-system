-- 샘플 도서 데이터 삽입 (book + book_hold)
-- book_id를 명시하여 book_hold FK가 안전하게 참조되도록 합니다.

INSERT INTO book (book_id, title, authors, translators, thumbnail, contents, url, isbn, published_at, publisher, price, sale_price)
VALUES
    (1,  '코딩 자율학습 HTML + CSS + 자바스크립트',
         '["김기수"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6052846%3Ftimestamp%3D20250716142640',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (2,  'Java',
         '["Savitch","Mock"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3383739%3Ftimestamp%3D20190220072908',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (3,  'Java',
         '["Schildt Herbert"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3506724%3Ftimestamp%3D20230918205000',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (4,  'Java',
         '["Herbert Schildt"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3189332%3Ftimestamp%3D20190218082046',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (5,  'Java Programming 입문 마스터',
         '["이태동","임정목","오연재"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5585286%3Ftimestamp%3D20240810152637',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (6,  'Power JAVA',
         '["천인국","하상호"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1377153%3Ftimestamp%3D20230217191354',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (7,  'Simply Java : An Introduction to Java Programming',
         '["Levenick James"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F2586230%3Ftimestamp%3D20250122113548',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (8,  'Java의 정석',
         '["남궁성"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1422248%3Ftimestamp%3D20250904110259',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (9,  'HTML5 + CSS3 + Javascript 웹 프로그래밍',
         '["황기태"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F961292%3Ftimestamp%3D20230310155338',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (10, 'Java',
         '["Walter Savitch"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4897617%3Ftimestamp%3D20250328131041',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (11, 'JavaScript + jQuery 입문(모던 웹을 위한)(3판)',
         '["윤인성"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F945010%3Ftimestamp%3D20211206153147',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (12, 'JAVA',
         '["김충석"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5458557%3Ftimestamp%3D20231024182203',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (13, 'JAVA 언어',
         '["채현석","김성학"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F956074%3Ftimestamp%3D20230217220905',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (14, 'Head First Java',
         '["케이시 시에라","버트 베이츠"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1102941%3Ftimestamp%3D20240721112431',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (15, 'JAVA 8.0 쉽게 배우기',
         '["임정목","이태동","김점구"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1126768%3Ftimestamp%3D20221108001540',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (16, '자바(Java) 1학년',
         '["모리 요시나오"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4854277%3Ftimestamp%3D20240420131734',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (17, 'Java',
         '["Michael Sikora"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4638033%3Ftimestamp%3D20190301223939',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (18, 'Java',
         '["Walter Savitch","Mock Kenrick"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4879676%3Ftimestamp%3D20230714215226',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (19, 'Java',
         '["Schildt Herbert"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F2885923%3Ftimestamp%3D20230714191648',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (20, 'Java',
         '["Arnold V Willemer"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4471001%3Ftimestamp%3D20190301011740',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (21, 'Java',
         '["Turner Peter"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1815624%3Ftimestamp%3D20230802195406',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (22, 'Java',
         '["Adams Joel","Nyhoff Jeffrey L","Nyhoff Larry R"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F2105237%3Ftimestamp%3D20230714203149',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (23, 'Bible (18 Books in 1 -- HTML, CSS, JavaScript, PHP, SQL, XML, Svg...',
         '["Chongqing"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3470105%3Ftimestamp%3D20230718191656',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (24, 'Java',
         '["Herbert Schildt","Danny Coward"]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7019948%3Ftimestamp%3D20251025143701',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (25, 'Java',
         '[]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7046703%3Ftimestamp%3D20251027150103',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (26, 'Java',
         '[]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6106032%3Ftimestamp%3D20251027144727',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (27, 'Java',
         '[]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6023665%3Ftimestamp%3D20251027144259',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (28, 'Java',
         '[]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5017838%3Ftimestamp%3D20251027124007',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (29, 'Java',
         '[]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5021768%3Ftimestamp%3D20251027124246',
         NULL, NULL, NULL, NULL, NULL, 0, 0),

    (30, 'Java',
         '[]', '[]',
         'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3877987%3Ftimestamp%3D20251027115423',
         NULL, NULL, NULL, NULL, NULL, 0, 0);

-- ────────────────────────────────────────────────────────────────
-- book_hold 삽입
-- 대출 가능 현황 분포:
--   book_id  1~20 : AVAILABLE 1권 (대출가능 뱃지)
--   book_id  8,14 : AVAILABLE 2권 추가 (인기 도서 복본)
--   book_id 21~25 : AVAILABLE 1권 + BORROWED 1권 (부분 대출중)
--   book_id 26~30 : BORROWED 1권만 (전체 대출중 → 대출중 뱃지)
-- ────────────────────────────────────────────────────────────────
INSERT INTO book_hold (book_id, status, location)
VALUES
    -- 대출가능 단권 (book_id 1~7, 9~13, 15~20)
    (1,  'AVAILABLE', '1층 일반자료실'),
    (2,  'AVAILABLE', '1층 일반자료실'),
    (3,  'AVAILABLE', '1층 일반자료실'),
    (4,  'AVAILABLE', '1층 일반자료실'),
    (5,  'AVAILABLE', '1층 일반자료실'),
    (6,  'AVAILABLE', '1층 일반자료실'),
    (7,  'AVAILABLE', '1층 일반자료실'),

    -- Java의 정석 — 복본 3권 (인기 도서)
    (8,  'AVAILABLE', '1층 일반자료실'),
    (8,  'AVAILABLE', '1층 일반자료실'),
    (8,  'AVAILABLE', '2층 참고자료실'),

    (9,  'AVAILABLE', '1층 일반자료실'),
    (10, 'AVAILABLE', '1층 일반자료실'),
    (11, 'AVAILABLE', '1층 일반자료실'),
    (12, 'AVAILABLE', '1층 일반자료실'),
    (13, 'AVAILABLE', '1층 일반자료실'),

    -- Head First Java — 복본 2권
    (14, 'AVAILABLE', '1층 일반자료실'),
    (14, 'AVAILABLE', '2층 참고자료실'),

    (15, 'AVAILABLE', '1층 일반자료실'),
    (16, 'AVAILABLE', '1층 일반자료실'),
    (17, 'AVAILABLE', '1층 일반자료실'),
    (18, 'AVAILABLE', '1층 일반자료실'),
    (19, 'AVAILABLE', '1층 일반자료실'),
    (20, 'AVAILABLE', '1층 일반자료실'),

    -- 부분 대출중 (AVAILABLE 1 + BORROWED 1)
    (21, 'AVAILABLE', '1층 일반자료실'),
    (21, 'BORROWED',  '1층 일반자료실'),
    (22, 'AVAILABLE', '1층 일반자료실'),
    (22, 'BORROWED',  '1층 일반자료실'),
    (23, 'AVAILABLE', '1층 일반자료실'),
    (23, 'BORROWED',  '1층 일반자료실'),
    (24, 'AVAILABLE', '2층 참고자료실'),
    (24, 'BORROWED',  '2층 참고자료실'),
    (25, 'AVAILABLE', '1층 일반자료실'),
    (25, 'BORROWED',  '1층 일반자료실'),

    -- 전체 대출중 (BORROWED만 존재 → 대출중 뱃지)
    (26, 'BORROWED',  '1층 일반자료실'),
    (27, 'BORROWED',  '1층 일반자료실'),
    (28, 'BORROWED',  '2층 참고자료실'),
    (29, 'BORROWED',  '1층 일반자료실'),
    (30, 'BORROWED',  '2층 참고자료실');
