FROM 1science/sbt

EXPOSE 8080

ENTRYPOINT [ "sbt", "project fana-service", "run"]



