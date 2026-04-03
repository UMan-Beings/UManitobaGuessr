# Mutation Tests for UManitobaGuessr

This folder keeps a tracked snapshot of the most recent PIT mutation test report so reviewers can inspect it without rerunning the test.

## Latest Report Snapshot

Open [`index.html`](./index.html) to view the current report.

The snapshot also includes the supporting files needed by the report:

- [`style.css`](./style.css)
- [`mutations.xml`](./mutations.xml)
- [`com.umanbeing.umg.services/index.html`](./com.umanbeing.umg.services/index.html)

## Run Mutation Tests

Make sure Docker is running first. The integration tests use Testcontainers and start a PostgreSQL container during the PIT run.

Run PIT from the backend directory:

```bash
cd backend
./gradlew pitest
```

## View the Report

You can view the mutation report without any VS Code extension.

From the repository root (`UManitobaGuessr`), use one of these options:

1. Right click `backend/mutation-test/index.html` in VS Code and choose Open with Live Server (if the extension is installed).
2. Right click `backend/mutation-test/index.html` and choose Reveal in File Explorer, then open the file in your browser.
