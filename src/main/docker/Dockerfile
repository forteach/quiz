FROM openjdk:8-jdk-alpine
MAINTAINER ang00/quit zhang10092009@hotmail.com
VOLUME /tmp
ADD quiz-0.0.1-SNAPSHOT.jar app.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
CMD ["java", "Xmx512", "-jar", "/tmp/app.jar"]
EXPOSE 8070
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]