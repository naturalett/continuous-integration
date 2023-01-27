FROM python:3.8-slim-buster
WORKDIR /app
ENV FLASK_APP=hello.py
ENV FLASK_RUN_HOST=0.0.0.0
COPY . ./
RUN pip install -r requirements.txt
EXPOSE 81
CMD ["flask", "run", "--host=0.0.0.0", "--port=81"]