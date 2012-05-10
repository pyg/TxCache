#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "TxCache.h"
#include "libpq-fe.h"

//gcc -shared -fpic -o libfoo.so -I/usr/lib/jvm/jdk1.7.0_03/include/ -I/usr/lib/jvm/jdk1.7.0_03/include/linux/ foo.c
//gcc libpqtest.c -o libpqtest -I/usr/local/pgsql/include -lpq

//g++ proxy.c -shared -fpic -o libproxy.so -I/usr/lib/jvm/jdk1.7.0_03/include/ -I/usr/lib/jvm/jdk1.7.0_03/include/linux/ -I/usr/local/pgsql/include -lpq

const char *umass_initialization_success = "SUCCESS";
const char *umass_initialization_fail = "FAIL";

const int umass_max_number_of_connections = 10;
const char *umass_too_many_connections = "TOO MANY CONNECTIONS";
const char *umass_connection_error = "UNKNOWN CONNECTION";
const int umass_connection_id_posi = 4;

const int umass_pqstatus_CONNECTION_OK = 0;
const int umass_pqstatus_CONNECTION_FAIL = 1;

const int umass_max_number_of_results = 20;
const char *umass_too_many_results = "TOO MANY RESULTS";
const char *umass_result_error = "UNKNOWN RESULT";

const int umass_pqstatus_PGRES_COMMAND_OK = 1;
const int umass_pqstatus_PGRES_COMMAND_FAIL = 0;

struct UMASS_PGresult {
	PGresult *res;
	char *res_id;
};

struct UMASS_PGconn {
	PGconn *conn;
	char *conn_id;
	int number_of_results;
	UMASS_PGresult results[umass_max_number_of_results];
};

int umass_number_of_connections;
UMASS_PGconn conns[umass_max_number_of_connections];

//==============================================================================

int UMASS_getConnId(JNIEnv *env, jobject obj, jstring conninfo) {
	const char *cInfo = (env)->GetStringUTFChars(conninfo, 0);
	int conn_id = cInfo[umass_connection_id_posi] - '0';
	env->ReleaseStringUTFChars(conninfo, cInfo);
	if (conn_id < 0 || conn_id >= umass_max_number_of_connections) return -1;
	if (conns[conn_id].conn == NULL) return -1;
	return conn_id;
}

int UMASS_getResultId(JNIEnv *env, jobject obj, jstring resinfo) {
	const char *rInfo = (env)->GetStringUTFChars(resinfo, 0);
	int conn_id = rInfo[1] - '0';
	if (conn_id < 0 || conn_id >= umass_max_number_of_connections) return -1;
	if (conns[conn_id].conn == NULL) return -1;
	int res_id = (rInfo[3] - '0') * 10 + (rInfo[4] - '0');
	if (conn_id < 0 || conn_id >= umass_max_number_of_results) return -1;
	if (conns[conn_id].results[res_id].res == NULL) return -1;
	env->ReleaseStringUTFChars(resinfo, rInfo);
	
	return conn_id * 100 + res_id;
}

//==============================================================================

JNIEXPORT jstring JNICALL Java_TxCache_UMASSPQinitialize (JNIEnv *env, jobject obj)
{
	jstring ret = 0;
	umass_number_of_connections = 0;
	for (int i = 0; i < umass_max_number_of_connections; ++i) {
		conns[i].conn = NULL;
		
		conns[i].conn_id = (char*)malloc(10);
		if (conns[i].conn_id == NULL) {
			ret = env->NewStringUTF(umass_initialization_fail);
			return ret;
		}
		conns[i].conn_id[0] = 'c';
		conns[i].conn_id[1] = 'o';
		conns[i].conn_id[2] = 'n';
		conns[i].conn_id[3] = 'n';
		conns[i].conn_id[umass_connection_id_posi] = (char)(i + '0');
		conns[i].conn_id[5] = '\0';
		
		conns[i].number_of_results = 0;
		for (int j = 0; j < umass_max_number_of_results; ++j) {
			conns[i].results[j].res = NULL;
			conns[i].results[j].res_id = (char*)malloc(10);
			if (conns[i].results[j].res_id == NULL) {
				ret = env->NewStringUTF(umass_initialization_fail);
				return ret;
			}
			conns[i].results[j].res_id[0] = 'c';
			conns[i].results[j].res_id[1] = (char)(i + '0');
			conns[i].results[j].res_id[2] = 'r';
			conns[i].results[j].res_id[3] = (char)((j / 10) + '0');
			conns[i].results[j].res_id[4] = (char)((j % 10) + '0');
			conns[i].results[j].res_id[5] = '\0';
		}
		
		
	}
	ret = env->NewStringUTF(umass_initialization_success);
	return ret;
}

JNIEXPORT jstring JNICALL Java_TxCache_PQconnectdb (JNIEnv *env, jobject obj, jstring conninfo)
{
	jstring ret = 0;
	if (umass_number_of_connections == umass_max_number_of_connections) {
		ret = env->NewStringUTF(umass_too_many_connections);
		return ret;
	}
	for (int i = 0; i < umass_max_number_of_connections; ++i) if (conns[i].conn == NULL) {
		const char *cInfo = (env)->GetStringUTFChars(conninfo, 0);
		conns[i].conn = PQconnectdb(cInfo);
		env->ReleaseStringUTFChars(conninfo, cInfo);
		ret = env->NewStringUTF(conns[i].conn_id);
		return ret;
	}
	ret = env->NewStringUTF(umass_connection_error);
	return ret;
}

JNIEXPORT jint JNICALL Java_TxCache_PQstatus (JNIEnv *env, jobject obj, jstring conninfo)
{
	int conn_id = UMASS_getConnId(env, obj, conninfo);
	jint ret = umass_pqstatus_CONNECTION_FAIL;
	if (conn_id == -1) return ret;

	ret = PQstatus(conns[conn_id].conn);
	if (ret != CONNECTION_OK) ret = umass_pqstatus_CONNECTION_FAIL;
	else ret = umass_pqstatus_CONNECTION_OK;
	return ret;
}

JNIEXPORT jstring JNICALL Java_TxCache_PQerrorMessage (JNIEnv *env, jobject obj, jstring conninfo)
{
	int conn_id = UMASS_getConnId(env, obj, conninfo);
	jstring ret = 0;
	if (conn_id == -1) {
		ret = env->NewStringUTF(umass_connection_error);
		return ret;
	}
	
	ret = env->NewStringUTF(PQerrorMessage(conns[conn_id].conn));
	return ret;
}

JNIEXPORT jstring JNICALL Java_TxCache_PQexec (JNIEnv *env, jobject obj, jstring conninfo, jstring sqlstmt)
{
	int conn_id = UMASS_getConnId(env, obj, conninfo);
	jstring ret = 0;
	if (conn_id == -1) {
		ret = env->NewStringUTF(umass_connection_error);
		return ret;
	}
	if (conns[conn_id].number_of_results == umass_max_number_of_results) {
		ret = env->NewStringUTF(umass_too_many_results);
		return ret;
	}
	for (int i = 0; i < umass_max_number_of_results; ++i) if (conns[conn_id].results[i].res == NULL) {
		const char *sql = (env)->GetStringUTFChars(sqlstmt, 0);
		conns[conn_id].results[i].res = PQexec(conns[conn_id].conn, sql);
		env->ReleaseStringUTFChars(sqlstmt, sql);
		ret = env->NewStringUTF(conns[conn_id].results[i].res_id);
		return ret;
	}
	ret = env->NewStringUTF(umass_result_error);
	return ret;
}

JNIEXPORT jint JNICALL Java_TxCache_PQresultStatus (JNIEnv *env, jobject obj, jstring resinfo)
{
	int res_id = UMASS_getResultId(env, obj, resinfo);
	jint ret = umass_pqstatus_PGRES_COMMAND_FAIL;
	if (res_id == -1) return ret;

	ret = PQresultStatus(conns[res_id / 100].results[res_id % 100].res);
	if (ret != PGRES_COMMAND_OK) ret = umass_pqstatus_PGRES_COMMAND_FAIL;
	else ret = umass_pqstatus_PGRES_COMMAND_OK;
	return ret;
}

JNIEXPORT void JNICALL Java_TxCache_PQclear (JNIEnv *env, jobject obj, jstring resinfo)
{
	int res_id = UMASS_getResultId(env, obj, resinfo);
	if (res_id == -1) return;
	
	int conn_id = res_id / 100;
	res_id %= 100;
	PQclear(conns[conn_id].results[res_id].res);
	--conns[conn_id].number_of_results;
	conns[conn_id].results[res_id].res = NULL; 
}

JNIEXPORT void JNICALL Java_TxCache_PQfinish (JNIEnv *env, jobject obj, jstring conninfo)
{
	int conn_id = UMASS_getConnId(env, obj, conninfo);
	if (conn_id == -1) return;

	--umass_number_of_connections;
	for (int i = 0; i < umass_max_number_of_results; ++i) 
		if (conns[conn_id].results[i].res != NULL) {
			PQclear(conns[conn_id].results[i].res);
			conns[conn_id].results[i].res = NULL;
		}
	conns[conn_id].number_of_results = 0;
	PQfinish(conns[conn_id].conn);
	conns[conn_id].conn = NULL; 
}

JNIEXPORT jstring JNICALL Java_TxCache_PQcmdStatus (JNIEnv *env, jobject obj, jstring resinfo)
{
	int res_id = UMASS_getResultId(env, obj, resinfo);
	jstring ret = 0;
	if (res_id == -1) {
		ret = env->NewStringUTF(umass_result_error);
		return ret;
	}
	
	ret = env->NewStringUTF(PQcmdStatus(conns[res_id / 100].results[res_id % 100].res));
	return ret;
}

JNIEXPORT jint JNICALL Java_TxCache_PQntuples (JNIEnv *env, jobject obj, jstring resinfo)
{
	int res_id = UMASS_getResultId(env, obj, resinfo);
	jint ret = 0;
	if (res_id == -1) { //ERROR
		ret = PQntuples(NULL);
		return ret;
	}

	ret = PQntuples(conns[res_id / 100].results[res_id % 100].res);
	return ret;
}

JNIEXPORT jint JNICALL Java_TxCache_PQnfields (JNIEnv *env, jobject obj, jstring resinfo)
{
	int res_id = UMASS_getResultId(env, obj, resinfo);
	jint ret = 0;
	if (res_id == -1) { //ERROR
		ret = PQnfields(NULL);
		return ret;
	}

	ret = PQnfields(conns[res_id / 100].results[res_id % 100].res);
	return ret;
}

JNIEXPORT jstring JNICALL Java_TxCache_PQgetvalue (JNIEnv *env, jobject obj, jstring resinfo, jint row_num, jint col_num)
{
	int res_id = UMASS_getResultId(env, obj, resinfo);
	jstring ret = 0;
	int r = row_num;
	int c = col_num;
	if (res_id == -1) {
		ret = env->NewStringUTF(PQgetvalue(NULL, r, c));
		return ret;
	}
	
	ret = env->NewStringUTF(PQgetvalue(conns[res_id / 100].results[res_id % 100].res, r, c));
	return ret;
}


