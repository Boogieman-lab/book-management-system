/**
 * 부기맨 시스템 전역 API Fetch 래퍼 (Interceptor 역할)
 * 모든 API 요청 시 localStorage에 저장된 accessToken을 Authorization 헤더에 자동으로 주입합니다.
 */
window.api = {
    /**
     * @param {string} url 
     * @param {RequestInit} options
     */
    async fetch(url, options = {}) {
        const accessToken = localStorage.getItem('accessToken');
        
        // 기본 헤더 설정
        let headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        // 토큰이 존재하면 헤더에 주입
        if (accessToken) {
            headers['Authorization'] = `Bearer ${accessToken}`;
        }

        const fetchOptions = {
            ...options,
            headers,
            // 쿠키(refreshToken 등)를 포함하여 요청하기 위함
            credentials: 'include' 
        };

        let response = await window.fetch(url, fetchOptions);

        // Access Token 만료에 따른 401 에러 발생 시 갱신(Refresh) 시도
        if (response.status === 401 && accessToken) {
            console.log('Access Token expired. Trying to refresh...');
            
            try {
                // refreshToken은 HttpOnly 쿠키로 전송된다고 가정
                const refreshResponse = await window.fetch('/api/auth/refresh', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include'
                });

                if (refreshResponse.ok) {
                    const data = await refreshResponse.json();
                    const newAccessToken = data.accessToken;
                    
                    if (newAccessToken) {
                        localStorage.setItem('accessToken', newAccessToken);
                        console.log('Token successfully refreshed.');
                        
                        // 새로운 토큰으로 원래의 요청을 재시도
                        headers['Authorization'] = `Bearer ${newAccessToken}`;
                        fetchOptions.headers = headers;
                        response = await window.fetch(url, fetchOptions);
                    }
                } else {
                    // 리프레시 토큰까지 무효/만료된 경우
                    console.error('Refresh Token is invalid or expired. Logging out.');
                    this.logout();
                }
            } catch (error) {
                console.error('Error during token refresh:', error);
                this.logout();
            }
        }

        return response;
    },

    // 글로벌 로그아웃 유틸 
    logout() {
        localStorage.removeItem('accessToken');
        alert('보안을 위해 다시 로그인해 주세요.');
        window.location.href = '/user/auth/login';
    },
    
    // 로그인 상태 확인 유틸
    isLoggedIn() {
        return !!localStorage.getItem('accessToken');
    }
};

// 메인 네비게이션(GNB) 토큰 유무에 따른 메뉴 스위칭 로직
document.addEventListener('DOMContentLoaded', () => {
    const authButtons = document.getElementById('auth-buttons');
    const userMenu = document.getElementById('user-menu');
    const logoutBtn = document.getElementById('logout-btn');
    
    // GNB 스위칭 제어
    if (authButtons && userMenu) {
        if (window.api.isLoggedIn()) {
            authButtons.classList.add('hidden');
            userMenu.classList.remove('hidden');
            userMenu.classList.add('flex');
            
            // 추후 JWT 디코딩을 통해 ROLE_ADMIN 권한 확인 후 admin-btn 활성화 로직 추가 가능
        } else {
            authButtons.classList.remove('hidden');
            userMenu.classList.add('hidden');
            userMenu.classList.remove('flex');
        }
    }

    // 전역 로그아웃 버튼 이벤트
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            try {
                // 서버 블랙리스트 처리 연동 (에러가 나도 클라이언트에서는 토큰을 지워야 함)
                await window.api.fetch('/api/auth/logout', { method: 'POST' });
            } catch(e) { 
                console.error(e); 
            } finally {
                localStorage.removeItem('accessToken');
                window.location.href = '/';
            }
        });
    }
});
