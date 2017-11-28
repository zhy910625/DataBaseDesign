import mysql.connector
import InitialDatabase

connect_database=mysql.connector.connect(user='root',password="ZHy@@910625",host='localhost')
cursor=connect_database.cursor()
query_show_databases=("show databases")
cursor.execute(query_show_databases)
database_lists=[]
for dbs in cursor:
    database_lists.append(dbs[0])
if "LibrarySystem" in database_lists:
    connect_database.cmd_init_db(database="LibrarySystem")
else:
    sql_content=open(r"C:/Users/HONGYU/Desktop/CS6360Project/createtable.sql","r")
    sql_script=sql_content.read()
    sql_script=sql_script.replace("\n","").split(";")
    for sql_line in sql_script:
        cursor.execute(sql_line)
    connect_database.commit()
    connect_database.cmd_init_db(database="LibrarySystem")
    initialDB=InitialDatabase.initialDatabase()
    initialDB.initialDatabase()

