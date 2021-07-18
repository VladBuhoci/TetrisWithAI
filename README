### Usage

The application can be run either locally, using a specialized IDE (or the CLI) or via Docker.

... and yes, the app's GUI is rendered even if it is running inside a container, by setting the DISPLAY env variable to `host.docker.internal:0` (note that I've only tested this on Windows 10 with WSL 2).

For the second option, a Dockerfile, as well as a docker-compose.yml file have been provided to quickly test the app.

To start with Compose run:

```
docker-compose up
```

<u><b>Note</b></u> that there are several entrypoint classes defined in the project, each one dealing with a specific scenario:
* TetrisForHumanPlayer
* TetrisForRandomAI
* TetrisForMultipleRandomAIs
* TetrisForMultipleGeneticAIs (the default entrypoint used in the Docker container)
