#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

#define MSG_LEN 256
#define PORT 3434

void error(const char *msg)
{
    perror(msg);
    exit(0);
}

int main(int argc, char *argv[])
{
    int sockfd, portno, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;

    char buffer[MSG_LEN];
    if (argc < 2) {
       fprintf(stderr,"usage %s hostname\n", argv[0]);
       exit(0);
    }
    portno = PORT;
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("client: ERROR opening socket");
    server = gethostbyname(argv[1]);
    if (server == NULL) {
        fprintf(stderr,"client: ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr,(char *)&serv_addr.sin_addr.s_addr,server->h_length);
    serv_addr.sin_port = htons(portno);
    if (connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) 
        error("Client: ERROR connecting");
    printf("Client: Please enter the message: ");
    bzero(buffer,MSG_LEN);
    fgets(buffer,MSG_LEN-1,stdin);
    n = write(sockfd,buffer,strlen(buffer));
    if (n < 0) 
        error("Client: ERROR writing to socket");
    bzero(buffer,MSG_LEN);
    n = read(sockfd,buffer,255);
    if (n < 0) 
        error("Client: ERROR reading from socket");
    printf("Client: %s\n",buffer);
    close(sockfd);
    return 0;
}
