import Borrower
import FINES
import check_window
import Check_out_search
from tkinter import *

system_gui=Tk()
system_gui.wm_title("SYSTEM GUI")
#def search_book():

front_label=Label(system_gui,text="Please choose which thing do you want to do")
front_label.pack(side="top")
quit_botton=Button(system_gui,text="QUIT",command=quit,width=20)
quit_botton.pack(side="bottom")
#search_function=Button(system_gui,text="Search_Books",command=search_book)

#search book
def search_book():
    search_book_window=Check_out_search.search()
    search_book_window.new_search_window()
search_book_button=Button(system_gui,text="Search Book",command=search_book,width=20)
search_book_button.pack(side="top")

#check_out
def checking_out():
    check_out_window=check_window.check_window()
    check_out_window.new_check_out_window()
check_out_button=Button(system_gui,text="Check Out",command=checking_out,width=20)
check_out_button.pack(side="top")

#check_in
def checking_in():
    check_in_window=check_window.check_window()
    check_in_window.new_check_in_window()
check_in_button=Button(system_gui,text="Check In",command=checking_in,width=20)
check_in_button.pack(side="top")

#borrower
def borrower():
    new_borrower=Borrower.borrower()
    new_borrower.new_borrower_window()
newBorrower_button=Button(system_gui,text="Create a new Borrower",command=borrower,width=20)
newBorrower_button.pack(side="top")

def fine():
    fine_window=FINES.Fine()
    fine_window.new_fine_window()
fine_button=Button(system_gui,text="FINE",command=fine,width=20)
fine_button.pack(side="top")

system_gui.mainloop()