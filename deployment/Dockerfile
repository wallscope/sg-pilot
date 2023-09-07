FROM secoresearch/fuseki:4.6.1
USER 9008:0

WORKDIR /

# Deployment with data baked in
# COPY ./data/triplestore/data /fuseki-base/databases

EXPOSE 3030

CMD ["/jena-fuseki/fuseki-server"]
