// lib.jsx — Shared data, helpers, icons. Loaded before screen files.

// ─── Money formatting ───────────────────────────────────────
const fmt = (n, cur = 'RUB', opts = {}) => {
  const sign = n < 0 ? '−' : '';
  const abs = Math.abs(n);
  const grouped = abs.toLocaleString('ru-RU', { maximumFractionDigits: opts.decimals ?? 0, minimumFractionDigits: opts.decimals ?? 0 });
  const sym = { RUB: '₽', USD: '$', EUR: '€' }[cur] || cur;
  return cur === 'USD' ? `${sign}${sym}${grouped}` : `${sign}${grouped} ${sym}`;
};
const fmtSigned = (n, cur) => (n >= 0 ? '+' : '') + fmt(n, cur);

// ─── Accounts ───────────────────────────────────────────────
const ACCOUNTS = [
  { id: 'main',    name: 'Основной',  nameEn: 'Main',       cur: 'RUB', balance: 187420,  type: 'card',  color: '#C5FF4A', last4: '4421' },
  { id: 'savings', name: 'Накопления', nameEn: 'Savings',    cur: 'RUB', balance: 412600,  type: 'depo',  color: '#9B8BFF', last4: '1109' },
  { id: 'cash',    name: 'Наличные',  nameEn: 'Cash',       cur: 'RUB', balance: 8350,    type: 'cash',  color: '#FF6B5B' },
  { id: 'usd',     name: 'USD карта', nameEn: 'USD card',   cur: 'USD', balance: 1240.80, type: 'card',  color: '#52E0C4', last4: '8870' },
  { id: 'eur',     name: 'EUR счёт',  nameEn: 'EUR account', cur: 'EUR', balance: 380.00,  type: 'card',  color: '#FFD24A', last4: '2210' },
];

// ─── Categories ─────────────────────────────────────────────
const CATEGORIES = [
  { id: 'food',     name: 'Продукты',     nameEn: 'Groceries',     glyph: '◐', color: '#C5FF4A', kind: 'expense', budget: 25000, spent: 18420 },
  { id: 'cafe',     name: 'Кафе и бары',  nameEn: 'Cafe & bars',   glyph: '◑', color: '#FF6B5B', kind: 'expense', budget: 12000, spent: 9840 },
  { id: 'transport',name: 'Транспорт',    nameEn: 'Transport',     glyph: '◒', color: '#52E0C4', kind: 'expense', budget: 6000,  spent: 4310 },
  { id: 'subs',     name: 'Подписки',     nameEn: 'Subscriptions', glyph: '◓', color: '#9B8BFF', kind: 'expense', budget: 3500,  spent: 3120 },
  { id: 'health',   name: 'Здоровье',     nameEn: 'Health',        glyph: '◔', color: '#FFD24A', kind: 'expense', budget: 8000,  spent: 2300 },
  { id: 'fun',      name: 'Развлечения',  nameEn: 'Entertainment', glyph: '◕', color: '#FF9DC4', kind: 'expense', budget: 10000, spent: 6580 },
  { id: 'home',     name: 'Дом и аренда', nameEn: 'Home & rent',   glyph: '◖', color: '#7BB8FF', kind: 'expense', budget: 45000, spent: 45000 },
  { id: 'clothes',  name: 'Одежда',       nameEn: 'Clothes',       glyph: '◗', color: '#E89AFF', kind: 'expense', budget: 6000,  spent: 4120 },
  { id: 'salary',   name: 'Зарплата',     nameEn: 'Salary',        glyph: '↗', color: '#C5FF4A', kind: 'income' },
  { id: 'freelance',name: 'Фриланс',      nameEn: 'Freelance',     glyph: '↗', color: '#52E0C4', kind: 'income' },
];

// ─── Transactions (last 14 days, mixed) ─────────────────────
const TXNS = [
  { id: 't01', d: '2026-05-27', t: '14:22', amt: -842,    cat: 'food',     acc: 'main',    note: 'Перекрёсток',           noteEn: 'Grocery store' },
  { id: 't02', d: '2026-05-27', t: '12:08', amt: -380,    cat: 'cafe',     acc: 'main',    note: 'Кофе у офиса',           noteEn: 'Office coffee' },
  { id: 't03', d: '2026-05-27', t: '09:14', amt: -85,     cat: 'transport',acc: 'main',    note: 'Метро',                   noteEn: 'Metro' },
  { id: 't04', d: '2026-05-26', t: '21:40', amt: -2840,   cat: 'cafe',     acc: 'main',    note: 'Ужин · «Сахалин»',        noteEn: 'Dinner · Sakhalin' },
  { id: 't05', d: '2026-05-26', t: '18:20', amt: -990,    cat: 'subs',     acc: 'usd',     note: 'Spotify Family',          noteEn: 'Spotify Family' },
  { id: 't06', d: '2026-05-25', t: '11:00', amt: 145000,  cat: 'salary',   acc: 'main',    note: 'Зарплата · Май',          noteEn: 'Salary · May' },
  { id: 't07', d: '2026-05-25', t: '10:30', amt: -45000,  cat: 'home',     acc: 'main',    note: 'Аренда квартиры',         noteEn: 'Apartment rent' },
  { id: 't08', d: '2026-05-24', t: '19:50', amt: -3240,   cat: 'fun',      acc: 'main',    note: 'Кино + попкорн',          noteEn: 'Cinema + popcorn' },
  { id: 't09', d: '2026-05-24', t: '15:11', amt: -1680,   cat: 'clothes',  acc: 'main',    note: 'Uniqlo · футболка',       noteEn: 'Uniqlo · t-shirt' },
  { id: 't10', d: '2026-05-23', t: '22:14', amt: -620,    cat: 'food',     acc: 'cash',    note: 'Магнит · вечер',          noteEn: 'Late night snacks' },
  { id: 't11', d: '2026-05-22', t: '13:00', amt: 28000,   cat: 'freelance',acc: 'usd',     note: 'Клиент · UI sprint',      noteEn: 'Client · UI sprint' },
  { id: 't12', d: '2026-05-22', t: '08:42', amt: -240,    cat: 'transport',acc: 'main',    note: 'Такси до встречи',        noteEn: 'Taxi to meeting' },
  { id: 't13', d: '2026-05-21', t: '20:00', amt: -1840,   cat: 'health',   acc: 'main',    note: 'Аптека',                  noteEn: 'Pharmacy' },
  { id: 't14', d: '2026-05-20', t: '11:25', amt: -510,    cat: 'cafe',     acc: 'main',    note: 'Завтрак на вынос',        noteEn: 'Takeaway breakfast' },
];

// ─── Helpers ────────────────────────────────────────────────
const catById = (id) => CATEGORIES.find((c) => c.id === id) || CATEGORIES[0];
const accById = (id) => ACCOUNTS.find((a) => a.id === id) || ACCOUNTS[0];
const totalRub = ACCOUNTS.reduce((s, a) => {
  const rate = { RUB: 1, USD: 92, EUR: 99 }[a.cur];
  return s + a.balance * rate;
}, 0);

// ─── Heatmap data (7×12 weeks, expense intensity 0..1) ──────
const HEATMAP = Array.from({ length: 7 * 12 }, (_, i) => {
  // pseudo-random deterministic
  const x = Math.sin(i * 1.37) * 10000;
  const v = Math.abs(x - Math.floor(x));
  return v < 0.18 ? 0 : v;
});

// ─── 12-month bars ──────────────────────────────────────────
const MONTHS_RU = ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек'];
const MONTHS_EN = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
const MONTHLY = [
  { in: 142000, out:  98000 },
  { in: 138000, out: 102000 },
  { in: 145000, out: 112000 },
  { in: 142000, out:  88000 },
  { in: 168000, out: 124000 },
  { in: 142000, out:  91000 },
  { in: 142000, out: 108000 },
  { in: 156000, out:  96000 },
  { in: 142000, out:  79000 },
  { in: 148000, out: 102000 },
  { in: 162000, out: 118000 },
  { in: 173000, out:  93000 },
];

// ─── 30-day sparkline data ──────────────────────────────────
const SPARK_MAIN = [70,72,68,71,74,73,76,75,72,70,68,71,73,76,78,77,79,82,85,83,80,78,76,79,82,85,84,86,88,87];
const SPARK_SAVE = [40,40,40,41,41,41,42,42,42,42,43,43,43,43,44,44,44,44,45,45,45,45,46,46,46,46,47,47,47,47];

Object.assign(window, {
  fmt, fmtSigned, ACCOUNTS, CATEGORIES, TXNS, catById, accById, totalRub,
  HEATMAP, MONTHS_RU, MONTHS_EN, MONTHLY, SPARK_MAIN, SPARK_SAVE,
});
