# Aktivitetskrav Backend
TODO

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles til team-esyfo

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #esyfo.

## Development

### 🧹 Code style and formatting

We use **Ktlint** (`intellij_idea` style) to ensure consistent Kotlin formatting.

👉 Please install the **Ktlint** plugin in IntelliJ:
- Go to *Preferences → Plugins → Marketplace → search “Ktlint” → Install*
- Then enable **“Format on Save”**

Alternatively, you can always run:
```bash
./gradlew ktlintFormat
```