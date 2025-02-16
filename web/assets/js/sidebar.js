document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.querySelector(".sidebar");
    const sidebarToggle = document.querySelector(".sidebar-logo");
    sidebarToggle.addEventListener("click", () => {
        sidebar.classList.toggle("collapsed");
    });

    document.querySelectorAll('li > .list').forEach(item => {
        item.addEventListener('click', function() {
            const submenu = this.nextElementSibling;
            // Vérifier si le sous-menu est déjà ouvert
            if (submenu.classList.contains('open')) {
                submenu.classList.remove('open');
            } else {
                closeSubmenus(submenu);
                submenu.classList.add('open');
            }
        });

        function closeSubmenus(parent) {
            const submenus = parent.querySelectorAll('.submenu.open');
            submenus.forEach(sub => {
                closeSubmenus(sub)
                sub.classList.remove('open');
            });
        }
    });
    
});
