// vault.jsx — Direction A · Ledger
// Tight, monospace, terminal-fintech. Status bar handled by AndroidDevice.

const V = {
  bg: '#0a0a0a',
  surf: '#131313',
  surf2: '#1c1c1c',
  border: 'rgba(255,255,255,0.06)',
  borderStrong: 'rgba(255,255,255,0.12)',
  text: '#f4f4f4',
  muted: '#8a8a8a',
  faint: '#5a5a5a',
  lime: '#C5FF4A',
  red: '#FF5B5B',
  mono: '"IBM Plex Mono", ui-monospace, monospace',
  sans: '"IBM Plex Sans", "Inter", system-ui, sans-serif',
};

// Light overrides — toggled by tweaks panel via `data-theme="light"` on root.
// We rely on prop drilling instead because the theming is too custom for class-only.
const V_LIGHT = {
  bg: '#f4f4f0', surf: '#ffffff', surf2: '#ebebe5',
  border: 'rgba(0,0,0,0.08)', borderStrong: 'rgba(0,0,0,0.16)',
  text: '#0a0a0a', muted: '#5a5a5a', faint: '#9a9a9a',
  lime: '#3D8B00', red: '#D63838',
  mono: V.mono, sans: V.sans,
};
const useVault = () => (window.__theme === 'light' ? V_LIGHT : V);

// ─── Tag chip & helpers ────────────────────────────────────
const VLabel = ({ children, c }) => {
  const t = useVault();
  return <span style={{ fontFamily: t.mono, fontSize: 10, letterSpacing: 1.4, textTransform: 'uppercase', color: c || t.faint }}>{children}</span>;
};
const VDivider = () => { const t = useVault(); return <div style={{ height: 1, background: t.border }} />; };

// ═══════════════════════════════════════════════════════════
// Reusable visualization primitives — used in screens AND in
// the design-code showcase artboards.
// ═══════════════════════════════════════════════════════════

// Donut chart with optional center slot.
// data = [{ value, color, label?, id? }, ...]
const VDonut = ({ data, size = 200, thickness = 28, gap = 0.006, center, trackColor }) => {
  const t = useVault();
  const r = size / 2 - 2;
  const ir = r - thickness;
  const total = data.reduce((s, d) => s + d.value, 0) || 1;
  const segs = (() => {
    let acc = 0;
    return data.map((d) => {
      const start = acc / total;
      acc += d.value;
      return { ...d, start, end: acc / total };
    });
  })();
  const cx = size / 2, cy = size / 2;
  const polar = (a, R) => [cx + Math.cos(a * 2 * Math.PI - Math.PI / 2) * R, cy + Math.sin(a * 2 * Math.PI - Math.PI / 2) * R];

  return (
    <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} style={{ display: 'block' }}>
      {/* Track */}
      <circle cx={cx} cy={cy} r={(r + ir) / 2} stroke={trackColor || t.surf2} strokeWidth={thickness} fill="none" />
      {segs.map((s, i) => {
        const gp = Math.min(gap, (s.end - s.start) / 3);
        const ss = s.start + gp;
        const ee = s.end - gp;
        if (ee <= ss) return null;
        const large = ee - ss > 0.5 ? 1 : 0;
        const [x1, y1] = polar(ss, r);
        const [x2, y2] = polar(ee, r);
        const [x3, y3] = polar(ee, ir);
        const [x4, y4] = polar(ss, ir);
        return <path key={i} d={`M${x1},${y1} A${r},${r} 0 ${large} 1 ${x2},${y2} L${x3},${y3} A${ir},${ir} 0 ${large} 0 ${x4},${y4} Z`} fill={s.color} />;
      })}
      {center && (
        <foreignObject x="0" y="0" width={size} height={size}>
          <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', textAlign: 'center', padding: 8 }}>
            {center}
          </div>
        </foreignObject>
      )}
    </svg>
  );
};

// Radial progress ring (single value 0..1).
const VRing = ({ value, size = 64, thickness = 4, color, trackColor, label, sublabel, fontSize = 14 }) => {
  const t = useVault();
  const r = size / 2 - thickness / 2;
  const c = 2 * Math.PI * r;
  const v = Math.max(0, Math.min(1, value));
  const dash = c * v;
  return (
    <div style={{ position: 'relative', width: size, height: size, display: 'inline-block' }}>
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} style={{ transform: 'rotate(-90deg)', display: 'block' }}>
        <circle cx={size / 2} cy={size / 2} r={r} stroke={trackColor || t.surf2} strokeWidth={thickness} fill="none" />
        <circle cx={size / 2} cy={size / 2} r={r} stroke={color || t.lime} strokeWidth={thickness} fill="none"
          strokeDasharray={`${dash} ${c}`} strokeLinecap="butt" />
      </svg>
      {(label || sublabel) && (
        <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', fontFamily: t.mono }}>
          {label && <div style={{ fontSize, fontWeight: 500, color: t.text, lineHeight: 1 }}>{label}</div>}
          {sublabel && <div style={{ fontSize: fontSize - 4, color: t.muted, lineHeight: 1, marginTop: 2 }}>{sublabel}</div>}
        </div>
      )}
    </div>
  );
};

// Sparkline (line + area).
const VSpark = ({ data, color, w = 100, h = 32, areaOpacity = 0.18 }) => {
  const t = useVault();
  const c = color || t.lime;
  if (!data || data.length < 2) return <svg width={w} height={h} />;
  const min = Math.min(...data), max = Math.max(...data);
  const range = max - min || 1;
  const pts = data.map((v, i) => [(i / (data.length - 1)) * w, h - ((v - min) / range) * (h - 2) - 1]);
  const line = `M${pts.map((p) => `${p[0]},${p[1]}`).join(' L')}`;
  const area = `${line} L${w},${h} L0,${h} Z`;
  return (
    <svg width={w} height={h} viewBox={`0 0 ${w} ${h}`} style={{ display: 'block' }}>
      <path d={area} fill={c} opacity={areaOpacity} />
      <path d={line} fill="none" stroke={c} strokeWidth="1.5" strokeLinecap="square" strokeLinejoin="miter" />
    </svg>
  );
};

// ─── Bottom nav (5 tabs, ledger style) ─────────────────────
const VNav = ({ active = 'home' }) => {
  const t = useVault();
  const tabs = [
    ['home', 'HOME',     'M3 11l9-8 9 8v10a2 2 0 01-2 2h-4v-7H9v7H5a2 2 0 01-2-2V11z'],
    ['ops',  'OPS',      'M4 6h16M4 12h16M4 18h10'],
    ['add',  '',         null],
    ['stat', 'STATS',    'M4 20V10M10 20V4M16 20v-6M22 20h-22'],
    ['set',  'MORE',     'M4 6h16M4 12h16M4 18h16'],
  ];
  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-around', padding: '0 8px',
      background: t.bg, borderTop: `1px solid ${t.border}`, height: 64, flexShrink: 0 }}>
      {tabs.map(([k, label, d]) => {
        if (k === 'add') return (
          <button key={k} style={{ width: 52, height: 52, borderRadius: 0, border: `1px solid ${t.text}`, background: t.lime,
            color: '#0a0a0a', fontFamily: t.mono, fontSize: 26, fontWeight: 500, display: 'flex',
            alignItems: 'center', justifyContent: 'center', lineHeight: 1, paddingBottom: 4 }}>+</button>
        );
        const on = k === active;
        return (
          <div key={k} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, paddingTop: 8 }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={on ? t.lime : t.muted} strokeWidth="1.5" strokeLinecap="square"><path d={d}/></svg>
            <span style={{ fontFamily: t.mono, fontSize: 9, letterSpacing: 1.2, color: on ? t.lime : t.muted }}>{label}</span>
          </div>
        );
      })}
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 1: HOME — Ledger overview
// ═══════════════════════════════════════════════════════════
const VaultHome = () => {
  const t = useVault();
  return (
    <div style={{ background: t.bg, color: t.text, fontFamily: t.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <div style={{ padding: '20px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <VLabel>LEDGER · WED 27 MAY</VLabel>
          <div style={{ fontFamily: t.mono, fontSize: 13, color: t.muted, marginTop: 6 }}>Доброе утро, Артём</div>
        </div>
        <div style={{ width: 32, height: 32, borderRadius: 16, background: t.surf, border: `1px solid ${t.border}`,
          display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: t.mono, fontSize: 12 }}>А</div>
      </div>

      {/* Total card */}
      <div style={{ padding: '24px 20px 16px' }}>
        <VLabel c={t.faint}>NET WORTH · ₽ EQUIVALENT</VLabel>
        <div style={{ fontFamily: t.mono, fontSize: 44, fontWeight: 500, letterSpacing: -1.5, marginTop: 8, lineHeight: 1 }}>
          653 850
        </div>
        <div style={{ display: 'flex', gap: 16, marginTop: 14, fontFamily: t.mono, fontSize: 12 }}>
          <span style={{ color: t.lime }}>↗ +12 480</span>
          <span style={{ color: t.muted }}>this week</span>
          <span style={{ color: t.faint }}>· +1.95%</span>
        </div>
      </div>

      {/* Account chips horizontal scroll */}
      <div style={{ padding: '0 20px 4px', display: 'flex', gap: 8, overflowX: 'auto' }}>
        {ACCOUNTS.map((a) => (
          <div key={a.id} style={{ minWidth: 132, padding: '12px 14px', background: t.surf, border: `1px solid ${t.border}` }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 18 }}>
              <div style={{ width: 6, height: 6, background: a.color, borderRadius: 0 }} />
              <span style={{ fontFamily: t.mono, fontSize: 10, color: t.faint }}>{a.cur}</span>
            </div>
            <div style={{ fontFamily: t.mono, fontSize: 11, color: t.muted, marginBottom: 4 }}>{a.name}</div>
            <div style={{ fontFamily: t.mono, fontSize: 17, fontWeight: 500, letterSpacing: -0.5 }}>{fmt(a.balance, a.cur)}</div>
          </div>
        ))}
      </div>

      {/* Today summary */}
      <div style={{ margin: '20px 20px 12px', border: `1px solid ${t.border}`, background: t.surf,
        display: 'grid', gridTemplateColumns: '1fr 1px 1fr 1px 1fr' }}>
        <div style={{ padding: '14px 12px' }}>
          <VLabel>TODAY IN</VLabel>
          <div style={{ fontFamily: t.mono, fontSize: 16, color: t.lime, marginTop: 6 }}>+0</div>
        </div>
        <div style={{ background: t.border }} />
        <div style={{ padding: '14px 12px' }}>
          <VLabel>OUT</VLabel>
          <div style={{ fontFamily: t.mono, fontSize: 16, color: t.red, marginTop: 6 }}>−1 307</div>
        </div>
        <div style={{ background: t.border }} />
        <div style={{ padding: '14px 12px' }}>
          <VLabel>OPS</VLabel>
          <div style={{ fontFamily: t.mono, fontSize: 16, marginTop: 6 }}>3</div>
        </div>
      </div>

      {/* Recent transactions ledger */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '4px 0 12px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 20px 6px' }}>
          <VLabel>RECENT OPERATIONS</VLabel>
          <span style={{ fontFamily: t.mono, fontSize: 10, color: t.lime, letterSpacing: 1.2 }}>ALL →</span>
        </div>
        {TXNS.slice(0, 8).map((tx, i) => {
          const cat = catById(tx.cat);
          const last = i === 7 || TXNS[i + 1]?.d !== tx.d;
          const newDate = i === 0 || TXNS[i - 1].d !== tx.d;
          return (
            <React.Fragment key={tx.id}>
              {newDate && (
                <div style={{ padding: '12px 20px 4px', fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 1.2 }}>
                  {tx.d === '2026-05-27' ? 'TODAY · 27.05' : tx.d === '2026-05-26' ? 'YESTERDAY · 26.05' : tx.d.split('-').reverse().slice(0,2).join('.')}
                </div>
              )}
              <div style={{ display: 'flex', alignItems: 'center', padding: '10px 20px', gap: 12,
                borderBottom: last ? 'none' : `1px solid ${t.border}` }}>
                <div style={{ width: 4, height: 28, background: cat.color }} />
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 14, fontWeight: 500, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{tx.note}</div>
                  <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, marginTop: 2, letterSpacing: 0.4 }}>
                    {tx.t} · {cat.name.toUpperCase()} · {accById(tx.acc).name.toUpperCase()}
                  </div>
                </div>
                <div style={{ fontFamily: t.mono, fontSize: 15, fontWeight: 500, color: tx.amt > 0 ? t.lime : t.text }}>
                  {tx.amt > 0 ? '+' : '−'}{Math.abs(tx.amt).toLocaleString('ru-RU')}
                </div>
              </div>
            </React.Fragment>
          );
        })}
      </div>

      <VNav active="home" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 2: ADD OPERATION — Fullscreen form
// ═══════════════════════════════════════════════════════════
const VaultAdd = () => {
  const t = useVault();
  const cats = CATEGORIES.filter((c) => c.kind === 'expense').slice(0, 8);
  return (
    <div style={{ background: t.bg, color: t.text, fontFamily: t.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Top bar */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '16px 20px',
        borderBottom: `1px solid ${t.border}` }}>
        <span style={{ fontFamily: t.mono, fontSize: 16, letterSpacing: 1 }}>×</span>
        <VLabel c={t.text}>NEW OPERATION</VLabel>
        <span style={{ fontFamily: t.mono, fontSize: 11, color: t.lime, letterSpacing: 1.2 }}>SAVE</span>
      </div>

      {/* Type toggle */}
      <div style={{ display: 'flex', margin: '20px 20px 0', border: `1px solid ${t.border}` }}>
        {['EXPENSE', 'INCOME', 'TRANSFER'].map((k, i) => (
          <div key={k} style={{ flex: 1, padding: '12px 0', textAlign: 'center', fontFamily: t.mono, fontSize: 11,
            letterSpacing: 1.3, background: i === 0 ? t.text : 'transparent', color: i === 0 ? t.bg : t.muted,
            borderRight: i < 2 ? `1px solid ${t.border}` : 'none' }}>{k}</div>
        ))}
      </div>

      {/* Amount */}
      <div style={{ padding: '32px 20px 28px', textAlign: 'center' }}>
        <VLabel>AMOUNT · RUB</VLabel>
        <div style={{ fontFamily: t.mono, fontSize: 56, fontWeight: 500, letterSpacing: -2, marginTop: 8, color: t.text }}>
          −<span style={{ color: t.lime }}>842</span><span style={{ color: t.faint }}>.00</span>
        </div>
        <div style={{ fontFamily: t.mono, fontSize: 12, color: t.muted, marginTop: 4 }}>≈ $9.15 · €8.50</div>
      </div>

      {/* From account */}
      <div style={{ padding: '0 20px', marginBottom: 18 }}>
        <VLabel>FROM ACCOUNT</VLabel>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px 14px',
          border: `1px solid ${t.border}`, background: t.surf, marginTop: 8 }}>
          <div style={{ width: 8, height: 24, background: V.lime }} />
          <div style={{ flex: 1 }}>
            <div style={{ fontFamily: t.mono, fontSize: 14 }}>Основной · 4421</div>
            <div style={{ fontFamily: t.mono, fontSize: 11, color: t.muted, marginTop: 2 }}>BAL 187 420 ₽</div>
          </div>
          <span style={{ fontFamily: t.mono, fontSize: 14, color: t.muted }}>›</span>
        </div>
      </div>

      {/* Category */}
      <div style={{ padding: '0 20px', flex: 1, overflowY: 'auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
          <VLabel>CATEGORY</VLabel>
          <span style={{ fontFamily: t.mono, fontSize: 10, color: t.lime, letterSpacing: 1.2 }}>+ NEW</span>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
          {cats.map((c, i) => {
            const sel = i === 0;
            return (
              <div key={c.id} style={{ border: `1px solid ${sel ? t.text : t.border}`, padding: '12px 6px',
                background: sel ? t.surf : 'transparent', textAlign: 'center', position: 'relative' }}>
                <div style={{ width: 20, height: 20, background: c.color, margin: '0 auto 6px' }} />
                <div style={{ fontFamily: t.mono, fontSize: 9, letterSpacing: 0.6, color: sel ? t.text : t.muted, lineHeight: 1.2 }}>
                  {c.name.toUpperCase().split(' ').slice(0, 2).join(' ')}
                </div>
                {sel && <div style={{ position: 'absolute', top: 4, right: 4, fontFamily: t.mono, fontSize: 9, color: t.lime }}>✓</div>}
              </div>
            );
          })}
        </div>

        {/* Note */}
        <div style={{ marginTop: 24 }}>
          <VLabel>NOTE</VLabel>
          <div style={{ borderBottom: `1px solid ${t.borderStrong}`, padding: '12px 0 10px', fontSize: 15, color: t.text, fontFamily: t.mono }}>
            Перекрёсток<span style={{ background: t.lime, width: 1, height: 16, display: 'inline-block', marginLeft: 1, verticalAlign: -3 }} />
          </div>
        </div>

        {/* Date + tags */}
        <div style={{ marginTop: 22, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <div style={{ border: `1px solid ${t.border}`, padding: '10px 12px' }}>
            <VLabel>DATE</VLabel>
            <div style={{ fontFamily: t.mono, fontSize: 13, marginTop: 4 }}>27.05.26 · 14:22</div>
          </div>
          <div style={{ border: `1px solid ${t.border}`, padding: '10px 12px' }}>
            <VLabel>REPEAT</VLabel>
            <div style={{ fontFamily: t.mono, fontSize: 13, marginTop: 4, color: t.muted }}>OFF</div>
          </div>
        </div>
        <div style={{ height: 24 }} />
      </div>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 3: OPERATION DETAIL / EDIT
// ═══════════════════════════════════════════════════════════
const VaultDetail = () => {
  const t = useVault();
  const tx = TXNS[3]; // 'Ужин · «Сахалин»'
  const cat = catById(tx.cat);
  return (
    <div style={{ background: t.bg, color: t.text, fontFamily: t.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '16px 20px',
        borderBottom: `1px solid ${t.border}` }}>
        <span style={{ fontFamily: t.mono, fontSize: 14 }}>← BACK</span>
        <VLabel c={t.text}>OP · #T04</VLabel>
        <span style={{ fontFamily: t.mono, fontSize: 11, color: t.lime, letterSpacing: 1.2 }}>EDIT</span>
      </div>

      {/* Big amount */}
      <div style={{ padding: '36px 20px 28px', textAlign: 'center', borderBottom: `1px solid ${t.border}` }}>
        <VLabel>EXPENSE · 26 MAY · 21:40</VLabel>
        <div style={{ fontFamily: t.mono, fontSize: 52, fontWeight: 500, letterSpacing: -2, marginTop: 10 }}>
          −2 840 <span style={{ color: t.muted, fontSize: 24 }}>₽</span>
        </div>
        <div style={{ marginTop: 14, display: 'inline-flex', alignItems: 'center', gap: 8, border: `1px solid ${t.border}`, padding: '6px 12px' }}>
          <div style={{ width: 8, height: 8, background: cat.color }} />
          <span style={{ fontFamily: t.mono, fontSize: 11, letterSpacing: 1, color: t.text }}>{cat.name.toUpperCase()}</span>
        </div>
      </div>

      {/* Meta rows */}
      <div style={{ padding: '8px 20px', flex: 1 }}>
        {[
          ['NOTE', tx.note],
          ['ACCOUNT', 'Основной · ****4421'],
          ['TAGS', 'ресторан · вечер · друзья'],
          ['LOCATION', 'Москва · ул. Ленинградская'],
          ['ATTACHMENT', '01 receipt · 1.2 MB'],
        ].map(([k, v]) => (
          <div key={k} style={{ padding: '14px 0', borderBottom: `1px solid ${t.border}`, display: 'flex', justifyContent: 'space-between', gap: 16 }}>
            <VLabel>{k}</VLabel>
            <span style={{ fontFamily: t.mono, fontSize: 13, color: t.text, textAlign: 'right', flex: 1 }}>{v}</span>
          </div>
        ))}

        {/* History */}
        <div style={{ marginTop: 20 }}>
          <VLabel>HISTORY</VLabel>
          <div style={{ marginTop: 10, fontFamily: t.mono, fontSize: 11, color: t.muted, lineHeight: 1.8 }}>
            <div>26.05 · 21:40 · CREATED</div>
            <div>27.05 · 09:01 · NOTE EDITED</div>
            <div>27.05 · 14:11 · TAGS ADDED · ресторан</div>
          </div>
        </div>
      </div>

      {/* Actions */}
      <div style={{ display: 'flex', borderTop: `1px solid ${t.border}` }}>
        <div style={{ flex: 1, padding: 18, textAlign: 'center', fontFamily: t.mono, fontSize: 11, letterSpacing: 1.4, color: t.muted, borderRight: `1px solid ${t.border}` }}>DUPLICATE</div>
        <div style={{ flex: 1, padding: 18, textAlign: 'center', fontFamily: t.mono, fontSize: 11, letterSpacing: 1.4, color: t.red }}>DELETE</div>
      </div>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 4: ACCOUNTS
// ═══════════════════════════════════════════════════════════
const VaultAccounts = () => {
  const t = useVault();
  return (
    <div style={{ background: t.bg, color: t.text, fontFamily: t.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '20px 20px 16px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', borderBottom: `1px solid ${t.border}` }}>
        <div>
          <VLabel>05 ACCOUNTS</VLabel>
          <div style={{ fontFamily: t.mono, fontSize: 30, fontWeight: 500, letterSpacing: -1, marginTop: 6 }}>653 850 ₽</div>
          <div style={{ fontFamily: t.mono, fontSize: 11, color: t.muted, marginTop: 2 }}>TOTAL · EQ. RUB</div>
        </div>
        <div style={{ width: 36, height: 36, border: `1px solid ${t.text}`, display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: t.mono, fontSize: 20 }}>+</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto' }}>
        {ACCOUNTS.map((a, i) => {
          const pct = (a.balance * ({ RUB: 1, USD: 92, EUR: 99 }[a.cur])) / totalRub;
          return (
            <div key={a.id} style={{ padding: '18px 20px', borderBottom: `1px solid ${t.border}`,
              display: 'flex', gap: 14, alignItems: 'center' }}>
              <div style={{ width: 8, height: 56, background: a.color }} />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
                  <div style={{ fontSize: 16, fontWeight: 500 }}>{a.name}</div>
                  <div style={{ fontFamily: t.mono, fontSize: 18, fontWeight: 500 }}>{fmt(a.balance, a.cur)}</div>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginTop: 6 }}>
                  <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, letterSpacing: 1 }}>
                    {a.type.toUpperCase()}{a.last4 ? ` · ****${a.last4}` : ''} · {a.cur}
                  </div>
                  <div style={{ fontFamily: t.mono, fontSize: 10, color: t.faint }}>{(pct * 100).toFixed(1)}%</div>
                </div>
                {/* Mini bar */}
                <div style={{ height: 2, background: t.border, marginTop: 8 }}>
                  <div style={{ height: '100%', width: `${pct * 100}%`, background: a.color }} />
                </div>
              </div>
            </div>
          );
        })}
        <div style={{ padding: '20px 20px 12px' }}>
          <VLabel>QUICK STATS</VLabel>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, marginTop: 10 }}>
            <div style={{ border: `1px solid ${t.border}`, padding: '12px 14px' }}>
              <VLabel>MONTH IN</VLabel>
              <div style={{ fontFamily: t.mono, fontSize: 16, color: t.lime, marginTop: 6 }}>+173 000</div>
            </div>
            <div style={{ border: `1px solid ${t.border}`, padding: '12px 14px' }}>
              <VLabel>MONTH OUT</VLabel>
              <div style={{ fontFamily: t.mono, fontSize: 16, color: t.red, marginTop: 6 }}>−93 220</div>
            </div>
          </div>
        </div>
      </div>

      <VNav active="set" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 5: CATEGORIES
// ═══════════════════════════════════════════════════════════
const VaultCats = () => {
  const t = useVault();
  const exp = CATEGORIES.filter((c) => c.kind === 'expense');
  const inc = CATEGORIES.filter((c) => c.kind === 'income');
  return (
    <div style={{ background: t.bg, color: t.text, fontFamily: t.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '20px 20px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ fontSize: 20, fontWeight: 500 }}>Категории</div>
        <div style={{ width: 36, height: 36, border: `1px solid ${t.text}`, display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: t.mono, fontSize: 20 }}>+</div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', margin: '18px 20px 0', borderBottom: `1px solid ${t.border}` }}>
        {['EXPENSE · 08', 'INCOME · 02'].map((k, i) => (
          <div key={k} style={{ padding: '10px 0', marginRight: 24, fontFamily: t.mono, fontSize: 11, letterSpacing: 1.3,
            color: i === 0 ? t.text : t.faint, borderBottom: i === 0 ? `2px solid ${t.lime}` : '2px solid transparent', marginBottom: -1 }}>{k}</div>
        ))}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 0' }}>
        {exp.map((c) => {
          const pct = c.spent / c.budget;
          const over = pct > 1;
          return (
            <div key={c.id} style={{ padding: '14px 20px', borderBottom: `1px solid ${t.border}` }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
                <div style={{ width: 32, height: 32, background: c.color, display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontFamily: t.mono, fontSize: 14, color: '#0a0a0a' }}>{c.glyph}</div>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ fontSize: 14, fontWeight: 500 }}>{c.name}</span>
                    <span style={{ fontFamily: t.mono, fontSize: 13, color: over ? t.red : t.text }}>
                      {c.spent.toLocaleString('ru-RU')} <span style={{ color: t.faint }}>/ {c.budget.toLocaleString('ru-RU')}</span>
                    </span>
                  </div>
                  {/* Bar */}
                  <div style={{ marginTop: 8, height: 4, background: t.surf2, position: 'relative' }}>
                    <div style={{ position: 'absolute', inset: 0, width: `${Math.min(pct, 1) * 100}%`, background: over ? t.red : c.color }} />
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 4 }}>
                    <span style={{ fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 0.8 }}>{(pct * 100).toFixed(0)}% · {(c.budget - c.spent).toLocaleString('ru-RU')} ₽ left</span>
                    <span style={{ fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 0.8 }}>14 OPS</span>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <VNav active="set" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 6: STATISTICS — Day / Week / Month / Year
// Donut breakdown + radial budget rings + 12-month bars +
// daily heat strip. Ledger-precise.
// ═══════════════════════════════════════════════════════════
const VaultStats = () => {
  const t = useVault();
  const exp = CATEGORIES.filter((c) => c.kind === 'expense');
  const totalSpent = exp.reduce((s, c) => s + c.spent, 0); // 93 690
  const totalBudget = exp.reduce((s, c) => s + c.budget, 0); // 115 500
  const totalIncome = 173000;
  const incomeTarget = 180000;
  // Donut data: take all 8 categories with their spent value
  const donutData = exp.map((c) => ({ value: c.spent, color: c.color, label: c.name, id: c.id }));
  const max = Math.max(...MONTHLY.map((m) => Math.max(m.in, m.out)));

  return (
    <div style={{ background: t.bg, color: t.text, fontFamily: t.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <div style={{ padding: '18px 20px 14px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
        borderBottom: `1px solid ${t.border}` }}>
        <div>
          <VLabel>STATS · MAY 2026</VLabel>
          <div style={{ fontFamily: t.mono, fontSize: 14, color: t.muted, marginTop: 6 }}>01 — 27 МАЯ · 27 ДНЕЙ</div>
        </div>
        <div style={{ display: 'flex', gap: 6 }}>
          <div style={{ width: 32, height: 32, border: `1px solid ${t.border}`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke={t.text} strokeWidth="1.6"><path d="M3 4h18l-7 9v6l-4-2v-4z"/></svg>
          </div>
          <div style={{ width: 32, height: 32, border: `1px solid ${t.border}`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke={t.text} strokeWidth="1.6"><path d="M12 5v14M5 12h14"/></svg>
          </div>
        </div>
      </div>

      {/* Period selector */}
      <div style={{ display: 'flex', borderBottom: `1px solid ${t.border}` }}>
        {['DAY', 'WEEK', 'MONTH', 'YEAR'].map((k, i) => (
          <div key={k} style={{ flex: 1, padding: '12px 0', textAlign: 'center', fontFamily: t.mono, fontSize: 11,
            letterSpacing: 1.4, color: i === 2 ? t.text : t.muted, background: i === 2 ? t.surf : 'transparent',
            borderRight: i < 3 ? `1px solid ${t.border}` : 'none',
            borderBottom: i === 2 ? `2px solid ${t.lime}` : 'none', marginBottom: i === 2 ? -2 : 0 }}>{k}</div>
        ))}
      </div>

      {/* Scroll content */}
      <div style={{ flex: 1, overflowY: 'auto' }}>
        {/* Hero donut */}
        <div style={{ padding: '24px 20px 12px', display: 'flex', alignItems: 'center', gap: 18 }}>
          <div style={{ flexShrink: 0 }}>
            <VDonut
              data={donutData}
              size={156}
              thickness={22}
              center={
                <React.Fragment>
                  <div style={{ fontFamily: t.mono, fontSize: 9, letterSpacing: 1.2, color: t.faint }}>SPENT</div>
                  <div style={{ fontFamily: t.mono, fontSize: 20, fontWeight: 500, color: t.text, letterSpacing: -0.5, marginTop: 2 }}>93 690</div>
                  <div style={{ fontFamily: t.mono, fontSize: 9, color: t.muted, marginTop: 4 }}>/115 500</div>
                </React.Fragment>
              }
            />
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <VLabel>BREAKDOWN</VLabel>
            <div style={{ marginTop: 8 }}>
              {donutData.slice(0, 5).map((d) => {
                const pct = d.value / totalSpent;
                return (
                  <div key={d.id} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '4px 0' }}>
                    <div style={{ width: 6, height: 6, background: d.color, flexShrink: 0 }} />
                    <span style={{ flex: 1, fontSize: 12, color: t.text, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{d.label}</span>
                    <span style={{ fontFamily: t.mono, fontSize: 11, color: t.muted, fontVariantNumeric: 'tabular-nums' }}>{(pct * 100).toFixed(0)}%</span>
                  </div>
                );
              })}
              <div style={{ padding: '4px 0', fontFamily: t.mono, fontSize: 11, color: t.faint, letterSpacing: 0.8 }}>+3 more</div>
            </div>
          </div>
        </div>

        {/* Radial KPI rings */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', borderTop: `1px solid ${t.border}`, borderBottom: `1px solid ${t.border}` }}>
          {[
            { ring: { value: totalIncome / incomeTarget, color: t.lime, label: `${Math.round(totalIncome / incomeTarget * 100)}%`, sublabel: 'OF GOAL' }, kpiLabel: 'INCOME', amt: `+${totalIncome.toLocaleString('ru-RU')}`, amtColor: t.lime },
            { ring: { value: totalSpent / totalBudget, color: t.text, label: `${Math.round(totalSpent / totalBudget * 100)}%`, sublabel: 'BUDGET' }, kpiLabel: 'EXPENSE', amt: `−${totalSpent.toLocaleString('ru-RU')}`, amtColor: t.text },
            { ring: { value: 0.27, color: t.lime, label: '27', sublabel: 'DAYS' }, kpiLabel: 'NET', amt: `+${(totalIncome - totalSpent).toLocaleString('ru-RU')}`, amtColor: t.lime },
          ].map((k, i) => (
            <div key={i} style={{ padding: '16px 12px 18px', textAlign: 'center', borderRight: i < 2 ? `1px solid ${t.border}` : 'none' }}>
              <div style={{ display: 'flex', justifyContent: 'center' }}>
                <VRing {...k.ring} size={54} thickness={3} fontSize={12} />
              </div>
              <VLabel>{k.kpiLabel}</VLabel>
              <div style={{ fontFamily: t.mono, fontSize: 13, fontWeight: 500, color: k.amtColor, marginTop: 4 }}>{k.amt}</div>
            </div>
          ))}
        </div>

        {/* 12-month bars */}
        <div style={{ padding: '20px 20px 12px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 14 }}>
            <VLabel>TREND · 12 MONTHS</VLabel>
            <div style={{ display: 'flex', gap: 12, fontFamily: t.mono, fontSize: 9, color: t.muted, letterSpacing: 0.8 }}>
              <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5 }}><span style={{ width: 8, height: 8, background: t.lime }} />IN</span>
              <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5 }}><span style={{ width: 8, height: 8, background: t.red }} />OUT</span>
            </div>
          </div>
          <div style={{ display: 'flex', alignItems: 'flex-end', gap: 4, height: 110 }}>
            {MONTHLY.map((m, i) => (
              <div key={i} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', height: '100%', justifyContent: 'flex-end', gap: 1 }}>
                <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 1 }}>
                  <div style={{ height: (m.in / max) * 60, background: t.lime, opacity: i === 11 ? 1 : 0.6 }} />
                  <div style={{ height: (m.out / max) * 60, background: t.red, opacity: i === 11 ? 1 : 0.5 }} />
                </div>
              </div>
            ))}
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 0.6, padding: '8px 2px 0' }}>
            {MONTHS_EN.map((m, i) => <span key={m} style={{ color: i === 11 ? t.text : t.faint }}>{m[0]}</span>)}
          </div>
        </div>

        {/* Category list with mini progress bars */}
        <div style={{ padding: '8px 20px 4px' }}>
          <VLabel>CATEGORIES · MAY</VLabel>
        </div>
        <div style={{ padding: '8px 0 0' }}>
          {exp.slice(0, 6).map((c, i) => {
            const pct = c.spent / c.budget;
            const over = pct > 1;
            return (
              <div key={c.id} style={{ display: 'flex', alignItems: 'center', padding: '10px 20px', gap: 12, borderTop: i === 0 ? `1px solid ${t.border}` : 'none', borderBottom: `1px solid ${t.border}` }}>
                <div style={{ width: 6, height: 32, background: c.color, flexShrink: 0 }} />
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
                    <span style={{ fontSize: 13, fontWeight: 500 }}>{c.name}</span>
                    <span style={{ fontFamily: t.mono, fontSize: 12, color: over ? t.red : t.text, fontVariantNumeric: 'tabular-nums' }}>−{c.spent.toLocaleString('ru-RU')}</span>
                  </div>
                  <div style={{ marginTop: 6, height: 2, background: t.surf2, position: 'relative' }}>
                    <div style={{ position: 'absolute', inset: 0, width: `${Math.min(pct, 1) * 100}%`, background: over ? t.red : c.color }} />
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 4, fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 0.6 }}>
                    <span>{(pct * 100).toFixed(0)}% OF {c.budget.toLocaleString('ru-RU')}</span>
                    <span>14 OPS</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Daily heat */}
        <div style={{ padding: '20px 20px 16px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 12 }}>
            <VLabel>DAILY ACTIVITY · 30D</VLabel>
            <span style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 0.8 }}>SUN — SAT</span>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(15, 1fr)', gap: 3 }}>
            {Array.from({ length: 30 }, (_, i) => {
              const v = HEATMAP[i] || 0;
              const op = v < 0.2 ? 0.08 : v < 0.4 ? 0.25 : v < 0.6 ? 0.5 : v < 0.8 ? 0.75 : 1;
              return <div key={i} style={{ aspectRatio: '1/1', background: v === 0 ? t.surf2 : t.lime, opacity: op }} />;
            })}
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 10, fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 0.6 }}>
            <span>LESS</span>
            <div style={{ display: 'flex', gap: 3 }}>
              {[0.08, 0.25, 0.5, 0.75, 1].map((o) => <div key={o} style={{ width: 10, height: 10, background: t.lime, opacity: o }} />)}
            </div>
            <span>MORE</span>
          </div>
        </div>
      </div>

      <VNav active="stat" />
    </div>
  );
};

Object.assign(window, {
  VaultHome, VaultAdd, VaultDetail, VaultAccounts, VaultCats, VaultStats,
  VDonut, VRing, VSpark, VLabel, VNav, useVault, V, V_LIGHT,
});
