#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

const int MSG_LEN = 10000; //The maximum length of message
const int PORT_NUM = 3434; //The port number
const int LISTEN_NUM = 4; //The listen maximum number

//const int MAX_VALUE_NUM = 1000000;

//TagSet tagset;
//Cache cache;
//int number_of_values = 0;
//InvertedIndex theII;

void listenToDB() {
	//listen to DB;
	//while (true) {
	//	receive tags and timestamp
	//	tagset.add(tags);
	//}
}

void commitTags() {
	//for (tag in tagset) {
	//	for (value in theII[tag]) value.interval.end = timestamp;
	//	clear theII[tag];
	//}
	//clearTags();
}

void clearTags() {
	//clear tagset;
}

void removeOneValue() {
	//Remove a value according to the strategy (for example, LeastRecentUsed)
	
	//1. remove it from theII;
	//2. remove it from cache;
}

void addValue(Key key, Value val, Interval itv, Tags tags, char *rtn_message) {
	//if (number_of_values == MAX_VALUE_NUM) removeOneValue();
	//else ++number_of_values;

	//value = make_value(val, itv, tags);	
	//if (key is not in cache) cache[key] = make_set(value);
	//else cache[key].add(value);
	
	//if (itv.end = RECENT) 
	//	for (tag in tags) theII[tag].add(value);
	
	//rtn_message = SUCCESS;
}

void readCache(char *message, char *rtn_message) {
	//The message should include: 1) the hash key; 2) a list of timestamps (sorted).
	//The return message should include: 1) the value with a list of timestamps, or 2) NOT_FOUND.
	
	//scan the cache to find out a valid value
	//if (the key is not in cache) return NOT_FOUND
	//values = cache[key];
	//time_idx = 0;
	//value_idx = 0;
	//while (time_idx < timestamp.length && value_idx < values.length)
	//	if (values[value_idx].interval.end < timestamp[time_idx]) ++value_idx;
	//	else if (values[value_idx].interval.begin > timestamp[time_idx]) ++time_idx;
	//	else { //Found!
	//		for (idx_end = time_idx + 1; idx_end < timestamp.length && values[value_idx].interval.end >= timestamp[idx_end]; ++idx_end) ;
	//		return (values[value_idx].value PLUS make_timestamp_set(time_idx, idx_end));
	//	}
	//possible optimization: get the one that covers most timestamps.
}

void writeCache(char *message, char *rtn_message) {
	//The message should include: 1) the hash key; 2) the value; 3) the interval; 4) the tags;
	//The rtn_message should include: 1) success, or 2) conflict.
	
	//if (the key is no in cache) addValue(key, val, itv, tags, rtn_message);
	//else {
	//	values = cache[key];
	//	for (idx = 0; idx < values.length && values[idx].interval.end < itv.begin; ++idx) ;
	//	if (idx == values.length) addValue(key, val, itv, tags, rtn_message);
	//	else 
	//		if (values[idx].begin <= itv.end) return CONFLICT;	
	//		else addValue(key, val, itv, rtn_message);
	//}
}

void respondClient(char *message, char *rtn_message) {
	//if (read) readCache(message, rtn_message);
	//else if (write) writeCache();
	//else if (commit) commiteTags();
	//else if (abort) clearTags();
	//else if (begin-ro || begin-rw) clearTags();
	//else return ERROR;
}

void listenToClient() {
	int file_descriptor1,file_descriptor2;
	struct sockaddr_in server_address, client_address;
	socklen_t client_len;
	char message[MSG_LEN];
	char rtn_message[MSG_LEN];

	bzero((char *) &server_address, sizeof(server_address));
	printf("Server port number: %d\n",PORT_NUM);
	printf("Server listen maximum: %d\n",LISTEN_NUM);
	
	server_address.sin_family = AF_INET;
	server_address.sin_port = htons(PORT_NUM); //network byte order
	file_descriptor1 = socket(AF_INET,SOCK_STREAM,0); // port to port; stream; TCP
	if(file_descriptor1 == -1) {
		printf("opening socket failed\n");
		return 1;
	}
	if(bind(file_descriptor1, (struct sockaddr *)&server_address,sizeof(server_address)) < 0) {
		printf("binding failed");
		return 1;
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
		
		respondClient(message, rtn_message);

		/*
		printf("Input message:");
		
		scanf("%s",message);
		if(strcmp(message,"quit") == 0) {
			close(file_descriptor1);
			close(file_descriptor2);
			return 1;
		}
		*/
		
		if(0 > write(file_descriptor2, message, MSG_LEN)) {
			printf("error in writing message.\n");
			close(file_descriptor1);
			close(file_descriptor2);
			return 1;
		}
		close(file_descriptor2);
	}
	close(file_descriptor1);
}

int main() {
	//TODO multi-thread
	listenToDB();
	listenToClient();
	return 0;
}

