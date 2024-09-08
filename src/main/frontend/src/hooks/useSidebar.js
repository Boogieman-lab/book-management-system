import {useState, useEffect} from 'react';
import {useNavigate, useLocation} from 'react-router-dom';
import {fetchSidebarItems} from '../services/sidebarService';

// 사이드바 메뉴 항목을 관리하는 커스텀 훅
export default function useSidebar() {
    const [menuItems, setMenuItems] = useState([]);
    const [open, setOpen] = useState({});
    const navigate = useNavigate(); // 네비게이션 훅
    const location = useLocation(); // 현재 경로를 가져오는 훅

    useEffect(() => {
        const fetchData = async () => {
            const data = await fetchSidebarItems();
            setMenuItems(data);

            // 초기 상태 설정: 모든 메뉴 아이템을 열어둠
            const initialOpenState = {};
            data.forEach(item => {
                if (item.subItems && item.subItems.length > 0) {
                    initialOpenState[item.menuId] = true;
                }
            });
            setOpen(initialOpenState);
        };
        fetchData();
    }, []);

    // 현재 경로와 메뉴 URL이 일치하는지 확인
    const isActive = (menuUrl) => location.pathname === menuUrl;

    // 메뉴 항목 클릭 시 네비게이션
    const handleItemClick = (menuUrl) => {
        if (menuUrl) {
            navigate(menuUrl);
        }
    };

    // 아이콘 클릭 시 서브메뉴 열기/닫기
    const handleIconClick = (menuId) => {
        setOpen(prevOpen => ({
            ...prevOpen,
            [menuId]: !prevOpen[menuId]
        }));
    };

    return {menuItems, open, isActive, handleItemClick, handleIconClick};
}
