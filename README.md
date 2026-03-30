# VitalTrack 

Aplicativo Android nativo para monitoramento integrado de saúde pessoal.

## Sobre o Projeto

O VitalTrack resolve um problema real: a maioria das pessoas não monitora seus hábitos
de saúde de forma integrada. Hidratação insuficiente, sono inadequado e sedentarismo
impactam diretamente a produtividade e bem-estar. Aplicativos existentes focam em apenas um aspecto,
o VitalTrack unifica tudo em um só lugar.

## Funcionalidades

- 💧 **Hidratação**: Registro de consumo de água com meta dinâmica ajustada por sensores
- 😴 **Sono** : Monitoramento de qualidade do sono com score e histórico
- 🏃 **Atividade Física**: Pedômetro nativo e rastreamento de trajetos GPS
- 🥗 **Nutrição**: Scanner de rótulos com OCR e registro manual de refeições
- 📊 **Dashboard**: Score geral de saúde e resumo do dia em tempo real
- 📈 **Estatísticas**: Gráficos de evolução dos últimos 7 e 30 dias

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| MVVM + Clean Architecture | Arquitetura do projeto |
| Room Database | Persistência local |
| Hilt | Injeção de dependências |
| WorkManager | Notificações em background |
| CameraX + ML Kit | Scanner de rótulos nutricionais |
| OSMDroid | Mapas e rastreamento GPS |
| FusedLocationProvider | GPS otimizado |
| MPAndroidChart | Gráficos de evolução |
| Kotlin Coroutines + Flow | Operações assíncronas |
| Material Design 3 | Interface do usuário |

## Sensores Utilizados

- **Acelerômetro**: Monitoramento de agitação noturna e detecção de atividade intensa
- **TYPE_STEP_COUNTER**: Pedômetro nativo do Android
- **GPS (FusedLocationProvider)**: Rastreamento de trajetos ao ar livre
- **Câmera (CameraX)**: Scanner de rótulos nutricionais com ML Kit OCR

## Arquitetura
```
UI Layer (Fragments/Activities)
        ↕ observa StateFlow/LiveData
ViewModel Layer
        ↕ chama funções suspend
Repository Layer
        ↕ abstrai fontes de dados
Data Layer (Room DB + Sensores)
```

## Estrutura do Projeto
```
app/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── database/
│   │   └── entity/
│   └── repository/
├── di/
├── service/
├── ui/
│   ├── activity/
│   ├── dashboard/
│   ├── hydration/
│   ├── sleep/
│   ├── activity_module/
│   ├── nutrition/
│   ├── stats/
│   └── profile/
├── worker/
└── util/
```

## Requisitos

- Android 8.0 (API 26) ou superior
- Permissões: Localização, Câmera, Atividade Física, Notificações

## Como Executar

1. Clone o repositório
```bash
git clone https://github.com/PedroKeita/VitalTrack.git
```

2. Abra no Android Studio

3. Sincronize o Gradle

4. Execute em um dispositivo físico (sensores não funcionam em emulador)



