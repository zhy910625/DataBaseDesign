import readcsv
import connectdb

class initialDatabase:
    def initialDatabase(self):
        #initial Three tables: BOOK, AUTHORS, BOOK_AUTHORS
        query_insert_book = ("Insert into BOOK VALUES (%s,%s,%s)")
        query_insert_author=("Insert into AUTHORS VALUES(%s,%s)")
        query_insert_book_author=("Insert into BOOK_AUTHORS VALUES(%s,%s)")
        author_list={}
        for i in range(len(readcsv.bookdata)):
            connectdb.cursor.execute(query_insert_book, (readcsv.bookdata[i][0], readcsv.bookdata[i][2],1))
            author_names=readcsv.bookdata[i][3]
            for author_name in set(author_names):
                if author_name not in author_list:
                    author_id = len(author_list) + 1
                    author_list[author_name]=author_id
                    connectdb.cursor.execute(query_insert_author,(author_id,author_name))
                if author_name in author_list:
                    author_id=author_list[author_name]
                connectdb.cursor.execute(query_insert_book_author,(readcsv.bookdata[i][0],author_id))

        #initial table BORROWER
        query_insert_borrower=("Insert into BORROWER VALUES (%s,%s,%s,%s,%s)")
        for borrower in readcsv.borrowerdata:
            card_id=borrower[0]
            ssn=borrower[1].replace("-","")
            borrower_name=borrower[2]+" "+borrower[3]
            borrower_address=borrower[5]+", "+borrower[6]+", "+borrower[7]
            borrower_phone=borrower[8]
            connectdb.cursor.execute(query_insert_borrower,(card_id,ssn,borrower_name,borrower_address,borrower_phone))

        connectdb.connect_database.commit()