import csv

bookdata=[]
borrowerdata=[]
with open(r"C:\Users\HONGYU\Desktop\CS6360Project\book.csv",'r',encoding="utf-8") as bookinfo:
    bookline=csv.reader(bookinfo,delimiter="\n")
    for row in bookline:
         bookdata.append(list(row[0].split("\t")))
book_header=bookdata.pop(0)
with open(r"C:\Users\HONGYU\Desktop\CS6360Project\borrower.csv","r",encoding="utf-8") as borrowerinfo:
    borrowerline=csv.reader(borrowerinfo,delimiter="\n")
    for row in borrowerline:
        borrowerdata.append((list(row[0].split(","))))
borrower_header=borrowerdata.pop(0)
for book in bookdata:
    book[3]=book[3].split(",")