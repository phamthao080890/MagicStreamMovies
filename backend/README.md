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


### Render configuration

Set `MONGODB_URI` and `DATABASE_NAME` as environment variables on the backend service, or use Spring Boot's `SPRING_DATA_MONGODB_URI` and `SPRING_DATA_MONGODB_DATABASE` names. If a URI includes the database name (for example `mongodb+srv://.../mystreamingdb`), the separate database name is optional. Use the name of the existing application database; pointing to a different database creates a separate catalog. Never commit credentials. Restart or redeploy the backend after changing the variables.

If startup fails, inspect the last `Caused by` line in the logs. A missing URI or database name is a configuration problem; authentication, DNS and connection timeouts require checking Atlas credentials, network access and availability.
