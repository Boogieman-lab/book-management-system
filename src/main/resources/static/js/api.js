/**
 * 부기맨 시스템 전역 API Fetch 래퍼
 *
 * [변경 이력]
 * - 이전: localStorage에서 JWT를 꺼내 Authorization 헤더에 주입하고, JWT를 직접 디코딩하여 Role 판단
 * - 현재: accessToken이 HttpOnly 쿠키로 관리됨.
 *         브라우저가 credentials: 'include' 설정에 의해 쿠키를 자동 전송하므로 헤더 주입 불필요.
 *         권한(Role) 판단은 서버(SecurityContext) + Thymeleaf sec:authorize 에서 처리.
 */
window.api = {
    /**
     * @param {string} url
     * @param {RequestInit} options
     */
    async fetch(url, options = {}) {
        const fetchOptions = {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...(options.headers || {})
            },
            // HttpOnly 쿠키(accessToken)가 자동으로 포함되어 서버에 전송됨
            credentials: 'include'
        };

        const response = await window.fetch(url, fetchOptions);

        // 401: 세션 만료 또는 미인증 → 로그인 페이지로 이동
        if (response.status === 401) {
            alert('세션이 만료되었습니다. 다시 로그인해 주세요.');
            window.location.href = '/user/auth/login';
        }

        return response;
    },

    // 로그아웃: 서버에 요청하여 토큰 블랙리스트 처리 + 쿠키 만료
    async logout() {
        try {
            await window.fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            });
        } catch (e) {
            console.error('Logout request failed:', e);
        } finally {
            window.location.href = '/';
        }
    }
};

document.addEventListener('DOMContentLoaded', () => {
    // 로그아웃 버튼 이벤트 (GNB에서 sec:authorize로 로그인 시에만 렌더링됨)
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => window.api.logout());
    }

    // 현재 경로에 맞춰 네비게이션 링크 활성화 처리
    const currentPath = window.location.pathname;
    document.querySelectorAll('nav a[href]').forEach(link => {
        const href = link.getAttribute('href');
        const isActive = (currentPath === href) ||
            (currentPath === '/' && href === '/about') ||
            (href !== '/' && href !== '/about' && currentPath.startsWith(href));

        if (isActive) {
            link.classList.remove('font-bold', 'text-gray-700', 'text-gray-800');
            link.classList.add('font-black', 'text-boogie-main');
            link.style.borderBottom = '3px solid #00A760';
            link.style.paddingBottom = '6px';
        } else {
            link.classList.remove('font-bold');
            link.classList.add('font-medium');
        }
    });
});
