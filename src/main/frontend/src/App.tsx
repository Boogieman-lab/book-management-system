import React, { useEffect, useState } from 'react';
import axios from 'axios';

interface MenuDTO {
    menuId: number;
    activeYn: boolean;
    menuName: string;
    menuOrder: number;
    menuType: string;
    menuUrl: string;
    parentId: number | null;
}

const App: React.FC = () => {
    const [menuList, setMenuList] = useState<MenuDTO[]>([]);
    const [searchKeyword, setSearchKeyword] = useState<string>('');
    const [pageNumber, setPageNumber] = useState<number>(0);

    useEffect(() => {
        fetchMenuList();
    }, []);

    const fetchMenuList = (query: string = '', page: number = 0) => {
        axios.get(`/api/menu?page=${page}&searchMenuName=${query}`)
            .then(response => setMenuList(response.data))
            .catch(error => console.error(error));
    };

    const handleSearch = () => {
        fetchMenuList(searchKeyword, pageNumber);
    };

    return (
        <div>
            <h1>Menu List</h1>
            <div>
                <input
                    type="text"
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                    placeholder="검색어를 입력하세요"
                />
                <input
                    type="number"
                    value={pageNumber}
                    onChange={(e) => setPageNumber(parseInt(e.target.value, 10))}
                    placeholder="페이지 번호"
                />
                <button onClick={handleSearch}>검색</button>
            </div>
            <ul>
                {menuList.map(menu => (
                    <li key={menu.menuId}>{menu.menuName}</li>
                ))}
            </ul>
        </div>
    );
}

export default App;