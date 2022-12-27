## Usage

The application can be run either locally, using a specialized IDE (or the CLI) or via Docker.

For the second option, a Dockerfile, as well as a docker-compose.yml file have been provided to quickly test the app (tested on Windows 10 - WSL v1 as well as WSL v2).

### To start with Docker, run:

```
docker run --rm --env DISPLAY=host.docker.internal:0 vladbuhoci/tetris-with-ai:1.0.0
```

### To start with Docker Compose, run:

```
docker-compose up
```

<b>NOTE:</b> there are several entrypoint classes defined in the project, each one dealing with a specific scenario:
* TetrisForHumanPlayer
* TetrisForRandomAI
* TetrisForMultipleRandomAIs
* TetrisForMultipleGeneticAIs (the default entrypoint used in the Docker container)

## Building from source

### Build OS-specific binary files:

This can be accomplished by any modern IDE that is integrated with Maven.

Otherwise, in a terminal, run the following command (you need to have both Java 8 and Maven installed for this to be possible):

```
mvn package -P dev
```

The newly created <i><b>target</b></i> folder contains your executable binary file, which can be run with the <b>java</b> command afterwards.

### Build a Docker image:

Run the following command (you need to have both Docker CLI and a Docker server installed for this to be possible):

```
docker build --no-cache --pull -t tetris-with-ai:1.0.0 .
```

Start a container based on this image:

```
docker run --rm --env DISPLAY=host.docker.internal:0 tetris-with-ai:1.0.0
```

<b>NOTE:</b> the <i>DISPLAY</i> variable must be set, otherwise the app will fail to initialize, as it needs a window surface to draw graphics to.
