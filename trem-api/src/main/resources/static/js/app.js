/* Amplia - front-end da API de trens (JS puro, sem build).
   Cada recurso e descrito em RECURSOS; a tela de lista e o formulario saem dessa configuracao. */
(() => {
  'use strict';

  const API = (window.API_BASE || '').replace(/\/$/, '');

  const SIT_ATIVA = [['A', 'Ativa'], ['I', 'Inativa']];
  const MASCULINOS = new Set(['passageiros', 'tipos-modais', 'trens', 'bilhetes']);
  const REFS_TEXTO = new Set(['tipos-modais', 'situacoes-trem']);
  const SIM_NAO = [['S', 'Sim'], ['N', 'Não']];

  // Grupos = "linhas" do mapa. cor: variavel CSS.
  const GRUPOS = [
    { nome: 'Cadastros base', cor: 'var(--blue)', itens: ['operadoras', 'estacoes', 'passageiros', 'tipos-modais', 'situacoes-trem'] },
    { nome: 'Operação', cor: 'var(--green)', itens: ['linhas', 'linhas-estacoes', 'trens', 'viagens'] },
    { nome: 'Vendas', cor: 'var(--red)', itens: ['bilhetes'] },
  ];

  const R = {
    'operadoras': {
      titulo: 'Operadoras', singular: 'operadora', ajuda: 'Empresas que operam as linhas.', chave: 'codigo',
      campos: [
        { k: 'nome', l: 'Nome', t: 'text', req: true, max: 100, cheio: true },
        { k: 'cnpj', l: 'CNPJ', t: 'text', max: 14, dica: '14 dígitos, sem pontuação', fmt: v => v },
      ],
    },
    'estacoes': {
      titulo: 'Estações', singular: 'estação', ajuda: 'Estações da malha e seu estado atual.', chave: 'codigo',
      campos: [
        { k: 'nome', l: 'Nome', t: 'text', req: true, max: 100, cheio: true },
        { k: 'endereco', l: 'Endereço', t: 'text', max: 200, cheio: true },
        { k: 'acessibilidade', l: 'Acessível', t: 'enum', req: true, op: SIM_NAO },
        { k: 'situacao', l: 'Situação', t: 'enum', req: true, op: SIT_ATIVA },
      ],
    },
    'passageiros': {
      titulo: 'Passageiros', singular: 'passageiro', ajuda: 'Pessoas cadastradas para comprar bilhetes.', chave: 'codigo',
      campos: [
        { k: 'nome', l: 'Nome completo', t: 'text', req: true, max: 100, cheio: true },
        { k: 'cpf', l: 'CPF', t: 'text', req: true, max: 11, dica: '11 dígitos, sem pontuação' },
        { k: 'dataNascimento', l: 'Nascimento', t: 'date' },
        { k: 'email', l: 'E-mail', t: 'email', max: 100, cheio: true },
      ],
    },
    'tipos-modais': {
      titulo: 'Tipos de modal', singular: 'tipo de modal', ajuda: 'Metrô, trem metropolitano, VLT e outros.', chave: 'codigo', chaveTexto: true,
      campos: [
        { k: 'codigo', l: 'Sigla', t: 'text', req: true, max: 1, dica: '1 letra', soCriar: true },
        { k: 'nome', l: 'Nome', t: 'text', req: true, max: 50 },
        { k: 'descricao', l: 'Descrição', t: 'text', max: 200, cheio: true },
      ],
    },
    'situacoes-trem': {
      titulo: 'Situações do trem', singular: 'situação do trem', ajuda: 'Estados operacionais possíveis de um trem.', chave: 'codigo', chaveTexto: true,
      campos: [
        { k: 'codigo', l: 'Sigla', t: 'text', req: true, max: 1, dica: '1 letra', soCriar: true },
        { k: 'nome', l: 'Nome', t: 'text', req: true, max: 50 },
        { k: 'descricao', l: 'Descrição', t: 'text', max: 200, cheio: true },
      ],
    },
    'linhas': {
      titulo: 'Linhas', singular: 'linha', ajuda: 'Linhas com seu modal e a operadora responsável.', chave: 'codigo',
      campos: [
        { k: 'nome', l: 'Nome', t: 'text', req: true, max: 100, cheio: true },
        { k: 'cor', l: 'Cor', t: 'text', max: 30, dica: 'Ex.: Vermelha' },
        { k: 'situacao', l: 'Situação', t: 'enum', req: true, op: SIT_ATIVA },
        { k: 'tipoModal', l: 'Modal', t: 'ref', req: true, ref: 'tipos-modais', val: 'codigo', rot: x => x.nome },
        { k: 'codigoOperadora', l: 'Operadora', t: 'ref', ref: 'operadoras', val: 'codigo', rot: x => x.nome },
      ],
    },
    'linhas-estacoes': {
      titulo: 'Linhas e estações', singular: 'parada', ajuda: 'Quais estações cada linha atende e em que ordem.', chave: 'codigo',
      campos: [
        { k: 'codigoLinha', l: 'Linha', t: 'ref', req: true, ref: 'linhas', val: 'codigo', rot: x => x.nome },
        { k: 'codigoEstacao', l: 'Estação', t: 'ref', req: true, ref: 'estacoes', val: 'codigo', rot: x => x.nome },
        { k: 'ordem', l: 'Ordem no percurso', t: 'number', req: true, min: 1 },
      ],
    },
    'trens': {
      titulo: 'Trens', singular: 'trem', ajuda: 'Frota, capacidade e linha de cada trem.', chave: 'codigo',
      campos: [
        { k: 'modelo', l: 'Modelo', t: 'text', max: 50 },
        { k: 'capacidade', l: 'Capacidade (passageiros)', t: 'number', req: true, min: 1 },
        { k: 'situacao', l: 'Situação', t: 'ref', req: true, ref: 'situacoes-trem', val: 'codigo', rot: x => x.nome },
        { k: 'codigoLinha', l: 'Linha', t: 'ref', req: true, ref: 'linhas', val: 'codigo', rot: x => x.nome },
      ],
    },
    'viagens': {
      titulo: 'Viagens', singular: 'viagem', ajuda: 'Partidas e chegadas de cada trem.', chave: 'codigo',
      campos: [
        { k: 'codigoTrem', l: 'Trem', t: 'ref', req: true, ref: 'trens', val: 'codigo', rot: x => `Trem ${x.codigo}${x.modelo ? ' · ' + x.modelo : ''}` },
        { k: 'situacao', l: 'Situação', t: 'enum', req: true, op: [['A', 'Ativa'], ['F', 'Finalizada'], ['C', 'Cancelada']] },
        { k: 'dataHoraPartida', l: 'Partida', t: 'datetime', req: true },
        { k: 'dataHoraChegada', l: 'Chegada', t: 'datetime' },
      ],
    },
    'bilhetes': {
      titulo: 'Bilhetes', singular: 'bilhete', ajuda: 'Bilhetes vendidos por viagem, com origem e destino.', chave: 'codigo',
      campos: [
        { k: 'codigoPassageiro', l: 'Passageiro', t: 'ref', req: true, ref: 'passageiros', val: 'codigo', rot: x => x.nome, cheio: true },
        { k: 'codigoViagem', l: 'Viagem', t: 'ref', req: true, ref: 'viagens', val: 'codigo', rot: x => `Viagem ${x.codigo} · ${fmtDataHora(x.dataHoraPartida)}`, cheio: true },
        { k: 'codigoEstacaoOrigem', l: 'Origem', t: 'ref', req: true, ref: 'estacoes', val: 'codigo', rot: x => x.nome },
        { k: 'codigoEstacaoDestino', l: 'Destino', t: 'ref', req: true, ref: 'estacoes', val: 'codigo', rot: x => x.nome },
        { k: 'valor', l: 'Valor (R$)', t: 'decimal', req: true },
        { k: 'dataCompra', l: 'Data da compra', t: 'datetime', req: true },
        { k: 'situacao', l: 'Situação', t: 'enum', req: true, op: [['A', 'Ativo'], ['U', 'Utilizado']] },
      ],
    },
  };

  // ---------- utilidades ----------
  const $ = (s, el = document) => el.querySelector(s);
  const el = (tag, attrs = {}, ...filhos) => {
    const n = document.createElement(tag);
    for (const [k, v] of Object.entries(attrs)) {
      if (v === false || v == null) continue;
      if (k === 'class') n.className = v;
      else if (k.startsWith('on')) n.addEventListener(k.slice(2), v);
      else n.setAttribute(k, v === true ? '' : v);
    }
    for (const f of filhos.flat()) n.append(f instanceof Node ? f : document.createTextNode(f ?? ''));
    return n;
  };

  function fmtDataHora(v) {
    if (!v) return '—';
    const d = new Date(v);
    return isNaN(d) ? v : d.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
  }
  const fmtData = v => (v ? v.split('-').reverse().join('/') : '—');
  const fmtMoeda = v => (v == null ? '—' : Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }));

  let toastTimer;
  function toast(msg, erro = false) {
    const t = $('#toast');
    t.textContent = msg;
    t.className = 'show' + (erro ? ' erro' : '');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => (t.className = ''), 4200);
  }

  async function chamar(metodo, caminho, corpo) {
    let resp;
    try {
      resp = await fetch(API + '/' + caminho, {
        method: metodo,
        headers: corpo ? { 'Content-Type': 'application/json' } : undefined,
        body: corpo ? JSON.stringify(corpo) : undefined,
      });
    } catch {
      throw { mensagem: 'Não foi possível falar com a API. Confira se o servidor está no ar.', detalhes: [] };
    }
    if (resp.status === 204) return null;
    const dados = await resp.json().catch(() => null);
    if (!resp.ok) throw { mensagem: dados?.mensagem || `Erro ${resp.status}`, detalhes: dados?.detalhes || [], status: resp.status };
    return dados;
  }

  const cache = {};
  async function carregar(recurso, forcar = false) {
    if (!forcar && cache[recurso]) return cache[recurso];
    return (cache[recurso] = await chamar('GET', recurso));
  }
  const invalidar = recurso => delete cache[recurso];

  // ---------- menu ----------
  function montarMenu() {
    const menu = $('#menu');
    for (const g of GRUPOS) {
      menu.append(el('section', { class: 'grupo', style: `--cor:${g.cor}` },
        el('h3', {}, g.nome),
        el('ul', {}, g.itens.map(id => el('li', {}, el('a', { href: `#/${id}`, 'data-id': id }, R[id].titulo))))));
    }
  }
  function marcarMenu(id) {
    document.querySelectorAll('#menu a').forEach(a => {
      if (a.dataset.id === id) a.setAttribute('aria-current', 'page');
      else a.removeAttribute('aria-current');
    });
  }

  // ---------- home ----------
  async function home() {
    marcarMenu(null);
    const main = $('#conteudo');
    main.replaceChildren(
      el('div', { class: 'faixa' }),
      el('div', { class: 'topo' }, el('div', {},
        el('h1', {}, 'Amplia'),
        el('p', { class: 'sub' }, 'Cadastre e consulte operadoras, estações, linhas, trens, viagens e bilhetes. Escolha uma estação do mapa para começar.')),
        el('span', { class: 'status', id: 'status' }, 'Conectando à API…')),
      el('div', { class: 'mapa', id: 'mapa' }));

    const contagens = {};
    const mapa = $('#mapa');
    for (const g of GRUPOS) {
      mapa.append(el('section', { class: 'linha-mapa', style: `--cor:${g.cor}` },
        el('h2', {}, g.nome),
        el('ul', { class: 'estacoes' }, g.itens.map(id =>
          el('li', {}, el('a', { href: `#/${id}` },
            el('span', { class: 'n', 'data-n': id }, '–'),
            el('strong', {}, R[id].titulo)))))));
    }
    let ok = 0, falhas = 0;
    await Promise.all(Object.keys(R).map(async id => {
      try {
        const lista = await carregar(id, true);
        contagens[id] = lista.length; ok++;
        const n = $(`[data-n="${id}"]`);
        if (n) n.textContent = lista.length;
      } catch { falhas++; }
    }));
    const st = $('#status');
    if (!st) return;
    if (falhas === 0) { st.className = 'status ok'; st.textContent = 'API conectada ao banco'; }
    else { st.className = 'status erro'; st.textContent = ok ? 'Algumas tabelas não responderam' : 'Sem conexão com a API ou o banco'; }
  }

  // ---------- lista ----------
  function rotuloRef(campo, valor) {
    const lista = cache[campo.ref] || [];
    const item = lista.find(x => String(x[campo.val]) === String(valor));
    return item ? campo.rot(item) : valor ?? '—';
  }

  function celula(campo, reg) {
    const v = reg[campo.k];
    if (v == null || v === '') return el('td', {}, '—');
    switch (campo.t) {
      case 'ref': return el('td', {}, rotuloRef(campo, v));
      case 'enum': {
        const par = campo.op.find(o => o[0] === v);
        const cor = { A: 'var(--green)', S: 'var(--green)', I: 'var(--ink-2)', N: 'var(--ink-2)', F: 'var(--blue)', C: 'var(--red)', U: 'var(--blue)' }[v];
        return el('td', {}, el('span', { class: 'chip', style: `--c:${cor}` }, par ? par[1] : v));
      }
      case 'date': return el('td', { class: 'num' }, fmtData(v));
      case 'datetime': return el('td', { class: 'num' }, fmtDataHora(v));
      case 'decimal': return el('td', { class: 'num' }, fmtMoeda(v));
      case 'number': return el('td', { class: 'num' }, v);
      default: return el('td', {}, v);
    }
  }

  async function lista(id) {
    const cfg = R[id];
    marcarMenu(id);
    const grupo = GRUPOS.find(g => g.itens.includes(id));
    const main = $('#conteudo');
    main.replaceChildren(
      el('div', { class: 'faixa', style: `--cor:${grupo.cor}` }),
      el('div', { class: 'topo' },
        el('div', {}, el('h1', {}, cfg.titulo), el('p', { class: 'sub' }, cfg.ajuda)),
        el('button', { class: 'primary', onclick: () => abrirForm(id) }, `${MASCULINOS.has(id) ? 'Novo' : 'Nova'} ${cfg.singular}`)),
      el('div', { id: 'corpo' }, el('p', { class: 'sub' }, 'Carregando…')));

    try {
      const refs = [...new Set(cfg.campos.filter(c => c.t === 'ref').map(c => c.ref))];
      const [dados] = await Promise.all([carregar(id, true), ...refs.map(r => carregar(r).catch(() => []))]);
      if (location.hash.slice(2) !== id) return;
      desenharTabela(id, dados);
    } catch (e) {
      if (location.hash.slice(2) !== id) return;
      $('#corpo').replaceChildren(el('div', { class: 'falha' },
        el('h2', {}, 'Não foi possível carregar os dados'),
        el('p', {}, e.mensagem + ' Se o banco Oracle ainda não estiver configurado, defina DB_URL, DB_USER e DB_PASSWORD no servidor.'),
        el('button', { class: 'ghost', onclick: () => lista(id) }, 'Tentar de novo')));
    }
  }

  function desenharTabela(id, dados) {
    const cfg = R[id];
    const corpo = $('#corpo');
    if (!dados.length) {
      corpo.replaceChildren(el('div', { class: 'vazio' },
        el('h2', {}, `Nenhuma ${cfg.singular} cadastrada`),
        el('p', {}, 'Cadastre o primeiro registro para ele aparecer aqui.'),
        el('button', { class: 'primary', onclick: () => abrirForm(id) }, 'Cadastrar agora')));
      return;
    }
    const colunas = cfg.campos;
    const tabela = el('table', {},
      el('thead', {}, el('tr', {},
        el('th', {}, 'Código'),
        colunas.filter(c => c.k !== 'codigo').map(c => el('th', {}, c.l)),
        el('th', { class: 'acoes' }))),
      el('tbody', {}, dados.map(reg => el('tr', {},
        el('td', { class: 'id' }, reg.codigo),
        colunas.filter(c => c.k !== 'codigo').map(c => celula(c, reg)),
        el('td', { class: 'acoes' },
          el('button', { onclick: () => abrirForm(id, reg), 'aria-label': `Editar ${cfg.singular} ${reg.codigo}` }, 'Editar'),
          el('button', { class: 'del', onclick: () => excluir(id, reg), 'aria-label': `Excluir ${cfg.singular} ${reg.codigo}` }, 'Excluir'))))));
    corpo.replaceChildren(el('div', { class: 'tabela-wrap' }, tabela));
  }

  // ---------- formulario ----------
  const dlg = () => $('#formDialog');
  let editando = null; // { id, reg }

  async function abrirForm(id, reg = null) {
    const cfg = R[id];
    editando = { id, reg };
    $('#formTitulo').textContent = reg ? `Editar ${cfg.singular}` : `Cadastrar ${cfg.singular}`;
    $('#formErros').hidden = true;
    const campos = $('#campos');
    campos.replaceChildren();

    for (const c of cfg.campos) {
      const nomeId = 'f_' + c.k;
      let input;
      const valor = reg ? reg[c.k] : null;
      if (c.t === 'enum') {
        input = el('select', { id: nomeId, name: c.k }, el('option', { value: '' }, 'Selecione'),
          c.op.map(([v, r]) => el('option', { value: v }, r)));
        input.value = valor ?? '';
      } else if (c.t === 'ref') {
        let itens = cache[c.ref];
        if (!itens) { try { itens = await carregar(c.ref); } catch { itens = []; } }
        input = el('select', { id: nomeId, name: c.k },
          el('option', { value: '' }, c.req ? 'Selecione' : 'Nenhuma'),
          itens.map(x => el('option', { value: x[c.val] }, c.rot(x))));
        input.value = valor ?? '';
      } else {
        const tipoHtml = { text: 'text', email: 'email', date: 'date', datetime: 'datetime-local', number: 'number', decimal: 'number' }[c.t];
        input = el('input', {
          id: nomeId, name: c.k, type: tipoHtml, maxlength: c.max, min: c.min,
          step: c.t === 'decimal' ? '0.01' : c.t === 'number' ? '1' : false,
          autocomplete: 'off', readonly: c.soCriar && !!reg,
        });
        if (valor != null) input.value = c.t === 'datetime' ? String(valor).slice(0, 16) : valor;
        if (c.max === 1) input.style.textTransform = 'uppercase';
      }
      campos.append(el('div', { class: 'campo' + (c.cheio ? ' cheio' : '') },
        el('label', { for: nomeId }, c.l, c.req ? el('i', { 'aria-hidden': 'true' }, '*') : ''),
        input, c.dica ? el('small', {}, c.dica) : ''));
    }
    dlg().showModal();
    const primeiro = campos.querySelector('input:not([readonly]), select');
    primeiro && primeiro.focus();
  }

  function lerForm(cfg) {
    const corpo = {};
    for (const c of cfg.campos) {
      const bruto = $('#f_' + c.k).value.trim();
      if (bruto === '') { corpo[c.k] = null; continue; }
      if (c.t === 'number' || (c.t === 'ref' && !REFS_TEXTO.has(c.ref))) corpo[c.k] = Number(bruto);
      else if (c.t === 'decimal') corpo[c.k] = Number(bruto);
      else if (c.t === 'datetime') corpo[c.k] = bruto.length === 16 ? bruto + ':00' : bruto;
      else corpo[c.k] = bruto;
    }
    return corpo;
  }

  async function salvar(ev) {
    ev.preventDefault();
    const { id, reg } = editando;
    const cfg = R[id];
    const btn = $('#salvar');
    btn.disabled = true;
    try {
      const corpo = lerForm(cfg);
      if (reg) await chamar('PUT', `${id}/${encodeURIComponent(reg.codigo)}`, corpo);
      else await chamar('POST', id, corpo);
      invalidar(id);
      dlg().close();
      toast(reg ? 'Alterações salvas' : 'Cadastro salvo');
      lista(id);
    } catch (e) {
      const ul = $('#formErros');
      const msgs = e.detalhes?.length ? e.detalhes : [e.mensagem];
      ul.replaceChildren(...msgs.map(m => el('li', {}, m)));
      ul.hidden = false;
    } finally {
      btn.disabled = false;
    }
  }

  async function excluir(id, reg) {
    const cfg = R[id];
    const d = $('#confirmDialog');
    $('#confirmTexto').textContent = `Excluir ${cfg.singular} ${reg.codigo}? Essa ação não pode ser desfeita.`;
    d.returnValue = '';
    d.showModal();
    d.addEventListener('close', async function onClose() {
      d.removeEventListener('close', onClose);
      if (d.returnValue !== 'ok') return;
      try {
        await chamar('DELETE', `${id}/${encodeURIComponent(reg.codigo)}`);
        invalidar(id);
        toast('Registro excluído');
        lista(id);
      } catch (e) {
        toast(e.mensagem, true);
      }
    });
  }

  // ---------- rotas ----------
  function rota() {
    const id = location.hash.replace(/^#\//, '');
    $('#conteudo').focus({ preventScroll: true });
    if (R[id]) lista(id); else home();
  }

  document.addEventListener('click', e => {
    if (e.target.closest('[data-fechar]')) dlg().close();
  });
  $('#form').addEventListener('submit', salvar);
  montarMenu();
  window.addEventListener('hashchange', rota);
  rota();
})();
