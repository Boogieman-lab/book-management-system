import React from "react";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import Sidebar from "./components/Sidebar";

function App() {
    return (
        <Router>
            <div className="App">
                <Sidebar>
                    <Routes>
                    </Routes>
                </Sidebar>
            </div>
        </Router>
    );
}


export default App;
