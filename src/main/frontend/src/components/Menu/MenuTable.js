import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

const MenuTable = ({rows}) => (
    <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} aria-label="menu table">
            <TableHead>
                <TableRow>
                    <TableCell>Menu ID</TableCell>
                    <TableCell>Menu Name</TableCell>
                    <TableCell>Menu URL</TableCell>
                    <TableCell>Parent ID</TableCell>
                    <TableCell>Order</TableCell>
                    <TableCell>Active</TableCell>
                    <TableCell>Type</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {rows.map((row) => (
                    <TableRow key={row.menuId}>
                        <TableCell>{row.menuId}</TableCell>
                        <TableCell>{row.menuName}</TableCell>
                        <TableCell>{row.menuUrl}</TableCell>
                        <TableCell>{row.parentId}</TableCell>
                        <TableCell>{row.menuOrder}</TableCell>
                        <TableCell>{row.activeYn}</TableCell>
                        <TableCell>{row.menuType}</TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    </TableContainer>
);

export default MenuTable;
