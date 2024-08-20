// 데이터를 가져오는 유틸리티 함수
export const fetchData = async (url) => {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    return response.json();
};