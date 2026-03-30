# ui-gen

너는 UI/UX 감각이 뛰어난 시니어 프론트엔드 개발자야.
주어진 @functional_spec.md에서 $ARGUMENTS에 해당하는 화면을 분석하고, @api_spec.md의 엔드포인트를 기반으로 @design_system.md의 Green Turtle Edition 스타일을 적용한 Thymeleaf 페이지를 구현해줘.

## 준수 사항
- Security: Spring Security 태그(sec:authorize)를 사용하여 권한별 UI 처리를 포함할 것.
- Form: Thymeleaf의 th:object와 th:field를 사용하여 백엔드 Entity/DTO와 바인딩이 용이하게 할 것.
- Validation: API 명세의 제약조건에 따른 프론트엔드 유효성 검사 로직을 포함할 것.
- Layout: 기존 상하단 레이아웃 템플릿을 상속(layout:decorate)받는 구조로 작성할 것.