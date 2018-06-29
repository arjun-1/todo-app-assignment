first, download and run the docker postgres image:
docker pull postgres
docker run -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres

Then, run the http server in one terminal:
sbt 'project httpServer' 'run'

And the GUI client in another:
sbt 'project guiClient' 'run'