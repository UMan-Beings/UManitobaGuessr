# Contributing

## Foundation
Please read the resources below. This document explains how we apply them in this repo.

- GitHub Flow for branches and pull requests  
  https://docs.github.com/en/get-started/using-github/github-flow#following-github-flow

- cbeams commit message style (not Conventional Commits)  
  https://cbea.ms/git-commit/

## Workflow

### Steps
1. Create a branch from `main`
2. Make small, focused commits
3. Open a pull request
4. Get at least one review
5. Merge when approved

#### Notes
- Do not mix unrelated changes in one commit or PR
- Keep PRs small and easy to review
- Automatic branch deletion on merge is enabled

## Branch names
Follow these rules when writing a branch name:
- Summarize work with a short, descriptive name
- All lowercase
- Words separated by dashes

#### Examples
- `add-code-of-conduct`
- `increase-test-timeout`
- `fix-startup-crash`
- `update-readme`
- `refactor-auth-logic`

## Commit messages

### Format
```
<subject>

<body>
```

### Subject line
Follow these rules when writing a subject line:
- Capitalize the subject line
- No period at the end
- Write in the imperative mood (Add, Fix, Update, Remove, ...)
- Soft limit at 50 characters, hard limit at 72 characters

#### Examples
- `Rework @PropertySource early parsing logic`
- `Add tests for ImportSelector meta-data`
- `Polish mockito usage`

### Body
Bodies are optional. Follow these rules when you include one:
- Wrap the body at 72 characters
- Use the body to explain what and why, not how
    - What problem were you seeing?
    - What behavior changed?
    - Why did you choose this approach?

#### Example
```
Add validation for missing config values

The app previously assumed all required config values were
present at startup. If any were missing, it failed later
with an error that was hard to trace.

This change validates required fields during initialization
and exits early with a clear error message.
```

## Pull requests

### Title
The PR title should follow the same rules as a commit subject line:
- Capitalized
- No period
- Imperative mood
- Short

### Description
The PR description should explain:
- What changed
- Why it changed
- How to test it

