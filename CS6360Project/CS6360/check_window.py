from tkinter import *
from tkinter import ttk
from tkinter import messagebox
import connectdb
import SearchFunction
import datetime

class check_window():

    #check_in_window
    def new_check_in_window(self):
        check_in_window=Tk()
        check_in_window.wm_title("Check in window")

        def search_loans():
            def check_paid(loan_id):
                query_check_paid = ("Select paid FROM FINES WHERE loan_id=%s" %loan_id)
                connectdb.cursor.execute(query_check_paid)
                paid=FALSE
                for fine in connectdb.cursor:
                    if fine[0]==True:
                        paid=True
                return paid
            def check_in_sql_query_no_loan(loan_id,loan_isbn):
                query_check_in = ("Update BOOK_LOANS set date_in=curdate() where loan_id=%s" % loan_id)
                query_update_stock = ("Update BOOK set num_in_stock=1 where isbn=\"%s\"" % loan_isbn)
                connectdb.cursor.execute(query_check_in)
                connectdb.cursor.execute(query_update_stock)
                connectdb.connect_database.commit()
            def pay_fine_before_check_in(loan_id,fine_amt):
                query_select_fine_loan_id=("Select * from FINES where loan_id=%s" %loan_id)
                connectdb.cursor.execute(query_select_fine_loan_id)
                exist_or_not=False
                for i in connectdb.cursor:
                    exist_or_not=True
                if exist_or_not==False:
                    query_insert_new_record=("Insert into FINES VALUES ($s,%s,%s)")
                    connectdb.cursor.execute(query_insert_new_record,(loan_id,fine_amt,0))
                if exist_or_not==True:
                    query_update_exit_record=("Update FINES set fine_amt=%s where loan_id=%s")
                    connectdb.cursor.execute(query_update_exit_record,(fine_amt,loan_id))
                connectdb.connect_database.commit()

            def check_in():
                if len(loan_result_tree.selection())==0:
                    messagebox.showinfo("check in error","please select an loan before check in")
                if len(loan_result_tree.selection())>0:
                    current_selected=loan_result_tree.selection()[0]
                    loan_id=loan_result_tree.item(current_selected)["text"]
                    query_get_certain_loan_id=("Select isbn,date_out,due_date From BOOK_LOANS where loan_id=%s" %loan_id)
                    connectdb.cursor.execute(query_get_certain_loan_id)
                    for i in connectdb.cursor:
                        loan_isbn=i[0]
                        due_date=i[2]
                    if datetime.date.today()<=due_date:
                        check_in_sql_query_no_loan(loan_id,loan_isbn)
                        messagebox.showinfo("check in success","Checking in is successful!")
                        check_in_window.destroy()
                    if datetime.date.today()>due_date:
                        paid_or_not=check_paid(loan_id)
                        if paid_or_not==True:
                            check_in_sql_query_no_loan(loan_id,loan_isbn)
                            messagebox.showinfo("check in success", "Checking in is successful!")
                            check_in_window.destroy()
                        else:
                            delta=datetime.date.today()-due_date
                            fine_amount = "%.2f" %(0.25*delta.days)
                            pay_fine_before_check_in(loan_id,fine_amount)
                            messagebox.showinfo("check in error","You can't check in, this loan have fine %s, please pay before check in" %fine_amount)

            search_book_list = SearchFunction.loan_result()
            search_result = search_book_list.search(isbn_entry.get(),card_id_entry.get(),part_of_borrower_name_entry.get())
            loan_result_tree =ttk.Treeview(check_in_window, columns=("A", "B", "C","D","E"),height=10)
            scrollbar=Scrollbar(check_in_window,orient="vertical")
            loan_result_tree.heading("#0", text="LOAN_ID")
            loan_result_tree.column("#0", minwidth=0, width=100, stretch="NO")
            loan_result_tree.heading("A", text="ISBN")
            loan_result_tree.column("A", minwidth=0, width=100, stretch="NO")
            loan_result_tree.heading("B", text="CARD_ID")
            loan_result_tree.column("B", minwidth=0, width=100, stretch="NO")
            loan_result_tree.heading("C", text="DATE_OUT")
            loan_result_tree.column("C", minwidth=0, width=100, stretch="NO")
            loan_result_tree.heading("D", text="DUE_DATE")
            loan_result_tree.column("D", minwidth=0, width=100, stretch="NO")
            loan_result_tree.heading("E", text="DATE_IN")
            loan_result_tree.column("E", minwidth=0, width=100, stretch="NO")
            scrollbar.configure(command=loan_result_tree.yview)
            loan_result_tree.configure(yscrollcommand=scrollbar.set)
            scrollbar.pack(side="right", fill=Y)
            for i in range(len(search_result)):
                loan_result_tree.insert("","end",text=search_result[i][0],values=(search_result[i][1],search_result[i][2],search_result[i][3],search_result[i][4],search_result[i][5]))
            loan_result_tree.pack(side="top",fill="both")
            #Check  in Button
            check_in_Button=Button(check_in_window,text="Check in",command=check_in,width=20)
            check_in_Button.pack(side="bottom")

        #isbn
        label_isbn=Label(check_in_window,text="ISBN",relief=RAISED,width=50,bg="Green Yellow")
        label_isbn.pack(side="top")
        isbn_entry=Entry(check_in_window,width=50)
        isbn_entry.pack(side="top")

        #card_id
        label_card_id=Label(check_in_window,text="CARD_ID",relief=RAISED,width=50,bg="Green Yellow")
        label_card_id.pack(side="top")
        card_id_entry=Entry(check_in_window,width=50)
        card_id_entry.pack(side="top")

        #part_of_borrower_name
        label_part_of_borrower_name=Label(check_in_window,text="First Name or Last Name of the Borrower",relief=RAISED,width=50,bg="Green Yellow")
        label_part_of_borrower_name.pack(side="top")
        part_of_borrower_name_entry=Entry(check_in_window,width=50)
        part_of_borrower_name_entry.pack(side="top")

        #QUIT Button
        #QUIT_Button=Button(check_in_window,text="QUIT",command=check_in_window.quit,width=20)
        #QUIT_Button.pack(side="bottom")

        #Search Result Button
        Search_Result_Button=Button(check_in_window,text="Search",command=search_loans,width=20)
        Search_Result_Button.pack(side="bottom")
        check_in_window.mainloop()


    check_out_isbn=[]
    #check_out_window
    def new_check_out_window(self):
        check_out_window = Tk()
        check_out_window.wm_title("CHECKOUT")
        def check_availability(isbn):
            query_book_availability=("Select num_in_stock from BOOK where isbn=%s" %isbn)
            connectdb.cursor.execute(query_book_availability)
            for i in connectdb.cursor:
                if(i[0]==0):
                    messagebox.showinfo("check out error","This book is not available right now")
                    return False
            return True

        def check_less_than_three(card_id):
            query_less_than_three=("Select card_id,count(*) from BOOK_LOANS where card_id=%s group by card_id" %card_id)
            connectdb.cursor.execute(query_less_than_three)
            for i in connectdb.cursor:
                if(i[1]>=3):
                    messagebox.showinfo("check out error","You already have 3 loans")
                    return False
            return True

        def check_in_database(isbn,card_id):
            query_check_in_database=("Select * from BOOK_LOANS where isbn=%s and card_id=%s and date_in is null" %(isbn,card_id))
            connectdb.cursor.execute(query_check_in_database)
            for i in connectdb.cursor:
                return True
            return False

        def check_out():
            self.check_out_isbn=entry_book_isbn.get()
            self.check_out_card_id=entry_card_id.get()
            fine_to_continue=check_availability(self.check_out_isbn)
            if fine_to_continue==True:
                fine_to_continue=check_less_than_three(self.check_out_card_id)
            if fine_to_continue==False:
                exit()
            if fine_to_continue==True:
                if check_in_database(self.check_out_isbn,self.check_out_card_id)==False:
                    query_check_out=("Insert into BOOK_LOANS(isbn,card_id,date_out,due_date) values (%s,%s,curdate(),curdate()+14)")
                    connectdb.cursor.execute(query_check_out,(self.check_out_isbn, self.check_out_card_id))
                    query_update_availability=("Update BOOK set num_in_stock=0 where isbn=\"%s\"" %self.check_out_isbn)
                    connectdb.cursor.execute(query_update_availability)
                    connectdb.connect_database.commit()
                    messagebox.showinfo("sucess","Check out successfully")

        label_book_isbn=Label(check_out_window,text="Please input the ISBN of the book:")
        label_book_isbn.pack({"side":"left","side":"top"})
        entry_book_isbn=Entry(check_out_window)
        entry_book_isbn.insert(0,self.check_out_isbn)
        entry_book_isbn.pack({"side":"right","side":"top"})
        label_card_id=Label(check_out_window,text="Please input the Borrower's card ID:")
        label_card_id.pack({"side":"left","side":"top"})
        entry_card_id=Entry(check_out_window)
        entry_card_id.pack({"side":"right","side":"top"})
        check_out_button=Button(check_out_window,text="Check_Out",command=check_out)
        check_out_button.pack({"side":"bottom"})
        check_out_window.mainloop()

