# 🌤️ Projeto Previsão do Tempo

Um projeto Java simples que consulta a API **WeatherAPI** e exibe as informações meteorológicas atuais de qualquer cidade do mundo diretamente no terminal!

---

## 📋 O que o projeto faz?

Ao executar o programa, ele irá:

1. 🏙️ Pedir que você digite o nome de uma cidade
2. 🌐 Consultar a API [WeatherAPI](https://www.weatherapi.com/) em tempo real
3. 📊 Exibir as seguintes informações meteorológicas da cidade informada:
   - 🌡️ Temperatura atual (°C)
   - 🤔 Sensação térmica (°C)
   - 🌦️ Condição do tempo (ex: Sunny, Rainy...)
   - 💧 Umidade (%)
   - 💨 Velocidade do vento (km/h)
   - 🔵 Pressão atmosférica (mb)

---

## 🛠️ O que foi implementado?

- ✅ Consulta HTTP à API WeatherAPI usando `HttpClient` nativo do Java
- ✅ Leitura e parse de resposta em **JSON** com a biblioteca `org.json`
- ✅ Tratamento de erros da API com mensagens amigáveis em português
- ✅ Tradução das 49 condições do tempo para o português do Brasil
- ✅ Formatação de data e hora para o padrão brasileiro (`dd/MM/yyyy HH:mm`)
- ✅ Suporte a nomes de cidades com espaços e caracteres especiais (URL encoding)
- ✅ Leitura da chave da API a partir de um arquivo externo (`APIKEY.txt`)
- ✅ **Versão terminal** — exibe os dados diretamente no console
- ✅ **Versão gráfica (JavaFX)** — janela com campo de busca, ícone do tempo e cards com os dados

---

## ✅ Pré-requisitos

Antes de rodar o projeto, você precisa ter instalado:

### ☕ 1. Java JDK 11 ou superior

> O projeto utiliza o `HttpClient`, disponível a partir do Java 11.

**Como verificar se já tem instalado:**
```bash
java -version
```

Se aparecer algo como `java version "17.x.x"` ou superior, você já está pronto! ✅

**Se não tiver instalado**, siga os passos abaixo:

**No Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
```

**No Windows:**
1. Acesse: https://adoptium.net/
2. Clique em **Latest LTS Release** e baixe o instalador
3. Execute o instalador e siga as instruções

**No macOS (com Homebrew):**
```bash
brew install openjdk@17
```

---

### 🖥️ 2. JavaFX SDK (apenas para a versão gráfica)

> Necessário somente se quiser rodar a versão com interface gráfica (`ProjetoPrevisaoDoTempoFX`).

**No Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjfx -y
```

**No Windows e macOS:**
1. Acesse: https://gluonhq.com/products/javafx/
2. Baixe o **JavaFX SDK** compatível com seu sistema operacional
3. Extraia o arquivo em uma pasta de sua preferência (ex: `C:\javafx-sdk` ou `/opt/javafx-sdk`)

> 📝 Anote o caminho da pasta `lib` do SDK — você vai precisar dele nos comandos de compilação e execução.

---

### 🔑 3. Chave de API da WeatherAPI (gratuita)

O projeto precisa de uma chave de acesso à **WeatherAPI**. Siga o passo a passo abaixo:

**Passo 1 —** Acesse o site: https://www.weatherapi.com/

**Passo 2 —** Clique em **Sign Up** no canto superior direito e crie sua conta gratuita
> 📧 Basta informar nome, e-mail e senha. Não precisa de cartão de crédito!

**Passo 3 —** Após criar a conta, você receberá um e-mail de confirmação. Clique no link para ativar a conta e faça o login.

**Passo 4 —** Após o login, você será redirecionado para o **Dashboard**. A sua API Key já estará visível na página inicial, em um campo chamado **"API Key"**.

> 🔍 Se não encontrar, acesse diretamente: https://www.weatherapi.com/my/

**Passo 5 —** Copie a chave (ela será algo parecido com: `a1b2c3d4e5f6g7h8i9j0...`)

**Passo 6 —** Dentro da pasta do projeto, crie o arquivo `src/APIKEY.txt` e cole a chave dentro dele:

```bash
# No terminal, dentro da pasta raiz do projeto:
echo "SUA_CHAVE_AQUI" > src/APIKEY.txt
```

> Ou crie o arquivo manualmente com qualquer editor de texto, cole a chave e salve.

O arquivo deve ficar assim:
```
a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
```

> ⚠️ **Atenção:** O arquivo `src/APIKEY.txt` já está no `.gitignore` do projeto, então ele **nunca será enviado ao GitHub** — sua chave ficará segura! 🔒

---

## 🚀 Como rodar o projeto

### 📁 Passo 1 – Clone ou baixe o repositório

```bash
git clone https://github.com/seu-usuario/projeto_previsao_do_tempo.git
cd projeto_previsao_do_tempo
```

> Ou baixe o ZIP pelo GitHub e extraia na sua máquina.

---

### 🔑 Passo 2 – Adicione sua chave de API

Abra o arquivo `src/APIKEY.txt` com qualquer editor de texto e cole sua chave:

```bash
# Exemplo usando o terminal:
echo "SUA_CHAVE_AQUI" > src/APIKEY.txt
```

---

### 🔨 Passo 3 – Compile o projeto (versão terminal)

No terminal, dentro da pasta raiz do projeto, execute:

```bash
javac -cp src/json-20230618.jar -d out src/ProjetoPrevisaoDoTempo.java
```

> 📝 Esse comando compila o arquivo `.java` e coloca os arquivos `.class` na pasta `out/`.

---

### ▶️ Passo 4 – Execute o projeto (versão terminal)

```bash
java -cp out:src/json-20230618.jar ProjetoPrevisaoDoTempo
```

> 💡 **No Windows**, substitua `:` por `;` no classpath:
> ```bash
> java -cp out;src/json-20230618.jar ProjetoPrevisaoDoTempo
> ```

---

### 💬 Passo 5 – Use o programa!

Ao executar, o programa pedirá o nome de uma cidade:

```
Digite o nome da cidade: São Paulo
```

E exibirá algo como:

```
Informações Meteorológicas para São Paulo/Sao Paulo, no Brazil
Data e Hora: 27/04/2026 14:30
Temperatura Atual: 24.0 °C
Sensação Térmica: 25.2 °C
Condição do Tempo: Parcialmente nublado
Umidade: 68%
Velocidade do Vendo: 15.1 km/h
Pressão Atmosférica: 1013.0 mb
```

---

## 🖼️ Versão Gráfica (JavaFX)

### 🔨 Passo 3 – Compile o projeto (versão JavaFX)

> Substitua `/caminho/para/javafx-sdk/lib` pelo caminho real do JavaFX SDK na sua máquina.
> Exemplo no Linux: `/usr/share/openjfx/lib` ou `/usr/lib/jvm/javafx-sdk-22/lib`

**Linux/macOS:**
```bash
javac \
  --module-path /caminho/para/javafx-sdk/lib \
  --add-modules javafx.controls,javafx.graphics \
  -cp src/json-20230618.jar \
  -d out \
  src/ProjetoComJavaFX/ProjetoPrevisaoDoTempoFX.java
```

**Windows:**
```bash
javac --module-path C:\caminho\para\javafx-sdk\lib --add-modules javafx.controls,javafx.graphics -cp src/json-20230618.jar -d out src/ProjetoComJavaFX/ProjetoPrevisaoDoTempoFX.java
```

---

### ▶️ Passo 4 – Execute o projeto (versão JavaFX)

**Linux/macOS:**
```bash
java \
  --module-path /caminho/para/javafx-sdk/lib \
  --add-modules javafx.controls,javafx.graphics \
  -cp out:src/json-20230618.jar \
  ProjetoComJavaFX.ProjetoPrevisaoDoTempoFX
```

**Windows:**
```bash
java --module-path C:\caminho\para\javafx-sdk\lib --add-modules javafx.controls,javafx.graphics -cp out;src/json-20230618.jar ProjetoComJavaFX.ProjetoPrevisaoDoTempoFX
```

---

### 💬 Passo 5 – Use o programa!

Uma janela gráfica será aberta. Basta digitar o nome da cidade no campo e clicar em **Buscar**. A interface exibirá:

- Nome da cidade, estado e país
- Data e hora local
- Ícone do tempo (carregado direto da WeatherAPI)
- Temperatura em destaque
- Condição do tempo em português
- Cards com: sensação térmica, umidade, velocidade do vento e pressão atmosférica

---

## 📂 Estrutura do Projeto

```
projeto_previsao_do_tempo/
│
├── src/
│   ├── ProjetoPrevisaoDoTempo.java        # Versão terminal
│   ├── json-20230618.jar                  # Biblioteca para manipulação de JSON
│   ├── APIKEY.txt                         # Sua chave da WeatherAPI (não versionar!)
│   └── ProjetoComJavaFX/
│       └── ProjetoPrevisaoDoTempoFX.java  # Versão gráfica com JavaFX
│
└── README.md
```

---

## 🐛 Possíveis erros e soluções

| Erro | Solução |
|------|---------|
| `Chave da API não foi informada` | Verifique se o arquivo `src/APIKEY.txt` existe e contém a chave |
| `Nenhuma localização foi encontrada` | Verifique se o nome da cidade está correto |
| `A chave da API informada é inválida` | Confirme que copiou a chave corretamente do painel da WeatherAPI |
| `java: command not found` | Instale o Java JDK conforme indicado nos pré-requisitos |
| `package javafx.application does not exist` | Instale o JavaFX SDK e use o `--module-path` nos comandos |
| Janela não abre (JavaFX) | Verifique se o caminho do `--module-path` está correto |

---

## 📌 Observações

- 🌍 O projeto suporta cidades de qualquer país
- 🆓 O plano gratuito da WeatherAPI permite até **1 milhão de chamadas por mês**
- 🔒 O arquivo `src/APIKEY.txt` está no `.gitignore` — sua chave **nunca será enviada ao GitHub**!

---

## 📚 Tecnologias utilizadas

- ☕ **Java 11+** — linguagem principal
- 🖥️ **JavaFX** — interface gráfica (versão FX)
- 🌐 **WeatherAPI** — fonte dos dados meteorológicos
- 📦 **org.json (json-20230618.jar)** — parse de JSON

---

Feito com ☕ e ❤️ em Java!
