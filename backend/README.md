# StreamingAPI

## Docker deployment

This project includes a multi-stage Docker build for the Spring Boot API.

### Build the image

```zsh
docker build -t streaming-api -f Dockerfile .
```

### Run the container

```zsh
docker run --rm -p 8080:8080 \
  -e PORT=8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/mystreamingdb \
  -e SPRING_DATA_MONGODB_DATABASE=mystreamingdb \
  streaming-api
```

### Notes

- The app listens on `PORT`, defaulting to `8080`.
- Inside Docker, `localhost` points to the container itself, not your Mac.
- If MongoDB is running on your host machine, use `host.docker.internal` in the Mongo URI.
- The image builds the jar inside the container, so you do not need to run Maven first.

