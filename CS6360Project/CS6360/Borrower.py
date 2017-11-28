import connectdb
from tkinter import messagebox
from tkinter import *

class borrower():
    def new_borrower_window(self):
        new_borrower=Tk()
        new_borrower.wm_title("New Borrower")

        def check_name():
            if len(fname_entry.get())==0 and len(lname_entry.get())==0:
                messagebox.showinfo("name input error","Please input correct name")
                return False
            return True

        def check_ssn():
            if len(ssn_entry.get())!=9:
                messagebox.showinfo("ssn input error","Please input correct ssn")
                return False
            ssn_check=ssn_entry.get()
            query_ssn_check=("Select * from BORROWER where ssn=\"%s\"" %ssn_check)
            connectdb.cursor.execute(query_ssn_check)
            check=0
            for i in connectdb.cursor:
                check+=1
            if check>0:
                messagebox.showinfo("ssn input error","ssn is in the database, please input your ssn")
                return False
            return True

        def check_address():
            if len(address_entry.get())==0:
                messagebox.showinfo("address input error","Please input correct address")
                return False
            return  True

        def check_phone():
            if len(phone_entry.get())==10 or len(phone_entry.get())==0:
                return True
            messagebox.showinfo("phone input error","Please input correct phone")
            return False

        def assign_new_card_id():
            query_new_card_id=("select * from BORROWER order by card_id DESC limit 1")
            connectdb.cursor.execute(query_new_card_id)
            for i in connectdb.cursor:
                card_id=str(int(i[0])+1)
            new_card_id="0"*(6-len(card_id))+card_id
            return new_card_id

        def create_new_borrower():
            name_ok=check_name()
            ssn_ok=check_ssn()
            address_ok=check_address()
            phone_ok=check_phone()
            if name_ok==True and ssn_ok==True and address_ok==True and phone_ok==True:
                card_id=assign_new_card_id()
                print(card_id)
                name=fname_entry.get()+" "+lname_entry.get()
                ssn=ssn_entry.get()
                address=address_entry.get()
                if len(phone_entry.get())==10:
                    phone="("+phone_entry.get()[:3]+")"+" "+phone_entry.get()[3:6]+"-"+phone_entry.get()[6:]
                else:
                    phone=""
                query_insert_new_borrower=("Insert into BORROWER values(%s,%s,%s,%s,%s)")
                connectdb.cursor.execute(query_insert_new_borrower,(card_id,ssn,name,address,phone))
                connectdb.connect_database.commit()

                messagebox.showinfo("success!","your card id is %s" %card_id)



        #new borrower first and last name
        fname_label=Label(new_borrower,text="Please input your first name(required): ",relief=RAISED,width=50,bg="Deep Sky Blue")
        fname_label.pack(side="top")
        fname_entry=Entry(new_borrower)
        fname_entry.pack(side="top")
        lname_label=Label(new_borrower,text="Please input your last name(required): ",relief=RAISED,width=50,bg="Deep Sky Blue")
        lname_label.pack(side="top")
        lname_entry=Entry(new_borrower)
        lname_entry.pack(side="top")

        #new borrower ssn
        ssn_label=Label(new_borrower,text="Please input your ssn(required): ",relief=RAISED,width=50,bg="Deep Sky Blue")
        ssn_label.pack(side="top")
        ssn_entry=Entry(new_borrower)
        ssn_entry.pack(side="top")

        #new borrower address
        address_label=Label(new_borrower,text="Please input your address(required: road,city,state): ",relief=RAISED,width=50,bg="Deep Sky Blue")
        address_label.pack(side="top")
        address_entry=Entry(new_borrower)
        address_entry.pack(side="top")

        #new borrwer phone
        phone_label=Label(new_borrower,text="Please input your phone(10 numbers):",relief=RAISED,width=50,bg="Deep Sky Blue")
        phone_label.pack(side="top")
        phone_entry=Entry(new_borrower)
        phone_entry.pack(side="top")

        #quit button
        #quit_button=Button(new_borrower,text="QUIT",command=quit,width=20)
        #quit_button.pack(side="bottom")

        #create button
        create_button=Button(new_borrower,text="CREATE",command=create_new_borrower,width=20)
        create_button.pack(side="bottom")

        new_borrower.mainloop()