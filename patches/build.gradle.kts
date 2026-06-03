group = "me.mikem"

patches {
    about {
        name = "Music League Patches"
        description = "Private Music League patch bundle for Morphe."
        source = "https://github.com/Mlkem/ml"
        author = "MDM"
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
