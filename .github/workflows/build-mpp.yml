name: Build Morphe MPP

on:
  workflow_dispatch:
  push:
    branches: [ main, dev ]

jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: read
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Gradle
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '9.5.0'

      - name: Build .mpp
        env:
          GITHUB_ACTOR: ${{ github.actor }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: gradle :patches:buildAndroid --stacktrace

      - name: Upload .mpp artifact
        uses: actions/upload-artifact@v4
        with:
          name: music-league-patches-mpp
          path: patches/build/libs/*.mpp
          if-no-files-found: error
