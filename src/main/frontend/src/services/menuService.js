import {fetchData} from '../utils/fetchData';

const MENU_API_URL = 'http://localhost:8080/api/menus';

export const fetchMenuData = async () => {
    try {
        const data = await fetchData(MENU_API_URL);
        return data;
    } catch (error) {
        console.error("Failed to fetch menu data", error);
        return [];
    }
};
