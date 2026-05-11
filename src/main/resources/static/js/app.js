(function () {
    const appState = window.app || (window.app = {});
    appState.charts = appState.charts || {};

    function parseJson(value) {
        if (!value) {
            return [];
        }
        try {
            return JSON.parse(value);
        } catch {
            return [];
        }
    }

    function destroyAllCharts() {
        Object.keys(appState.charts).forEach(function (key) {
            const chart = appState.charts[key];
            if (chart) {
                chart.destroy();
            }
            delete appState.charts[key];
        });
    }

    function closeOpenModal() {
        const modalEl = document.querySelector('.modal.show');
        if (!modalEl || typeof bootstrap === 'undefined') {
            return;
        }
        const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
        modal.hide();
        document.querySelectorAll('.modal-backdrop').forEach(function (backdrop) {
            backdrop.remove();
        });
        document.body.classList.remove('modal-open');
        document.body.style.removeProperty('padding-right');
    }

    function normalizePath(path) {
        if (!path) {
            return '/';
        }
        if (path.length > 1 && path.endsWith('/')) {
            return path.slice(0, -1);
        }
        return path;
    }

    function setActiveSidebarLink(pathname) {
        const normalizedPath = normalizePath(pathname);
        document.querySelectorAll('.sidebar-wrapper .nav-link').forEach(function (link) {
            let active = false;
            try {
                const linkPath = normalizePath(new URL(link.href, window.location.origin).pathname);
                const isHomeLink = link.dataset.homeLink === 'true';
                if (isHomeLink) {
                    active = normalizedPath === linkPath || normalizedPath === normalizePath(linkPath.replace(/\/dashboard$/, ''));
                } else {
                    active = normalizedPath === linkPath;
                }
                if (!active && !isHomeLink && linkPath !== '/' && normalizedPath.startsWith(linkPath)) {
                    const nextChar = normalizedPath.charAt(linkPath.length);
                    active = !nextChar || nextChar === '/' || nextChar === '?';
                }
            } catch {
                active = false;
            }
            link.classList.toggle('active', active);
        });
    }

    function renderCharts() {
        document.querySelectorAll('canvas[data-type]').forEach(function (canvas, index) {
            const type = (canvas.getAttribute('data-type') || '').toLowerCase();
            const labels = parseJson(canvas.getAttribute('data-labels'));
            const values = parseJson(canvas.getAttribute('data-values'));
            const key = canvas.id || `${type}-${index}`;
            if (appState.charts[key]) {
                appState.charts[key].destroy();
                delete appState.charts[key];
            }

            let config;
            if (type === 'bar') {
                config = {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Số lịch',
                            data: values,
                            backgroundColor: '#20c997',
                            borderRadius: 6
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                display: false
                            }
                        }
                    }
                };
            } else {
                const palette = ['#0d6efd', '#198754', '#fd7e14', '#6f42c1', '#20c997', '#dc3545', '#ffc107', '#0dcaf0'];
                config = {
                    type: type === 'doughnut' ? 'doughnut' : 'pie',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: values,
                            backgroundColor: values.map(function (_, i) {
                                return palette[i % palette.length];
                            }),
                            borderColor: '#ffffff',
                            borderWidth: 2
                        }]
                    },
                    options: {
                        responsive: true,
                    }
                };
            }

            appState.charts[key] = new Chart(canvas, config);
        });
    }

    function extractFragment(html) {
        const doc = new DOMParser().parseFromString(html, 'text/html');
        const wrapped = doc.querySelector('#main-content');
        if (wrapped) {
            return wrapped.innerHTML;
        }
        const fragment = doc.querySelector('[th\\:fragment="content"]');
        if (fragment) {
            return fragment.innerHTML;
        }
        return doc.body ? doc.body.innerHTML : html;
    }

    function updateMainContent(html) {
        const target = document.getElementById('main-content');
        if (!target) {
            return;
        }
        closeOpenModal();
        destroyAllCharts();
        target.innerHTML = extractFragment(html);
        renderCharts();
    }

    function fetchFragment(url, pushState) {
        const cleanUrl = new URL(url, window.location.origin);
        const requestUrl = new URL(cleanUrl.toString());
        requestUrl.searchParams.set('ajax', 'true');
        return fetch(requestUrl.toString(), {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        }).then(function (response) {
            return response.text();
        }).then(function (html) {
            updateMainContent(html);
            setActiveSidebarLink(cleanUrl.pathname);
            if (pushState) {
                window.history.pushState({ url: cleanUrl.pathname + cleanUrl.search }, '', cleanUrl.pathname + cleanUrl.search);
            }
        });
    }

    document.addEventListener('click', function (event) {
        const link = event.target.closest('a');
        if (!link) {
            return;
        }
        const href = link.getAttribute('href');
        if (!href || href.startsWith('#') || href.startsWith('javascript:')) {
            return;
        }
        const isSameOrigin = new URL(link.href, window.location.origin).origin === window.location.origin;
        if (!isSameOrigin) {
            return;
        }
        if (link.dataset.ajax === 'false') {
            return;
        }
        const inSidebar = !!link.closest('.sidebar-wrapper');
        const inMainContent = !!link.closest('#main-content');
        if (!inSidebar && !inMainContent && !link.classList.contains('ajax-link')) {
            return;
        }
        event.preventDefault();
        fetchFragment(href, true);
    });

    document.addEventListener('click', function (event) {
        const approveButton = event.target.closest('[data-action="open-approve-modal"]');
        if (approveButton) {
            event.preventDefault();
            const modalEl = document.getElementById('approveSessionModal');
            if (!modalEl) {
                return;
            }
            modalEl.querySelector('input[name="sessionId"]').value = approveButton.dataset.sessionId || '';
            modalEl.querySelector('input[name="studentName"]').value = approveButton.dataset.studentName || '';
            modalEl.querySelector('input[name="sessionDate"]').value = approveButton.dataset.sessionDate || '';
            modalEl.querySelector('input[name="sessionTime"]').value = (approveButton.dataset.startTime || '') + ' - ' + (approveButton.dataset.endTime || '');
            modalEl.querySelector('input[name="sort"]').value = approveButton.dataset.sort || 'date_asc';
            modalEl.querySelector('select[name="equipmentId"]').value = '';
            if (typeof bootstrap !== 'undefined') {
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
        }
        const evaluateButton = event.target.closest('[data-action="open-evaluate-modal"]');
        if (evaluateButton) {
            event.preventDefault();
            const modalEl = document.getElementById('evaluateSessionModal');
            if (!modalEl) {
                return;
            }
            modalEl.querySelector('input[name="sessionId"]').value = evaluateButton.dataset.sessionId || '';
            modalEl.querySelector('input[name="studentName"]').value = evaluateButton.dataset.studentName || '';
            modalEl.querySelector('input[name="sessionDate"]').value = evaluateButton.dataset.sessionDate || '';
            modalEl.querySelector('input[name="sessionTime"]').value = (evaluateButton.dataset.startTime || '') + ' - ' + (evaluateButton.dataset.endTime || '');
            modalEl.querySelector('input[name="sort"]').value = evaluateButton.dataset.sort || 'date_asc';
            modalEl.querySelector('textarea[name="evaluationNotes"]').value = '';
            modalEl.querySelector('select[name="performanceRating"]').value = '5';
            if (typeof bootstrap !== 'undefined') {
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
        }
    });

    document.addEventListener('change', function (event) {
        const select = event.target.closest('select.ajax-sort');
        if (!select) {
            return;
        }
        event.preventDefault();
        const baseUrl = select.getAttribute('data-url') || window.location.pathname;
        const url = new URL(baseUrl, window.location.origin);
        url.searchParams.set('sort', select.value);
        fetchFragment(url.toString(), true);
    });

    document.addEventListener('submit', function (event) {
        const form = event.target.closest('form');
        if (!form) {
            return;
        }
        event.preventDefault();
        if (form.dataset.ajax === 'false') {
            form.submit();
            return;
        }
        const method = (form.getAttribute('method') || 'GET').toUpperCase();
        const action = form.getAttribute('action') || window.location.href;
        if (method === 'GET') {
            const params = new URLSearchParams(new FormData(form));
            const url = new URL(action, window.location.origin);
            params.forEach(function (value, key) {
                url.searchParams.set(key, value);
            });
            fetchFragment(url.toString(), true);
            return;
        }
        fetch(action, {
            method: method,
            body: new FormData(form),
            credentials: 'same-origin',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        }).then(function (response) {
            return response.text();
        }).then(function (html) {
            updateMainContent(html);
        });
    });

    window.addEventListener('popstate', function () {
        fetchFragment(window.location.pathname + window.location.search, false);
    });

    document.addEventListener('DOMContentLoaded', function () {
        renderCharts();
        setActiveSidebarLink(window.location.pathname);
    });
})();

