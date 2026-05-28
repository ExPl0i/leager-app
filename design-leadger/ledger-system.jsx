// ledger-system.jsx — Design system showcase artboards for Ledger (A).
// Built as wide artboards (not phone frames) to live alongside screens
// in the design canvas. They reuse useVault() so light/dark works.

// ─── Small docs helpers ────────────────────────────────────────────
const L_GROUP = ({ children, label, span = 1, hint }) => {
  const t = useVault();
  return (
    <div style={{ gridColumn: `span ${span}`, padding: '0' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 14 }}>
        <VLabel>{label}</VLabel>
        {hint && <span style={{ fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 0.6 }}>{hint}</span>}
      </div>
      {children}
    </div>
  );
};

const L_SHEET = ({ title, sub, kicker, children, height }) => {
  const t = useVault();
  return (
    <div style={{
      background: t.bg, color: t.text, fontFamily: t.sans, width: '100%', height,
      borderRadius: 0, overflow: 'hidden', display: 'flex', flexDirection: 'column',
      border: `1px solid ${t.border}`,
    }}>
      {/* Header band */}
      <div style={{ display: 'flex', alignItems: 'stretch', borderBottom: `1px solid ${t.border}` }}>
        <div style={{ padding: '20px 24px', borderRight: `1px solid ${t.border}`, minWidth: 220 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div style={{ width: 18, height: 18, background: t.lime }} />
            <span style={{ fontFamily: t.mono, fontSize: 12, letterSpacing: 1.6, color: t.text }}>LEDGER</span>
          </div>
          <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1.4, marginTop: 4 }}>SELFFINANCE · v0.1</div>
        </div>
        <div style={{ padding: '20px 24px', flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div>
            <VLabel>{kicker}</VLabel>
            <div style={{ fontFamily: t.mono, fontSize: 28, fontWeight: 500, letterSpacing: -1, marginTop: 6 }}>{title}</div>
            {sub && <div style={{ fontSize: 13, color: t.muted, marginTop: 4, maxWidth: 720, lineHeight: 1.5 }}>{sub}</div>}
          </div>
        </div>
      </div>
      <div style={{ flex: 1, overflow: 'hidden' }}>{children}</div>
    </div>
  );
};

// ═════════════════════════════════════════════════════════════════════
// SHEET 1 · FOUNDATIONS (Tokens)
// Colors · Typography · Spacing · Border & Surface · Motion · Icon rules
// ═════════════════════════════════════════════════════════════════════
const LedgerFoundations = () => {
  const t = useVault();
  const colorTokens = [
    ['bg',       t.bg,      'background',    'BASE'],
    ['surf',     t.surf,    'surface · raised', 'BASE'],
    ['surf-2',   t.surf2,   'surface · sunken', 'BASE'],
    ['border',   t.borderStrong, 'border',   'BASE'],
    ['text',     t.text,    'text · primary',   'TEXT'],
    ['muted',    t.muted,   'text · muted',     'TEXT'],
    ['faint',    t.faint,   'text · faint',     'TEXT'],
    ['lime',     t.lime,    'accent · positive · active', 'SEMANTIC'],
    ['red',      t.red,     'accent · expense · destructive', 'SEMANTIC'],
  ];
  const typeSamples = [
    { label: 'Display · Mono · 500', size: 44, weight: 500, font: t.mono, body: '653 850 ₽', sub: 'Used for net worth, hero numbers in operation detail.' },
    { label: 'Headline · Mono · 500', size: 28, weight: 500, font: t.mono, body: '−2 840', sub: 'Stats hero, account list total.' },
    { label: 'Title · Sans · 500',    size: 20, weight: 500, font: t.sans, body: 'Категории', sub: 'Screen titles, page headings.' },
    { label: 'Body · Sans · 500',     size: 14, weight: 500, font: t.sans, body: 'Ужин · «Сахалин»', sub: 'Row primary text, button labels.' },
    { label: 'Body · Sans · 400',     size: 14, weight: 400, font: t.sans, body: 'Перекрёсток · продукты', sub: 'Row primary text, secondary.' },
    { label: 'Mono · Number · 500',   size: 14, weight: 500, font: t.mono, body: '−842.00', sub: 'All numeric values use Plex Mono with tabular nums.' },
    { label: 'Mono · Caption · 400',  size: 11, weight: 400, font: t.mono, body: '14:22 · продукты · основной', sub: 'Meta info under primary rows.' },
    { label: 'Mono · Label · 400',    size: 10, weight: 400, font: t.mono, body: 'RECENT OPERATIONS', sub: 'All section headers — uppercased, 1.4px letter-spacing.' },
  ];
  const spacing = [4, 8, 12, 16, 20, 24, 32, 48];
  const radii = [
    ['radius-none', 0,  'Default. All surfaces.'],
    ['radius-xs',   2,  'Receipts thumbnails.'],
    ['radius-pill', 999,'Avatars only.'],
  ];

  return (
    <L_SHEET kicker="01 · FOUNDATIONS" title="Design tokens"
      sub="Корневые токены. Все экраны и компоненты строятся из этого набора. Сетка 4px, без скруглений по умолчанию, всё число — IBM Plex Mono."
      height={1200}>
      <div style={{ display: 'grid', gridTemplateColumns: '1.1fr 1fr', gap: 0, height: '100%' }}>

        {/* LEFT — Colors + spacing + radii */}
        <div style={{ padding: 28, borderRight: `1px solid ${t.border}`, overflow: 'auto' }}>
          <L_GROUP label="COLOR · 09 TOKENS" hint="oklch base · web safe">
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8 }}>
              {colorTokens.map(([name, value, role, group]) => (
                <div key={name} style={{ border: `1px solid ${t.border}` }}>
                  <div style={{ height: 76, background: value, borderBottom: `1px solid ${t.border}` }} />
                  <div style={{ padding: '8px 10px 10px' }}>
                    <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1.2 }}>{group}</div>
                    <div style={{ fontFamily: t.mono, fontSize: 12, fontWeight: 500, marginTop: 2 }}>--{name}</div>
                    <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, marginTop: 2 }}>{value.toUpperCase()}</div>
                    <div style={{ fontSize: 11, color: t.muted, marginTop: 5, lineHeight: 1.3 }}>{role}</div>
                  </div>
                </div>
              ))}
            </div>
          </L_GROUP>

          <div style={{ height: 32 }} />

          <L_GROUP label="SPACING · 08 STEPS" hint="4px base unit">
            <div style={{ display: 'flex', gap: 8, alignItems: 'flex-end' }}>
              {spacing.map((s) => (
                <div key={s} style={{ flex: 1, textAlign: 'center' }}>
                  <div style={{ height: s + 8, background: t.surf, borderTop: `2px solid ${t.lime}`, marginBottom: 8 }} />
                  <div style={{ fontFamily: t.mono, fontSize: 11, color: t.text }}>{s}</div>
                  <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint }}>--s-{s}</div>
                </div>
              ))}
            </div>
          </L_GROUP>

          <div style={{ height: 32 }} />

          <L_GROUP label="RADIUS · 03 ONLY">
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10 }}>
              {radii.map(([name, val, hint]) => (
                <div key={name} style={{ border: `1px solid ${t.border}`, padding: 12 }}>
                  <div style={{ height: 64, background: t.surf, borderRadius: val, marginBottom: 10, display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: t.mono, fontSize: 11, color: t.muted }}>
                    {val}px
                  </div>
                  <div style={{ fontFamily: t.mono, fontSize: 11, fontWeight: 500 }}>--{name}</div>
                  <div style={{ fontSize: 11, color: t.muted, marginTop: 3 }}>{hint}</div>
                </div>
              ))}
            </div>
          </L_GROUP>

          <div style={{ height: 32 }} />

          <L_GROUP label="MOTION" hint="cubic-bezier(.2, .7, .3, 1)">
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10 }}>
              {[
                ['fast',   '150ms', 'Hover, focus rings'],
                ['normal', '220ms', 'Sheets, expansions'],
                ['slow',   '320ms', 'Page transitions'],
              ].map(([name, ms, hint]) => (
                <div key={name} style={{ border: `1px solid ${t.border}`, padding: 12 }}>
                  <div style={{ fontFamily: t.mono, fontSize: 20, fontWeight: 500 }}>{ms}</div>
                  <div style={{ fontFamily: t.mono, fontSize: 11, color: t.text, marginTop: 4 }}>--ease-{name}</div>
                  <div style={{ fontSize: 11, color: t.muted, marginTop: 4 }}>{hint}</div>
                </div>
              ))}
            </div>
          </L_GROUP>
        </div>

        {/* RIGHT — Type + Iconography + grid + rules */}
        <div style={{ padding: 28, overflow: 'auto' }}>
          <L_GROUP label="TYPOGRAPHY · 08 STYLES" hint="Plex Mono / Plex Sans">
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              {typeSamples.map((s, i) => (
                <div key={i} style={{ padding: '12px 0', borderTop: i === 0 ? `1px solid ${t.border}` : 'none', borderBottom: `1px solid ${t.border}`, display: 'grid', gridTemplateColumns: '110px 1fr', gap: 14, alignItems: 'baseline' }}>
                  <div>
                    <div style={{ fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 0.8 }}>{s.label}</div>
                    <div style={{ fontFamily: t.mono, fontSize: 9, color: t.muted, marginTop: 4 }}>{s.size}/{Math.round(s.size * 1.2)}</div>
                  </div>
                  <div>
                    <div style={{ fontFamily: s.font, fontSize: s.size, fontWeight: s.weight, letterSpacing: s.size > 22 ? -0.8 : 0, lineHeight: 1.1, color: t.text }}>{s.body}</div>
                    <div style={{ fontSize: 11, color: t.muted, marginTop: 4 }}>{s.sub}</div>
                  </div>
                </div>
              ))}
            </div>
          </L_GROUP>

          <div style={{ height: 32 }} />

          <L_GROUP label="ICONOGRAPHY" hint="24×24 · stroke 1.6 · square caps">
            <div style={{ display: 'flex', alignItems: 'center', gap: 20, flexWrap: 'wrap' }}>
              {[
                'M3 11l9-8 9 8v10a2 2 0 01-2 2h-4v-7H9v7H5a2 2 0 01-2-2V11z',
                'M4 6h16M4 12h16M4 18h10',
                'M4 20V10M10 20V4M16 20v-6M22 20h-22',
                'M12 5v14M5 12h14',
                'M3 4h18l-7 9v6l-4-2v-4z',
                'M21 21l-4.3-4.3M11 18a7 7 0 110-14 7 7 0 010 14z',
                'M5 12h14M13 6l6 6-6 6',
                'M19 12H5M11 18l-6-6 6-6',
              ].map((d, i) => (
                <div key={i} style={{ width: 44, height: 44, border: `1px solid ${t.border}`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={t.text} strokeWidth="1.6" strokeLinecap="square" strokeLinejoin="miter"><path d={d}/></svg>
                </div>
              ))}
            </div>
            <div style={{ marginTop: 14, fontSize: 11, color: t.muted, lineHeight: 1.5 }}>
              Никаких заливок. Контуры — 1.6px square. Иконки соответствуют сетке 24×24 с 2px паддингом. Используется в навигации, кнопках и метках.
            </div>
          </L_GROUP>

          <div style={{ height: 32 }} />

          <L_GROUP label="LAYOUT GRID · MOBILE" hint="412 dp · 4 col · 16 gutter">
            <div style={{ position: 'relative', height: 180, border: `1px solid ${t.border}` }}>
              <div style={{ position: 'absolute', inset: 0, display: 'flex', padding: 16, gap: 16 }}>
                {Array.from({ length: 4 }, (_, i) => (
                  <div key={i} style={{ flex: 1, background: `${t.lime}1F`, border: `1px dashed ${t.lime}88` }} />
                ))}
              </div>
              <div style={{ position: 'absolute', top: 8, left: 8, fontFamily: t.mono, fontSize: 9, color: t.faint }}>SAFE 16</div>
              <div style={{ position: 'absolute', bottom: 8, right: 8, fontFamily: t.mono, fontSize: 9, color: t.faint }}>412 × 892</div>
            </div>
          </L_GROUP>

          <div style={{ height: 24 }} />

          <L_GROUP label="USAGE RULES">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, fontSize: 12, color: t.muted, lineHeight: 1.6 }}>
              {[
                ['ДА', '· Числа всегда Mono · Tabular Nums', t.lime],
                ['ДА', '· Все секции открываются Label-caps', t.lime],
                ['ДА', '· Цветной 6px / 4px тик у строки', t.lime],
                ['НЕТ', '· Не используем градиенты', t.red],
                ['НЕТ', '· Не используем тени и blur', t.red],
                ['НЕТ', '· Не округляем углы у поверхностей', t.red],
              ].map(([k, txt, c], i) => (
                <div key={i} style={{ display: 'flex', gap: 10, alignItems: 'flex-start', padding: '6px 0' }}>
                  <span style={{ fontFamily: t.mono, fontSize: 10, letterSpacing: 1.2, color: c, minWidth: 26 }}>{k}</span>
                  <span style={{ color: t.text, fontSize: 11 }}>{txt}</span>
                </div>
              ))}
            </div>
          </L_GROUP>
        </div>
      </div>
    </L_SHEET>
  );
};

// ═════════════════════════════════════════════════════════════════════
// SHEET 2 · COMPONENTS
// Buttons · Inputs · Cards · Chips · Nav · Lists · Toasts · States
// ═════════════════════════════════════════════════════════════════════
const LedgerComponents = () => {
  const t = useVault();

  // Building blocks
  const Btn = ({ kind = 'primary', children, full, icon }) => {
    const styles = {
      primary:   { bg: t.lime, fg: '#0a0a0a', border: t.lime },
      secondary: { bg: 'transparent', fg: t.text, border: t.text },
      ghost:     { bg: 'transparent', fg: t.text, border: t.border },
      danger:    { bg: 'transparent', fg: t.red, border: t.red },
    }[kind];
    return (
      <div style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8,
        padding: '10px 18px', background: styles.bg, color: styles.fg,
        border: `1px solid ${styles.border}`,
        fontFamily: t.mono, fontSize: 11, letterSpacing: 1.4, fontWeight: 500,
        width: full ? '100%' : 'auto' }}>
        {icon}{children}
      </div>
    );
  };
  const Chip = ({ children, c, dot }) => (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6, padding: '4px 10px', border: `1px solid ${c || t.border}`, fontFamily: t.mono, fontSize: 10, letterSpacing: 1.2, color: c || t.text }}>
      {dot && <span style={{ width: 6, height: 6, background: c || t.text }} />}
      {children}
    </span>
  );

  return (
    <L_SHEET kicker="02 · COMPONENTS" title="Component library"
      sub="Атомарные блоки: кнопки, чипы, поля ввода, строки, навигация, состояния. Каждый компонент следует sharp-edge правилам Ledger."
      height={1500}>
      <div style={{ padding: 28, overflow: 'auto', height: '100%' }}>
        {/* BUTTONS */}
        <L_GROUP label="BUTTONS · 04 KINDS × 03 SIZES">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 10 }}>
            {[
              { k: 'primary',   label: 'PRIMARY',   note: 'Save · Confirm · CTA' },
              { k: 'secondary', label: 'SECONDARY', note: 'Cancel · Pair с PRIMARY' },
              { k: 'ghost',     label: 'GHOST',     note: 'Skip · Tertiary' },
              { k: 'danger',    label: 'DANGER',    note: 'Delete · Удалить' },
            ].map((b) => (
              <div key={b.k} style={{ padding: '14px 12px', border: `1px solid ${t.border}` }}>
                <Btn kind={b.k}>{b.label}</Btn>
                <div style={{ fontSize: 11, color: t.muted, marginTop: 10 }}>{b.note}</div>
              </div>
            ))}
          </div>
          {/* Size variants and FAB */}
          <div style={{ display: 'flex', gap: 10, marginTop: 14, alignItems: 'center', flexWrap: 'wrap' }}>
            <div style={{ padding: '6px 12px', background: t.lime, color: '#0a0a0a', fontFamily: t.mono, fontSize: 9, letterSpacing: 1.2 }}>S · 28</div>
            <div style={{ padding: '10px 18px', background: t.lime, color: '#0a0a0a', fontFamily: t.mono, fontSize: 11, letterSpacing: 1.4 }}>M · 36 (DEFAULT)</div>
            <div style={{ padding: '14px 24px', background: t.lime, color: '#0a0a0a', fontFamily: t.mono, fontSize: 13, letterSpacing: 1.4 }}>L · 48</div>
            <div style={{ width: 36, height: 36, border: `1px solid ${t.text}`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke={t.text} strokeWidth="1.6"><path d="M5 12h14M12 5v14"/></svg>
            </div>
            <div style={{ width: 52, height: 52, background: t.lime, color: '#0a0a0a', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: t.mono, fontSize: 24, paddingBottom: 4 }}>+</div>
            <span style={{ fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 1, marginLeft: 8 }}>FAB · NAV CENTER</span>
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* INPUTS */}
        <L_GROUP label="INPUTS · TEXT · NUMBER · DATE · SEARCH">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
            {/* Text */}
            <div>
              <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 6 }}>NOTE · TEXT</div>
              <div style={{ borderBottom: `1px solid ${t.borderStrong}`, padding: '10px 0', fontFamily: t.mono, fontSize: 14 }}>
                Перекрёсток<span style={{ background: t.lime, width: 1, height: 14, display: 'inline-block', marginLeft: 1, verticalAlign: -3 }} />
              </div>
            </div>
            {/* Amount */}
            <div>
              <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 6 }}>AMOUNT · MONO</div>
              <div style={{ borderBottom: `1px solid ${t.borderStrong}`, padding: '6px 0', fontFamily: t.mono, fontSize: 28, fontWeight: 500, letterSpacing: -0.8 }}>
                −<span style={{ color: t.lime }}>842</span><span style={{ color: t.faint }}>.00</span>
              </div>
            </div>
            {/* Search */}
            <div>
              <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 6 }}>SEARCH</div>
              <div style={{ border: `1px solid ${t.border}`, background: t.surf, padding: '10px 12px', display: 'flex', alignItems: 'center', gap: 10 }}>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke={t.muted} strokeWidth="1.6"><path d="M21 21l-4.3-4.3M11 18a7 7 0 110-14 7 7 0 010 14z"/></svg>
                <span style={{ fontFamily: t.mono, fontSize: 13, color: t.muted }}>искать операцию…</span>
              </div>
            </div>
            {/* Picker */}
            <div>
              <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 6 }}>PICKER · ACCOUNT</div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '10px 12px', border: `1px solid ${t.border}`, background: t.surf }}>
                <div style={{ width: 6, height: 18, background: t.lime }} />
                <span style={{ fontFamily: t.mono, fontSize: 13, flex: 1 }}>Основной · 4421</span>
                <span style={{ fontFamily: t.mono, fontSize: 13, color: t.muted }}>›</span>
              </div>
            </div>
          </div>
          {/* Segmented control */}
          <div style={{ marginTop: 16 }}>
            <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 8 }}>SEGMENTED · 03 OPTIONS</div>
            <div style={{ display: 'flex', border: `1px solid ${t.border}`, maxWidth: 360 }}>
              {['EXPENSE', 'INCOME', 'TRANSFER'].map((k, i) => (
                <div key={k} style={{ flex: 1, padding: '11px 0', textAlign: 'center', fontFamily: t.mono, fontSize: 11, letterSpacing: 1.4,
                  background: i === 0 ? t.text : 'transparent', color: i === 0 ? t.bg : t.muted, borderRight: i < 2 ? `1px solid ${t.border}` : 'none' }}>{k}</div>
              ))}
            </div>
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* CHIPS / TAGS */}
        <L_GROUP label="CHIPS · TAGS · BADGES">
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
            <Chip>DEFAULT</Chip>
            <Chip c={t.lime} dot>ACTIVE</Chip>
            <Chip c={t.red} dot>OVER BUDGET</Chip>
            <Chip c={t.muted}>+ ADD TAG</Chip>
            {/* category pill */}
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 8, padding: '4px 10px', border: `1px solid ${t.border}`, background: t.surf, fontFamily: t.mono, fontSize: 10, letterSpacing: 1.2 }}>
              <span style={{ width: 8, height: 8, background: '#FF6B5B' }} />КАФЕ И БАРЫ
            </span>
            <span style={{ padding: '3px 9px', background: t.lime, color: '#0a0a0a', fontFamily: t.mono, fontSize: 10, letterSpacing: 1.2 }}>+1.95%</span>
            <span style={{ padding: '3px 9px', background: t.red, color: '#fff', fontFamily: t.mono, fontSize: 10, letterSpacing: 1.2 }}>OVER</span>
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* LIST / CARDS */}
        <L_GROUP label="LIST ROWS · 03 KINDS">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            {/* Ledger row */}
            <div>
              <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 8 }}>OP · LEDGER ROW</div>
              <div style={{ border: `1px solid ${t.border}` }}>
                {[
                  ['Перекрёсток', '14:22 · продукты · основной', '#C5FF4A', '−842', false],
                  ['Кофе у офиса', '12:08 · кафе · основной', '#FF6B5B', '−380', false],
                  ['Зарплата · Май', '11:00 · доход · основной', '#C5FF4A', '+145 000', true],
                ].map((r, i) => (
                  <div key={i} style={{ display: 'flex', alignItems: 'center', padding: '10px 14px', gap: 12, borderBottom: i < 2 ? `1px solid ${t.border}` : 'none' }}>
                    <div style={{ width: 4, height: 28, background: r[2] }} />
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 13, fontWeight: 500 }}>{r[0]}</div>
                      <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, letterSpacing: 0.4, marginTop: 2 }}>{r[1]}</div>
                    </div>
                    <div style={{ fontFamily: t.mono, fontSize: 14, fontWeight: 500, color: r[4] ? t.lime : t.text }}>{r[3]}</div>
                  </div>
                ))}
              </div>
            </div>
            {/* Account card */}
            <div>
              <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 8 }}>ACCOUNT · ROW</div>
              <div style={{ padding: '14px 14px', background: t.surf, border: `1px solid ${t.border}`, display: 'flex', gap: 14, alignItems: 'center' }}>
                <div style={{ width: 8, height: 48, background: t.lime }} />
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
                    <span style={{ fontSize: 15, fontWeight: 500 }}>Основной</span>
                    <span style={{ fontFamily: t.mono, fontSize: 16, fontWeight: 500 }}>187 420 ₽</span>
                  </div>
                  <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, marginTop: 4, letterSpacing: 1 }}>CARD · ****4421 · RUB</div>
                  <div style={{ height: 2, background: t.border, marginTop: 8, position: 'relative' }}>
                    <div style={{ position: 'absolute', inset: 0, width: '28%', background: t.lime }} />
                  </div>
                </div>
              </div>
              {/* Category card */}
              <div style={{ marginTop: 10 }}>
                <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 1, marginBottom: 8 }}>CATEGORY · ROW</div>
                <div style={{ padding: '12px 14px', border: `1px solid ${t.border}`, display: 'flex', alignItems: 'center', gap: 12 }}>
                  <div style={{ width: 28, height: 28, background: '#C5FF4A', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: t.mono, color: '#0a0a0a', fontSize: 14 }}>◐</div>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ fontSize: 13, fontWeight: 500 }}>Продукты</span>
                      <span style={{ fontFamily: t.mono, fontSize: 12 }}>18 420 <span style={{ color: t.faint }}>/ 25 000</span></span>
                    </div>
                    <div style={{ height: 3, background: t.surf2, marginTop: 8, position: 'relative' }}>
                      <div style={{ position: 'absolute', inset: 0, width: '74%', background: '#C5FF4A' }} />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* NAV */}
        <L_GROUP label="BOTTOM NAVIGATION · 5 SLOTS">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-around', padding: '0 8px',
            background: t.bg, border: `1px solid ${t.border}`, height: 64, maxWidth: 412 }}>
            {[
              ['HOME', true],
              ['OPS', false],
              ['+',   'add'],
              ['STATS', false],
              ['MORE', false],
            ].map(([label, on]) => {
              if (on === 'add') return <div key={label} style={{ width: 52, height: 52, border: `1px solid ${t.text}`, background: t.lime, color: '#0a0a0a', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: t.mono, fontSize: 26, paddingBottom: 4 }}>+</div>;
              return (
                <div key={label} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, paddingTop: 8 }}>
                  <div style={{ width: 18, height: 18, border: `1.5px solid ${on ? t.lime : t.muted}` }} />
                  <span style={{ fontFamily: t.mono, fontSize: 9, letterSpacing: 1.2, color: on ? t.lime : t.muted }}>{label}</span>
                </div>
              );
            })}
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* STATES */}
        <L_GROUP label="STATES · EMPTY · LOADING · ERROR · SUCCESS">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 10 }}>
            {/* Empty */}
            <div style={{ padding: '22px 16px', border: `1px solid ${t.border}`, textAlign: 'center' }}>
              <div style={{ width: 36, height: 36, border: `1px dashed ${t.muted}`, margin: '0 auto 12px' }} />
              <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, letterSpacing: 1, textTransform: 'uppercase' }}>NO OPERATIONS</div>
              <div style={{ fontSize: 11, color: t.faint, marginTop: 6 }}>Журнал пуст. Нажмите +.</div>
            </div>
            {/* Loading */}
            <div style={{ padding: '22px 16px', border: `1px solid ${t.border}` }}>
              <div style={{ height: 12, background: t.surf2, marginBottom: 8, width: '70%' }} />
              <div style={{ height: 8, background: t.surf2, marginBottom: 4, width: '90%' }} />
              <div style={{ height: 8, background: t.surf2, marginBottom: 14, width: '60%' }} />
              <div style={{ fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 1, textTransform: 'uppercase' }}>LOADING…</div>
            </div>
            {/* Error */}
            <div style={{ padding: '14px 16px', border: `1px solid ${t.red}`, background: `${t.red}11`, display: 'flex', gap: 10 }}>
              <div style={{ width: 18, height: 18, background: t.red, color: '#fff', fontFamily: t.mono, fontSize: 12, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>!</div>
              <div>
                <div style={{ fontFamily: t.mono, fontSize: 10, color: t.red, letterSpacing: 1.2 }}>SAVE FAILED</div>
                <div style={{ fontSize: 11, color: t.text, marginTop: 4 }}>Нет соединения. Повторить?</div>
              </div>
            </div>
            {/* Success */}
            <div style={{ padding: '14px 16px', border: `1px solid ${t.lime}`, background: `${t.lime}14`, display: 'flex', gap: 10 }}>
              <div style={{ width: 18, height: 18, background: t.lime, color: '#0a0a0a', fontFamily: t.mono, fontSize: 12, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>✓</div>
              <div>
                <div style={{ fontFamily: t.mono, fontSize: 10, color: t.lime, letterSpacing: 1.2 }}>SAVED · LOCAL</div>
                <div style={{ fontSize: 11, color: t.text, marginTop: 4 }}>Запись в журнале · 14:22</div>
              </div>
            </div>
          </div>
        </L_GROUP>
      </div>
    </L_SHEET>
  );
};

// ═════════════════════════════════════════════════════════════════════
// SHEET 3 · DATA VIZ
// Donuts · Rings · Sparklines · Bars · Heat · KPI
// ═════════════════════════════════════════════════════════════════════
const LedgerDataViz = () => {
  const t = useVault();
  const exp = CATEGORIES.filter((c) => c.kind === 'expense');
  const donutData = exp.map((c) => ({ value: c.spent, color: c.color, label: c.name, id: c.id }));
  const totalSpent = exp.reduce((s, c) => s + c.spent, 0);
  const max = Math.max(...MONTHLY.map((m) => Math.max(m.in, m.out)));

  return (
    <L_SHEET kicker="03 · DATA VIZ" title="Visualization primitives"
      sub="Графики Ledger — без скруглений, без теней, без градиентов. Цвет рассказывает что; контур — где; число — сколько."
      height={1380}>
      <div style={{ padding: 28, height: '100%', overflow: 'auto' }}>

        {/* DONUTS */}
        <L_GROUP label="DONUT · 03 SIZES">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
            {[
              { size: 200, thickness: 28, title: 'Hero · stats month', center:
                <React.Fragment>
                  <div style={{ fontFamily: t.mono, fontSize: 9, letterSpacing: 1.2, color: t.faint }}>SPENT</div>
                  <div style={{ fontFamily: t.mono, fontSize: 24, fontWeight: 500, color: t.text }}>93 690</div>
                  <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, marginTop: 4 }}>8 КАТ · 81%</div>
                </React.Fragment> },
              { size: 140, thickness: 18, title: 'Card · breakdown', center:
                <React.Fragment>
                  <div style={{ fontFamily: t.mono, fontSize: 16, color: t.text }}>81%</div>
                  <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, marginTop: 2 }}>OF BUDGET</div>
                </React.Fragment> },
              { size: 96, thickness: 12, title: 'Inline · KPI', center:
                <div style={{ fontFamily: t.mono, fontSize: 12, color: t.text }}>06</div> },
            ].map((d, i) => (
              <div key={i} style={{ padding: 18, border: `1px solid ${t.border}` }}>
                <div style={{ display: 'flex', justifyContent: 'center', padding: '4px 0 14px' }}>
                  <VDonut data={donutData} size={d.size} thickness={d.thickness} center={d.center} />
                </div>
                <div style={{ fontFamily: t.mono, fontSize: 10, color: t.muted, letterSpacing: 1, textAlign: 'center' }}>{d.title.toUpperCase()}</div>
              </div>
            ))}
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* RADIAL RINGS */}
        <L_GROUP label="RADIAL RING · PROGRESS">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(6, 1fr)', gap: 12, alignItems: 'center', justifyItems: 'center' }}>
            {[0.12, 0.34, 0.5, 0.78, 0.95, 1].map((v, i) => (
              <div key={i} style={{ textAlign: 'center' }}>
                <VRing value={v} size={70} thickness={4} color={v > 0.9 ? t.red : t.lime} label={`${Math.round(v * 100)}%`} sublabel="USED" fontSize={14} />
                <div style={{ fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 0.8, marginTop: 8 }}>STEP {String(i + 1).padStart(2, '0')}</div>
              </div>
            ))}
          </div>
          <div style={{ marginTop: 18, fontSize: 11, color: t.muted, lineHeight: 1.5 }}>
            Кольцо переключается на --red при превышении 90%. Размер 54 / 70 / 96 / 140. Толщина пропорциональна.
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* SPARKLINES */}
        <L_GROUP label="SPARKLINE · TREND">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
            {[
              { label: 'BALANCE 30D', d: SPARK_MAIN, c: t.lime },
              { label: 'SAVINGS 30D', d: SPARK_SAVE, c: '#9B8BFF' },
              { label: 'CAFE 30D',    d: [3,2,4,5,3,7,8,4,3,5,7,6,9,5,4,7,8,6,5,4,8,9,5,7,6,9,7,8,5,6], c: '#FF6B5B' },
            ].map((s) => (
              <div key={s.label} style={{ padding: 16, border: `1px solid ${t.border}` }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
                  <span style={{ fontFamily: t.mono, fontSize: 10, color: t.faint, letterSpacing: 1.2 }}>{s.label}</span>
                  <span style={{ fontFamily: t.mono, fontSize: 14, fontWeight: 500, color: s.c }}>{Math.round(s.d[s.d.length-1])}</span>
                </div>
                <div style={{ marginTop: 10 }}>
                  <VSpark data={s.d} color={s.c} w={260} h={48} />
                </div>
              </div>
            ))}
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* BARS */}
        <L_GROUP label="STACKED BARS · 12 MONTHS">
          <div style={{ padding: 18, border: `1px solid ${t.border}` }}>
            <div style={{ display: 'flex', alignItems: 'flex-end', gap: 8, height: 140 }}>
              {MONTHLY.map((m, i) => (
                <div key={i} style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'flex-end', height: '100%', gap: 2 }}>
                  <div style={{ height: (m.in / max) * 100, background: t.lime, opacity: i === 11 ? 1 : 0.55 }} />
                  <div style={{ height: (m.out / max) * 100, background: t.red, opacity: i === 11 ? 1 : 0.45 }} />
                </div>
              ))}
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: t.mono, fontSize: 9, color: t.faint, marginTop: 8 }}>
              {MONTHS_EN.map((m, i) => <span key={m} style={{ color: i === 11 ? t.text : t.faint }}>{m}</span>)}
            </div>
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* HEATMAP */}
        <L_GROUP label="HEAT GRID · DAILY ACTIVITY">
          <div style={{ padding: 18, border: `1px solid ${t.border}` }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(15, 1fr)', gap: 4, maxWidth: 540 }}>
              {Array.from({ length: 30 }, (_, i) => {
                const v = HEATMAP[i] || 0;
                const op = v < 0.2 ? 0.08 : v < 0.4 ? 0.25 : v < 0.6 ? 0.5 : v < 0.8 ? 0.75 : 1;
                return <div key={i} style={{ aspectRatio: '1/1', background: v === 0 ? t.surf2 : t.lime, opacity: op }} />;
              })}
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 12, fontFamily: t.mono, fontSize: 9, color: t.faint, letterSpacing: 0.6, maxWidth: 540 }}>
              <span>LESS</span>
              <div style={{ display: 'flex', gap: 3 }}>
                {[0.08, 0.25, 0.5, 0.75, 1].map((o) => <div key={o} style={{ width: 10, height: 10, background: t.lime, opacity: o }} />)}
              </div>
              <span>MORE</span>
            </div>
          </div>
        </L_GROUP>

        <div style={{ height: 32 }} />

        {/* KPI BLOCK */}
        <L_GROUP label="KPI BLOCK · COMPOSITE">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', border: `1px solid ${t.border}` }}>
            {[
              { ring: { value: 0.96, color: t.lime, label: '96%', sublabel: 'OF GOAL' }, kpi: 'INCOME', amt: `+${(173000).toLocaleString('ru-RU')}`, c: t.lime },
              { ring: { value: 0.81, color: t.text, label: '81%', sublabel: 'BUDGET' },   kpi: 'EXPENSE', amt: `−${(93690).toLocaleString('ru-RU')}`, c: t.text },
              { ring: { value: 0.27, color: t.lime, label: '27', sublabel: 'DAYS' },       kpi: 'NET', amt: `+${(79310).toLocaleString('ru-RU')}`, c: t.lime },
            ].map((b, i) => (
              <div key={i} style={{ padding: '16px 12px', borderRight: i < 2 ? `1px solid ${t.border}` : 'none', textAlign: 'center' }}>
                <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 6 }}>
                  <VRing {...b.ring} size={56} thickness={3} fontSize={12} />
                </div>
                <VLabel>{b.kpi}</VLabel>
                <div style={{ fontFamily: t.mono, fontSize: 14, fontWeight: 500, color: b.c, marginTop: 4 }}>{b.amt}</div>
              </div>
            ))}
          </div>
        </L_GROUP>
      </div>
    </L_SHEET>
  );
};

Object.assign(window, { LedgerFoundations, LedgerComponents, LedgerDataViz });
