CREATE DATABASE LibrarySystem;
USE LibrarySystem;
CREATE TABLE BOOK (
  isbn        character(10) not null,
  title      varchar(250) not null, 
  num_in_stock int not null,
  CONSTRAINT pk_Book primary key (isbn)
);
CREATE TABLE BOOK_AUTHORS (
  isbn        character(10) not null,
  author_id      int not null, 
  CONSTRAINT pk_book_authors primary key (isbn, Author_id),
  CONSTRAINT fk_book_authors_book foreign key(isbn) references BOOK(isbn)
);
CREATE TABLE AUTHORS (
  author_id      int not null,
  author_name      varchar(100) not null, 
  CONSTRAINT pk_library_branch primary key (Author_id)
);
CREATE TABLE BORROWER (
  card_id  char(6) not null,
  ssn    char(9) not null,
  borrower_name   varchar(50) not null,
  borrower_address  varchar(100) not null,  
  borrower_phone    char(14), 
  CONSTRAINT pk_borrower primary key (card_id)
);
CREATE TABLE BOOK_LOANS (
  loan_id  int auto_increment not null,
  isbn    character(10) not null,
  card_id   char(6) not null, 
  date_out    date not null,  
  due_date date  not null, 
  date_in date, 
  CONSTRAINT pk_book_loans primary key (loan_id),
  CONSTRAINT fk_book_loans_book foreign key(isbn) references BOOK(isbn),
  CONSTRAINT fk_book_loans_borrower foreign key(card_id) references BORROWER(card_id)
);
CREATE TABLE  FINES(
 loan_id  int not null,
 fine_amt decimal(10,2) not null,
 paid boolean not null,
 CONSTRAINT pk_fines primary key(loan_id),
 CONSTRAINT fk_book_loans_loan_id foreign key (loan_id) references BOOK_LOANS(loan_id)
);