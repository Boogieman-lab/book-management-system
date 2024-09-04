// pages/Menu/MenuPage.js
import React from 'react';
import MenuTable from '../../components/Menu/MenuTable';
import useMenu from '../../hooks/useMenu';

const MenuPage = () => {
    const {menuItems} = useMenu();

    return (
        <div>
            <h1>Menu List</h1>
            <MenuTable rows={menuItems}/>
        </div>
    );
};

export default MenuPage;
