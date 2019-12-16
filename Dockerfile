FROM java:8
WORKDIR /smcusers
#ENV 格式为  ENV <key> <value> 指定一个环境变量，会被后续  RUN  指令使用，并在容器运行时保持。
ENV PROJECT_ARTIFACTID="smcusers" PROJECT_VERSION="0.0.1-SNAPSHOT"
#COPY 格式为 COPY <src> <dest>
#复制本地主机的 <src>（为 Dockerfile 所在目录的相对路径）到容器中的 <dest>
#当使用本地目录为源目录时，推荐使用 COPY
COPY target/$PROJECT_ARTIFACTID-$PROJECT_VERSION.jar /smcusers/users.jar
EXPOSE 8751
# ENTRYPOINT ["java", "-jar", "/smcusers/users.jar"]
ENTRYPOINT ["java","-Xms400m","-Xmx500m","-jar","/smcusers/users.jar"]
