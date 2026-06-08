document.addEventListener('DOMContentLoaded', () => {

    const initSortableTable = (tableElement) => {
        if (!tableElement) return;

        const tbody = tableElement.querySelector('tbody');
        const headers = tableElement.querySelectorAll('th.sortable');

        const sortTable = (index, isNumeric, currentOrder, th) => {
            const allRows = Array.from(tbody.querySelectorAll('tr'));
            const dataRows = allRows.filter(row => !row.classList.contains('collapse'));

            dataRows.sort((a, b) => {
                let valA = a.children[index].innerText.trim().replace(/S\/\s*/g, '').replace(/,/g, '');
                let valB = b.children[index].innerText.trim().replace(/S\/\s*/g, '').replace(/,/g, '');

                if (isNumeric) {
                    return currentOrder === 'asc' ? parseFloat(valA || 0) - parseFloat(valB || 0) : parseFloat(valB || 0) - parseFloat(valA || 0);
                } else {
                    return currentOrder === 'asc' ? valA.localeCompare(valB) : valB.localeCompare(valA);
                }
            });

            dataRows.forEach(row => {
                tbody.appendChild(row);
                const detalleTarget = row.querySelector('[data-bs-toggle="collapse"]')?.getAttribute('href');
                if (detalleTarget) {
                    const detalleRow = tbody.querySelector(detalleTarget);
                    if (detalleRow) tbody.appendChild(detalleRow);
                }
            });
        };

        headers.forEach(th => {
            th.style.cursor = 'pointer';
            th.addEventListener('click', () => {
                const index = Array.from(th.parentNode.children).indexOf(th);
                const isNumeric = th.dataset.type === 'number';
                const currentOrder = th.dataset.order === 'asc' ? 'desc' : 'asc';

                tableElement.querySelectorAll('th.sortable').forEach(h => {
                    h.dataset.order = '';
                    const icon = h.querySelector('.sort-icon');
                    if(icon) icon.className = 'bi bi-arrow-down-up sort-icon text-muted';
                });

                th.dataset.order = currentOrder;
                const activeIcon = th.querySelector('.sort-icon');
                if(activeIcon) {
                    activeIcon.className = currentOrder === 'asc' ? 'bi bi-arrow-up sort-icon text-danger' : 'bi bi-arrow-down sort-icon text-danger';
                }
                sortTable(index, isNumeric, currentOrder, th);
            });
        });

        if (headers.length > 0) {
            const firstHeader = headers[0];
            firstHeader.dataset.order = 'desc';
            const icon = firstHeader.querySelector('.sort-icon');
            if(icon) icon.className = 'bi bi-arrow-down sort-icon text-danger';
            sortTable(0, firstHeader.dataset.type === 'number', 'desc', firstHeader);
        }
    };

    document.querySelectorAll('table').forEach(table => {
        initSortableTable(table);
    });
});