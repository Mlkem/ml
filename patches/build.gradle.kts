group = "me.mikem"

patches {
    about {
        name = "Music League patches compatible with Morphe"
        description = "Personal patch bundle for Music League."
        source = "https://github.com/YOUR_GITHUB_USER/music-league-patches"
        author = "Michael MacDonald"
        contact = "na"
        website = "na"
        license = "Private / personal use"
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
