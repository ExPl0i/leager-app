// drift.jsx — Direction C · Drift
// Neon-tinted off-black with gradient cards. Geist Mono numbers + Geist Sans UI.
// Stats: stream-river flow over time + mosaic donut breakdown.

const D = {
  bg: '#08090B',
  bg2: '#0F1014',
  surf: 'rgba(255,255,255,0.04)',
  surf2: 'rgba(255,255,255,0.08)',
  border: 'rgba(255,255,255,0.08)',
  text: '#F2F4F7',
  muted: '#9AA0AE',
  faint: '#5C6170',
  neon: '#7CFF6B',      // neon green
  neon2: '#E8FF52',     // electric lime
  pink: '#FF5BD4',      // hot magenta
  blue: '#5B8DFF',
  amber: '#FFB54A',
  mono: '"Geist Mono", "JetBrains Mono", ui-monospace, monospace',
  sans: '"Geist", "Inter", system-ui, sans-serif',
};
const D_LIGHT = {
  ...D,
  bg: '#F4F5F7', bg2: '#FFFFFF', surf: 'rgba(0,0,0,0.04)', surf2: 'rgba(0,0,0,0.08)',
  border: 'rgba(0,0,0,0.08)',
  text: '#08090B', muted: '#5C6170', faint: '#9AA0AE',
  neon: '#1F7A0F', neon2: '#6B7700', pink: '#C6238E', blue: '#2D5BC9', amber: '#A86A00',
};
const useDrift = () => (window.__theme === 'light' ? D_LIGHT : D);

// ─── Curved tab bar with floating add ───────────────────────
const DNav = ({ active = 'home' }) => {
  const d = useDrift();
  const items = [
    ['home', 'M3 11l9-8 9 8M5 9v11h14V9'],
    ['ops',  'M4 6h16M4 12h16M4 18h10'],
    ['add',  null],
    ['stat', 'M4 20V8M10 20V4M16 20v-10'],
    ['set',  'M4 6h16M4 12h16M4 18h16'],
  ];
  return (
    <div style={{ position: 'relative', flexShrink: 0, paddingBottom: 8 }}>
      {/* Big floating add button */}
      <div style={{ position: 'absolute', left: '50%', top: -28, transform: 'translateX(-50%)', zIndex: 2,
        width: 64, height: 64, borderRadius: 32, background: `radial-gradient(circle at 30% 25%, ${d.neon} 0%, ${d.neon} 40%, #45C038 100%)`,
        boxShadow: `0 0 30px ${d.neon}44, 0 6px 20px rgba(0,0,0,0.5)`,
        display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#08090B',
        fontSize: 30, fontWeight: 500, lineHeight: 1, paddingBottom: 4 }}>+</div>
      <div style={{ background: d.bg2, borderTop: `1px solid ${d.border}`, height: 76, display: 'flex',
        alignItems: 'center', padding: '0 12px' }}>
        {items.map(([k, path]) => {
          if (k === 'add') return <div key={k} style={{ flex: 1 }} />;
          const on = k === active;
          return (
            <div key={k} style={{ flex: 1, display: 'flex', justifyContent: 'center' }}>
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={on ? d.neon : d.muted} strokeWidth="1.7" strokeLinecap="round"><path d={path}/></svg>
            </div>
          );
        })}
      </div>
    </div>
  );
};

// ─── Sparkline ──────────────────────────────────────────────
const Spark = ({ data, color, h = 36, w = 100 }) => {
  const min = Math.min(...data), max = Math.max(...data);
  const range = max - min || 1;
  const pts = data.map((v, i) => [(i / (data.length - 1)) * w, h - ((v - min) / range) * h]);
  const area = `M0,${h} ${pts.map(([x, y]) => `L${x},${y}`).join(' ')} L${w},${h} Z`;
  const line = `M${pts.map(([x, y]) => `${x},${y}`).join(' L')}`;
  return (
    <svg width={w} height={h} viewBox={`0 0 ${w} ${h}`}>
      <defs>
        <linearGradient id={`sg-${color.replace('#', '')}`} x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={color} stopOpacity="0.35" />
          <stop offset="100%" stopColor={color} stopOpacity="0" />
        </linearGradient>
      </defs>
      <path d={area} fill={`url(#sg-${color.replace('#', '')})`} />
      <path d={line} stroke={color} strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 1: HOME — Stack of glowing account cards
// ═══════════════════════════════════════════════════════════
const DriftHome = () => {
  const d = useDrift();
  return (
    <div style={{ background: d.bg, color: d.text, fontFamily: d.sans, height: '100%', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      {/* Subtle glow */}
      <div style={{ position: 'absolute', top: -100, left: -50, right: -50, height: 300,
        background: `radial-gradient(ellipse, ${d.neon}22 0%, transparent 70%)`, pointerEvents: 'none' }} />

      <div style={{ position: 'relative', flex: 1, overflowY: 'auto' }}>
        {/* Greeting */}
        <div style={{ padding: '24px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ width: 36, height: 36, borderRadius: 18, background: `linear-gradient(135deg, ${d.neon}, ${d.pink})`,
              display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 14, fontWeight: 600, color: '#08090B' }}>А</div>
            <div>
              <div style={{ fontSize: 11, color: d.muted }}>Привет, Артём</div>
              <div style={{ fontSize: 14, fontWeight: 500 }}>27 мая, среда</div>
            </div>
          </div>
          <div style={{ width: 36, height: 36, borderRadius: 18, background: d.surf, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke={d.text} strokeWidth="1.7"><path d="M15 17h5l-1.4-1.4A2 2 0 0118 14V11a6 6 0 10-12 0v3a2 2 0 01-.6 1.6L4 17h5m6 0a3 3 0 11-6 0"/></svg>
          </div>
        </div>

        {/* Total balance with sparkline */}
        <div style={{ padding: '24px 20px 8px' }}>
          <div style={{ fontSize: 11, color: d.faint, letterSpacing: 1.2, textTransform: 'uppercase' }}>Общий баланс</div>
          <div style={{ display: 'flex', alignItems: 'flex-end', gap: 16, marginTop: 8 }}>
            <div style={{ fontFamily: d.mono, fontSize: 38, fontWeight: 500, letterSpacing: -1.5, lineHeight: 1 }}>
              653 850 <span style={{ fontSize: 18, color: d.muted }}>₽</span>
            </div>
            <Spark data={SPARK_MAIN} color={d.neon} h={30} w={80} />
          </div>
          <div style={{ marginTop: 10, display: 'flex', gap: 10, alignItems: 'center' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, padding: '3px 9px', borderRadius: 100,
              background: `${d.neon}1F`, color: d.neon, fontSize: 12, fontFamily: d.mono }}>↗ +1.95%</span>
            <span style={{ fontSize: 12, color: d.muted }}>+12 480 ₽ за неделю</span>
          </div>
        </div>

        {/* Account card stack */}
        <div style={{ padding: '20px 16px 0', display: 'flex', flexDirection: 'column', gap: 12 }}>
          {ACCOUNTS.slice(0, 3).map((a, i) => {
            const gradients = {
              main:    `linear-gradient(135deg, #2A3A12 0%, #1A2008 40%, #0F1404 100%)`,
              savings: `linear-gradient(135deg, #2A2541 0%, #1E1A33 40%, #131121 100%)`,
              cash:    `linear-gradient(135deg, #2A1818 0%, #1E0F0F 40%, #150A0A 100%)`,
            };
            const accents = { main: d.neon, savings: '#9B8BFF', cash: '#FF6B5B' };
            const ac = accents[a.id] || d.neon;
            return (
              <div key={a.id} style={{ position: 'relative', padding: '18px 20px', borderRadius: 20, overflow: 'hidden',
                background: gradients[a.id] || d.surf, border: `1px solid ${d.border}` }}>
                {/* Accent glow */}
                <div style={{ position: 'absolute', top: -40, right: -40, width: 120, height: 120, borderRadius: 60,
                  background: `radial-gradient(circle, ${ac}55 0%, transparent 70%)`, pointerEvents: 'none' }} />
                <div style={{ position: 'relative', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div>
                    <div style={{ fontSize: 12, color: d.muted, letterSpacing: 0.4 }}>{a.name}</div>
                    <div style={{ fontFamily: d.mono, fontSize: 26, fontWeight: 500, letterSpacing: -0.8, marginTop: 4 }}>{fmt(a.balance, a.cur)}</div>
                    <div style={{ fontSize: 11, color: d.faint, fontFamily: d.mono, marginTop: 6 }}>
                      {a.type === 'card' ? `•••• ${a.last4} · ${a.cur}` : a.type === 'depo' ? `Депозит · 14% · ${a.cur}` : `Cash · ${a.cur}`}
                    </div>
                  </div>
                  <Spark data={a.id === 'savings' ? SPARK_SAVE : SPARK_MAIN.slice(-15)} color={ac} h={28} w={60} />
                </div>
              </div>
            );
          })}
          {/* See all chip */}
          <div style={{ textAlign: 'center', padding: '12px 0', fontSize: 13, color: d.muted }}>
            показать ещё 2 счёта  <span style={{ color: d.neon }}>→</span>
          </div>
        </div>

        {/* Today flow waveform */}
        <div style={{ margin: '8px 20px 0', padding: '18px 20px', background: d.bg2, borderRadius: 20, border: `1px solid ${d.border}` }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
            <div style={{ fontSize: 13, fontWeight: 500 }}>Сегодня · поток</div>
            <span style={{ fontFamily: d.mono, fontSize: 12, color: d.muted }}>3 ops · −1 307 ₽</span>
          </div>
          {/* Waveform */}
          <svg viewBox="0 0 320 50" style={{ width: '100%', height: 50, marginTop: 12, display: 'block' }}>
            <defs>
              <linearGradient id="dwave" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor={d.pink} stopOpacity="0.4" />
                <stop offset="100%" stopColor={d.pink} stopOpacity="0" />
              </linearGradient>
            </defs>
            <path d="M0,25 Q20,25 30,20 T60,22 T90,28 T120,25 T140,18 L142,18 L144,8 L146,8 L148,25 Q170,25 200,23 T240,18 L242,18 L244,4 L246,4 L248,25 Q270,28 290,21 T320,20 L320,50 L0,50 Z" fill="url(#dwave)" />
            <path d="M0,25 Q20,25 30,20 T60,22 T90,28 T120,25 T140,18 L142,18 L144,8 L146,8 L148,25 Q170,25 200,23 T240,18 L242,18 L244,4 L246,4 L248,25 Q270,28 290,21 T320,20" stroke={d.pink} fill="none" strokeWidth="1.5" />
          </svg>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: d.mono, fontSize: 10, color: d.faint, marginTop: 6 }}>
            <span>00</span><span>06</span><span>12</span><span>18</span><span>24</span>
          </div>
        </div>

        {/* Quick recent ops list */}
        <div style={{ padding: '20px 20px 24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 12 }}>
            <div style={{ fontSize: 13, fontWeight: 500 }}>Последние операции</div>
            <span style={{ fontSize: 12, color: d.neon }}>все →</span>
          </div>
          {TXNS.slice(0, 5).map((tx) => {
            const cat = catById(tx.cat);
            return (
              <div key={tx.id} style={{ display: 'flex', alignItems: 'center', padding: '10px 0', gap: 12 }}>
                <div style={{ width: 36, height: 36, borderRadius: 18, background: `${cat.color}22`, border: `1px solid ${cat.color}55`,
                  display: 'flex', alignItems: 'center', justifyContent: 'center', color: cat.color, fontFamily: d.mono, fontSize: 13 }}>{cat.glyph}</div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 14, fontWeight: 500 }}>{tx.note}</div>
                  <div style={{ fontSize: 11, color: d.faint, fontFamily: d.mono, marginTop: 2 }}>{cat.name} · {tx.t}</div>
                </div>
                <div style={{ fontFamily: d.mono, fontSize: 15, fontWeight: 500, color: tx.amt > 0 ? d.neon : d.text }}>
                  {tx.amt > 0 ? '+' : '−'}{Math.abs(tx.amt).toLocaleString('ru-RU')}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <DNav active="home" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 2: ADD OPERATION
// ═══════════════════════════════════════════════════════════
const DriftAdd = () => {
  const d = useDrift();
  const cats = CATEGORIES.filter((c) => c.kind === 'expense').slice(0, 8);
  return (
    <div style={{ background: d.bg, color: d.text, fontFamily: d.sans, height: '100%', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      {/* Top glow */}
      <div style={{ position: 'absolute', top: -120, left: -80, right: -80, height: 280,
        background: `radial-gradient(ellipse, ${d.pink}22 0%, transparent 70%)`, pointerEvents: 'none' }} />

      <div style={{ position: 'relative', padding: '20px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ width: 36, height: 36, borderRadius: 18, background: d.surf, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20 }}>×</div>
        <div style={{ fontSize: 15, fontWeight: 500 }}>Новая операция</div>
        <div style={{ padding: '8px 14px', borderRadius: 100, background: d.neon, color: '#08090B', fontSize: 13, fontWeight: 600 }}>Готово</div>
      </div>

      {/* Type pills */}
      <div style={{ position: 'relative', padding: '18px 20px 0', display: 'flex', gap: 8 }}>
        {[['Расход', d.pink], ['Доход', d.neon], ['Перевод', d.blue]].map(([k, c], i) => (
          <div key={k} style={{ flex: 1, padding: '10px 0', textAlign: 'center', borderRadius: 100, fontSize: 13,
            background: i === 0 ? `${c}26` : 'transparent', color: i === 0 ? c : d.muted,
            border: `1px solid ${i === 0 ? c : d.border}` }}>{k}</div>
        ))}
      </div>

      {/* Amount */}
      <div style={{ position: 'relative', padding: '36px 20px 28px', textAlign: 'center' }}>
        <div style={{ fontSize: 11, color: d.faint, letterSpacing: 1.4, textTransform: 'uppercase' }}>Сумма</div>
        <div style={{ fontFamily: d.mono, fontSize: 64, fontWeight: 500, letterSpacing: -2, marginTop: 12, lineHeight: 1 }}>
          <span style={{ color: d.pink }}>−842</span>
          <span style={{ color: d.faint, fontSize: 36 }}>.00</span>
        </div>
        <div style={{ fontFamily: d.mono, fontSize: 12, color: d.muted, marginTop: 6 }}>≈ $9.15 USD · €8.50 EUR</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 16px 16px' }}>
        {/* Category grid */}
        <div style={{ padding: '0 4px 12px', display: 'flex', justifyContent: 'space-between' }}>
          <div style={{ fontSize: 12, color: d.faint, letterSpacing: 1.2, textTransform: 'uppercase' }}>Категория</div>
          <div style={{ fontSize: 12, color: d.neon }}>+ новая</div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8, marginBottom: 22 }}>
          {cats.map((c, i) => {
            const sel = i === 0;
            return (
              <div key={c.id} style={{ padding: '12px 6px', borderRadius: 14, textAlign: 'center',
                background: sel ? `${c.color}22` : d.surf,
                border: `1px solid ${sel ? c.color : 'transparent'}` }}>
                <div style={{ width: 28, height: 28, borderRadius: 14, background: c.color, margin: '0 auto 8px' }} />
                <div style={{ fontSize: 11, fontWeight: sel ? 600 : 400, color: sel ? d.text : d.muted, lineHeight: 1.2 }}>{c.name}</div>
              </div>
            );
          })}
        </div>

        {/* Account */}
        <div style={{ padding: '0 4px 10px', fontSize: 12, color: d.faint, letterSpacing: 1.2, textTransform: 'uppercase' }}>Со счёта</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px 16px', borderRadius: 16,
          background: `linear-gradient(135deg, #2A3A12 0%, #1A2008 100%)`, border: `1px solid ${d.neon}33`, marginBottom: 16 }}>
          <div style={{ width: 32, height: 32, borderRadius: 16, background: d.neon, color: '#08090B', fontFamily: d.mono,
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 13, fontWeight: 600 }}>О</div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 14, fontWeight: 500 }}>Основной</div>
            <div style={{ fontFamily: d.mono, fontSize: 11, color: d.muted, marginTop: 2 }}>•••• 4421 · {fmt(187420)}</div>
          </div>
          <span style={{ color: d.muted, fontSize: 18 }}>›</span>
        </div>

        {/* Note + date row */}
        <div style={{ padding: '0 4px 10px', fontSize: 12, color: d.faint, letterSpacing: 1.2, textTransform: 'uppercase' }}>Детали</div>
        <div style={{ padding: '14px 16px', borderRadius: 16, background: d.surf, marginBottom: 8, fontSize: 15 }}>
          <input defaultValue="Перекрёсток" style={{ background: 'transparent', border: 'none', color: d.text, fontSize: 15, fontFamily: d.sans, width: '100%', outline: 'none' }} />
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
          <div style={{ padding: '12px 14px', background: d.surf, borderRadius: 16 }}>
            <div style={{ fontSize: 10, color: d.faint, letterSpacing: 1, textTransform: 'uppercase' }}>Дата</div>
            <div style={{ fontFamily: d.mono, fontSize: 13, marginTop: 4 }}>27.05 · 14:22</div>
          </div>
          <div style={{ padding: '12px 14px', background: d.surf, borderRadius: 16 }}>
            <div style={{ fontSize: 10, color: d.faint, letterSpacing: 1, textTransform: 'uppercase' }}>Теги</div>
            <div style={{ fontFamily: d.mono, fontSize: 13, marginTop: 4, color: d.muted }}>+ добавить</div>
          </div>
        </div>
      </div>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 3: OPERATION DETAIL
// ═══════════════════════════════════════════════════════════
const DriftDetail = () => {
  const d = useDrift();
  const tx = TXNS[3];
  const cat = catById(tx.cat);
  return (
    <div style={{ background: d.bg, color: d.text, fontFamily: d.sans, height: '100%', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      <div style={{ position: 'absolute', top: -120, left: -80, right: -80, height: 280,
        background: `radial-gradient(ellipse, ${d.pink}33 0%, transparent 70%)`, pointerEvents: 'none' }} />

      <div style={{ position: 'relative', padding: '20px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ width: 36, height: 36, borderRadius: 18, background: d.surf, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 18 }}>‹</div>
        <span style={{ fontSize: 13, color: d.muted, fontFamily: d.mono }}>op #t04</span>
        <div style={{ width: 36, height: 36, borderRadius: 18, background: d.surf, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke={d.text} strokeWidth="2"><path d="M5 12h14M12 5v14"/></svg>
        </div>
      </div>

      {/* Hero */}
      <div style={{ position: 'relative', padding: '32px 20px 24px', textAlign: 'center' }}>
        <div style={{ width: 64, height: 64, borderRadius: 32, background: `linear-gradient(135deg, ${cat.color}, ${cat.color}88)`,
          boxShadow: `0 0 30px ${cat.color}66`, margin: '0 auto 18px',
          display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 26, color: '#08090B', fontFamily: d.mono }}>{cat.glyph}</div>
        <div style={{ fontFamily: d.mono, fontSize: 48, fontWeight: 500, letterSpacing: -1.5, lineHeight: 1 }}>
          <span style={{ color: d.pink }}>−2 840</span> <span style={{ color: d.muted, fontSize: 24 }}>₽</span>
        </div>
        <div style={{ fontSize: 17, fontWeight: 500, marginTop: 14 }}>{tx.note}</div>
        <div style={{ fontFamily: d.mono, fontSize: 12, color: d.muted, marginTop: 4 }}>{cat.name.toLowerCase()} · 26 мая, 21:40</div>
      </div>

      <div style={{ position: 'relative', flex: 1, overflowY: 'auto', padding: '0 16px 16px' }}>
        {/* Detail card */}
        <div style={{ background: d.bg2, borderRadius: 20, border: `1px solid ${d.border}`, overflow: 'hidden' }}>
          {[
            ['Счёт', 'Основной · 4421', d.neon],
            ['Категория', cat.name, cat.color],
            ['Теги', 'ресторан · вечер', null],
            ['Локация', 'Москва, ул. Ленинградская', null],
            ['Чек', 'receipt-0526.jpg', d.blue],
          ].map(([k, v, c], i, arr) => (
            <div key={k} style={{ padding: '14px 16px', borderBottom: i === arr.length - 1 ? 'none' : `1px solid ${d.border}`,
              display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
              <span style={{ fontSize: 12, color: d.muted, letterSpacing: 0.5 }}>{k}</span>
              <span style={{ fontSize: 13, fontFamily: c === d.neon || c === d.blue ? d.mono : d.sans, color: c || d.text }}>{v}</span>
            </div>
          ))}
        </div>

        {/* Context — neon callout */}
        <div style={{ marginTop: 14, padding: '14px 16px', borderRadius: 16, background: `${d.amber}11`, border: `1px solid ${d.amber}33` }}>
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: 10 }}>
            <div style={{ width: 24, height: 24, borderRadius: 12, background: d.amber, color: '#08090B', flexShrink: 0,
              display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: d.mono, fontSize: 12, fontWeight: 600 }}>!</div>
            <div style={{ fontSize: 13, lineHeight: 1.5 }}>
              <span style={{ color: d.amber, fontWeight: 600 }}>Категория «Кафе»</span> на <span style={{ fontFamily: d.mono }}>82%</span> от лимита. Осталось 2 160 ₽ до конца месяца.
            </div>
          </div>
        </div>

        {/* Buttons */}
        <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
          <div style={{ flex: 1, padding: '14px 0', borderRadius: 100, border: `1px solid ${d.border}`, textAlign: 'center', fontSize: 13 }}>Дублировать</div>
          <div style={{ flex: 1, padding: '14px 0', borderRadius: 100, background: `${d.pink}22`, color: d.pink, border: `1px solid ${d.pink}55`, textAlign: 'center', fontSize: 13, fontWeight: 500 }}>Удалить</div>
        </div>
      </div>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 4: ACCOUNTS
// ═══════════════════════════════════════════════════════════
const DriftAccounts = () => {
  const d = useDrift();
  return (
    <div style={{ background: d.bg, color: d.text, fontFamily: d.sans, height: '100%', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      <div style={{ position: 'absolute', top: -100, left: -50, right: -50, height: 260,
        background: `radial-gradient(ellipse, ${d.neon}1A 0%, transparent 70%)`, pointerEvents: 'none' }} />

      <div style={{ position: 'relative', padding: '20px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ fontSize: 22, fontWeight: 600 }}>Счета</div>
        <div style={{ width: 40, height: 40, borderRadius: 20, background: d.neon, color: '#08090B', fontSize: 22, fontWeight: 500,
          display: 'flex', alignItems: 'center', justifyContent: 'center', lineHeight: 1, paddingBottom: 3 }}>+</div>
      </div>

      <div style={{ position: 'relative', padding: '24px 20px 12px' }}>
        <div style={{ fontSize: 11, color: d.faint, letterSpacing: 1.2, textTransform: 'uppercase' }}>Всего · ₽ эквивалент</div>
        <div style={{ fontFamily: d.mono, fontSize: 38, fontWeight: 500, letterSpacing: -1.5, marginTop: 4 }}>653 850</div>
        <div style={{ display: 'flex', gap: 6, marginTop: 12, height: 6, borderRadius: 3, overflow: 'hidden' }}>
          {ACCOUNTS.map((a) => {
            const rub = a.balance * ({ RUB: 1, USD: 92, EUR: 99 }[a.cur]);
            const pct = rub / totalRub;
            return <div key={a.id} style={{ width: `${pct * 100}%`, background: a.color }} />;
          })}
        </div>
      </div>

      <div style={{ position: 'relative', flex: 1, overflowY: 'auto', padding: '8px 16px 16px' }}>
        {ACCOUNTS.map((a) => {
          const accents = { main: d.neon, savings: '#9B8BFF', cash: '#FF6B5B', usd: '#52E0C4', eur: '#FFD24A' };
          const ac = accents[a.id] || d.neon;
          return (
            <div key={a.id} style={{ position: 'relative', padding: '16px 18px', borderRadius: 18, overflow: 'hidden',
              background: d.bg2, border: `1px solid ${d.border}`, marginBottom: 10 }}>
              <div style={{ position: 'absolute', top: 0, left: 0, bottom: 0, width: 3, background: ac }} />
              <div style={{ position: 'absolute', top: -30, right: -30, width: 80, height: 80, borderRadius: 40,
                background: `radial-gradient(circle, ${ac}33 0%, transparent 70%)`, pointerEvents: 'none' }} />
              <div style={{ position: 'relative', display: 'flex', alignItems: 'center', gap: 12 }}>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 15, fontWeight: 500 }}>{a.name}</div>
                  <div style={{ fontFamily: d.mono, fontSize: 11, color: d.faint, marginTop: 2 }}>
                    {a.type === 'card' ? `карта · ${a.last4}` : a.type === 'depo' ? 'депозит · 14%' : 'наличные'} · {a.cur}
                  </div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontFamily: d.mono, fontSize: 18, fontWeight: 500 }}>{fmt(a.balance, a.cur)}</div>
                  <Spark data={a.id === 'savings' ? SPARK_SAVE : SPARK_MAIN.slice(-15)} color={ac} h={16} w={50} />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <DNav active="set" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 5: CATEGORIES
// ═══════════════════════════════════════════════════════════
const DriftCats = () => {
  const d = useDrift();
  const exp = CATEGORIES.filter((c) => c.kind === 'expense');
  return (
    <div style={{ background: d.bg, color: d.text, fontFamily: d.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '20px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ fontSize: 22, fontWeight: 600 }}>Категории</div>
        <div style={{ width: 40, height: 40, borderRadius: 20, background: d.neon, color: '#08090B', fontSize: 22, fontWeight: 500,
          display: 'flex', alignItems: 'center', justifyContent: 'center', lineHeight: 1, paddingBottom: 3 }}>+</div>
      </div>

      <div style={{ display: 'flex', gap: 8, padding: '16px 20px 0' }}>
        {['Расходы · 8', 'Доходы · 2'].map((k, i) => (
          <div key={k} style={{ padding: '8px 14px', borderRadius: 100, fontSize: 12,
            background: i === 0 ? d.text : d.surf, color: i === 0 ? d.bg : d.muted, fontWeight: i === 0 ? 600 : 400 }}>{k}</div>
        ))}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '14px 16px 12px' }}>
        {/* Total monthly chip */}
        <div style={{ margin: '0 4px 14px', padding: '14px 16px', background: d.bg2, borderRadius: 16, border: `1px solid ${d.border}`,
          display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontSize: 11, color: d.faint, letterSpacing: 1 }}>ПОТРАЧЕНО В МАЕ</div>
            <div style={{ fontFamily: d.mono, fontSize: 22, fontWeight: 500, marginTop: 2 }}>93 690 / <span style={{ color: d.muted, fontSize: 16 }}>115 500</span></div>
          </div>
          <div style={{ fontFamily: d.mono, fontSize: 13, color: d.neon }}>81%</div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {exp.map((c) => {
            const pct = c.spent / c.budget;
            const over = pct > 0.95;
            return (
              <div key={c.id} style={{ position: 'relative', padding: '14px 14px', borderRadius: 16, background: d.bg2, border: `1px solid ${d.border}`, overflow: 'hidden' }}>
                <div style={{ position: 'absolute', top: -20, right: -20, width: 60, height: 60, borderRadius: 30,
                  background: `radial-gradient(circle, ${c.color}33 0%, transparent 70%)`, pointerEvents: 'none' }} />
                <div style={{ position: 'relative' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 10 }}>
                    <div style={{ width: 26, height: 26, borderRadius: 13, background: c.color, color: '#08090B',
                      display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: d.mono, fontSize: 13 }}>{c.glyph}</div>
                    <div style={{ fontSize: 13, fontWeight: 500 }}>{c.name}</div>
                  </div>
                  <div style={{ fontFamily: d.mono, fontSize: 16, fontWeight: 500 }}>{c.spent.toLocaleString('ru-RU')}</div>
                  <div style={{ fontFamily: d.mono, fontSize: 10, color: d.faint, marginTop: 2 }}>из {c.budget.toLocaleString('ru-RU')} ₽</div>
                  {/* circular progress mini ring */}
                  <div style={{ marginTop: 10, height: 3, background: d.surf2, borderRadius: 2, overflow: 'hidden' }}>
                    <div style={{ height: '100%', width: `${Math.min(pct, 1) * 100}%`, background: over ? d.pink : c.color }} />
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <DNav active="set" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 6: STATISTICS — Stream river + mosaic donut
// ═══════════════════════════════════════════════════════════
const DriftStats = () => {
  const d = useDrift();
  const cats = CATEGORIES.filter((c) => c.kind === 'expense').slice(0, 6);
  const total = cats.reduce((s, c) => s + c.spent, 0);
  // donut segments
  let acc = 0;
  const segs = cats.map((c) => {
    const start = acc / total;
    acc += c.spent;
    return { c, start, end: acc / total };
  });
  const polar = (a, r = 70) => [80 + Math.cos(a * 2 * Math.PI - Math.PI / 2) * r, 80 + Math.sin(a * 2 * Math.PI - Math.PI / 2) * r];

  return (
    <div style={{ background: d.bg, color: d.text, fontFamily: d.sans, height: '100%', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      <div style={{ position: 'absolute', top: 80, left: -60, right: -60, height: 300,
        background: `radial-gradient(ellipse, ${d.pink}1A 0%, transparent 70%)`, pointerEvents: 'none' }} />

      <div style={{ position: 'relative', padding: '20px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontSize: 22, fontWeight: 600 }}>Статистика</div>
          <div style={{ fontFamily: d.mono, fontSize: 12, color: d.muted, marginTop: 4 }}>last 12 weeks · stream</div>
        </div>
        <div style={{ display: 'flex', gap: 6 }}>
          <div style={{ width: 36, height: 36, borderRadius: 18, background: d.surf, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 14 }}>⌕</div>
          <div style={{ width: 36, height: 36, borderRadius: 18, background: d.surf, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 14 }}>⇄</div>
        </div>
      </div>

      <div style={{ position: 'relative', display: 'flex', gap: 8, padding: '14px 20px 0', overflowX: 'auto' }}>
        {['День', 'Неделя', 'Месяц', 'Год'].map((k, i) => (
          <div key={k} style={{ padding: '7px 14px', borderRadius: 100, fontSize: 12, whiteSpace: 'nowrap',
            background: i === 1 ? d.neon : d.surf, color: i === 1 ? '#08090B' : d.muted, fontWeight: i === 1 ? 600 : 400 }}>{k}</div>
        ))}
      </div>

      <div style={{ position: 'relative', flex: 1, overflowY: 'auto', padding: '20px 20px 16px' }}>
        <div style={{ fontFamily: d.mono, fontSize: 28, fontWeight: 500, letterSpacing: -1 }}>93 690 ₽</div>
        <div style={{ fontFamily: d.mono, fontSize: 12, color: d.muted, marginTop: 2 }}>средне 21 423 ₽/нед</div>

        {/* Stream river */}
        <div style={{ marginTop: 18, padding: '16px 12px', borderRadius: 18, background: d.bg2, border: `1px solid ${d.border}` }}>
          <div style={{ fontSize: 11, color: d.faint, letterSpacing: 1, textTransform: 'uppercase', padding: '0 4px' }}>Поток расходов · 12 недель</div>
          <svg viewBox="0 0 320 140" style={{ width: '100%', height: 140, marginTop: 8, display: 'block' }}>
            <defs>
              {[d.neon, d.pink, d.blue, d.amber, '#9B8BFF', '#52E0C4'].map((c, i) => (
                <linearGradient key={i} id={`riv-${i}`} x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor={c} stopOpacity="0.85" />
                  <stop offset="100%" stopColor={c} stopOpacity="0.4" />
                </linearGradient>
              ))}
            </defs>
            {(() => {
              // 12 weeks, 6 categories stacked as stream graph
              const W = 320, H = 140;
              const weeks = 12;
              const cols = [d.neon, d.pink, d.blue, d.amber, '#9B8BFF', '#52E0C4'];
              // synthetic per-week values per category (rounded org-like waves)
              const vals = cols.map((_, c) => Array.from({ length: weeks }, (_, w) => {
                const v = Math.sin((w + c * 1.7) * 0.6) * 0.5 + 0.55 + (c === 0 ? 0.3 : 0);
                return Math.max(0.15, v);
              }));
              const totals = Array.from({ length: weeks }, (_, w) => vals.reduce((s, v) => s + v[w], 0));
              const layers = [];
              for (let c = 0; c < cols.length; c++) {
                const top = [], bot = [];
                let baseAbove = 0;
                for (let w = 0; w < weeks; w++) {
                  // stream-graph baseline: center stack
                  const stackHigh = vals.slice(0, c).reduce((s, vv) => s + vv[w], 0);
                  const off = (totals[w] / 2) - stackHigh;
                  const x = (w / (weeks - 1)) * W;
                  const yTop = H / 2 - (off * (H * 0.42)) / Math.max(...totals);
                  const yBot = yTop + (vals[c][w] * (H * 0.42)) / Math.max(...totals);
                  top.push([x, yTop]);
                  bot.unshift([x, yBot]);
                }
                const path = `M${top.map((p, i) => (i === 0 ? `${p[0]},${p[1]}` : `S${(p[0] + top[i-1][0])/2},${p[1]} ${p[0]},${p[1]}`)).join(' ')} L${bot.map((p) => `${p[0]},${p[1]}`).join(' L')} Z`;
                layers.push(<path key={c} d={path} fill={`url(#riv-${c})`} opacity="0.95" />);
              }
              return layers;
            })()}
          </svg>
          <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 4px 0', fontFamily: d.mono, fontSize: 10, color: d.faint }}>
            <span>−12 нед</span><span>−6</span><span>сегодня</span>
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 10, padding: '0 4px' }}>
            {cats.slice(0, 6).map((c, i) => (
              <div key={c.id} style={{ display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 11, color: d.muted }}>
                <span style={{ width: 8, height: 8, borderRadius: 4, background: [d.neon, d.pink, d.blue, d.amber, '#9B8BFF', '#52E0C4'][i] }} />
                {c.name.toLowerCase()}
              </div>
            ))}
          </div>
        </div>

        {/* Mosaic donut */}
        <div style={{ marginTop: 14, padding: '18px 16px', borderRadius: 18, background: d.bg2, border: `1px solid ${d.border}`,
          display: 'flex', gap: 16, alignItems: 'center' }}>
          <svg viewBox="0 0 160 160" width="140" height="140">
            {segs.map((s, i) => {
              const r = 70, ir = 44;
              const sa = s.start * 2 * Math.PI - Math.PI / 2;
              const ea = s.end * 2 * Math.PI - Math.PI / 2;
              const large = s.end - s.start > 0.5 ? 1 : 0;
              const x1 = 80 + Math.cos(sa) * r, y1 = 80 + Math.sin(sa) * r;
              const x2 = 80 + Math.cos(ea) * r, y2 = 80 + Math.sin(ea) * r;
              const x3 = 80 + Math.cos(ea) * ir, y3 = 80 + Math.sin(ea) * ir;
              const x4 = 80 + Math.cos(sa) * ir, y4 = 80 + Math.sin(sa) * ir;
              return <path key={i} d={`M${x1},${y1} A${r},${r} 0 ${large} 1 ${x2},${y2} L${x3},${y3} A${ir},${ir} 0 ${large} 0 ${x4},${y4} Z`} fill={s.c.color} opacity="0.92" />;
            })}
            <text x="80" y="78" textAnchor="middle" fill={d.text} fontFamily={d.mono} fontSize="18" fontWeight="500">93k</text>
            <text x="80" y="93" textAnchor="middle" fill={d.muted} fontFamily={d.mono} fontSize="10">расходы</text>
          </svg>
          <div style={{ flex: 1, fontSize: 12 }}>
            {segs.slice(0, 5).map((s) => (
              <div key={s.c.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '3px 0' }}>
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
                  <span style={{ width: 8, height: 8, borderRadius: 4, background: s.c.color }} />
                  {s.c.name}
                </span>
                <span style={{ fontFamily: d.mono, color: d.muted }}>{Math.round((s.end - s.start) * 100)}%</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <DNav active="stat" />
    </div>
  );
};

Object.assign(window, { DriftHome, DriftAdd, DriftDetail, DriftAccounts, DriftCats, DriftStats });
