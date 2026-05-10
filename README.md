<div align="center">

# 🏠 𝑫𝒐𝒎𝒖𝒔𝑪𝒐𝒏𝒕𝒓𝒐𝒍

**Programação Orientada aos Objetos · Universidade do Minho · 2025/2026**

![Language](https://img.shields.io/badge/linguagem-Java-red?style=flat-square)
![Pattern](https://img.shields.io/badge/padrão-MVC-blue?style=flat-square)
![Interface](https://img.shields.io/badge/interface-CLI-green?style=flat-square)
![Persistence](https://img.shields.io/badge/persistência-Serializable-purple?style=flat-square)

*Sistema de automação doméstica com gestão de dispositivos, automações, escalonamentos, cenários e sugestões automáticas baseadas no histórico de interações.*

</div>

---
<div align="center">

## 👥 Grupo 99

| Nome | Número |
|---|---|
| Ana Patrícia da Silva Machado | a111661 |
| Carolina Francisca Vieira Dias | a111179 |

</div>

---

## ⚙️ Compilar

```bash
javac -d bin -sourcepath src src/Main.java
```

---

## 🚀 Utilização

**1. Arrancar a aplicação**
```bash
java -cp bin Main
```

O programa inicia com um estado de teste populado automaticamente.

**2. Iniciar sessão (estado de teste)**
- Administradora: `u1` / `1234` (Ana Silva)
- Regular: `u2` / `5678` (Bruno Costa)

**3. Carregar estado de apresentação**
No menu principal, opção **9 - Carregar estado**, indicar: `estado_apresentacao.bin`

---

## 📁 Estrutura

```
.
├── src/
│   ├── Main.java
│   ├── controller/                   # DomusController, lógica central
│   ├── exceptions/                   # exceções personalizadas
│   ├── model/
│   │   ├── devices/                  # hierarquia de dispositivos
│   │   ├── house/                    # casas e divisões
│   │   ├── users/                    # utilizadores e papéis
│   │   └── automations/              # automações, escalonamentos, cenários, sugestões
│   ├── utils/                        # relógio simulado, motor de sugestões, estado de teste
│   └── view/                         # menus em modo texto
├── docs/
│   ├── Relatorio_DomusControl_G099.pdf   # relatório do projeto
│   └── DomusControl_UML_G99.pdf          # diagrama UML completo
├── estado_apresentacao.bin           # estado para a defesa
└── README.md
```

---

---

## 📄 Documentação

O relatório completo encontra-se em [`docs/Relatorio_DomusControl_G099.pdf`](docs/Relatorio_DomusControl_G099.pdf). O diagrama UML em alta resolução está em [`docs/DomusControl_UML_G99.pdf`](docs/DomusControl_UML_G99.pdf).

---

<div align="center">

*Desenvolvido para a UC de Programação Orientada aos Objetos — LEI, Universidade do Minho · Grupo 99 · 2025/2026*

</div>
