-- V1.14__insert_vibe_coding_books.sql
-- 바이브 코딩 관련 도서 데이터 삽입

-- ─────────────────────────────────────────
-- book
-- (기존 max book_id = 30 → 31부터 시작)
-- ─────────────────────────────────────────
INSERT INTO book (
    book_id, title, authors, translators, thumbnail,
    contents, url, isbn, published_at, publisher, price, sale_price
)
VALUES
    (31, '혼자 공부하는 바이브 코딩 with 클로드 코드',
     '["조태호"]', '[]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7091811%3Ftimestamp%3D20260301121311',
     '매일 반복되는 일을 AI에게 맡길 수 있다면...',
     'https://search.daum.net/search?w=bookpage&bookId=7091811',
     '1199529842 9791199529847', '2025-12-16',
     '한빛미디어', 30000, 27000),

    (32, '요즘 바이브 코딩 안티그래비티 완벽 가이드',
     '["최지호(코드팩토리)"]', '[]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7132690%3Ftimestamp%3D20260301121304',
     '이 책은 구글의 차세대 코딩 에이전트...',
     'https://search.daum.net/search?w=bookpage&bookId=7132690',
     '1194383734 9791194383734', '2026-02-06',
     '골든래빗(주)', 28000, 25200),

    (33, '어쨌든, 바이브 코딩',
     '["코다프레스"]', '["양희은"]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7100484%3Ftimestamp%3D20251221133641',
     '창작 경험 50개...',
     'https://search.daum.net/search?w=bookpage&bookId=7100484',
     '8966265065 9788966265060', '2025-12-18',
     '인사이트', 22000, 19800),

    (34, '요즘 바이브 코딩 클로드 코드 완벽 가이드',
     '["최지호(코드팩토리)"]', '[]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7001712%3Ftimestamp%3D20260110151927',
     '이 책은 현존 최강의 코딩 AI 파트너...',
     'https://search.daum.net/search?w=bookpage&bookId=7001712',
     '1194383432 9791194383437', '2025-09-01',
     '골든래빗(주)', 24000, 21600),

    (35, '바이브 코딩 (커서 AI와 클로드 코드로 누구나!)',
     '["이석현"]', '[]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7060645%3Ftimestamp%3D20251218153548',
     '과거 웹 서비스 개발은...',
     'https://search.daum.net/search?w=bookpage&bookId=7060645',
     '1164262742 9791164262748', '2025-11-15',
     '아이콕스', 27000, 24300),

    (36, '클로드 코드를 활용한 바이브 코딩 완벽 입문',
     '["히라카와 토모히데"]', '["최용"]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7165343%3Ftimestamp%3D20260321123854',
     '애플리케이션의 규모와 복잡성이...',
     'https://search.daum.net/search?w=bookpage&bookId=7165343',
     '1158396686 9791158396688', '2026-03-12',
     '위키북스', 26000, 23400),

    (37, '한 걸음 앞선 일잘러가 지금 꼭 알아야 할 바이브 코딩 with 커서',
     '["김태헌"]', '[]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7144056%3Ftimestamp%3D20260212121718',
     '웹 명함 제작부터...',
     'https://search.daum.net/search?w=bookpage&bookId=7144056',
     '1140717677 9791140717675', '2026-02-10',
     '길벗', 30000, 27000),

    (38, '바이브 코딩 너머 개발자 생존법',
     '["애디 오스마니"]', '["강민혁"]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7070717%3Ftimestamp%3D20251216153000',
     'AI가 코드를 대신 작성하는 시대...',
     'https://search.daum.net/search?w=bookpage&bookId=7070717',
     '1169214479 9791169214476', '2025-11-07',
     '한빛미디어', 23000, 20700),

    (39, 'AI 자율학습 클로드 코드·코덱스 CLI·제미나이 CLI 완전 활용법',
     '["Dave Lee"]', '[]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7137911%3Ftimestamp%3D20260319124623',
     'AI 코딩 도구를 처음 접하는 사람에게...',
     'https://search.daum.net/search?w=bookpage&bookId=7137911',
     '114071760X 9791140717606', '2026-02-02',
     '길벗', 32000, 28800),

    (40, '요즘 바이브 코딩 커서 AI 30가지 프로그램 만들기',
     '["박현규"]', '[]',
     'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F7014656%3Ftimestamp%3D20260110151955',
     '활용해보세요...',
     'https://search.daum.net/search?w=bookpage&bookId=7014656',
     '1194383459 9791194383451', '2025-09-16',
     '골든래빗(주)', 24000, 21600);

-- ─────────────────────────────────────────
-- book_hold
-- ─────────────────────────────────────────
INSERT INTO book_hold (book_id, status, location)
VALUES
    (31, 'AVAILABLE', '1층 일반자료실'),
    (32, 'AVAILABLE', '1층 일반자료실'),
    (33, 'AVAILABLE', '1층 일반자료실'),
    (34, 'AVAILABLE', '1층 일반자료실'),
    (35, 'AVAILABLE', '1층 일반자료실'),
    (36, 'AVAILABLE', '1층 일반자료실'),

    -- 인기 (복본)
    (37, 'AVAILABLE', '1층 일반자료실'),
    (37, 'AVAILABLE', '2층 참고자료실'),
    (38, 'AVAILABLE', '1층 일반자료실'),
    (38, 'AVAILABLE', '2층 참고자료실'),

    -- 부분 대출
    (39, 'AVAILABLE', '1층 일반자료실'),
    (39, 'BORROWED',  '1층 일반자료실'),
    (40, 'AVAILABLE', '2층 참고자료실'),
    (40, 'BORROWED',  '2층 참고자료실');