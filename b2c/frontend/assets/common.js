/* ===== 通用脚本：接口请求 / 格式化 / 提示 ===== */
// 本地静态调试（8010）直连后端；容器部署时由 nginx 反向代理到 /api/shop
const API = location.port === '8010' ? 'http://localhost:8091' : '/api/shop';
const SHOP_ID = 3;
const USER_ID = 1001;

/** 统一请求封装 */
async function request(path, options) {
    const opt = Object.assign({ headers: {} }, options || {});
    if (opt.body && typeof opt.body !== 'string') {
        opt.headers['Content-Type'] = 'application/json';
        opt.body = JSON.stringify(opt.body);
    }
    const resp = await fetch(API + path, opt);
    if (!resp.ok) {
        throw new Error('HTTP ' + resp.status);
    }
    return resp.json();
}

function get(path, params) {
    return request(path + (params ? '?' + qs(params) : ''));
}

function post(path, body) {
    return request(path, { method: 'POST', body: body || {} });
}

function del(path, params) {
    return request(path + (params ? '?' + qs(params) : ''), { method: 'DELETE' });
}

/** 拼接查询串，忽略空值 */
function qs(obj) {
    return Object.keys(obj || {})
        .filter(k => obj[k] !== null && obj[k] !== undefined && obj[k] !== '')
        .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(obj[k]))
        .join('&');
}

function escapeHtml(s) {
    return String(s == null ? '' : s)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

function money(v) {
    const n = Number(v || 0);
    return n.toFixed(2);
}

/** 图片占位图（无主图时使用） */
const IMG_PLACEHOLDER = 'data:image/svg+xml;charset=utf-8,'
    + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200">'
        + '<rect width="200" height="200" fill="#f3f4f6"/>'
        + '<text x="100" y="105" font-size="14" fill="#c0c4cc" text-anchor="middle">暂无图片</text></svg>');

function imgSrc(url) {
    return url ? url : IMG_PLACEHOLDER;
}

let toastTimer = null;
function toast(msg) {
    let el = document.getElementById('toast');
    if (!el) {
        el = document.createElement('div');
        el.id = 'toast';
        el.className = 'toast';
        document.body.appendChild(el);
    }
    el.textContent = msg;
    el.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => el.classList.remove('show'), 2400);
}

/** 通用确认框（原生确认，满足二次确认需求） */
function confirmAction(message) {
    return window.confirm(message);
}

/** 当前登录店铺信息 */
const SHOP_NAME = localStorage.getItem('shopName') || 'ZSL 甄选旗舰店';

/** 状态中文 */
const STATUS_LABEL = {
    CREATED: '待付款', PAID: '待发货', SENT: '待收货',
    COMPLETED: '交易成功', CANCELLED: '交易失败', GROUPING: '待成团'
};

function statusBadge(status) {
    const cls = {
        CREATED: 'badge-orange', PAID: 'badge-blue', SENT: 'badge-cyan',
        COMPLETED: 'badge-green', CANCELLED: 'badge-gray', GROUPING: 'badge-purple'
    }[status] || 'badge-gray';
    return '<span class="badge ' + cls + '">' + (STATUS_LABEL[status] || status) + '</span>';
}

/** 简易分页渲染 */
function renderPagination(el, pageNum, pageSize, total, onChange) {
    const pages = Math.max(1, Math.ceil(total / pageSize));
    let html = '<span>共 ' + total + ' 条</span>';
    html += '<button ' + (pageNum <= 1 ? 'disabled' : '') + ' data-page="' + (pageNum - 1) + '">上一页</button>';
    const start = Math.max(1, pageNum - 2);
    const end = Math.min(pages, start + 4);
    for (let i = start; i <= end; i++) {
        html += '<button class="' + (i === pageNum ? 'active' : '') + '" data-page="' + i + '">' + i + '</button>';
    }
    html += '<button ' + (pageNum >= pages ? 'disabled' : '') + ' data-page="' + (pageNum + 1) + '">下一页</button>';
    el.innerHTML = html;
    el.querySelectorAll('button[data-page]').forEach(btn => {
        btn.onclick = () => {
            const p = Number(btn.dataset.page);
            if (p >= 1 && p <= pages && p !== pageNum) onChange(p);
        };
    });
}

/** 打开/关闭弹窗 */
function openModal(id) { document.getElementById(id).classList.add('show'); }
function closeModal(id) { document.getElementById(id).classList.remove('show'); }

/** 顶部导航高亮 */
function highlightNav(key) {
    document.querySelectorAll('.header-nav-item').forEach(el => {
        if (el.dataset.nav === key) el.classList.add('active');
    });
}

/** 日期 yyyy-MM-dd */
function ymd(date) {
    const d = new Date(date);
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return d.getFullYear() + '-' + m + '-' + day;
}
