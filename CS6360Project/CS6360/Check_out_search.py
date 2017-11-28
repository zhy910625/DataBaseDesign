import SearchFunction
import check_window
import unicodedata
from tkinter import *
from tkinter import ttk
from tkinter import messagebox


class search():
    def new_search_window(self):
        def search_book():
            def merge_author(original_set):
                isbn_dictionary = {}
                for i in original_set:
                    isbn_key = i[0]
                    if isbn_key not in isbn_dictionary:
                        isbn_dictionary[isbn_key] = []
                        isbn_dictionary[isbn_key] =str(i[2])
                    elif isbn_key in isbn_dictionary:
                        isbn_dictionary[isbn_key] += ", " + str((i[2]))
                book_isbn = []
                new_set = []
                for j in original_set:
                    if j[0] not in book_isbn:
                        book_isbn.append(j[0])
                        new_set.append([j[0], j[1],str(isbn_dictionary[j[0]]), j[3]])
                    if j[0] in book_isbn:
                        continue
                return new_set
            def check_out():
                if len(result_tree.selection())==0:
                    messagebox.showinfo("error","you should select a book before check out")
                if len(result_tree.selection())>0:
                    i = result_tree.selection()[0]
                    isbn = result_tree.item(i)["text"]
                    checkOut = check_window.check_window()
                    checkOut.check_out_isbn = isbn
                    checkOut.new_check_out_window()
            search_list = SearchFunction.book_result()
            search_result=search_list.search(entry_isbn.get(),entry_book_title.get(),entry_book_authors.get())
            search_result_new=merge_author(search_result)
            result_tree =ttk.Treeview(root, columns=("A", "B", "C"),height=10)
            scrollbar=Scrollbar(root,orient="vertical")
            result_tree.heading("#0", text="ISBN")
            result_tree.column("#0", minwidth=0, width=100, stretch="NO")
            result_tree.heading("A", text="BOOK TITLE")
            result_tree.column("A", minwidth=0, width=500, stretch="NO")
            result_tree.heading("B", text="AUTHOR(S)")
            result_tree.column("B", minwidth=0, width=500, stretch="NO")
            result_tree.heading("C", text="NUM")
            result_tree.column("C", minwidth=0, width=30, stretch="NO")
            scrollbar.configure(command=result_tree.yview)
            result_tree.configure(yscrollcommand=scrollbar.set)
            scrollbar.pack(side="right", fill=Y)
            for i in range(len(search_result_new)):
                result_tree.insert("","end",text=search_result_new[i][0],values=(search_result_new[i][1],search_result_new[i][2],search_result_new[i][3]))
            result_tree.pack(side="top",fill="both")
            check_out = Button(root, text="CHECK_OUT", command=check_out,width=20)
            check_out.pack(side="bottom")
        root = Tk()
        root.wm_title("SEARCH")
        #QUIT = Button(root,text="QUIT",command=quit,width=20)
        #QUIT.pack(side="bottom")

        #isbn_entry
        lab_isbn =Label(root,text="ISBN: Please enter isbn10",relief=RAISED,width=50,bg="orange")
        lab_isbn.pack(side="top")
        #lab_isbn.grid(column=0,row=1,sticky=E+W)
        entry_isbn = Entry(root,width=50)
        entry_isbn.pack(side="top")
        #entry_isbn.grid(column=1,row=1,sticky=E+W)

        #title_entry
        lab_book_title = Label(root,text="BOOK TITLE: Please enter keyword",relief=RAISED,width=50,bg="orange")
        lab_book_title.pack(side="top")
        entry_book_title = Entry(root,width=50)
        entry_book_title.pack(side="top")

        #book_authors_entry
        lab_book_authors = Label(root,text="BOOK AUTHOR(S),please enter author(s) seperated by comma",relief=RAISED,width=50,bg="orange")
        lab_book_authors.pack(side="top")
        entry_book_authors=Entry(root,width=50)
        entry_book_authors.pack(side="top")

        search = Button(root,text="Search",command=search_book,width=20)
        search.pack(side="bottom")
        root.mainloop()