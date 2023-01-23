# Test it
```bash
docker build -t workshop/hello-world .
```

# Run it
```bash
docker run -it --name python-hello-world -d -p81:81 workshop/hello-world
```

# Check logs
```bash
docker logs python-hello-world
```

# Test it
```bash
curl -X GET localhost:81
curl -X POST localhost:81
curl localhost:81/home
```

# Kill it
```bash
docker stop python-hello-world && docker rm python-hello-world
```
