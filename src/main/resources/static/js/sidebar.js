function foldSidebar() {
    const sidebar = document.getElementById('bmSidebar');
    const foldBtn = document.getElementById('foldBtn');
    sidebar.classList.toggle('folded');
    foldBtn.innerHTML = sidebar.classList.contains('folded') ? '≫' : '≪';
}