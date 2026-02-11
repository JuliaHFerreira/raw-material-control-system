# Raw Material Control System

Sistema/API para **controle de matéria-prima e manufatura**, com **cadastro de produtos**, **cadastro de matérias-primas**, **estrutura (BOM)** e **controle de estoque** — permitindo avaliar o que pode ser produzido com o estoque disponível.


## 🔗 Repositórios

- Back-end: https://github.com/JuliaHFerreira/raw-material-control-system
- Front-end: https://github.com/JuliaHFerreira/raw-material-front-end


---

## 🧰 Tecnologias

- Java 21  
- Spring Boot  
- REST  
- Swagger / OpenAPI  
- Oracle  
- JPA/Hibernate  
- Flyway  
- JUnit  

---

## 🐳 Subindo o Oracle no Docker (Windows)

### 1) Pull e tag da imagem
```bash
docker pull gvenzl/oracle-free:latest
docker tag gvenzl/oracle-free:latest oracle:latest
```

### 2) Remover container antigo (se existir)
```bash
docker rm -f oracle
```

### 3) Subir o container
```bash
docker run -d ^
  --name oracle ^
  -p 1521:1521 ^
  -p 5500:5500 ^
  -e ORACLE_PASSWORD=Oracle#123456 ^
  oracle:latest
```

---

## 👤 Criar usuário no banco (Oracle)

Execute no Oracle (ex.: SQL Developer):

```sql
CREATE USER SYSTEM_MATERIAL IDENTIFIED BY "MATERIAL#123456";
GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE SEQUENCE, CREATE PROCEDURE TO SYSTEM_MATERIAL;
ALTER USER SYSTEM_MATERIAL QUOTA UNLIMITED ON USERS;
```

---

## ▶️ Rodando o projeto

### Pré-requisitos
- Java instalado
- Oracle rodando via Docker (passo acima)

### Subir a aplicação
```bash
./mvnw spring-boot:run
```

No Windows:
```bash
mvnw.cmd spring-boot:run
```

---

## 📚 Documentação da API (Swagger)

Após subir a aplicação, acesse:

- http://localhost:8080/swagger-ui/index.html


## 📌 Endpoints

### 🧾 Cadastro de Produto

**POST -> `/product/new`** (Cria um novo produto)  
Body:
```json
{
  "code": "CODE",
  "description": "DESCRIÇÃO MAX 90 CARACTERE",
  "typeProduct": "FP",
  "barcode": "CODIGO DE BARRAS EAN 13",
  "price": 0.00
}
```

**GET -> `/product`** (TRAS TODOS OS PRODUTOS)

**GET -> `/product/{CODE}`** (TRAS O PRODUTO PELO CODIGO)

**PUT -> `/product/edit/{id}`** (EDITA UM PRODUTO PELO ID)  
Body:
```json
{
  "code": "CODE",
  "description": "DESCRIÇÃO MAX 90 CARACTERE",
  "typeProduct": "FP",
  "barcode": "CODIGO DE BARRAS EAN 13",
  "price": 0.00
}
```

**DELETE -> `/product/{id}`** (DELETA UM PRODUTO PELO ID)

---

### 🧱 Cadastro de Matéria-Prima

**POST -> `/rawmaterial/new`** (Cria uma nova matéria prima)  
Body:
```json
{
  "code": "CODE",
  "description": "DESCRIÇÃO MAX 90 CARACTERE",
  "typeProduct": "RAW",
  "barcode": "CODIGO DE BARRAS EAN 13",
  "cost": 0.00
}
```

**GET -> `/rawmaterial`** (TRAS TODOS AS MATERIAS PRIMAS)

**GET -> `/rawmaterial/{CODE}`** (TRAS A MATERIA PRIMA PELO CODIGO)

**PUT -> `/rawmaterial/edit/{id}`** (EDITA UMA MATERIA PRIMA PELO ID)  
Body:
```json
{
  "code": "CODE",
  "description": "DESCRIÇÃO MAX 90 CARACTERE",
  "typeProduct": "FP",
  "barcode": "CODIGO DE BARRAS EAN 13",
  "cost": 0.00
}
```

**DELETE -> `/rawmaterial/{id}`** (DELETA UM PRODUTO PELO ID)

---

### 🧩 Estrutura do Produto (BOM)

**POST -> `/structure/new`** (Cria uma nova ESTRUTURA)  
Body:
```json
{
  "productCode": "CODE PRODUTO",
  "rawCode": "CODE MATERIA PRIMA",
  "quantity": 0.00,
  "loss": 0.00
}
```

**GET -> `/structure`** (TRAS TODOS AS ESTRUTURAS)

**GET -> `/structure/{CODE}`** (TRAS A ESRUTURA PELO CODIGO)

**PUT -> `/structure/edit/{id}`** (EDITA A ESTRUTURA PELO ID)  
Body:
```json
{
  "quantity": 0.0,
  "loss": 0.0
}
```

**DELETE -> `/structure/{productCode}`** (DELETA TODA ESTRUTURA DAQUELE PRODUTO)

**DELETE -> `/structure/{id}`** (DELETA UM UMA LINHA DA ESTRUTURA)

---

## 🏭 Manufatura

### 📦 Estoque

**GET -> `/stock`** (Trás todo o estoque)

**PUT -> `/stock/clear/{code}`** (limpa o estoque pelo código)  
> Recomendação: implementar aviso/confirmação no front antes de chamar esse endpoint.

---

## ✅ Disponível para Produção

**GET -> `/product/production`**  
(Trás uma lista de tudo que pode ser produzido, caso 0 não produza)

Retorno:
```json
{
  "productCode": "CODE",
  "productDescription": "DESCRIÇÕ",
  "maxProducible": 0,
  "structureStatus": "OK or N/A"
}
```

---

## 🧾 Produção (entrada/saída no estoque)

**PUT -> `/stock/update/{code}`**  
(adiciona ou tira estoque pelo código)

Body:
```json
{
  "code": "CODE",
  "barcode": "EAN 13",
  "stockQuantity": 0.0,
  "stockUpdate": "ENTRY OR OUTPUT"
}
```

---

## 📝 Observações

- `typeProduct`:
  - `FP` = Produto acabado
  - `RAW` = Matéria-prima
- Em produção, ao **produzir uma peça**:
  - adiciona estoque do produto acabado (`ENTRY`)
  - consome estoque das matérias-primas (`OUTPUT`)
