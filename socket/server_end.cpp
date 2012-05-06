#include<stdio.h>
#include<string.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>

#define MSG_LEN 10000 //The maximum length of message
#define PORT_NUM 3434 //The port number
#define LISTEN_NUM 4 //The listen maximum number

int main() {
	int file_descriptor1,file_descriptor2;
	struct sockaddr_in server_address, client_address;
	socklen_t client_len;
	char message[MSG_LEN];

	bzero((char *) &server_address, sizeof(server_address));
	printf("Server port number: %d\n",PORT_NUM);
	printf("Server listen maximum: %d\n",LISTEN_NUM);
	
	server_address.sin_family = AF_INET;
	server_address.sin_port = htons(PORT_NUM); //network byte order
	file_descriptor1 = socket(AF_INET,SOCK_STREAM,0); // port to port; stream; TCP
	if(file_descriptor1 == -1) {
		printf("opening socket failed\n");
		return 1;
	} else {
		if(bind(file_descriptor1, (struct sockaddr *)&server_address,sizeof(server_address)) < 0) {
			printf("binding failed");
			return 1;
		}
	}
	
	listen(file_descriptor1,LISTEN_NUM);
	while(1) {
		client_len = sizeof(client_address);
		file_descriptor2 = accept(file_descriptor1,(struct sockaddr*) & client_address, & client_len);
		memset(message,'\0',MSG_LEN);
		if(0 > read(file_descriptor2, message, MSG_LEN)) {
			printf("error in reading message.\n");
		}
		printf("Receive: %s\n", message);

		printf("Input message:");
		scanf("%s",message);
		if(strcmp(message,"quit") == 0) {
			close(file_descriptor1);
			close(file_descriptor2);
			return 1;
		}
		if(0 > write(file_descriptor2, message, MSG_LEN)) {
			printf("error in writing message.\n");
			close(file_descriptor1);
			close(file_descriptor2);
			return 1;
		}
		close(file_descriptor2);
	}
	close(file_descriptor1);
	return 0;
}
