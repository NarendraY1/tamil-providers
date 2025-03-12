# Tamil Providers for CloudStream

A collection of Tamil content providers for [CloudStream](https://github.com/recloudstream/cloudstream). This repository contains various providers for Tamil movies, TV shows, and other content.

## Installation

1. Open CloudStream
2. Go to Settings > Extensions/Plugins
3. Click on "Repository" and add the following URL:
   ```
   https://raw.githubusercontent.com/NarendraY1/tamil-providers/builds/repos.json
   ```
4. Install the desired providers from the repository

## Available Providers

This repository includes providers for various Tamil content sources including:
- AllMovieLand
- AnimeWorld
- Av1Encoded
- BanglaPlex
- Bolly2Tolly
- Cinevez
- Einthusan
- FDM
- Flbd
- IBomma
- KatMovies
- MadStream
- MassTamilan
- MovieHUB
- MultiMovies
- NOXX
- OlaMovies
- PrivateMoviez
- ShowFlix
- StreamBlasters
- SubsKing
- TamilDhool
- TamilUltra
- TamilYogi
- TeluguFlix
- UHDmovies

## Building from Source

### Requirements
- JDK 17
- Android SDK
- Gradle 8.5 or newer

### Build Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/NarendraY1/tamil-providers.git
   cd tamil-providers
   ```

2. Build all providers:
   ```bash
   ./gradlew make
   ```
   
   Or build a specific provider:
   ```bash
   ./gradlew [ProviderName]:make
   ```

3. The built plugins will be in their respective `build` folders with the `.cs3` extension

## Contributing

Feel free to open issues or submit pull requests if you find any bugs or have suggestions for improvements.

## License

This project is licensed under the same terms as the original CloudStream project.

## Credits

- Original repository by LikDev-256
- Based on [CloudStream](https://github.com/recloudstream/cloudstream)
- Plugin system based on [Aliucord](https://github.com/Aliucord)
