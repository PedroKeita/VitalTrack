# VitalTrack — Documentação do Projeto

## Descrição do Problema

A maioria das pessoas ativas não monitora de maneira integrada seus hábitos de saúde básicos. Hidratação insuficiente, qualidade de sono inadequada e sedentarismo são problemas frequentes que impactam diretamente a produtividade e bem-estar. Aplicativos existentes tendem a focar em apenas um desses aspectos, exigindo o uso de múltiplos apps ou nenhum controle efetivo.

Segundo estudos de saúde pública, mais de 75% das pessoas vivem cronicamente desidratadas, e cerca de 60% não atingem a meta diária de atividade física recomendada pela OMS. A falta de um sistema unificado e contextual ajuda a agravar ainda mais esse cenário.

---

## Solução Proposta

O **VitalTrack** é um aplicativo Android nativo desenvolvido em Kotlin que monitora de forma integrada três pilares da saúde: hidratação, sono e atividade física. Utilizando sensores nativos do dispositivo (acelerômetro, GPS e câmera), o app ajusta metas dinamicamente ao contexto do usuário e oferece insights personalizados para promover hábitos mais saudáveis.

---

## Objetivos

- Desenvolver um aplicativo Android funcional com Kotlin e Material Design 3
- Integrar ao menos três sensores nativos: acelerômetro, GPS e câmera (ML Kit)
- Monitorar hidratação, sono e atividade física de forma centralizada
- Gerar estatísticas e gráficos de evolução nos últimos 7 e 30 dias
- Persistir dados localmente com Room Database sem necessidade de internet
- Aplicar arquitetura MVVM com boas práticas de engenharia de software

---

## Público-Alvo

Pessoas entre 16 e 60 anos que desejam monitorar e melhorar seus hábitos de saúde de forma prática, especialmente quem pratica atividades físicas regularmente ou trabalha em ambientes de alta demanda cognitiva.

---

## Épicas

| ID | Nome | Descrição |
|---|---|---|
| EP-01 | Gestão de Perfil | Cadastro e configuração do usuário, metas personalizadas e preferências do app |
| EP-02 | Monitoramento de Hidratação | Registro de consumo de água, meta diária dinâmica ajustada por sensores e notificações |
| EP-03 | Monitoramento de Sono | Registro de horários de sono, detecção de agitação noturna e cálculo de score de qualidade |
| EP-04 | Monitoramento de Atividade Física | Monitoramento de passos, rastreamento de trajetos GPS e estimativa de calorias |
| EP-05 | Módulo de Nutrição | Scanner de rótulos nutricionais via câmera com OCR e registro manual de refeições |
| EP-06 | Dashboard e Estatísticas | Tela principal com resumo do dia, score geral de saúde e gráficos de evolução |

---

## EP-01 — Gestão de Perfil

**Sensores utilizados:** Nenhum

### US-01 — Cadastrar Perfil

> **Como** novo usuário, **quero** cadastrar meus dados pessoais no primeiro acesso, **para** que o app calcule minhas metas de saúde automaticamente.

**Critérios de Aceitação:**
- Campos: nome, peso, altura, idade e nível de atividade
- Metas calculadas automaticamente após cadastro
- Validação inline em campos obrigatórios
- Dados salvos localmente no Room DB
- Redirecionamento para o Dashboard após conclusão

**Caso de Uso:** UC-01 — Cadastrar Perfil do Usuário
**Status:** Em Desenvolvimento

---

### US-02 — Editar Perfil

> **Como** usuário cadastrado, **quero** editar meus dados e metas a qualquer momento, **para** manter as informações atualizadas conforme minha rotina muda.

**Critérios de Aceitação:**
- Todos os campos do cadastro editáveis
- Metas recalculadas automaticamente após edição
- Confirmação antes de salvar alterações
- Dados atualizados no Room DB

**Caso de Uso:** UC-02 — Editar Perfil e Metas
**Status:** Em Desenvolvimento

---

## EP-02 — Monitoramento de Hidratação

**Sensores utilizados:** Acelerômetro, GPS

### US-03 — Registrar Consumo de Água

> **Como** usuário que quer se hidratar melhor, **quero** registrar rapidamente quanto água bebi, **para** acompanhar meu progresso diário de hidratação.

**Critérios de Aceitação:**
- Botões rápidos de 200ml, 300ml e 500ml visíveis
- Volume customizado aceito entre 50ml e 2000ml
- Barra de progresso atualiza imediatamente após registro
- Registro salvo com data e hora no Room DB
- Animação de celebração ao atingir a meta

**Caso de Uso:** UC-03 — Registrar Consumo de Água
**Status:** Em Desenvolvimento

---

### US-04 — Ajustar Meta por Contexto

> **Como** usuário ativo, **quero** que minha meta de água seja ajustada automaticamente, **para** beber a quantidade certa conforme minha atividade e ambiente.

**Critérios de Aceitação:**
- Acelerômetro detecta atividade física intensa por mais de 20 min
- Meta aumenta +15% ao detectar atividade intensa
- GPS identifica ambiente externo com temperatura elevada
- Meta aumenta +10% em ambiente externo
- Notificação enviada ao usuário informando o ajuste
- Ajuste revertido ao fim do dia

**Caso de Uso:** UC-04 — Ajustar Meta por Contexto
**Status:** Em Desenvolvimento

---

### US-05 — Receber Lembrete de Hidratação

> **Como** usuário esquecido, **quero** receber lembretes periódicos para beber água, **para** não passar horas sem me hidratar sem perceber.

**Critérios de Aceitação:**
- Notificações enviadas a cada 2h via WorkManager
- Notificação só disparada se progresso estiver abaixo do esperado
- Notificações pausadas no horário de sono configurado
- Usuário pode desativar lembretes nas configurações

**Caso de Uso:** UC-05 — Receber Lembrete de Hidratação
**Status:** Em Desenvolvimento

---

## EP-03 — Monitoramento de Sono

**Sensores utilizados:** Acelerômetro

### US-06 — Registrar Horário de Sono

> **Como** usuário que quer dormir melhor, **quero** registrar quando fui dormir e quando acordei, **para** acompanhar a duração e consistência do meu sono.

**Critérios de Aceitação:**
- Botão "Dormir agora" registra horário de início do sono
- Botão "Acordei" registra horário de fim do sono
- Duração total calculada automaticamente
- Inserção manual de horários disponível como alternativa
- Acelerômetro ativado durante o período de sono
- Registro salvo no Room DB com data, início, fim e duração

**Caso de Uso:** UC-06 — Registrar Horário de Sono
**Status:** Em Desenvolvimento

---

### US-07 — Ver Score de Qualidade do Sono

> **Como** usuário preocupado com a qualidade do sono, **quero** ver um score que avalie minha noite de descanso, **para** entender se estou dormindo bem e o que posso melhorar.

**Critérios de Aceitação:**
- Score calculado de 0 a 100 após registrar acordar
- Score considera duração, consistência de horário e agitação noturna
- Classificação exibida: Ruim, Regular, Bom ou Excelente
- Dica personalizada exibida com base no score
- Histórico dos últimos 7 dias visível na tela

**Caso de Uso:** UC-07 — Calcular Score de Sono
**Status:** Em Desenvolvimento

---

## EP-04 — Monitoramento de Atividade Física

**Sensores utilizados:** Acelerômetro (TYPE_STEP_COUNTER), GPS (FusedLocationProvider)

### US-08 — Contar Passos

> **Como** usuário que quer ser mais ativo, **quero** que o app conte meus passos automaticamente, **para** saber se estou atingindo minha meta diária de atividade.

**Critérios de Aceitação:**
- Contagem de passos exibida em tempo real na tela
- Sensor TYPE_STEP_COUNTER nativo do Android utilizado
- Calorias estimadas calculadas com base em peso e passos
- Contador reinicia automaticamente à meia-noite
- Dados persistidos no Room DB ao final do dia
- Contagem não é perdida ao fechar o app (Service em background)
- Meta diária de passos configurável no perfil

**Caso de Uso:** UC-08 — Contar Passos
**Status:** Em Desenvolvimento

---

### US-09 — Rastrear Trajeto GPS

> **Como** usuário que pratica atividades ao ar livre, **quero** registrar meu trajeto de caminhada ou corrida no mapa, **para** acompanhar distância, ritmo e evolução ao longo do tempo.

**Critérios de Aceitação:**
- Botão "Iniciar atividade" começa o rastreamento GPS
- Trajeto desenhado em tempo real no mapa (Google Maps SDK)
- Distância percorrida atualizada em tempo real
- Velocidade média calculada durante a atividade
- Botão "Finalizar" encerra o rastreamento e salva o trajeto
- Histórico de trajetos acessível na tela de Atividade
- GPS otimizado com FusedLocationProvider para economizar bateria

**Caso de Uso:** UC-09 — Rastrear Trajeto GPS
**Status:** Em Desenvolvimento

---

## EP-05 — Módulo de Nutrição

**Sensores utilizados:** Câmera (CameraX), ML Kit Text Recognition (OCR)

### US-10 — Escanear Rótulo Nutricional

> **Como** usuário que se preocupa com alimentação, **quero** escanear o rótulo de um alimento com a câmera, **para** registrar automaticamente as informações nutricionais sem digitar nada.

**Critérios de Aceitação:**
- Câmera abre em tela cheia ao tocar em "Escanear rótulo"
- ML Kit OCR extrai texto do rótulo em tempo real
- App identifica calorias, proteínas, carboidratos e gorduras
- Dados extraídos exibidos para confirmação antes de salvar
- Usuário pode editar campos antes de confirmar
- Registro salvo no Room DB com horário e categoria da refeição
- Fallback para entrada manual se OCR falhar

**Caso de Uso:** UC-10 — Escanear Rótulo Nutricional
**Status:** Em Desenvolvimento

---

### US-11 — Registrar Refeição Manual

> **Como** usuário que nem sempre tem rótulo disponível, **quero** registrar minhas refeições manualmente, **para** manter o controle calórico mesmo sem escanear nada.

**Critérios de Aceitação:**
- Formulário com campos: descrição, categoria e calorias
- Categorias disponíveis: Café da manhã, Almoço, Jantar e Lanche
- Horário preenchido automaticamente com timestamp atual
- Total de calorias do dia atualizado após cada registro
- Possibilidade de editar ou excluir registros do dia
- Registro salvo no Room DB

**Caso de Uso:** UC-11 — Registrar Refeição Manual
**Status:** Em Desenvolvimento

---

## EP-06 — Dashboard e Estatísticas

**Sensores utilizados:** Nenhum

### US-12 — Ver Dashboard do Dia

> **Como** usuário do VitalTrack, **quero** ver um resumo visual de todos os meus dados de saúde do dia, **para** entender rapidamente como estou me saindo sem precisar abrir cada módulo separadamente.

**Critérios de Aceitação:**
- Tela inicial do app após onboarding
- Anel de progresso para hidratação com % do dia
- Anel de progresso para atividade física com passos do dia
- Card de sono com score da última noite
- Score geral de saúde de 0 a 100 em destaque
- Cards de acesso rápido para cada módulo
- Dados atualizados em tempo real via StateFlow
- Saudação personalizada com nome do usuário e horário do dia

**Caso de Uso:** UC-12 — Visualizar Dashboard do Dia
**Status:** Em Desenvolvimento

---

### US-13 — Consultar Gráficos e Histórico

> **Como** usuário que quer acompanhar sua evolução, **quero** visualizar gráficos dos meus dados dos últimos dias, **para** identificar tendências e entender se estou evoluindo nos meus hábitos de saúde.

**Critérios de Aceitação:**
- Gráfico de linha para hidratação dos últimos 7 e 30 dias
- Gráfico de barras para passos dos últimos 7 e 30 dias
- Gráfico de linha para score de sono dos últimos 7 e 30 dias
- Alternância entre períodos de 7 e 30 dias com toggle
- Média do período calculada e exibida abaixo do gráfico
- Indicador de tendência: melhorando, estável ou piorando
- Gráficos renderizados com MPAndroidChart
- Tela acessível via Bottom Navigation

**Caso de Uso:** UC-13 — Consultar Histórico e Gráficos
**Status:** Em Desenvolvimento

---

## Requisitos Funcionais

| ID | Módulo | Descrição | Prioridade |
|---|---|---|---|
| RF-01 | Perfil | Permitir cadastro com nome, peso, altura, idade e nível de atividade | 🔴 Alta |
| RF-02 | Perfil | Recalcular metas automaticamente ao editar perfil | 🔴 Alta |
| RF-03 | Hidratação | Registrar consumo de água com volumes pré-definidos e customizáveis | 🔴 Alta |
| RF-04 | Hidratação | Ajustar meta de água com base no acelerômetro e GPS | 🔴 Alta |
| RF-05 | Hidratação | Enviar notificações periódicas de lembrete via WorkManager | 🟡 Média |
| RF-06 | Sono | Registrar início e fim do sono e calcular duração total | 🔴 Alta |
| RF-07 | Sono | Calcular score de qualidade com base em duração e agitação | 🟡 Média |
| RF-08 | Atividade | Contar passos usando sensor TYPE_STEP_COUNTER do Android | 🔴 Alta |
| RF-09 | Atividade | Rastrear trajetos ao ar livre com GPS e exibir em mapa | 🔴 Alta |
| RF-10 | Nutrição | Escanear rótulos nutricionais com câmera via ML Kit OCR | 🔴 Alta |
| RF-11 | Nutrição | Permitir registro manual de refeições com calorias | 🟡 Média |
| RF-12 | Dashboard | Exibir anéis de progresso e score geral de saúde | 🔴 Alta |
| RF-13 | Estatísticas | Gerar gráficos de evolução para períodos de 7 e 30 dias | 🟡 Média |

---

## Requisitos Não Funcionais

| ID | Categoria | Descrição |
|---|---|---|
| RNF-01 | Desempenho | App deve inicializar em menos de 3 segundos em dispositivos com Android 8.0+ |
| RNF-02 | Usabilidade | Interface deve seguir Material Design 3 com suporte a tema claro e escuro |
| RNF-03 | Privacidade | Todos os dados do usuário armazenados exclusivamente no dispositivo via Room DB |
| RNF-04 | Compatibilidade | App compatível com Android 8.0 (API 26) ou superior |
| RNF-05 | Confiabilidade | Monitoramento de passos não deve perder dados ao fechar o app |
| RNF-06 | Manutenção | Código deve seguir arquitetura MVVM com separação clara de camadas |
| RNF-07 | Bateria | GPS otimizado com FusedLocationProvider para minimizar consumo |

---

## Permissões Necessárias

| Permissão | Tipo | Justificativa |
|---|---|---|
| ACCESS_FINE_LOCATION | Perigosa | Rastreamento de trajetos com GPS |
| ACCESS_COARSE_LOCATION | Perigosa | Contexto de localização para meta de água |
| CAMERA | Perigosa | Scanner de rótulos nutricionais |
| ACTIVITY_RECOGNITION | Perigosa (API 29+) | Acesso ao sensor de pedômetro nativo |
| BODY_SENSORS | Perigosa | Acelerômetro para monitoramento de sono |
| POST_NOTIFICATIONS | Perigosa (API 33+) | Lembretes de hidratação |
| FOREGROUND_SERVICE | Normal | Serviço de rastreamento GPS em foreground |
| RECEIVE_BOOT_COMPLETED | Normal | Reagendar notificações após reinicialização |

---

## Arquitetura

**Padrão:** MVVM (Model-View-ViewModel)



| Camada | Componentes | Responsabilidade |
|---|---|---|
| UI Layer | Fragments, Activities, Adapters | Exibição de dados e captura de interações |
| ViewModel | ViewModels por feature | Lógica de apresentação e estado da UI |
| Repository | Repositories por módulo | Única fonte de verdade, abstrai Room e Sensores |
| Data | Room DB, DAOs, SensorManager | Persistência local e acesso a sensores |

---

## Telas

| Tela | Componente | Épica |
|---|---|---|
| Splash Screen | `SplashActivity` | — |
| Onboarding | `OnboardingActivity` + `ViewPager2` | EP-01 |
| Dashboard | `DashboardFragment` | EP-06 |
| Hidratação | `HydrationFragment` | EP-02 |
| Sono | `SleepFragment` | EP-03 |
| Atividade Física | `ActivityFragment` | EP-04 |
| Mapa GPS | `MapActivity` | EP-04 |
| Nutrição | `NutritionFragment` | EP-05 |
| Scanner de Rótulo | `ScannerActivity` | EP-05 |
| Estatísticas | `StatsFragment` | EP-06 |
| Perfil | `ProfileFragment` | EP-01 |

---

## Stack Tecnológica

| Biblioteca | Versão | Uso |
|---|---|---|
| Kotlin | 2.0.21 | Linguagem principal |
| Android SDK | API 35 (target) / API 26 (min) | Base do desenvolvimento |
| Room Database | 2.8.4 | Persistência local |
| Hilt | 2.59.2 | Injeção de dependências |
| WorkManager | 2.11.1 | Notificações em background |
| CameraX | 1.5.3 | Acesso à câmera |
| ML Kit Text Recognition | 16.0.1 | OCR de rótulos nutricionais |
| Google Maps SDK | 20.0.0 | Exibição de trajetos GPS |
| FusedLocationProvider | 21.3.0 | Captura otimizada de GPS |
| MPAndroidChart | 3.1.0 | Gráficos de linha e barra |
| Navigation Component | 2.9.7 | Navegação entre Fragments |
| Material Design 3 | 1.13.0 | Componentes visuais e theming |
| Kotlin Coroutines + Flow | 1.10.2 | Operações assíncronas |