import * as React from 'react';
import { AppBar, Box, Toolbar, Typography, Button, IconButton } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import styled from '@emotion/styled';

// Custom Components

// CustomBox: Box 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// flex-grow 속성을 1로 설정하여, 부모 컨테이너 안에서 가능한 공간을 모두 차지하게 함
const CustomBox = styled(Box)`
  flex-grow: 1;
`;

// CustomAppBar: AppBar 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// 배경색을 사이드바와 동일한 색상(#202734)으로 설정하여 통일감을 줌
const CustomAppBar = styled(AppBar)`
  background-color: #202734; /* Sidebar와 동일한 배경색으로 설정 */
`;

// CustomToolbar: Toolbar 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// min-height 속성을 48px로 강제 설정하여 모든 화면에서 동일한 높이를 유지하도록 함
// padding 속성을 0 16px로 설정하여 좌우 여백을 추가
const CustomToolbar = styled(Toolbar)`
  min-height: 48px !important; /* 모든 화면에서 Toolbar의 최소 높이를 48px로 설정 */
  padding: 0 16px !important; /* Toolbar의 좌우 패딩 설정 */
`;

// CustomIconButton: IconButton 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// 기본적으로 오른쪽에 16px의 여백을 추가하여 아이콘과 다른 요소들 사이에 공간을 줌
const CustomIconButton = styled(IconButton)`
  margin-right: 16px; /* 기본 margin-right 설정 */
`;

// CustomTypography: Typography 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// 글자 색상을 회색 계열(#9ca3af)로 설정하고, 폰트 크기를 25px, 폰트 굵기를 bold로 설정하여
// 중요한 텍스트를 강조
const CustomTypography = styled(Typography)`
  color: #9ca3af; /* 글자 색상을 #9ca3af로 설정 */
  font-size: 25px;
  font-weight: bold; /* 텍스트를 굵게 설정 */
`;

// CustomButton: Button 컴포넌트를 기반으로 하는 사용자 정의 스타일 컴포넌트
// 버튼의 글자 색상을 회색 계열(#9ca3af)로 설정하여 전체적인 스타일과 일관성 유지
const CustomButton = styled(Button)`
  color: #9ca3af; /* 버튼 글자 색상을 #9ca3af로 설정 */
`;

// ButtonAppBar 컴포넌트
export default function Header() {
    return (
        <CustomBox>
            <CustomAppBar position="fixed">
                <CustomToolbar>
                    <CustomIconButton size="large" edge="start" color="inherit" aria-label="menu">
                        <MenuIcon />
                    </CustomIconButton>
                    <CustomTypography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        BMS
                    </CustomTypography>
                    <CustomButton color="inherit">Login</CustomButton>
                </CustomToolbar>
            </CustomAppBar>
        </CustomBox>
    );
}
