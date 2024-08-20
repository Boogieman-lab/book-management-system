import React, { useState, useEffect } from 'react';
import ListSubheader from '@mui/material/ListSubheader';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Collapse from '@mui/material/Collapse';
import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import { useNavigate } from 'react-router-dom';
import { fetchSidebarItems } from '../../services/menuService';

export default function Sidebar() {
    const [menuItems, setMenuItems] = useState([]);
    const [open, setOpen] = useState({});
    const navigate = useNavigate(); // useNavigate 훅 사용

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

    const handleItemClick = (menuUrl) => {
        if (menuUrl) {
            navigate(menuUrl);
        }
    };

    const handleIconClick = (menuId) => {
        setOpen(prevOpen => ({
            ...prevOpen,
            [menuId]: !prevOpen[menuId]
        }));
    };

    return (
        <List
            sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}
            component="nav"
            aria-labelledby="nested-list-subheader"
            subheader={
                <ListSubheader component="div" id="nested-list-subheader">
                    <h1>BMS</h1>
                </ListSubheader>
            }
        >
            {menuItems.map((item) => (
                <React.Fragment key={item.menuId}>
                    <ListItemButton onClick={() => handleItemClick(item.menuUrl)}>
                        <ListItemText primary={item.menuName} />
                        {item.subItems.length > 0 && (
                            open[item.menuId] ?
                                <ExpandLess onClick={(e) => { e.stopPropagation(); handleIconClick(item.menuId); }} /> :
                                <ExpandMore onClick={(e) => { e.stopPropagation(); handleIconClick(item.menuId); }} />
                        )}
                    </ListItemButton>
                    {item.subItems.length > 0 && (
                        <Collapse in={open[item.menuId]} timeout="auto" unmountOnExit>
                            <List component="div" disablePadding>
                                {item.subItems.map((subItem) => (
                                    <ListItemButton
                                        sx={{ pl: 4 }}
                                        key={subItem.menuId}
                                        onClick={() => navigate(subItem.menuUrl)}
                                    >
                                        <ListItemText primary={subItem.menuName} />
                                    </ListItemButton>
                                ))}
                            </List>
                        </Collapse>
                    )}
                </React.Fragment>
            ))}
        </List>
    );
}
