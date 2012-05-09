#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<string.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include <netdb.h> 

#define MSG_LEN 10000
#define PORT_NUM 3434

int main(int argc, char *argv[]) {
	struct sockaddr_in server_address;
	struct hostent *host_server;
	int file_descriptor;
	char message[MSG_LEN],host_name[100];

	bzero((char *) &server_address, sizeof(server_address));

	printf("input server name:\n");
	scanf("%s",host_name);
	host_server = gethostbyname(host_name);
	if(host_server == NULL) {
		printf("No such host.\n");
		return 1;
	}

	server_address.sin_family = AF_INET;
	bcopy((char *)host_server->h_addr,(char *)&server_address.sin_addr.s_addr,host_server->h_length);
	server_address.sin_port = htons(PORT_NUM);


	while(1) {
		file_descriptor = socket(AF_INET, SOCK_STREAM, 0);
		if(file_descriptor == -1) {
			printf("could not get socket built.\n");
			return 1;
		}
		if(0 > connect(file_descriptor,(struct sockaddr *) &server_address,sizeof(server_address))) {
			printf("could not connect server.\n");
			return 1;
		}
		memset(message,'\0',MSG_LEN);
		printf("Input message:");
		scanf("%s",message);
		if(strcmp(message,"quit") == 0) {
			close(file_descriptor);
			return 1;
		}
		if( 0 > write(file_descriptor,message,strlen(message))) {
			printf("could not send message to server.\n");
			close(file_descriptor);
			return 1;
		}
	
		memset(message,'\0',MSG_LEN);
		if( 0 > read(file_descriptor,message,MSG_LEN)) {
			printf("could not read message from server.\n");
			close(file_descriptor);
			return 1;
		} else {
			printf("Received: %s\n",message);
		}
    		close(file_descriptor);
	}
	return 0;
}