/* TCP */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

#define MSG_LEN 256
#define PORT 3434

void error(const char *msg)
{
    perror(msg);
    exit(1);
}

int main()
{
     int sockfd, newsockfd;
     socklen_t clilen;
     char buffer[MSG_LEN];
     struct sockaddr_in serv_addr, cli_addr;
     int n;

     sockfd = socket(AF_INET, SOCK_STREAM, 0);
     if (sockfd < 0) 
        error("Server: ERROR opening socket");
     bzero((char *) &serv_addr, sizeof(serv_addr));
     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = INADDR_ANY;
     serv_addr.sin_port = htons(PORT);
     if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) 
              error("Server: ERROR on binding");

     while(1) {
          listen(sockfd,5);
          clilen = sizeof(cli_addr);
          newsockfd = accept(sockfd,(struct sockaddr *) &cli_addr, &clilen);
          if (newsockfd < 0) 
               error("ERROR on accept");
          bzero(buffer,MSG_LEN);
          n = read(newsockfd,buffer,MSG_LEN);
          if (n < 0) error("Server: ERROR reading from socket");
               printf("Server Got: %s\n",buffer);
          n = write(newsockfd,"Server Got MSG",15);
          if (n < 0) error("Server: ERROR writing to socket");
     }
     close(newsockfd);
     close(sockfd);
     return 0; 
}
