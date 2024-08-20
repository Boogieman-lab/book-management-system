import React, {useState, useEffect} from 'react';
import styled from '@emotion/styled';
import ListSubheader from '@mui/material/ListSubheader';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Collapse from '@mui/material/Collapse';
import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import {useNavigate, useLocation} from 'react-router-dom';
import {fetchSidebarItems} from '../../services/menuService';

// 사이드바 컨테이너 스타일
const SidebarContainer = styled(List)`
  width: 100%;
  max-width: 270px;
  background-color: #202734;
  color: #9ca3af;
`;

// 사용자 정의 리스트 서브헤더 스타일
const CustomListSubheader = styled(ListSubheader)`
  background-color: inherit;
  color: inherit;
  font-size: 25px;
  font-weight: bold; /* 텍스트를 굵게 설정 */
`;

// 사용자 정의 ListItemText 스타일
const CustomListItemText = styled(ListItemText)`
  .MuiTypography-root {
    color: ${(props) => (props.active ? 'white' : 'inherit')}; /* 활성화된 항목의 글자 색상 설정 */
    font-weight: ${(props) => (props.active ? 'bold' : 'normal')}; /* 활성화된 항목의 글자 굵기 설정 */
    /* font-size: ${(props) => (props.active ? '18px' : '16px')}; */ /* 활성화된 항목의 글자 크기 설정 */
  }
`;

// 사용자 정의 ExpandLess 아이콘 스타일
const CustomExpandLess = styled(ExpandLess)`
  color: #9ca3af;
`;

// 사용자 정의 ExpandMore 아이콘 스타일
const CustomExpandMore = styled(ExpandMore)`
  color: #9ca3af;
`;

export default function Sidebar() {
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

    return (
        <SidebarContainer
            component="nav"
            aria-labelledby="nested-list-subheader"
            subheader={
                <CustomListSubheader component="div" id="nested-list-subheader">
                    BMS
                </CustomListSubheader>
            }
        >
            {menuItems.map((item) => (
                <React.Fragment key={item.menuId}>
                    <ListItemButton
                        onClick={() => handleItemClick(item.menuUrl)}
                    >
                        <CustomListItemText primary={item.menuName} active={isActive(item.menuUrl)}/>
                        {item.subItems.length > 0 && (
                            open[item.menuId] ?
                                <CustomExpandLess onClick={(e) => {
                                    e.stopPropagation();
                                    handleIconClick(item.menuId);
                                }}/> :
                                <CustomExpandMore onClick={(e) => {
                                    e.stopPropagation();
                                    handleIconClick(item.menuId);
                                }}/>
                        )}
                    </ListItemButton>
                    {item.subItems.length > 0 && (
                        <Collapse in={open[item.menuId]} timeout="auto" unmountOnExit>
                            <List component="div" disablePadding>
                                {item.subItems.map((subItem) => (
                                    <ListItemButton
                                        sx={{pl: 4}}
                                        key={subItem.menuId}
                                        onClick={() => navigate(subItem.menuUrl)}
                                    >
                                        <CustomListItemText
                                            primary={subItem.menuName}
                                            active={isActive(subItem.menuUrl)} // 서브메뉴 항목의 활성화 상태 설정
                                        />
                                    </ListItemButton>
                                ))}
                            </List>
                        </Collapse>
                    )}
                </React.Fragment>
            ))}
        </SidebarContainer>
    );
}
