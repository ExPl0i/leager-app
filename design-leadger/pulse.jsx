// pulse.jsx — Direction B · Pulse
// Editorial: deep charcoal + cream + lime accent. Serif display for numbers,
// Inter for UI. Stats: calendar heatmap + sankey-style category flow.

const P = {
  bg: '#13110E',
  surf: '#1B1815',
  surf2: '#252118',
  border: 'rgba(245,236,219,0.08)',
  text: '#F5ECDB',
  muted: '#A39885',
  faint: '#6B6354',
  lime: '#DAFF54',
  warm: '#FF9B6A',
  cool: '#7BC8FF',
  pink: '#FF9DC4',
  serif: '"Fraunces", "Cormorant Garamond", "Tiempos", Georgia, serif',
  sans: '"Inter", -apple-system, system-ui, sans-serif',
};
const P_LIGHT = {
  ...P,
  bg: '#F5ECDB', surf: '#FFFFFF', surf2: '#EDE1C8',
  border: 'rgba(19,17,14,0.08)',
  text: '#13110E', muted: '#5A4F3D', faint: '#9B907A',
  lime: '#4A7000', warm: '#C84F0F', cool: '#1C7ABF', pink: '#C44A85',
};
const usePulse = () => (window.__theme === 'light' ? P_LIGHT : P);

// ─── Floating tab bar (pill) ────────────────────────────────
const PNav = ({ active = 'home' }) => {
  const p = usePulse();
  const items = [
    ['home', 'Сводка'],
    ['ops', 'Операции'],
    ['add', '+'],
    ['stat', 'Статистика'],
    ['set', 'Меню'],
  ];
  return (
    <div style={{ padding: '12px 16px 18px', flexShrink: 0 }}>
      <div style={{ background: p.surf, borderRadius: 100, padding: 4, border: `1px solid ${p.border}`,
        display: 'flex', alignItems: 'center', gap: 2 }}>
        {items.map(([k, label]) => {
          if (k === 'add') return (
            <div key={k} style={{ width: 56, height: 44, background: p.lime, borderRadius: 100,
              display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: p.serif,
              fontSize: 28, color: '#13110E', fontWeight: 400, lineHeight: 1, marginTop: -1 }}>+</div>
          );
          const on = k === active;
          return (
            <div key={k} style={{ flex: 1, padding: '10px 0', textAlign: 'center', fontFamily: p.sans, fontSize: 12,
              fontWeight: on ? 600 : 400, color: on ? p.text : p.muted,
              background: on ? p.surf2 : 'transparent', borderRadius: 100 }}>{label}</div>
          );
        })}
      </div>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 1: HOME — Editorial summary
// ═══════════════════════════════════════════════════════════
const PulseHome = () => {
  const p = usePulse();
  return (
    <div style={{ background: p.bg, color: p.text, fontFamily: p.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Editorial masthead */}
      <div style={{ padding: '24px 24px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ fontFamily: p.sans, fontSize: 11, letterSpacing: 2.5, color: p.muted, textTransform: 'uppercase' }}>СРЕДА · 27 МАЯ</div>
          <div style={{ fontFamily: p.serif, fontSize: 28, fontStyle: 'italic', marginTop: 6, letterSpacing: -0.5 }}>Добрый день, <br/>Артём.</div>
        </div>
        <div style={{ width: 40, height: 40, borderRadius: 20, background: p.surf, display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: p.serif, fontSize: 16 }}>А</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto' }}>
        {/* Editorial number */}
        <div style={{ padding: '36px 24px 16px' }}>
          <div style={{ fontFamily: p.sans, fontSize: 11, letterSpacing: 2, color: p.faint, textTransform: 'uppercase' }}>Всего ваших средств</div>
          <div style={{ fontFamily: p.serif, fontSize: 76, fontWeight: 300, letterSpacing: -3, lineHeight: 0.95, marginTop: 12 }}>
            653 850
          </div>
          <div style={{ fontFamily: p.serif, fontSize: 18, fontStyle: 'italic', color: p.muted, marginTop: 4 }}>рублей <span style={{ color: p.faint }}>· эквивалент</span></div>
          <div style={{ display: 'flex', gap: 14, marginTop: 16, fontSize: 13, alignItems: 'center' }}>
            <span style={{ padding: '4px 10px', background: p.lime, color: '#13110E', borderRadius: 100, fontWeight: 600, fontSize: 12 }}>+1.95%</span>
            <span style={{ color: p.muted }}>+12 480 ₽ за неделю</span>
          </div>
        </div>

        {/* Today's pulse — flowing bar */}
        <div style={{ margin: '20px 24px 0', padding: '18px 20px', background: p.surf, borderRadius: 18 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
            <div style={{ fontFamily: p.serif, fontSize: 22, fontStyle: 'italic' }}>Сегодня</div>
            <div style={{ fontFamily: p.sans, fontSize: 12, color: p.muted }}>3 операции</div>
          </div>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 14, marginTop: 14 }}>
            <div>
              <div style={{ fontFamily: p.serif, fontSize: 34, color: p.warm }}>−1 307</div>
              <div style={{ fontSize: 11, color: p.faint, marginTop: 2, letterSpacing: 0.6 }}>РАСХОД</div>
            </div>
            <div style={{ flex: 1 }}>
              {/* Flow bar with categories */}
              <div style={{ display: 'flex', height: 8, borderRadius: 4, overflow: 'hidden' }}>
                <div style={{ flex: 842, background: P.lime }} />
                <div style={{ flex: 380, background: P.warm }} />
                <div style={{ flex: 85, background: P.cool }} />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8, fontFamily: p.sans, fontSize: 10, color: p.muted }}>
                <span>еда 842</span>
                <span>кафе 380</span>
                <span>транспорт 85</span>
              </div>
            </div>
          </div>
        </div>

        {/* Account stack — minimalist cards */}
        <div style={{ padding: '24px 24px 0' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 14 }}>
            <div style={{ fontFamily: p.serif, fontSize: 22, fontStyle: 'italic' }}>Счета</div>
            <span style={{ fontSize: 12, color: p.muted }}>5 · все →</span>
          </div>
          {ACCOUNTS.slice(0, 3).map((a, i) => (
            <div key={a.id} style={{ display: 'flex', alignItems: 'center', padding: '14px 0',
              borderTop: i === 0 ? `1px solid ${p.border}` : 'none',
              borderBottom: `1px solid ${p.border}` }}>
              <div style={{ width: 36, height: 36, borderRadius: 18, background: a.color, opacity: 0.9,
                display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: p.serif, fontSize: 14, color: '#13110E', fontWeight: 600 }}>
                {a.name[0]}
              </div>
              <div style={{ flex: 1, marginLeft: 14 }}>
                <div style={{ fontSize: 15, fontWeight: 500 }}>{a.name}</div>
                <div style={{ fontSize: 11, color: p.faint, marginTop: 2 }}>{a.type === 'card' ? `•••• ${a.last4}` : a.type === 'depo' ? 'Депозит · 14% годовых' : 'Наличные'}</div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontFamily: p.serif, fontSize: 19 }}>{fmt(a.balance, a.cur)}</div>
              </div>
            </div>
          ))}
        </div>

        {/* Recent ops */}
        <div style={{ padding: '24px 24px 16px' }}>
          <div style={{ fontFamily: p.serif, fontSize: 22, fontStyle: 'italic', marginBottom: 12 }}>Последнее</div>
          {TXNS.slice(0, 4).map((tx) => {
            const cat = catById(tx.cat);
            return (
              <div key={tx.id} style={{ display: 'flex', alignItems: 'center', padding: '12px 0', borderTop: `1px solid ${p.border}` }}>
                <div style={{ width: 28, height: 28, borderRadius: 14, background: cat.color, opacity: 0.9, marginRight: 14 }} />
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 14, fontWeight: 500 }}>{tx.note}</div>
                  <div style={{ fontSize: 11, color: p.faint, marginTop: 2 }}>{cat.name} · {tx.t}</div>
                </div>
                <div style={{ fontFamily: p.serif, fontSize: 17, color: tx.amt > 0 ? p.lime : p.text }}>
                  {tx.amt > 0 ? '+' : '−'}{Math.abs(tx.amt).toLocaleString('ru-RU')}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <PNav active="home" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 2: ADD OPERATION
// ═══════════════════════════════════════════════════════════
const PulseAdd = () => {
  const p = usePulse();
  const cats = CATEGORIES.filter((c) => c.kind === 'expense').slice(0, 6);
  return (
    <div style={{ background: p.bg, color: p.text, fontFamily: p.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '20px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ fontSize: 14, color: p.muted }}>Отменить</span>
        <span style={{ fontFamily: p.serif, fontSize: 18, fontStyle: 'italic' }}>Новая операция</span>
        <span style={{ fontSize: 14, color: p.lime, fontWeight: 600 }}>Готово</span>
      </div>

      {/* Type segmented */}
      <div style={{ margin: '0 24px', background: p.surf, borderRadius: 100, padding: 4, display: 'flex' }}>
        {['Расход', 'Доход', 'Перевод'].map((k, i) => (
          <div key={k} style={{ flex: 1, padding: '10px 0', textAlign: 'center', fontSize: 13, fontWeight: i === 0 ? 600 : 400,
            background: i === 0 ? p.warm : 'transparent', color: i === 0 ? '#13110E' : p.muted, borderRadius: 100 }}>{k}</div>
        ))}
      </div>

      {/* Big amount, editorial */}
      <div style={{ padding: '32px 24px 24px', textAlign: 'center' }}>
        <div style={{ fontSize: 12, letterSpacing: 1.6, color: p.faint, textTransform: 'uppercase' }}>Сумма</div>
        <div style={{ fontFamily: p.serif, fontSize: 84, fontWeight: 300, letterSpacing: -3, lineHeight: 1, marginTop: 14 }}>
          <span style={{ color: p.warm }}>−842</span>
          <span style={{ fontSize: 40, color: p.faint, fontStyle: 'italic' }}>,00</span>
        </div>
        <div style={{ fontFamily: p.serif, fontSize: 18, fontStyle: 'italic', color: p.muted, marginTop: 2 }}>рублей</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 24px 16px' }}>
        {/* Category swatches */}
        <div style={{ fontSize: 12, letterSpacing: 1.4, color: p.faint, textTransform: 'uppercase', marginBottom: 12 }}>Категория</div>
        <div style={{ display: 'flex', gap: 10, overflowX: 'auto', paddingBottom: 4, marginBottom: 22 }}>
          {cats.map((c, i) => {
            const sel = i === 0;
            return (
              <div key={c.id} style={{ minWidth: 92, padding: '14px 12px', textAlign: 'center', borderRadius: 14,
                background: sel ? p.surf2 : p.surf, border: `1px solid ${sel ? p.text : 'transparent'}` }}>
                <div style={{ width: 32, height: 32, borderRadius: 16, background: c.color, margin: '0 auto 8px' }} />
                <div style={{ fontSize: 12, fontWeight: sel ? 600 : 400, color: sel ? p.text : p.muted }}>{c.name}</div>
              </div>
            );
          })}
        </div>

        {/* Account row */}
        <div style={{ fontSize: 12, letterSpacing: 1.4, color: p.faint, textTransform: 'uppercase', marginBottom: 12 }}>Со счёта</div>
        <div style={{ display: 'flex', alignItems: 'center', padding: '14px 16px', background: p.surf, borderRadius: 14, marginBottom: 22 }}>
          <div style={{ width: 32, height: 32, borderRadius: 16, background: P.lime, marginRight: 14 }} />
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 15, fontWeight: 500 }}>Основной</div>
            <div style={{ fontSize: 11, color: p.faint, marginTop: 2 }}>•••• 4421 · 187 420 ₽</div>
          </div>
          <span style={{ color: p.muted, fontSize: 16 }}>›</span>
        </div>

        {/* Note */}
        <div style={{ fontSize: 12, letterSpacing: 1.4, color: p.faint, textTransform: 'uppercase', marginBottom: 12 }}>Заметка</div>
        <div style={{ padding: '14px 16px', background: p.surf, borderRadius: 14, fontFamily: p.serif, fontSize: 18, fontStyle: 'italic', color: p.text }}>
          «Перекрёсток»
        </div>

        {/* Meta row */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, marginTop: 16 }}>
          <div style={{ padding: '12px 14px', background: p.surf, borderRadius: 14 }}>
            <div style={{ fontSize: 11, color: p.faint, letterSpacing: 1.2 }}>ДАТА</div>
            <div style={{ fontSize: 14, marginTop: 4 }}>27 мая, 14:22</div>
          </div>
          <div style={{ padding: '12px 14px', background: p.surf, borderRadius: 14 }}>
            <div style={{ fontSize: 11, color: p.faint, letterSpacing: 1.2 }}>ПОВТОР</div>
            <div style={{ fontSize: 14, marginTop: 4, color: p.muted }}>Не повторять</div>
          </div>
        </div>
      </div>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 3: OPERATION DETAIL
// ═══════════════════════════════════════════════════════════
const PulseDetail = () => {
  const p = usePulse();
  const tx = TXNS[3];
  const cat = catById(tx.cat);
  return (
    <div style={{ background: p.bg, color: p.text, fontFamily: p.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '20px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ fontSize: 22 }}>‹</span>
        <span style={{ fontSize: 13, color: p.muted }}>Операция</span>
        <span style={{ fontSize: 14, color: p.lime, fontWeight: 600 }}>Изменить</span>
      </div>

      {/* Hero */}
      <div style={{ padding: '24px 24px 32px', textAlign: 'center' }}>
        <div style={{ width: 56, height: 56, borderRadius: 28, background: cat.color, margin: '0 auto 16px' }} />
        <div style={{ fontFamily: p.serif, fontSize: 64, fontWeight: 300, letterSpacing: -2, lineHeight: 1 }}>
          −2 840<span style={{ fontSize: 30, color: p.faint, fontStyle: 'italic' }}> ₽</span>
        </div>
        <div style={{ fontFamily: p.serif, fontSize: 22, fontStyle: 'italic', marginTop: 14 }}>«Сахалин»</div>
        <div style={{ fontSize: 13, color: p.muted, marginTop: 6 }}>{cat.name} · 26 мая, 21:40</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '0 24px' }}>
        {/* Sectional card */}
        <div style={{ background: p.surf, borderRadius: 18, overflow: 'hidden' }}>
          {[
            ['Счёт', 'Основной · 4421'],
            ['Категория', cat.name],
            ['Теги', 'ресторан · вечер'],
            ['Локация', 'Москва, ул. Ленинградская'],
          ].map(([k, v], i, arr) => (
            <div key={k} style={{ padding: '14px 18px', borderBottom: i === arr.length - 1 ? 'none' : `1px solid ${p.border}`,
              display: 'flex', justifyContent: 'space-between', gap: 16 }}>
              <span style={{ color: p.muted, fontSize: 13 }}>{k}</span>
              <span style={{ fontSize: 14, textAlign: 'right' }}>{v}</span>
            </div>
          ))}
        </div>

        {/* Receipt attached */}
        <div style={{ marginTop: 14, background: p.surf, borderRadius: 18, padding: 18, display: 'flex', gap: 14, alignItems: 'center' }}>
          <div style={{ width: 56, height: 72, background: P.cool, opacity: 0.2, borderRadius: 6,
            backgroundImage: `repeating-linear-gradient(45deg, transparent, transparent 4px, ${p.cool}44 4px, ${p.cool}44 5px)` }} />
          <div style={{ flex: 1 }}>
            <div style={{ fontFamily: p.serif, fontStyle: 'italic', fontSize: 17 }}>Чек прикреплён</div>
            <div style={{ fontSize: 12, color: p.faint, marginTop: 4 }}>receipt-0526.jpg · 1.2 MB</div>
          </div>
          <span style={{ color: p.muted, fontSize: 20 }}>›</span>
        </div>

        {/* Tip — context */}
        <div style={{ marginTop: 18, padding: '16px 18px', borderRadius: 18, border: `1px dashed ${p.border}` }}>
          <div style={{ fontSize: 11, color: p.faint, letterSpacing: 1.4, textTransform: 'uppercase' }}>В контексте</div>
          <div style={{ marginTop: 8, fontFamily: p.serif, fontSize: 16, fontStyle: 'italic', lineHeight: 1.4 }}>
            Это 6-й ужин в мае. Категория <span style={{ color: p.warm }}>«Кафе»</span> на 82% от лимита.
          </div>
        </div>

        <div style={{ display: 'flex', gap: 10, marginTop: 18, paddingBottom: 18 }}>
          <div style={{ flex: 1, padding: '14px 0', textAlign: 'center', borderRadius: 100, border: `1px solid ${p.border}`, fontSize: 13 }}>Дублировать</div>
          <div style={{ flex: 1, padding: '14px 0', textAlign: 'center', borderRadius: 100, background: p.warm, color: '#13110E', fontSize: 13, fontWeight: 600 }}>Удалить</div>
        </div>
      </div>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 4: ACCOUNTS
// ═══════════════════════════════════════════════════════════
const PulseAccounts = () => {
  const p = usePulse();
  return (
    <div style={{ background: p.bg, color: p.text, fontFamily: p.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '24px 24px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ fontFamily: p.serif, fontSize: 32, fontStyle: 'italic' }}>Ваши счета</div>
          <div style={{ fontSize: 13, color: p.muted, marginTop: 6 }}>5 счетов · 3 валюты</div>
        </div>
        <div style={{ width: 40, height: 40, borderRadius: 20, background: p.lime, color: '#13110E', fontFamily: p.serif,
          display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 24, lineHeight: 1, paddingBottom: 3 }}>+</div>
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '24px 16px 16px' }}>
        {ACCOUNTS.map((a) => {
          const rate = { RUB: 1, USD: 92, EUR: 99 }[a.cur];
          const rub = a.balance * rate;
          return (
            <div key={a.id} style={{ margin: '0 8px 14px', padding: '22px 22px', borderRadius: 20, position: 'relative', overflow: 'hidden',
              background: p.surf, border: `1px solid ${p.border}` }}>
              {/* Color band */}
              <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 4, background: a.color }} />
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ fontFamily: p.serif, fontSize: 22, fontStyle: 'italic' }}>{a.name}</div>
                  <div style={{ fontSize: 12, color: p.faint, marginTop: 4, letterSpacing: 0.5 }}>
                    {a.type === 'card' ? `Карта · •••• ${a.last4}` : a.type === 'depo' ? 'Депозит · 14% годовых' : 'Наличные'}
                  </div>
                </div>
                <div style={{ fontSize: 11, padding: '4px 10px', borderRadius: 100, background: p.surf2, color: p.muted }}>{a.cur}</div>
              </div>
              <div style={{ marginTop: 18, fontFamily: p.serif, fontSize: 34, fontWeight: 300, letterSpacing: -1 }}>
                {fmt(a.balance, a.cur)}
              </div>
              {a.cur !== 'RUB' && <div style={{ fontSize: 12, color: p.muted, marginTop: 2 }}>≈ {Math.round(rub).toLocaleString('ru-RU')} ₽</div>}
            </div>
          );
        })}
      </div>

      <PNav active="set" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 5: CATEGORIES
// ═══════════════════════════════════════════════════════════
const PulseCats = () => {
  const p = usePulse();
  const exp = CATEGORIES.filter((c) => c.kind === 'expense');
  return (
    <div style={{ background: p.bg, color: p.text, fontFamily: p.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '24px 24px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ fontFamily: p.serif, fontSize: 32, fontStyle: 'italic' }}>Категории</div>
          <div style={{ fontSize: 13, color: p.muted, marginTop: 6 }}>Май · бюджет 115 500 ₽</div>
        </div>
        <div style={{ width: 40, height: 40, borderRadius: 20, background: p.lime, color: '#13110E',
          display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: p.serif, fontSize: 24, lineHeight: 1, paddingBottom: 3 }}>+</div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: 8, padding: '20px 24px 0' }}>
        {['Расходы', 'Доходы'].map((k, i) => (
          <div key={k} style={{ padding: '8px 16px', borderRadius: 100, fontSize: 13,
            background: i === 0 ? p.text : p.surf, color: i === 0 ? p.bg : p.muted, fontWeight: i === 0 ? 600 : 400 }}>{k}</div>
        ))}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '18px 16px 16px' }}>
        {exp.map((c) => {
          const pct = c.spent / c.budget;
          return (
            <div key={c.id} style={{ margin: '0 8px 10px', padding: '16px 18px', background: p.surf, borderRadius: 18 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
                <div style={{ width: 36, height: 36, borderRadius: 18, background: c.color }} />
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 15, fontWeight: 500 }}>{c.name}</div>
                  <div style={{ fontSize: 11, color: p.faint, marginTop: 2 }}>{Math.round(pct * 100)}% от лимита</div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontFamily: p.serif, fontSize: 18 }}>{c.spent.toLocaleString('ru-RU')}</div>
                  <div style={{ fontSize: 11, color: p.faint, marginTop: 2 }}>из {c.budget.toLocaleString('ru-RU')}</div>
                </div>
              </div>
              {/* Bar */}
              <div style={{ marginTop: 12, height: 4, background: p.surf2, borderRadius: 2, overflow: 'hidden' }}>
                <div style={{ height: '100%', width: `${Math.min(pct, 1) * 100}%`, background: pct > 0.9 ? p.warm : c.color, borderRadius: 2 }} />
              </div>
            </div>
          );
        })}
      </div>

      <PNav active="set" />
    </div>
  );
};

// ═══════════════════════════════════════════════════════════
// SCREEN 6: STATISTICS — Calendar heatmap + flow sankey
// ═══════════════════════════════════════════════════════════
const PulseStats = () => {
  const p = usePulse();
  // Pulse stats focus on Day/Week/Month/Year toggles; current = Month
  return (
    <div style={{ background: p.bg, color: p.text, fontFamily: p.sans, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '24px 24px 0' }}>
        <div style={{ fontFamily: p.serif, fontSize: 32, fontStyle: 'italic' }}>Статистика</div>
        <div style={{ fontSize: 13, color: p.muted, marginTop: 6 }}>Май 2026 · взгляд сверху</div>
      </div>

      {/* Period selector */}
      <div style={{ display: 'flex', gap: 8, padding: '16px 24px 0', overflowX: 'auto' }}>
        {['День', 'Неделя', 'Месяц', 'Год'].map((k, i) => (
          <div key={k} style={{ padding: '8px 16px', borderRadius: 100, fontSize: 13, whiteSpace: 'nowrap',
            background: i === 2 ? p.text : p.surf, color: i === 2 ? p.bg : p.muted, fontWeight: i === 2 ? 600 : 400 }}>{k}</div>
        ))}
      </div>

      <div style={{ flex: 1, overflowY: 'auto', padding: '20px 24px 16px' }}>
        {/* Hero number */}
        <div style={{ padding: '8px 0 18px' }}>
          <div style={{ fontSize: 12, color: p.faint, letterSpacing: 1.4, textTransform: 'uppercase' }}>Потрачено в мае</div>
          <div style={{ fontFamily: p.serif, fontSize: 56, fontWeight: 300, letterSpacing: -2, marginTop: 8 }}>
            93 220<span style={{ color: p.faint, fontSize: 28, fontStyle: 'italic' }}> ₽</span>
          </div>
          <div style={{ fontFamily: p.serif, fontSize: 17, fontStyle: 'italic', color: p.muted, marginTop: 4 }}>
            на 14 270 ₽ <span style={{ color: p.lime }}>меньше</span>, чем в апреле
          </div>
        </div>

        {/* Calendar heatmap */}
        <div style={{ marginTop: 4 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
            <div style={{ fontSize: 12, color: p.faint, letterSpacing: 1.4, textTransform: 'uppercase' }}>Активность по дням</div>
            <div style={{ fontSize: 11, color: p.faint }}>12 нед</div>
          </div>
          <div style={{ display: 'flex', gap: 3, marginTop: 14, alignItems: 'flex-start' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 3, fontSize: 9, color: p.faint, paddingTop: 2 }}>
              {['ПН','ВТ','СР','ЧТ','ПТ','СБ','ВС'].map((d, i) => (
                <div key={d} style={{ height: 18, display: 'flex', alignItems: 'center', opacity: i % 2 ? 1 : 0 }}>{d}</div>
              ))}
            </div>
            <div style={{ flex: 1, display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', gap: 3 }}>
              {Array.from({ length: 12 }, (_, col) => (
                <div key={col} style={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                  {Array.from({ length: 7 }, (_, row) => {
                    const i = col * 7 + row;
                    const v = HEATMAP[i] || 0;
                    const op = v < 0.2 ? 0.06 : v < 0.4 ? 0.2 : v < 0.6 ? 0.4 : v < 0.8 ? 0.65 : 1;
                    return <div key={row} style={{ aspectRatio: '1/1', borderRadius: 3, background: v === 0 ? p.surf : p.lime, opacity: op }} />;
                  })}
                </div>
              ))}
            </div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 10, fontSize: 10, color: p.faint }}>
            <span>меньше</span>
            <div style={{ display: 'flex', gap: 3 }}>
              {[0.1, 0.3, 0.5, 0.75, 1].map((o) => <div key={o} style={{ width: 12, height: 12, borderRadius: 2, background: p.lime, opacity: o }} />)}
            </div>
            <span>больше</span>
          </div>
        </div>

        {/* Sankey-style flow */}
        <div style={{ marginTop: 28 }}>
          <div style={{ fontSize: 12, color: p.faint, letterSpacing: 1.4, textTransform: 'uppercase', marginBottom: 14 }}>Куда уходят деньги</div>
          <div style={{ position: 'relative', height: 220 }}>
            <svg viewBox="0 0 360 220" style={{ width: '100%', height: '100%' }}>
              {/* left big "income" bar */}
              <rect x="0" y="40" width="14" height="140" fill={p.text} />
              <text x="22" y="56" fill={p.text} fontFamily={p.sans} fontSize="11">Май</text>
              <text x="22" y="72" fill={p.muted} fontFamily={p.sans} fontSize="10">93 220 ₽</text>

              {/* flow lines into category nodes */}
              {[
                { y0: 40, y1: 6,   c: '#C5FF4A', label: 'Аренда', val: 45000, h: 60 },
                { y0: 100, y1: 70, c: '#FF9B6A', label: 'Кафе',    val: 9840,  h: 18 },
                { y0: 120, y1: 90, c: '#7BC8FF', label: 'Продукты',val: 18420, h: 30 },
                { y0: 150, y1: 110, c: '#9B8BFF', label: 'Подписки',val: 3120, h: 12 },
                { y0: 162, y1: 130, c: '#FF9DC4', label: 'Развлеч.', val: 6580, h: 18 },
                { y0: 180, y1: 158, c: '#52E0C4', label: 'Транспорт',val: 4310, h: 14 },
              ].map((f, i) => (
                <g key={i}>
                  <path d={`M14,${f.y0} C 160,${f.y0} 200,${f.y1 + f.h/2} 260,${f.y1 + f.h/2}`}
                    stroke={f.c} strokeWidth={f.h} fill="none" strokeLinecap="butt" opacity="0.6" />
                  <rect x="260" y={f.y1} width="14" height={f.h} fill={f.c} />
                  <text x="282" y={f.y1 + 11} fill={p.text} fontFamily={p.sans} fontSize="11">{f.label}</text>
                  <text x="282" y={f.y1 + 24} fill={p.muted} fontFamily={p.sans} fontSize="10">{f.val.toLocaleString('ru-RU')}</text>
                </g>
              ))}
            </svg>
          </div>
        </div>
      </div>

      <PNav active="stat" />
    </div>
  );
};

Object.assign(window, { PulseHome, PulseAdd, PulseDetail, PulseAccounts, PulseCats, PulseStats });
