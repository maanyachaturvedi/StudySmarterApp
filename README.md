# StudySmarter App

A smart study planner app built with Kotlin, Jetpack Compose, and integrated AI flashcards.

---

## Features

- Task creation with due dates and priorities
- Sorting and filtering of tasks
- AI-generated flashcards based on user input
- Retrofit networking for API calls
- BuildConfig integration for API key management

---

## Setup

### 1. Clone the repository

```bash
git clone git@github.com:maanyachaturvedi/StudySmarterApp.git
cd StudySmarterApp

### 2. Add your API key safely

**⚠️ Each developer must enter their own Gemini API key before running the app.**

1. Create or open `~/.gradle/gradle.properties`
2. Add your key like this:

```properties
GEMINI_API_KEY=your-own-api-key

