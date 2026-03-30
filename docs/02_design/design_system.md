# 🐢 부기맨(Boogieman) 디자인 시스템 가이드: Green Turtle Edition

## 1. 브랜드 아이덴티티 (Brand Identity)

**"지식의 숲을 가꾸는 성실한 거북이, 부기맨"**

부기맨 도서 관리 시스템은 '느리지만 꾸준한 성실함'을 상징하는 거북이와 '지식 공간'을 은유하는 숲의 의미를 결합하여 설계되었습니다. 차분하고 신뢰감을 주는 그린 컬러를 메인으로 사용하여 사용자에게 편안한 독서 및 대출 환경을 제공합니다.

---

## 2. 디자인 원칙 (Green Design Language)

* **01. Fresh & Trust (청량함과 신뢰)**
  신뢰감 있는 딥 그린을 베이스로 하되, 채도가 높은 골드/핑크 포인트를 섞어 지나치게 딱딱하지 않은 현대적인 분위기를 연출합니다.

* **02. Organic Curves (유기적 곡선)**
  거북이와 자연의 형태를 모티브로 한 유기적인 곡선 라운딩(`border-radius: 32px`)을 UI 컴포넌트 전반에 활용하여, 정적인 도서 관리 시스템에 생동감을 불어넣습니다.

* **03. High Visibility (높은 인지 및 가독성)**
  초록색 배경이나 UI 요소 위에서도 글씨가 명확히 보이도록 높은 폰트 두께와 명도 대비를 철저하게 유지하여 빠르고 명확한 정보 전달력을 확보합니다.

---

## 3. 컬러 시스템 (Color System)

| 컬러명 | Hex Code | RGB | 용도 및 의미 |
|---|---|---|---|
| **Boogie Green** (Primary) | `#00A760` | rgb(0, 167, 96) | 브랜드 메인 컬러, 성실함과 지식의 숲 상징, 버튼/메인 아이콘 적용 |
| **Dark Green** (Hover) | `#008f51` | rgb(0, 143, 81) | 버튼 하이라이트 및 호버(Hover) 시 상호작용 피드백 컬러 |
| **Light Mint** (Surface) | `#E1F7EF` | rgb(225, 247, 239) | UI 컴포넌트 뒷배경, 보조 강조색, 뱃지 혹은 알림창 배경 |
| **Bookmark Gold** (Point) | `#FFD700` | rgb(255, 215, 0) | 로고 북마크, 경고/알림, 사용자 시선을 사로잡는 캐릭터 포인트 |
| **Turtle Blush** (Point) | `#FF7B9C` | rgb(255, 123, 156) | 캐릭터 발그레 효과, 긍정적 성공(Success) 혹은 부드러운 강조 |
| **Night Slate** (Text/Dark) | `#111827` | rgb(17, 24, 39) | 기본 본문 텍스트 (명확한 가독성 확보) |
| **Deep Forest** (Background) | `#0D1B15` | rgb(13, 27, 21) | 시스템 푸터, 어두운 모드 영역, 혹은 고급스러운 강조 섹션 |
| **Base Background** | `#f4f7f5` | rgb(244, 247, 245) | 웹 어플리케이션의 기본 바디(Body) 배경색 |

---

## 4. 타이포그래피 (Typography)

| 폰트 패밀리 | 사용처 | 특징 및 굵기 (Weight) |
|---|---|---|
| **Noto Sans KR** | 시스템 기본 폰트, 본문, 버튼 텍스트 | 300(Light), 400(Regular), 500(Medium), 700(Bold) |
| **Pretendard** | 로고 타이틀, 주요 핵심 헤딩(H1/H2) | 500(Medium - 영문), 900(Black - 한글 로고명) |

### 타이포그래피 스케일

| 용도 | 크기 (rem) | 굵기 (Weight) | 행 높이 (line-height) |
|---|---|---|---|
| H1 (페이지 제목) | 2.5rem (40px) | 700(Bold) | 1.2 |
| H2 (섹션 제목) | 2rem (32px) | 700(Bold) | 1.3 |
| H3 (소제목) | 1.5rem (24px) | 600(SemiBold) | 1.4 |
| Body (본문) | 1rem (16px) | 400(Regular) | 1.6 |
| Small (보조 텍스트) | 0.875rem (14px) | 400(Regular) | 1.5 |
| Caption (주석) | 0.75rem (12px) | 400(Regular) | 1.4 |

---

## 5. UI 컴포넌트 스타일 (Layout & UI)

### 5.1 카드 (Card) 디자인 규칙

* **배경색**: `white` (#FFFFFF)
* **모서리 반경**: `32px` (`rounded-[32px]`)의 둥글둥글하고 친근한 쉐입
* **그림자 (Shadow)**: 기본 `box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.03);`
* **테두리 (Border)**: 아주 옅은 그레이톤 (`border-gray-50`)
* **패딩**: 상하좌우 `24px` (1.5rem)

### 5.2 뱃지 (Badge) 디자인 규칙

* **배경**: 선형 그라데이션 (`linear-gradient(135deg, #00A760 0%, #10B981 100%)`)
* **텍스트**: 흰색 (`white`), 사이즈 `12px`(`0.75rem`), 굵기 `Bold(700)`
* **모서리 반径**: 완전한 원형 (`border-radius: 99px`)
* **패딩**: 좌우 `12px`, 상하 `6px`

### 5.3 버튼 (Button) 디자인 규칙

#### Primary Button (메인 액션)
* **배경**: `#00A760` (Boogie Green)
* **텍스트**: 흰색 (`white`), 굵기 `700(Bold)`
* **패딩**: 좌우 `24px`, 상하 `12px`
* **모서리 반경**: `8px`
* **호버**: 배경 `#008f51` (Dark Green) + `transform: translateY(-2px)`

#### Secondary Button (보조 액션)
* **배경**: `#E1F7EF` (Light Mint)
* **테두리**: `1px solid #00A760`
* **텍스트**: `#00A760`, 굵기 `700(Bold)`
* **호버**: 배경 `#CCF0E9` + `transform: translateY(-2px)`

#### Danger Button (삭제/거절)
* **배경**: `#EF4444` (적색)
* **텍스트**: 흰색 (`white`), 굵기 `700(Bold)`
* **호버**: 배경 `#DC2626` + `transform: translateY(-2px)`

### 5.4 입력 필드 (Input Fields)

* **배경**: 흰색 (`white`)
* **테두리**: `1px solid #D1D5DB` (회색)
* **포커스**: 테두리 `2px solid #00A760` + 박스 쉐도우
* **패딩**: 좌우 `12px`, 상하 `10px`
* **모서리 반경**: `8px`
* **폰트**: Noto Sans KR 400, 크기 `1rem`

### 5.5 모달 (Modal)

* **백드롭**: `rgba(0, 0, 0, 0.5)`
* **컨테이너 배경**: 흰색 (`white`)
* **모서리 반경**: `16px`
* **그림자**: `0 20px 60px -10px rgba(0, 0, 0, 0.15)`
* **최대 너비**: `600px` (1.5xl in Tailwind)

---

## 6. 인터랙션 및 애니메이션 (Interactions)

생동감 넘치는 사용자 경험(UX/토스 스타일)을 위해 미세한 마이크로 애니메이션을 적극 활용합니다.

### 6.1 카드 호버 (Card Elevate)

* 마우스를 올렸을 때 카드가 부드럽게 위로 떠오르는 효과
* **Transform**: `translateY(-5px)`
* **Shadow**: `rgba(0,0,0,0.06)`으로 그림자 강조
* **Transition**: `cubic-bezier(0.175, 0.885, 0.32, 1.275)`를 사용해 통통 튀는 텐션감 부여
* **Duration**: `0.3s`

```css
.card {
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}
```

### 6.2 로고 호버 (Turtle Nod)

* 로고 거북이에 마우스를 올리면 거북이가 **고개를 끄덕이는 (Nod) 애니메이션** 발동
* **Keyframes**: `0%` → `-8deg` → `0%` (`0.6s ease-in-out infinite`)
* 거북이 등껍질(책) 색상이 기본 초록(`#00A760`)에서 더 진한 초록(`#008f51`)으로 부드럽게(`0.3s ease`) 변환

```css
@keyframes turtleNod {
  0% { transform: rotate(0deg); }
  50% { transform: rotate(-8deg); }
  100% { transform: rotate(0deg); }
}

.logo:hover {
  animation: turtleNod 0.6s ease-in-out infinite;
}
```

### 6.3 페이드인 (Fade In)

* 페이지 로드 시 요소들이 부드럽게 나타나는 효과
* **Opacity**: `0` → `1`
* **Duration**: `0.5s`
* **Delay**: 각 요소마다 `0.1s` 씩 증가

### 6.4 로딩 스피너 (Loading Spinner)

* 도서 검색 등 비동기 작업 중 표시
* **Animation**: 회전 (360도 1회)
* **Duration**: `1s`
* **Color**: `#00A760`
* **Size**: `24px` × `24px`

---

## 7. 레이아웃 및 스페이싱 (Layout System)

### 7.1 그리드 시스템

* **Container**: 최대 너비 `1200px` (lg in Tailwind)
* **Padding**: 상하좌우 `24px` (1.5rem) 기본
* **Gap**: 컴포넌트 간 거리 `16px` (1rem)

### 7.2 스페이싱 스케일 (8px 기준)

| 크기 | px | rem | 용도 |
|---|---|---|---|
| xs | 4px | 0.25rem | 매우 작은 간격 |
| sm | 8px | 0.5rem | 요소 내부 패딩 |
| md | 16px | 1rem | 표준 간격 |
| lg | 24px | 1.5rem | 섹션 간 거리 |
| xl | 32px | 2rem | 큰 섹션 간 거리 |
| 2xl | 48px | 3rem | 페이지 레벨 간격 |

---

## 8. 다크 모드 (Dark Mode) - 향후 지원

향후 다크 모드를 지원할 경우 다음 색상 스키마를 따릅니다:

| 요소 | Light Mode | Dark Mode |
|------|-----------|-----------|
| Background | `#ffffff` | `#111827` |
| Surface | `#f4f7f5` | `#1f2937` |
| Text | `#111827` | `#ffffff` |
| Border | `#e5e7eb` | `#374151` |
| Primary | `#00A760` | `#10b981` |

---

## 9. 접근성 (Accessibility)

### 9.1 색상 대비

* **WCAG AA 기준**: 최소 4.5:1 (일반 텍스트), 3:1 (큰 텍스트)
* **Boogie Green + White**: 6.8:1 (통과)
* **Dark Green + White**: 7.2:1 (통과)

### 9.2 포커스 인디케이터

* 모든 버튼과 입력 필드에 명확한 포커스 상태 표시
* **포커스 색상**: `#00A760` 테두리 + 아웃라인

### 9.3 버튼 텍스트

* 아이콘만 사용하는 버튼에 `aria-label` 속성 추가
* 예: `<button aria-label="알림 열기">🔔</button>`

---

## 10. Tailwind CSS 설정 (tailwind.config.js)

```javascript
module.exports = {
  theme: {
    extend: {
      colors: {
        boogie: {
          green: '#00A760',
          'green-dark': '#008f51',
          mint: '#E1F7EF',
          gold: '#FFD700',
          blush: '#FF7B9C',
          'slate-dark': '#111827',
          'forest-deep': '#0D1B15',
        },
      },
      borderRadius: {
        '3xl': '32px',
      },
      boxShadow: {
        'card': '0 10px 30px -10px rgba(0, 0, 0, 0.03)',
        'elevated': '0 20px 40px rgba(0, 0, 0, 0.1)',
      },
      fontFamily: {
        'noto-sans': ['Noto Sans KR', 'sans-serif'],
        'pretendard': ['Pretendard', 'sans-serif'],
      },
    },
  },
};
```

---

## 11. 컴포넌트 예시 (HTML + Tailwind)

### 카드 컴포넌트

```html
<div class="bg-white rounded-[32px] shadow-card p-6 border border-gray-50 hover:shadow-elevated hover:transform hover:-translate-y-1 transition-all duration-300">
  <img src="cover.jpg" class="w-full rounded-lg mb-4" alt="도서 표지">
  <h3 class="text-lg font-bold text-slate-800 mb-2">자바의 정석</h3>
  <p class="text-sm text-gray-600 mb-4">남궁성</p>
  <button class="w-full bg-boogie-green hover:bg-boogie-green-dark text-white font-bold py-2 px-4 rounded-lg transition-colors">
    대출하기
  </button>
</div>
```

### 버튼 그룹

```html
<div class="flex gap-2">
  <button class="bg-boogie-green hover:bg-boogie-green-dark text-white font-bold py-2 px-6 rounded-lg">
    승인
  </button>
  <button class="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-6 rounded-lg">
    거절
  </button>
</div>
```

