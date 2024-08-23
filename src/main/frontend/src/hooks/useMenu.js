import {useState, useEffect} from 'react';
import {fetchMenuData} from '../services/menuService';

const useMenu = () => {
    const [menuItems, setMenuItems] = useState([]);

    useEffect(() => {
        const loadMenuData = async () => {
            const data = await fetchMenuData();
            setMenuItems(data);
        };

        loadMenuData();
    }, []);

    return {menuItems};
};

export default useMenu;
