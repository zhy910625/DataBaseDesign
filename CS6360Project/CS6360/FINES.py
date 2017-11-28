from tkinter import ttk
from tkinter import *
from tkinter import messagebox
import connectdb
import datetime

class Fine:
    def new_fine_window(self):
        fine_window=Tk()
        fine_window.wm_title("Fine")

        #show_card_id_fines
        def show_card_id_fines():
            def pay_fine():
                if len(fine_result_tree.selection())==0:
                    messagebox.showinfo("Fine Error","Please choose a fine before pay it")
                if len(fine_result_tree.selection())>0:
                    line_id= fine_result_tree.selection()[0]
                    loan_id= fine_result_tree.item(line_id)["text"]
                    query=("Update FINES set paid=true where loan_id=%s" %loan_id)
                    connectdb.cursor.execute(query)
                    connectdb.connect_database.commit()
            card_id=fine_entry_card_id.get()
            fine_result_tree =ttk.Treeview(fine_window, columns=("A", "B", "C"),height=10)
            scrollbar=Scrollbar(fine_window,orient="vertical")
            fine_result_tree.heading("#0", text="LOAN_ID")
            fine_result_tree.column("#0", minwidth=0, width=100, stretch="NO")
            fine_result_tree.heading("A", text="CARD_ID")
            fine_result_tree.column("A", minwidth=0, width=100, stretch="NO")
            fine_result_tree.heading("B", text="FINE_AMOUNT")
            fine_result_tree.column("B", minwidth=0, width=100, stretch="NO")
            fine_result_tree.heading("C", text="PAID")
            fine_result_tree.column("C", minwidth=0, width=30, stretch="NO")
            scrollbar.configure(command=fine_result_tree.yview)
            fine_result_tree.configure(yscrollcommand=scrollbar.set)
            scrollbar.pack(side="right", fill=Y)
            if len(card_id)>0:
                query_search_card_id=("select B.loan_id,B.card_id, F.fine_amt, F.paid from BOOK_LOANS as B RIGHT join FINES as F on B.loan_id=F.loan_id where B.card_id=%s" %card_id)
                connectdb.cursor.execute(query_search_card_id)
                for j in connectdb.cursor:
                    fine_result_tree.insert("","end",text=j[0],values=(j[1],j[2],j[3]))
            fine_result_tree.pack(side="top")
            pay_fine_button=Button(fine_window,text="Pay Fine",command=pay_fine,width=20)
            pay_fine_button.pack(side="top")

        #update_all_fines
        def update_all_fines():
            fine_set=[]
            query_all_unfinished_loan=("Select * from BOOK_LOANS where date_in is null")
            connectdb.cursor.execute(query_all_unfinished_loan)
            for i in connectdb.cursor:
                loan_id=i[0]
                due_date=i[4]
                today=datetime.date.today()
                if today>due_date:
                    delta=today-due_date
                    fine_amount="%.2f" %(0.25*delta.days)
                    fine_set.append([loan_id,fine_amount])
            query_all_fines=("Select * from FINES")
            connectdb.cursor.execute(query_all_fines)
            fine_loan_id=[]
            for j in connectdb.cursor:
                fine_loan_id.append(j[0])
            for a_loan in fine_set:
                if a_loan[0] not in fine_loan_id:
                    query_insert_new_fines=("Insert into FINES VALUES (%s,%s,%s)")
                    connectdb.cursor.execute(query_insert_new_fines,(a_loan[0],a_loan[1],0))
                    connectdb.connect_database.commit()
                if a_loan[0] in fine_loan_id:
                    query_update_exist_fines=("update FINES set fine_amt=%s where loan_id=%s")
                    connectdb.cursor.execute(query_update_exist_fines,(a_loan[1],a_loan[0]))
                    connectdb.connect_database.commit()
            messagebox.showinfo("successful","All fines are updated")
        fine_label_card_id=Label(fine_window,text="Card_id",relief=RAISED,width=30,bg="red")
        fine_label_card_id.pack(side="top")
        fine_entry_card_id=Entry(fine_window,width=30)
        fine_entry_card_id.pack(side="top")

        #Quit
        #fine_button_quit=Button(fine_window,text="QUIT",command=quit,width=20)
        #fine_button_quit.pack(side="bottom")

        #show fines
        fine_button_show_fines=Button(fine_window,text="Show Fines",command=show_card_id_fines,width=20)
        fine_button_show_fines.pack(side="bottom")

        #update all fines
        fine_button_update_all_fines=Button(fine_window,text="Update all Fines",command=update_all_fines,width=20)
        fine_button_update_all_fines.pack(side="bottom")
        fine_window.mainloop()

