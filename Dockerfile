FROM public.ecr.aws/amazonlinux/amazonlinux:latest
RUN yum -y install java-21-amazon-corretto
RUN mkdir -p /opt/hh
COPY ./artifact.jar /opt/hh/artifact.jar
COPY ./startup.sh /startup.sh
RUN chmod 755 /startup.sh
EXPOSE 80
CMD ["/bin/sh", "/startup.sh"]