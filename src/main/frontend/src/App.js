import React from "react";
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Sidebar from "./components/Common/Sidebar";
import Header from "./components/Common/Header";
import MenuPage from './pages/Menu/MenuPage';
import {Box, CssBaseline} from '@mui/material';

function App() {
    return (
        <Router>
            <Box sx={{display: 'flex'}}>
                {/* 브라우저 기본 스타일을 초기화 */}
                <CssBaseline/>
                {/* 좌측 고정 사이드바 */}
                <Sidebar/>
                {/* 상단 고정 헤더 */}
                <Header/>

                {/* 메인 콘텐츠 영역 */}
                <Box componet="main" sx={{flexGrow: 4, p: 4, mt: 0}}>
                    <Routes>
                        /<Route path="/admin/system/menus" element={<MenuPage />} /> {/* 메뉴 페이지 라우트 */}
                        {/* 라우트 정의는 여기에 추가 */}
                    </Routes>
                </Box>
            </Box>
        </Router>
    );
}

export default App;