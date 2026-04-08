# CNPJ Utils API

API RESTful para manipulação de CNPJs, tendo geração, validação e análise. Compatível com o formato tradicional numérico e preparada para o novo padrão alfanumérico que será implementado pela Receita Federal.

## Deploy

Após ler a minha descrição detalhada do projeto, você já pode estar dando uma olhadinha nele, fiz deploy no Render e a ui do swagger está disponível em [https://cnpjutils.onrender.com/swagger-ui/index.html]

Obs: Talvez demore um pouco para carregar, pois o projeto está em uma plataforma de deploy grátis por enquanto, então se ficar um tempo sem requisição, a API precisa subir novamente, mas é só aguardar alguns segundos!

## A Evolução do CNPJ: Do Numérico ao Alfanumérico

O CNPJ serve como identidade fiscal das empresas brasileiras há décadas, utilizando exclusivamente dígitos numéricos em sua estrutura. Com o crescimento exponencial de novas empresas no país, o sistema numérico tradicional começou a mostrar sinais de esgotamento.

Em resposta a este desafio, a Receita Federal introduziu uma inovação histórica através da **Instrução Normativa RFB nº 2.229**: o CNPJ Alfanumérico. Este novo formato incorpora letras maiúsculas e números, expandindo significativamente a capacidade de geração de inscrições únicas.

### Timeline da Transição

| Período | Marco |
|---------|-------|
| Out/2024 | Publicação da normativa que cria o formato alfanumérico |
| Out/2024 | Vigência oficial das novas regras |
| **Jul/2026** | **Início efetivo da emissão de CNPJs alfanuméricos** |

**Nota fundamental**: A mudança afeta apenas novas inscrições. CNPJs existentes mantêm seu formato numérico original, garantindo continuidade operacional para todos os negócios já estabelecidos.

## Anatomia do Novo CNPJ

A estrutura básica preserva os 14 caracteres, mas com nova composição:

```
X X . X X X . X X X / X X X X - D D
   |                    |       |
   |                    |       |---- Verificadores (sempre numéricos)
   |                    |------------ Filial (4 posições alfanuméricas)
   |--------------------------------- Matriz (8 posições alfanuméricas)
```

| Parte | Posições | Conteúdo | Exemplo |
|-------|----------|----------|---------|
| Raiz | 1-8 | Identifica a empresa (pode ter letras) | `12ABC345` |
| Ordem | 9-12 | Identifica o estabelecimento (pode ter letras) | `01AB` |
| Dígitos Verificadores | 13-14 | Sempre numéricos (0-9) | `77` |

No novo formato, as posições de 1 a 12 aceitam os caracteres A-Z e 0-9. As posições 13 e 14 (dígitos verificadores) continuam sendo sempre numéricas.

A máscara de exibição é: `XX.XXX.XXX/XXXX-DD`

## Mecanismo de Validação: O Algoritmo Módulo 11

A autenticidade dos CNPJs é garantida por um algoritmo matemático robusto conhecido como módulo 11. Com a introdução do formato alfanumérico, a Receita Federal adaptou este mecanismo para processar tanto números quanto letras.

### Transformação Alfanumérica

O primeiro passo consiste em converter caracteres alfabéticos em valores numéricos através de um mapeamento ASCII:

- **Números (0-9)**: Mantêm valor original (0 a 9)
- **Letras (A-Z)**: Convertidas pela fórmula `ASCII - 48`
  - A = 65 - 48 = 17
  - B = 66 - 48 = 18
  - [...]
  - Z = 90 - 48 = 42

### Processo de Cálculo

#### Primeiro Dígito Verificador
1. **Base de cálculo**: 12 caracteres iniciais (matriz + filial)
2. **Aplicação de pesos**: Multiplicação sequencial decrescente (5→2, 9→2)
3. **Soma ponderada**: Acumula todos os produtos
4. **Módulo 11**: Calcula resto da divisão por 11
5. **Regra de validação**: 
   - Resto 0 ou 1 → dígito = 0
   - Resto 2 a 10 → dígito = 11 - resto

#### Segundo Dígito Verificador
1. **Base expandida**: 13 caracteres (inclui primeiro dígito calculado)
2. **Novos pesos**: Sequência (6→2, 9→2)
3. **Processo idêntico**: Soma, módulo 11, mesma regra de validação

Este mecanismo garante que cada CNPJ, numérico ou alfanumérico, possua uma assinatura matemática única, tornando praticamente impossível a geração de números falsos que passem pela validação.
## Sobre a API

Esta API oferece funcionalidades completas para trabalhar com CNPJs (Cadastro Nacional da Pessoa Jurídica), incluindo:

- **Geração de CNPJs** válidos nos padrões NEW (alfanumérico) e OLD (numérico)
- **Validação de CNPJs** conforme o algoritmo oficial da Receita Federal
- **Verificação de padrão** para identificar se um CNPJ segue o formato novo ou antigo
- **Operações em lote** para processar múltiplos CNPJs simultaneamente
- **Normalização automática** de entrada (conversão para maiúsculas, remoção de máscara)

## Tecnologias Utilizadas

- **Java 21** com Spring Boot 3.4.4
- **Maven** para gerenciamento de dependências
- **Bean Validation** para validação de entrada
- **OpenAPI 3.0** com Swagger UI para documentação
- **JUnit 5** e **Mockito** para testes automatizados

## Endpoints Disponíveis

### Base URL
```
http://localhost:8080/v1/api
```

### Endpoints

#### Geração de CNPJs
- **POST** `/generate` - Gera um CNPJ válido
- **POST** `/generateBatch` - Gera múltiplos CNPJs

#### Verificação de Padrão
- **POST** `/check` - Identifica padrão do CNPJ (NEW/OLD)
- **POST** `/isAllNew` - Verifica se todos são NEW
- **POST** `/isAllOld` - Verifica se todos são OLD

#### Validação de CNPJs
- **POST** `/validate` - Valida CNPJ individual
- **POST** `/isAllValid` - Verifica se todos são válidos
- **POST** `/validateBatch` - Validação em lote detalhada

### Padrões Suportados
- **NEW**: Formato alfanumérico (aceita A-Z e 0-9 nas 12 primeiras posições)
- **OLD**: Formato numérico legado (apenas dígitos 0-9)

---

## Segurança e Rate Limiting

A API implementa controle de rate limiting para proteger contra abusos e garantir uso justo dos recursos.

### Configuração

O rate limiting é configurado via `application.properties`:

```properties
# Rate Limiting Configuration
rate-limit.enabled=true
rate-limit.requests=20
rate-limit.minutes=1
```

### Parâmetros

- **enabled**: Ativa/desativa o rate limiting (default: true)
- **requests**: Número máximo de requisições permitidas (default: 20)
- **minutes**: Janela de tempo em minutos (default: 1)

### Comportamento

- **Controle por IP**: Cada endereço IP tem seu próprio contador
- **Janela deslizante**: O contador reseta a cada período configurado
- **Resposta HTTP 429**: Quando o limite é excedido

```json
{
  "message": "Limite de requisições excedido. Tente novamente em alguns minutos.",
  "success": false,
  "error": "RATE_LIMIT_EXCEEDED"
}
```

### Exceções

Os seguintes endpoints são **excluídos** do rate limiting:
- `/swagger-ui/**` - Documentação Swagger
- `/v3/api-docs/**` - OpenAPI JSON
- `/actuator/**` - Endpoints de monitoramento

---

## Tratamento de Erros

A API utiliza códigos HTTP padrão e respostas padronizadas:

### 400 Bad Request
```json
{
  "data": null,
  "message": "CNPJ não pode ser vazio",
  "success": false
}
```

### 404 Not Found
```json
{
  "data": null,
  "message": "Endpoint não encontrado",
  "success": false
}
```

### 500 Internal Server Error
```json
{
  "data": null,
  "message": "Erro interno do servidor",
  "success": false
}
```

---

## Documentação Interativa

A API inclui documentação interativa via Swagger UI:

- **URL**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

Nela você pode:
- Visualizar todos os endpoints
- Testar as operações diretamente no navegador
- Verificar schemas de request/response
- Baixar a especificação OpenAPI

---

## Como Executar

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6 ou superior

### Execução via Maven
```bash
# Compilar e executar
./mvnw spring-boot:run

# Ou em Windows
mvnw.cmd spring-boot:run
```

### Build para Produção
```bash
# Compilar e empacotar
./mvnw clean install

# Executar o JAR
java -jar target/cnpjutils-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em `http://localhost:8080`

---

## Testes

A API possui 63 testes automatizados cobrindo:
- Testes unitários de todos os controllers
- Testes de integração de fluxos completos
- Testes de tratamento de exceções
- Testes de validação de entrada

### Executar Testes
```bash
# Executar todos os testes
./mvnw test

# Executar testes de uma classe específica
./mvnw test -Dtest="CheckControllerTest"

# Executar com relatório de cobertura
./mvnw test jacoco:report
```

---

## Contribuição

Contribuições são bem-vindas! Por favor:
1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

---

## Contato

Para dúvidas, sugestões ou problemas:
- Abra uma issue no repositório
- Entre em contato através do email: [edu.docxl@gmail.com]
- Ou conecte comigo e me chame no linkedin: [https://www.linkedin.com/in/eduardomarinho237/]
