import connectdb
class book_result:
    def search_isbn(self,isbn):
        query_isbn= ("select B.isbn,B.title,ABA.author_name, B.num_in_stock FROM (select A.author_id,A.author_name,BA.isbn from AUTHORS as A left join BOOK_AUTHORS as BA on A.author_id=BA.author_id) as ABA left join BOOK as B on ABA.isbn=B.isbn where B.isbn=\"%s\"" %isbn)
        connectdb.cursor.execute(query_isbn)
        isbn_set=[]
        for i in connectdb.cursor:
            isbn_set.append(i)
        return isbn_set

    def search_title(self,title):
        query_title=("select B.isbn,B.title,ABA.author_name, B.num_in_stock FROM (select A.author_id,A.author_name,BA.isbn from AUTHORS as A left join BOOK_AUTHORS as BA on A.author_id=BA.author_id) as ABA left join BOOK as B on ABA.isbn=B.isbn where B.title like \"%%%s%%\"" %title)
        connectdb.cursor.execute(query_title)
        title_set=[]
        for i in connectdb.cursor:
            title_set.append(i)
        return title_set

    def search_author(self,author):
        author_set=[]
        author=author.split(",")
        for i in author:
            query_author=("select B.isbn,B.title,ABA.author_name, B.num_in_stock FROM (select A.author_id,A.author_name,BA.isbn from AUTHORS as A left join BOOK_AUTHORS as BA on A.author_id=BA.author_id) as ABA left join BOOK as B on ABA.isbn=B.isbn where ABA.author_name like \"%%%s%%\"" %i)
            connectdb.cursor.execute(query_author)
            for j in connectdb.cursor:
                author_set.append(j)
        return author_set

    def search(self,isbn,title,author):
        final_set=[]
        if len(isbn)>0:
            final_set.extend(self.search_isbn(isbn))
        if len(title)>0:
            final_set.extend(self.search_title(title))
        if len(author)>0:
            final_set.extend(self.search_author(author))
        return final_set



class loan_result:
    def search_isbn(self,isbn):
        query_isbn= ("select * FROM BOOK_LOANS where isbn=\"%s\" and date_in is null" %isbn)
        connectdb.cursor.execute(query_isbn)
        isbn_set=[]
        for i in connectdb.cursor:
            isbn_set.append(i)
        return isbn_set

    def search_card_id(self,card_id):
        query_card_id=("select * FROM BOOK_LOANS where card_id=\"%s\" and date_in is null" %card_id)
        connectdb.cursor.execute(query_card_id)
        card_id_set=[]
        for i in connectdb.cursor:
            card_id_set.append(i)
        return card_id_set

    def search_borrower_name(self,borrower_name):
        borrower_set=[]
        query_borrower_name=("select BA.* from BOOK_LOANS as BA left join BORROWER as B on BA.card_id=B.card_id where B.borrower_name like \"%%%s%%\" and date_in is NULL" %borrower_name)
        connectdb.cursor.execute(query_borrower_name)
        for j in connectdb.cursor:
            borrower_set.append(j)
        return borrower_set

    def search(self,isbn,card_id,borrower_name):
        final_set=[]
        if len(isbn)>0:
            final_set.extend(self.search_isbn(isbn))
        if len(card_id)>0:
            final_set.extend(self.search_card_id(card_id))
        if len(borrower_name)>0:
            final_set.extend(self.search_borrower_name(borrower_name))
        return final_set