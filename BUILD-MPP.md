# Build the `.mpp`

This repo is ready to build as a Morphe patch bundle.

## Local build

Requires JDK 17+, Gradle 9.5.0, and access to Morphe's GitHub Packages Maven registry.
Set these env vars or Gradle properties first:

```bash
export GITHUB_ACTOR="your-github-username"
export GITHUB_TOKEN="your-github-token-with-package-read-access"
gradle :patches:buildAndroid --stacktrace
```

Output:

```text
patches/build/libs/patches-0.1.0.mpp
```

## GitHub Actions build

1. Push this folder to a GitHub repo.
2. Open the repo on GitHub.
3. Go to Actions > Build Morphe MPP > Run workflow.
4. Download the `music-league-patches-mpp` artifact.

The workflow uses GitHub's built-in `GITHUB_TOKEN` for package read access.
