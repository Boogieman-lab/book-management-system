import React, {useState, useEffect} from 'react';
import styled from '@emotion/styled';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Collapse from '@mui/material/Collapse';
import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import useSidebar from '../../hooks/useSidebar';

// 사이드바 컨테이너 스타일
// SidebarContainer: List 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// 사이드바의 전체 레이아웃과 스타일을 관리
const SidebarContainer = styled(List)`
  width: 240px; /* 사이드바의 너비를 250px로 고정 */
  max-width: 240px; /* 사이드바의 최대 너비를 250px로 고정 */
  background-color: #202734; /* 사이드바의 배경색을 어두운 회색(#202734)으로 설정 */
  color: #9ca3af; /* 사이드바 내부 텍스트 색상을 회색(#9ca3af)으로 설정 */
  position: fixed; /* 사이드바를 화면에 고정시켜, 스크롤해도 위치가 유지되도록 설정 */
  height: calc(100vh - 48px); /* 사이드바의 높이를 화면 전체 높이에서 AppBar의 높이(48px)를 뺀 값으로 설정 */
  top: 48px; /* 사이드바의 상단 위치를 AppBar 높이(48px)만큼 아래로 설정 */
  overflow-y: auto; /* 내용이 넘칠 경우, 세로 스크롤이 가능하도록 설정 */
`;

// 사용자 정의 ListItemText 스타일
// CustomListItemText: ListItemText 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// 각 메뉴 항목의 텍스트 스타일을 관리
const CustomListItemText = styled(ListItemText)`
  .MuiTypography-root {
    color: inherit; /* 기본 색상 유지 */
    font-weight: inherit; /* 기본 굵기 유지 */
  }

  &.active .MuiTypography-root {
    color: white; /* 활성화된 항목에 대해 흰색으로 변경 */
    font-weight: bold; /* 활성화된 항목에 대해 굵게 변경 */
  }
`;

// 사용자 정의 ExpandLess 아이콘 스타일
// CustomExpandLess: ExpandLess 아이콘을 기반으로 하는 사용자 정의 스타일 컴포넌트
// 서브메뉴가 열려 있을 때 표시되는 아이콘의 스타일을 관리
const CustomExpandLess = styled(ExpandLess)`
  color: #9ca3af; /* 아이콘의 색상을 회색(#9ca3af)으로 설정 */
`;

// 사용자 정의 ExpandMore 아이콘 스타일
// CustomExpandMore: ExpandMore 아이콘을 기반으로 하는 사용자 정의 스타일 컴포넌트
// 서브메뉴가 닫혀 있을 때 표시되는 아이콘의 스타일을 관리
const CustomExpandMore = styled(ExpandMore)`
  color: #9ca3af; /* 아이콘의 색상을 회색(#9ca3af)으로 설정 */
`;

export default function Sidebar() {
    const { menuItems, open, isActive, handleItemClick, handleIconClick } = useSidebar();

    return (
        <SidebarContainer component="nav" aria-labelledby="nested-list-subheader">
            {menuItems.map((item) => (
                <React.Fragment key={item.menuId}>
                    <ListItemButton onClick={() => handleItemClick(item.menuUrl)}>
                        <CustomListItemText primary={item.menuName}  className={isActive(item.menuUrl) ? "active" : ""}/>
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
                                        onClick={() => handleItemClick(subItem.menuUrl)}
                                    >
                                        <CustomListItemText
                                            primary={subItem.menuName}
                                            className={isActive(subItem.menuUrl) ? "active" : ""}
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
