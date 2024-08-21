import { fetchData } from '../utils/fetchData';

const SIDEBAR_API_URL = 'http://localhost:8080/api/sidebar';

export const fetchSidebarItems = async () => {
    try {
        const data = await fetchData(SIDEBAR_API_URL);
        return structureSidebarData(data);
    } catch (error) {
        console.error("Failed to fetch menu items", error);
        return [];
    }
};

// 사이드바 메뉴의 구조를 설정하는 함수
function structureSidebarData(items) {
    const topLevelItems = items.filter(item => item.parentId === null);
    const itemMap = new Map(items.map(item => [item.menuId, { ...item, subItems: [] }]));

    topLevelItems.forEach(item => {
        itemMap.get(item.menuId).subItems = items.filter(subItem => subItem.parentId === item.menuOrder);
    });

    return topLevelItems.map(item => itemMap.get(item.menuId));
}