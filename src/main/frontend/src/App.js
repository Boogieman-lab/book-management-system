import React from "react";
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Sidebar from "./components/Common/Sidebar";
import Header from "./components/Common/Header";
import {Box, CssBaseline} from '@mui/material';

function App() {
    return (
        <Router>
            <Box sx={{display: 'flex'}}>
                <CssBaseline/> {/* 브라우저 기본 스타일을 초기화 */}

                <Sidebar/> {/* 좌측 고정 사이드바 */}
                <Header/> {/* 상단 고정 헤더 */}

                {/* 메인 콘텐츠 영역 */}
                <Box component="main" sx={{flexGrow: 1, p: 3, mt: 8}}>
                    <Routes>
                        {/* 라우트 정의는 여기에 추가 */}
                    </Routes>
                </Box>
            </Box>
        </Router>
    );
}

export default App;