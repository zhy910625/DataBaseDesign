import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.*;

public class HongyuBase {

	/* This can be changed to whatever you like */
	static String prompt = "DavisBaseSql> ";
	static String version = "v1.0";
	static String copyright = "2017 CS6360 Hongyu Zhang";
	static boolean isExit = false;
	static ZoneId zoneId = ZoneId.of ( "America/Chicago" );
	static String operatorList="= != >= <= > <";
	static ArrayList<String> operator=new ArrayList<String>(Arrays.asList(operatorList.split(" ")));
	/*
	 * Page size for all files is 512 bytes by default.
	 * You may choose to make it user modifiable
	 */
	static int pageSize = 512; 

	/* 
	 *  The Scanner class is used to collect user commands from the prompt
	 *  There are many ways to do this. This is just one.
	 *
	 *  Each time the semicolon (;) delimiter is entered, the userCommand 
	 *  String is re-populated.
	 */
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	
	/** ***********************************************************************
	 *  Main method
	 */
    public static void main(String[] args) {
    	dbInitialization();

		/* Display the welcome screen */
		splashScreen();

		/* Variable to collect user input from the prompt */
		String userCommand = ""; 

		while(!isExit) {
			System.out.print(prompt);
			/* toLowerCase() renders command case insensitive */
			userCommand = scanner.next().replace("\n", "").replace("\r", "").trim().toLowerCase();
			// userCommand = userCommand.replace("\n", "").replace("\r", "");
			ArrayList<String> commandString=new ArrayList<String>(Arrays.asList(userCommand.split(";")));
			for(int i=0;i<commandString.size();i++){
				parseUserCommand(commandString.get(i));
			}
			//parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");


	}
    /** ***********************************************************************
	 *  Method definitions
	 */

	/**
	 *  Display the splash screen
	 */
	public static void splashScreen() {
		System.out.println(line("-",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-",80));
	}
	
	/**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
		/**
		 *  Help: Display supported commands
		 */
		public static void help() {
			System.out.println(line("*",80));
			System.out.println("SUPPORTED COMMANDS");
			System.out.println("All commands below are case insensitive");
			System.out.println();
			System.out.println("\tCreate Table table_name (\n\tColumn_name2 int primary key,\n\tColumn_name2 type [not null],\n\t.....\n\t);                        Create a new table");
			System.out.println("\tSelect * From table_name;                        Display all records in the table.");
			System.out.println("\tSelect * From table_name WHERE primaryKey = <value>;  Display records whose rowid is <id>.");
			System.out.println("\tDrop table table_name;                           Remove table data and its schema.");
			System.out.println("\tInsert into table_name [column1,column2] values (value1,value2);                       Insert a record into a table");
			System.out.println("\tDelete from table_name where primaryKey = <value>;                        Delete a record from a table");
			System.out.println("\tUpdate table_name set column_name = <value> \n\t[where column_name = <value>];                       update a record");
			System.out.println("\tVersino;                                         Show the program version.");
			System.out.println("\tHelp;                                            Show this help information");
			System.out.println("\tExit;                                            Exit the program");
			System.out.println();
			System.out.println();
			System.out.println(line("*",80));
		}

	/** return the DavisBase version */
	public static String getVersion() {
		return version;
	}
	
	public static String getCopyright() {
		return copyright;
	}
	
	public static void displayVersion() {
		System.out.println("HongyuBaseLite Version " + getVersion());
		System.out.println(getCopyright());
	}
		
	public static void parseUserCommand (String userCommand) {
		
		/* commandTokens is an array of Strings that contains one token per array element 
		 * The first token can be used to determine the type of command 
		 * The other tokens can be used to pass relevant parameters to each command-specific
		 * method inside each case statement */
		// String[] commandTokens = userCommand.split(" ");
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.toLowerCase().split(" ")));

		/*
		*  This switch handles a very small list of hardcoded commands of known syntax.
		*  You will want to rewrite this method to interpret more complex commands. 
		*/
		switch (commandTokens.get(0)) {
			case "show":
				parseShowTable(userCommand);
				break;
			case "select":
				parseQueryString(userCommand);
				break;
			case "drop":
				dropTable(userCommand);
				break;
			case "create":
				parseCreateString(userCommand);
				break;
			case "insert":
				parseInsertTable(userCommand);
				break;
			case "help":
				help();
				break;
			case "version":
				displayVersion();
				break;
			case "exit":
				isExit = true;
				break;
			case "quit":
				isExit = true;
			case "delete":
				parseDelete(userCommand);
				break;
			case "update":
				updateValue(userCommand);
				break;
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
				break;
		}
	}
	

	/**
	 *  Stub method for executing queries
	 *  @param queryString is a String of the user input
	 */
	public static void parseQueryString(String queryString) {
		int wherePosition=queryString.indexOf("where");
		String conditionString=queryString.substring(wherePosition+5).trim();
		String modifiedString=queryString;
		for(int i=0;i<operator.size();i++){
			String oper=operator.get(i);
			int index=conditionString.indexOf(oper);
			int length=operator.get(i).length();
			if(index!=-1){
				String beforeOperator=conditionString.substring(0, index).trim();
				String afterOperator=conditionString.substring(index+length).trim();
				modifiedString=beforeOperator+" "+oper+" "+afterOperator;
				queryString=queryString.substring(0,wherePosition+5).trim()+" "+modifiedString.trim();
			}
		}
		ArrayList<String> queryCommand = new ArrayList<String>(Arrays.asList(queryString.split(" ")));
		if(queryCommand.size()!=4&&queryCommand.size()!=8){
			System.out.println("Wrong syntax, please check");
			return;
		}
		String table_name=queryCommand.get(3);
		ArrayList<String> dbNameList=dbList();
		String path="";
		int[] column_sizes={};
		//check if table_name exists
		if(!dbNameList.contains(table_name)){
			System.out.println("this table is not in the database, please check!!");
			return;
		}
		ArrayList<String> column_names=getColumnsName(table_name);
		ArrayList<String> column_types=getColumnTypes(table_name);
		if((table_name.equals("davisbase_tables"))||(table_name.equals("davisbase_columns"))){
			path="data\\catalog\\";
			if(table_name.equals("davisbase_columns")){
				int[] size={4,20,20,8,1,4};
				column_sizes=size;
			}
			if(table_name.equals("davisbase_tables")){
				int[] size={4,20};
				column_sizes=size;
			}
		}
		else{
			path="data\\user_data\\";
			column_sizes=getSize(column_types);
		}
		String table_path=path+table_name+".tbl";
		ArrayList<Object[]> content=getColumnsContent(table_path,column_sizes,column_types);
		if(queryCommand.size()==8){
			String column_filter=queryCommand.get(5);
			String operator=queryCommand.get(6);
			String filter_value=queryCommand.get(7).toLowerCase();
			filter_value=filter_value.replace("\"", "");
			content=getFilteredContent(content,column_names,column_types,column_filter,operator,filter_value);
		}
		if(queryCommand.get(1).equals("*")){
			format_print(content,column_sizes,column_names,column_names,column_types);
		}
		else{
			ArrayList<String> need_columns=new ArrayList<String>(Arrays.asList(queryCommand.get(1).split(",")));
			for(int i=0;i<need_columns.size();i++){
				if(!column_names.contains(need_columns.get(i))){
					System.out.println("Wrong column name(s), please check! ");
					return;
				}
			}
			format_print(content,column_sizes,column_names,need_columns,column_types);	
		}
	}
	
	
	/**
	 *  Stub method for creating new tables
	 *  @param queryString is a String of the user input
	 */
	public static void parseCreateString(String createTableString) {
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));
		//get all existing tables and check if table is already exist
		ArrayList<String> dbName=dbList();
		if (dbName.contains(createTableTokens.get(2))){
			System.out.println("This table is already in the database");
			System.out.println("please confirm your sentence and try it again");
			return ;
		}
		//end check
		String columnString=createTableString.substring(createTableString.indexOf("(")+1,createTableString.indexOf(")"));
		ArrayList<String> columns=new ArrayList<String>(Arrays.asList(columnString.split(",")));
		ArrayList<ArrayList<String>> columnsInformation=new ArrayList<ArrayList<String>>();
		for(int i=0;i<columns.size();i++){
			ArrayList<String> a_column=new ArrayList<String>(Arrays.asList(columns.get(i).split(" ")));
			columnsInformation.add(a_column);
		}
		String tableName=createTableTokens.get(2);
		creataNewTable(tableName,columnsInformation);
	}
	
	public static void parseShowTable(String showTableString){
		/***
		if(showTableString!="show tables"){
			System.out.println("wrong syntax");
			return;
		}
		***/
		parseUserCommand("select * from davisbase_tables");
	}
	
	public static void parseInsertTable(String insertIntoString){
		int position=insertIntoString.toLowerCase().indexOf("values");
		String subString1=insertIntoString.substring(0,position);
		String subString2=insertIntoString.substring(position);
		String tableName="";
		ArrayList<String> column_List=new ArrayList<String>();
		if(subString1.indexOf("(")==-1){
			ArrayList<String> subStringList1=new ArrayList<String>(Arrays.asList(subString1.split(" ")));
			tableName=subStringList1.get(2);
		}
		else {
			ArrayList<String> subStringList1=new ArrayList<String>(Arrays.asList(subString1.split(" ")));
			column_List=new ArrayList<String>(Arrays.asList(subString1.substring(subString1.indexOf("(")+1,subString1.indexOf(")")).replaceAll("\\s+", "").split(",")));
			tableName=subStringList1.get(2);
		}
		ArrayList<String> tableNames=dbList();
		if(!tableNames.contains(tableName)){
			System.out.println("Wrong table name, please check!");
			return;
		}
		String values=subString2.substring(subString2.indexOf("(")+1,subString2.indexOf(")")).replaceAll("\\s+", "").replaceAll("'", "").replaceAll("\"", "");
		ArrayList<String> values_List=new ArrayList<String>(Arrays.asList(values.split(",")));
		ArrayList<String> column_types=getColumnTypes(tableName);
		ArrayList<String> column_names=getColumnsName(tableName);
		for(int i=0;i<column_List.size();i++){
			if(!column_names.contains(column_List.get(i))){
				System.out.println("please input correct column name");
				return;
			}
		}
		if(column_List.size()==0){
			if(values_List.size()!=column_types.size()){
				System.out.println("Please make sure the number of values");
				return;
			}
		}
		else{
			if(values_List.size()!=column_List.size()){
				System.out.println("Please make sure the number of columns and the number of values are correct!");
				return;
			}
		}
		insertIntoTable(tableName,column_List,values_List,column_types);
	}
	
	/**
	 *  Stub method for dropping tables
	 *  @param dropTableString is a String of the user input
	 */
	public static void dropTable(String dropTableString) {
		ArrayList<String> commandTokens=new ArrayList<String>(Arrays.asList(dropTableString.split(" ")));
		String tableName=commandTokens.get(2);
		if((tableName.equals("davisbase_tables"))||(tableName.endsWith("davisbase_columns"))){
			System.out.println("System table can't be dropped!!!");
			return;
		}
		ArrayList<String> tableNames=dbList();
		if(!tableNames.contains(tableName)){
			System.out.println("Please input correct table name!");
			return;
		}
		String path="";
		path="data\\user_data\\"+tableName+".tbl";
		String davisTablePath="data\\catalog\\davisbase_tables.tbl";
		String davisColumnPath="data\\catalog\\davisbase_columns.tbl";
		ArrayList<String> davisTableTypes=getColumnTypes("davisbase_tables");
		ArrayList<String> davisColumnTypes=getColumnTypes("davisbase_columns");
		int[] davisColumnSize={4,20,20,8,1,4};
		int[] davisTableSize={4,20};
		ArrayList<Object[]> davisTableContent=getColumnsContent(davisTablePath,davisTableSize,davisTableTypes);
		ArrayList<Object[]> davisColumnContent=getColumnsContent(davisColumnPath,davisColumnSize,davisColumnTypes);
		for(int i=0;i<davisTableContent.size();i++){
			if(davisTableContent.get(i)[1].equals(tableName)){
				davisTableContent.remove(i);
			}
		}
		ArrayList<Object[]> removedColumn=new ArrayList<Object[]>();
		for(int j=0;j<davisColumnContent.size();j++){
			if(davisColumnContent.get(j)[1].equals(tableName)){
				removedColumn.add(davisColumnContent.get(j));
			}
		}
		davisColumnContent.removeAll(removedColumn);
		returnDeletedFile("davisbase_tables",davisTableContent);
		for(int i=0;i<removedColumn.size();i++){
			returnDeletedFile("davisbase_columns",davisColumnContent);
		}
		try{
			File file=new File(path);
			file.delete();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void parseDelete(String userCommand){
		int wherePosition=userCommand.indexOf("where");
		String conditionString=userCommand.substring(wherePosition+5).trim();
		String modifiedString=userCommand.trim();
		for(int i=0;i<operator.size();i++){
			String oper=operator.get(i);
			int index=conditionString.indexOf(oper);
			int length=operator.get(i).length();
			if(index!=-1){
				String beforeOperator=conditionString.substring(0, index).trim();
				String afterOperator=conditionString.substring(index+length).trim();
				modifiedString=beforeOperator+" "+oper+" "+afterOperator;
				userCommand=userCommand.substring(0,wherePosition+5).trim()+" "+modifiedString.trim();
			}
		}
		ArrayList<String> commandTokens=new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		if(commandTokens.size()!=7){
			System.out.println("Wrong Syntax!!");
			return;
		}
		String tableName=commandTokens.get(2);
		ArrayList<String> tableNames=dbList();
		if(!tableNames.contains(tableName)){
			System.out.println("Please input correct table name");
			return;
		}
		String columnName=commandTokens.get(4);
		int pkValue=Integer.parseInt(commandTokens.get(6));
		ArrayList<String> columnNames=getColumnsName(tableName);
		if(!columnNames.get(0).equals(columnName)){
			System.out.println("Please check your primary key!");
			return;
		}
		ArrayList<String> columnTypes=getColumnTypes(tableName);
		int[] column_sizes=getSize(columnTypes);
		String tablePath="data\\user_data\\"+tableName+".tbl";
		ArrayList<Object[]> tableContent=getColumnsContent(tablePath,column_sizes,columnTypes);
		for(int i=0;i<tableContent.size();i++){
			int pk=(Integer)tableContent.get(i)[0];
			if(pk==pkValue){
				tableContent.remove(i);
			}
		}
		returnDeletedFile(tableName,tableContent);
	}
	public static void updateValue(String userCommand){
		int setPosition=userCommand.indexOf("set");
		int wherePosition=userCommand.indexOf("where");
		if(wherePosition!=-1){
			String setString=userCommand.substring(setPosition+3,wherePosition).trim();
			String conditionString=userCommand.substring(wherePosition+5).trim();
			String modifiedset=userCommand;
			for(int i=0;i<operator.size();i++){
				String oper=operator.get(i);
				int index=setString.indexOf(oper);
				int length=operator.get(i).length();
				if(index!=-1){
					String beforeOperator=setString.substring(0, index).trim();
					String afterOperator=setString.substring(index+length).replaceAll("'", "").trim();
					modifiedset=beforeOperator+" "+oper+" "+afterOperator;
				}
			}
			String modifiedString=userCommand;
			for(int i=0;i<operator.size();i++){
				String oper=operator.get(i);
				int index=conditionString.indexOf(oper);
				int length=operator.get(i).length();
				if(index!=-1){
					String beforeOperator=conditionString.substring(0, index).trim();
					String afterOperator=conditionString.substring(index+length).trim();
					modifiedString=beforeOperator+" "+oper+" "+afterOperator;
				}
			}
			userCommand=userCommand.substring(0,setPosition+3).trim()+" "+modifiedset.trim()+" where "+modifiedString.trim();
		}
		else{
			String setString=userCommand.substring(setPosition+3).trim();
			String modifiedset=userCommand;
			for(int i=0;i<operator.size();i++){
				String oper=operator.get(i);
				int index=setString.indexOf(oper);
				int length=operator.get(i).length();
				if(index!=-1){
					String beforeOperator=setString.substring(0, index).trim();
					String afterOperator=setString.substring(index+length).replaceAll("'", "").trim();
					modifiedset=beforeOperator+" "+oper+" "+afterOperator;
					userCommand=userCommand.substring(0,setPosition+3)+" "+modifiedset;
				}
			}
		}
		ArrayList<String> commandTokens=new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		if((commandTokens.size()!=6)&&(commandTokens.size()!=10)){
			System.out.println("Wrong syntax!! Please check!");
			return;
		}
		String tableName=commandTokens.get(1);
		ArrayList<String> tableNames=dbList();
		if(!tableNames.contains(tableName)){
			System.out.println("Please input correct table name");
			return;
		}
		String setColumnName=commandTokens.get(3);
		String whereColumnName="";
		Object changedValue=commandTokens.get(5);
		Object whereValue="";
		if(commandTokens.size()==10){
			whereColumnName=commandTokens.get(7);
			whereValue=commandTokens.get(9);
		}
		ArrayList<String> columnNames=getColumnsName(tableName);
		if(!columnNames.contains(setColumnName)){
			System.out.println("Please check your set column name!");
			return;
		}
		if((whereColumnName.length()!=0)&&(!columnNames.contains(whereColumnName))){
			System.out.println("Please check your where column name!");
			return;
		}
		if(setColumnName.equals(columnNames.get(0))){
			System.out.println("Primary key value can not be changed");
		}
		String path="data\\user_data\\"+tableName+".tbl";
		int setColumnPosition=columnNames.indexOf(setColumnName);
		int whereColumnPosition=columnNames.indexOf(whereColumnName);
		ArrayList<Object[]> tableContent=getColumnsContent(path,getSize(getColumnTypes(tableName)),getColumnTypes(tableName));
		whereValue=String.valueOf(whereValue);
		for(int i=0;i<tableContent.size();i++){
			if(whereColumnPosition!=-1){
				String transferValue=String.valueOf(tableContent.get(i)[whereColumnPosition]);
				if(transferValue.equals(whereValue)){
					tableContent.get(i)[setColumnPosition]=String.valueOf(changedValue);
				}
			}
			if(whereColumnPosition==-1){
				
			    tableContent.get(i)[setColumnPosition]=changedValue;
			}
		}
		returnUpdatedFile(tableName,tableContent);
	}

	//done
	public static ArrayList<String> dbList(){
		String dir=System.getProperty("user.dir");
		String dir_davisbase_tables=dir+"\\data\\catalog\\davisbase_tables.tbl";
		ArrayList<String> dbNameList=new ArrayList<String>();
		try{
			RandomAccessFile davisbase_tables=new RandomAccessFile(dir_davisbase_tables,"rw");
			int pageTotal=pageNumber(dir_davisbase_tables);
			for(int page=0;page<pageTotal;page++){
				int record_position=page*pageSize+8;
				davisbase_tables.seek(record_position);
				short position;
				position=davisbase_tables.readShort();
				while(position!=0){
					davisbase_tables.seek(position+2);
					davisbase_tables.seek(position+8);
					byte[] trans=new byte[20];
					davisbase_tables.readFully(trans);
					String dbName="";
					for(int i=0;i<trans.length;i++){
						dbName+=(char)trans[i];
					}
					dbName=dbName.trim();
					dbNameList.add(dbName);
					record_position+=2;
					davisbase_tables.seek(record_position);
					position=davisbase_tables.readShort();
				}
			}
			davisbase_tables.close();
		}
		catch(Exception e){
			System.out.println("dbList method error");
			
		}
		return dbNameList;
	}

	//DavisBase Initialization
	public static void dbInitialization(){
		String dir=System.getProperty("user.dir");
		String dir_data=dir+"\\data";
		String dir_data_catalog=dir_data+"\\catalog";
		String dir_data_user_data=dir_data+"\\user_data";
		String davis_table=dir_data_catalog+"\\davisbase_tables.tbl";
		String davis_column=dir_data_catalog+"\\davisbase_columns.tbl";
		File folder_data=new File(dir_data);
		File folder_data_catalog=new File(dir_data_catalog);
		File folder_data_user_data=new File(dir_data_user_data);
		File davis_table_tbl=new File(davis_table);
		File davis_column_tbl=new File(davis_column);
		if(!folder_data.exists()){
			folder_data.mkdir();
			folder_data_catalog.mkdir();
			folder_data_user_data.mkdir();
		}
		else{
			if(!folder_data_catalog.exists()){folder_data_catalog.mkdir();}
			if(!folder_data_user_data.exists()){folder_data_user_data.mkdir();}
		}
		if(!davis_table_tbl.exists()){
			createDavisbaseTable();
		}
		if(!davis_column_tbl.exists()){
			createDavisbaseColumn();
		}
	}
	
	//done
	public static void createDavisbaseTable(){
		String filename="davisbase_tables.tbl";
		try{
			String path="data\\catalog\\";
			RandomAccessFile table=new RandomAccessFile(path+filename,"rw");
			table.setLength(pageSize*1);
			int recordSize=28;
			//Initialize headers
			table.seek(0);
			table.write(0x0D);
			table.seek(1);
			table.write(0x02); 
			int[] recordpositions = new int[2];
			recordpositions[0]=pageSize-recordSize;
			recordpositions[1]=recordpositions[0]-recordSize;
			table.seek(2);
			table.writeShort(recordpositions[1]);
			table.seek(4);
			table.writeInt(-1);
			table.seek(8);
			table.writeShort(recordpositions[0]);
			table.seek(10);
			table.writeShort(recordpositions[1]);
			//headers end
			//insert 1st record-davisbase_tables
			table.seek(recordpositions[0]);
			table.writeShort(22);
			table.seek(recordpositions[0]+2);
			table.writeInt(1);
			table.seek(recordpositions[0]+6);
			table.write(1);
			table.seek(recordpositions[0]+7);
			table.write(0x0C);
			table.seek(recordpositions[0]+8);
			table.writeBytes("davisbase_tables");
			//insert 2nd record-davisbase_columns
			table.seek(recordpositions[1]);
			table.writeShort(22);
			table.seek(recordpositions[1]+2);
			table.writeInt(2);
			table.seek(recordpositions[1]+6);
			table.write(1);
			table.seek(recordpositions[1]+7);
			table.write(0x0C);
			table.seek(recordpositions[1]+8);
			table.writeBytes("davisbase_columns");
			table.close();
		} 
		catch(Exception e){
			System.out.println("createDavisbaseTable error");
		}
	}
	//Initialization end
	
	//done
	public static void createDavisbaseColumn(){
		String filename="davisbase_columns.tbl";
		try{
			String path="data\\catalog\\";
			RandomAccessFile table=new RandomAccessFile(path+filename,"rw");
			table.setLength(pageSize);
			table.seek(0);
			table.write(0x0D);
			table.seek(4);
			table.writeInt(-1);
			String[] column_names={"rowid","table_name","rowid","table_name","column_name","data_type","ordinal_position","is_nullable"};
			String[] data_types={"INT","TEXT","INT","TEXT","TEXT","TEXT","TINYINT","TEXT"};
			int[] ordinal_positions={1,2,1,2,3,4,5,6};
			String is_nullable="NO";
			int recordSize=65;
			int[] recordpositions = new int[8];
			for(int j=0;j<8;j++){
				int startPosition=(pageNumber(path+filename)-1)*pageSize;
				table.seek(startPosition+1);
				int  fileNumber=table.read();
				if(fileNumber==0){
					recordpositions[j]=pageSize*pageNumber(path+filename)-recordSize;
				}
				else{
					recordpositions[j]=recordpositions[j-1]-recordSize;
				}
				table.seek(startPosition+2);
				table.writeShort(recordpositions[j]);
				table.seek(startPosition+1);
				table.write(fileNumber+1);
				if(j==7){
					table.seek(startPosition+8);
				}
				else{
					table.seek(startPosition+8+2*j);
				}
				table.writeShort(recordpositions[j]);
				table.seek(recordpositions[j]);
				table.writeShort(59);
				table.seek(recordpositions[j]+2);
				table.writeInt(j+1);
				table.seek(recordpositions[j]+6);
				table.write(5);
				table.seek(recordpositions[j]+7);
				table.write(0x0C);
				table.seek(recordpositions[j]+8);
				table.write(0x0C);
				table.seek(recordpositions[j]+9);
				table.write(0x0C);
				table.seek(recordpositions[j]+10);
				table.write(0x04);
				table.seek(recordpositions[j]+11);
				table.write(0x0C);
				table.seek(recordpositions[j]+12);
				if(j<=1){table.writeBytes("davisbase_tables");}
				else{table.writeBytes("davisbase_columns");}
				table.seek(recordpositions[j]+32);
				table.writeBytes(column_names[j]);
				table.seek(recordpositions[j]+52);
				table.writeBytes(data_types[j]);
				table.seek(recordpositions[j]+60);
				table.write(ordinal_positions[j]);
				table.seek(recordpositions[j]+61);
				table.writeBytes(is_nullable);
			}
			table.close();
		}
		catch(Exception e){
			System.out.println("createDavisbaseColumn error");
		}
	}
	
	static void displayBinaryHex(RandomAccessFile raf) {
		try {
			System.out.println("Dec\tHex\t 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F");
			raf.seek(0);
			long size = raf.length();
			int row = 1;
			System.out.print("0000\t0x0000\t");
			while(raf.getFilePointer() < size) {
				System.out.print(String.format("%02X ", raf.readByte()));
				if(row % 16 == 0) {
					System.out.println();
					System.out.print(String.format("%04d\t0x%04X\t", row, row));
				}
				row++;
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	public static int type_size(String type){
		String need_type=type.toLowerCase();
		int size = 0;
		switch(need_type){
		case "tinyint":
			size=1;
			break;
		case "smallint":
			size=2;
			break;
		case "int":
			size=4;
			break;
		case "bigint":
			size=8;
			break;
		case "float":
		case "real":
			size=4;
			break;
		case "double":
			size=4;
			break;
		case "datetime":
			size=8;
			break;
		case "date":
			size=8;
			break;
		case "text":
			size=20;
			break;
		default:
			System.out.println("this type "+type+" is not correct, please confirm!");
			break;
		}
		return size;
	}
	
	public static int[] getSize(ArrayList<String> columns){
		int[] size=new int[columns.size()];
		for(int i=0;i<columns.size();i++){
			size[i]=type_size(columns.get(i));
		}
		return size;
	}
	//done
	public static HashMap<String,ArrayList<Integer>> columnsHash(){
		String path="data\\catalog\\";
		String filename="davisbase_columns.tbl";
		HashMap<String,ArrayList<Integer>> hash=new HashMap<String,ArrayList<Integer>>();
		try{
			ArrayList<Integer> ids=new ArrayList<Integer>();
			RandomAccessFile davisbase_columns=new RandomAccessFile(path+filename,"rw");
			int pageTotal=pageNumber(path+filename);
			String cur_name="davisbase_tables";
			for(int page=0;page<pageTotal;page++){
				int record_position=page*pageSize+8;
				davisbase_columns.seek(record_position);
				short position;
				position=davisbase_columns.readShort();
				while(position!=0){
					davisbase_columns.seek(position+2);
					int id=davisbase_columns.readInt();
					String table_name=getString(position+12,davisbase_columns,20);
					if(cur_name.equals(table_name)){
						ids.add(id);
					}
					else{
						hash.put(cur_name, ids);
						cur_name=table_name;
						ids=new ArrayList<Integer>();
						ids.add(id);
					}
					record_position+=2;
					davisbase_columns.seek(record_position);
					position=davisbase_columns.readShort();
				}
			}
			hash.put(cur_name, ids);
			davisbase_columns.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
		return hash;
	}
	//done
	public static String getString(int position,RandomAccessFile table,int size){
		byte[] a_string=new byte[size];
		String final_string="";
		try{
		table.seek(position);
		table.readFully(a_string);
		for(int i=0;i<a_string.length;i++){
			final_string+=(char)a_string[i];
		}
		final_string=final_string.trim();
		}
		catch(Exception e){
			System.out.println("getSize error");
		}
		return final_string;
	}
	//done
	public static ArrayList<String> getColumnsName(String table){
		HashMap<String,ArrayList<Integer>> hash=columnsHash();
		ArrayList<Integer> ids=hash.get(table);
		ArrayList<String> columnsName=new ArrayList<String>();
		try{
			RandomAccessFile davisbase_columns=new RandomAccessFile("data\\catalog\\davisbase_columns.tbl","rw");
			int pageTotal=pageNumber("data\\catalog\\davisbase_columns.tbl");
			for(int page=0;page<pageTotal;page++){
				int record_position=page*pageSize+8;
				davisbase_columns.seek(record_position);
				short position;
				position=davisbase_columns.readShort();
				while(position!=0){
					davisbase_columns.seek(position+2);
					int id=davisbase_columns.readInt();
					if(ids.contains(id)){
						columnsName.add(getString(position+32,davisbase_columns,20));
					}
					record_position+=2;
					davisbase_columns.seek(record_position);
					position=davisbase_columns.readShort();
				}
			}
			davisbase_columns.close();
		}
		catch(Exception e){
			System.out.println("getColumnNames error");
		}
		return columnsName;
	}
	//done
	public static ArrayList<String> getColumnTypes(String table){
		HashMap<String,ArrayList<Integer>> hash=columnsHash();
		ArrayList<Integer> ids=hash.get(table);
		ArrayList<String> columnTypes=new ArrayList<String>();
		try{
			RandomAccessFile davisbase_columns=new RandomAccessFile("data\\catalog\\davisbase_columns.tbl","rw");
			int pageTotal=pageNumber("data\\catalog\\davisbase_columns.tbl");
			for(int page=0;page<pageTotal;page++){
				int record_position=page*pageSize+8;
				davisbase_columns.seek(record_position);
				short position;
				position=davisbase_columns.readShort();
				while(position!=0){
					davisbase_columns.seek(position+2);
					int id=davisbase_columns.readInt();
					if(ids.contains(id)){
						columnTypes.add(getString(position+52,davisbase_columns,8));
					}
					record_position+=2;
					davisbase_columns.seek(record_position);
					position=davisbase_columns.readShort();
				}
			}
			davisbase_columns.close();
		}
		catch(Exception e){
			System.out.println("getColumnTypes error");
		}
		return columnTypes;
	}
	//done
	public static ArrayList<Object[]> getColumnsContent(String table_path,int[] column_sizes,ArrayList<String> column_types){
		ArrayList<Object[]> content=new ArrayList<Object[]>();
		try{
			RandomAccessFile db=new RandomAccessFile(table_path,"rw");
			int pageTotal=pageNumber(table_path);
			int record_position=0;
			int position=0;
			for(int page=0;page<pageTotal;page++){
				record_position=page*pageSize+8;
				db.seek(record_position);
				position=db.readShort();
				while(position!=0){
					Object[] one_row_content=new Object[column_sizes.length];	
					position+=2;
					for(int i=0;i<column_sizes.length;i++){
						db.seek(position);
						switch(column_types.get(i).toLowerCase()){
						case "tinyint":
							if(db.read()==0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							one_row_content[i]=db.read();
							break;
						case "smallint":
							if(db.readShort()==0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							one_row_content[i]=db.readShort();
							break;
						case "int":
							if(db.readInt()==0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							one_row_content[i]=db.readInt();
							break;
						case "bigint":
							if(db.readLong()==0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							one_row_content[i]=db.readInt();
							break;
						case "float":
						case "real":
							if(db.readFloat()==0.0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							one_row_content[i]=db.readFloat();
							break;
						case "double":
							if(db.readDouble()==0.0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							one_row_content[i]=db.readDouble();
							break;
						case "datetime":
							if(db.readLong()==0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							one_row_content[i]=db.readLong();//read datetime late;
							break;
						case "date":
							if(db.readLong()==0){
								one_row_content[i]="";
								break;
							}
							db.seek(position);
							long longValue=db.readLong();
							Instant thisDate= Instant.ofEpochSecond (longValue); 
							ZonedDateTime zdt = ZonedDateTime.ofInstant (thisDate, zoneId ); 
							//String longToString=String.valueOf(longValue);
							//String dateString="";
							//if(longToString.length()==8){
							//	dateString=longToString.substring(0,4)+"-"+longToString.substring(4,6)+"-"+longToString.substring(6);
							//}
							one_row_content[i]=zdt.toLocalDate();//dateString;//read datetime late;
							break;
						case "text":
							db.seek(position);
							one_row_content[i]=getString(position,db,column_sizes[i]);
							break;
						default:
							break;
					}
						if(i==0){
							position+=(4+column_sizes.length);
						}
						else{
							position+=column_sizes[i];
						}
					}
					content.add(one_row_content);
					record_position+=2;
					db.seek(record_position);
					position=db.readShort();
				}
			}
			db.close();
		}
		catch(Exception e){
			System.out.println("getColumnsContent error");
		}
		return content;
	}
	
	public static ArrayList<Object[]> getFilteredContent(ArrayList<Object[]> content,ArrayList<String> column_names,ArrayList<String> column_types,String filter_column_name,String operator,String filter_value){
		ArrayList<Object[]> filteredContent=new ArrayList<Object[]>();
		if(!column_names.contains(filter_column_name)){
			System.out.println("Column "+filter_column_name+" is not in table");
		}
		int filter_column_index=column_names.indexOf(filter_column_name);
		String filter_column_type=column_types.get(filter_column_index).toLowerCase();
		long valueF=0;
		if(!filter_column_type.equals("text")){
			valueF=Long.parseLong(filter_value);
		}
		if((filter_column_type.equals("text")&&((!operator.equals("="))&&(!operator.equals("!="))))){
			System.out.println("data type doesn't have other operators exception operators: \"=\" and \"!=\"");
		}
		else{
			switch(operator){
			case "=":
				for(int i=0;i<content.size();i++){
					Object[] one_row=content.get(i);
					if(filter_column_type.equals("text")){
						if(one_row[filter_column_index].equals(filter_value)){
							filteredContent.add(one_row);
						}	
					}
					else{
						Number tmp = (Number) one_row[filter_column_index];
						if(tmp.longValue()==valueF){
							filteredContent.add(one_row);
						}
					}
				}
				break;
			case "!=":
				for(int i=0;i<content.size();i++){
					Object[] one_row=content.get(i);
					if(filter_column_type.equals("text")){
						if(!one_row[filter_column_index].equals(filter_value)){
							filteredContent.add(one_row);
						}	
					}
					else{
						Number tmp = (Number) one_row[filter_column_index];
						if(tmp.longValue()!=valueF){
							filteredContent.add(one_row);
						}
					}
				}
				break;
			case ">=":
				for(int i=0;i<content.size();i++){
					Object[] one_row=content.get(i);
					Number tmp = (Number) one_row[filter_column_index];
					if(tmp.longValue()>=valueF){
						filteredContent.add(one_row);
					}
				}
				break;
			case "<=":
				for(int i=0;i<content.size();i++){
					Object[] one_row=content.get(i);
					Number tmp = (Number) one_row[filter_column_index];
					if(tmp.longValue()<=valueF){
						filteredContent.add(one_row);
					}
				}
				break;
			case ">":
				for(int i=0;i<content.size();i++){
					Object[] one_row=content.get(i);
					Number tmp = (Number) one_row[filter_column_index];
					if(tmp.longValue()>valueF){
						filteredContent.add(one_row);
					}
				}
				break;
			case "<":
				for(int i=0;i<content.size();i++){
					Object[] one_row=content.get(i);
					Number tmp = (Number) one_row[filter_column_index];
					if(tmp.longValue()<valueF){
						filteredContent.add(one_row);
					}
				}
				break;
			default:
				System.out.println("undefined operator");
				break;
			}
		}
		return filteredContent;
	}	
	
	public static void format_print(ArrayList<Object[]> content,int[] column_sizes, ArrayList<String> column_names,ArrayList<String> need_columns,ArrayList<String> columnTypes){
		//create title
		int linelength=0;
		int[] column_length=new int[column_sizes.length];
		for(int i=0;i<column_sizes.length;i++){
			if(need_columns.contains(column_names.get(i))){
			column_length[i]=Math.max(column_sizes[i], column_names.get(i).length());
			linelength+=(column_length[i]+1);
			System.out.print(String.format("%-"+column_length[i]+ "s", column_names.get(i)));
			System.out.print("|");
			}
		}
		System.out.println();
		System.out.println(line("-",linelength));
		for(int j=0;j<content.size();j++){
			Object[] row=content.get(j);
			for(int k=0;k<row.length;k++){
				if(need_columns.contains(column_names.get(k))){
					System.out.print(String.format("%-"+column_length[k]+ "s", row[k]));
					System.out.print("|");
				}
			}
			System.out.println();
		}
		System.out.println(line("-",linelength));
	}
	
	public static void creataNewTable(String tableName,ArrayList<ArrayList<String>> columnsInformation){
		String tableFileName = "data\\user_data\\"+tableName+".tbl";
		try{
			RandomAccessFile newTable=new RandomAccessFile(tableFileName,"rw");
			newTable.setLength(pageSize);
			newTable.seek(0);
			newTable.write(0x0D);
			newTable.seek(1);
			newTable.write(0x00); 
			newTable.seek(2);
			newTable.writeShort(0);
			newTable.seek(4);
			newTable.writeInt(-1);
			newTable.close();
			insertIntoDavisbaseTables(tableName);
			insertIntoDavisBaseColumns(tableName,columnsInformation);
			newTable.close();
		}
		catch(Exception e){
			System.out.println("createNewTable error");
		}
	}
	//done
	public static void insertIntoDavisbaseTables(String tableName){
		String filePath="data\\catalog\\davisbase_tables.tbl";
		try{
			RandomAccessFile table=new RandomAccessFile(filePath,"rw");	
			int startPage=(pageNumber(filePath)-1)*pageSize;
			int lastKey=lastKeyForTable();
			table.seek(startPage+1);
			int recordNumber=table.read();
			table.seek(startPage+1);
			table.write(recordNumber+1);
			table.seek(startPage+8+2*(recordNumber-1));
			int recordPosition=startPage+8+recordNumber*2;
			int position=table.readShort()-28;
			table.seek(recordPosition);
			table.writeShort(position);
			table.seek(position);
			table.writeShort(0x16);
			table.seek(position+2);
			table.writeInt(lastKey+1);
			table.seek(position+6);
			table.write(1);
			table.seek(position+7);
			table.write(0x0C);
			table.seek(position+8);
			table.writeBytes(tableName);
			table.close();
		}
		catch(Exception e){
			System.out.println("insertIntoDavisbaseTables error ");
		}
	}
	//done
	public static void insertIntoDavisBaseColumns(String tableName,ArrayList<ArrayList<String>> columnsInformation){
		String filePath="data\\catalog\\davisbase_columns.tbl";
		try{
			RandomAccessFile table=new RandomAccessFile(filePath,"rw");
			for(int i=0;i<columnsInformation.size();i++){
				int lastKey=lastKeyForColumn()+1;
				int pageNumber=pageNumber(filePath);
				int startPage=(pageNumber-1)*pageSize;
				table.seek(startPage+1);
				int columnSize=table.read();
				int position=0;
				if(columnSize==0){
					position=pageSize*pageNumber-65;
				}
				else{
				table.seek(startPage+8+2*(columnSize-1));
					position=table.readShort()-65;
				}
				table.seek(startPage+2);
				table.writeShort(position);
				table.seek(startPage+8+2*columnSize);
				table.writeShort(position);
				table.seek(position);
				table.writeShort(59);
				table.seek(position+2);
				table.writeInt(lastKey);
				table.seek(position+6);
				table.write(5);
				table.seek(position+7);
				table.write(0x0C);
				table.seek(position+8);
				table.write(0x0C);
				table.seek(position+9);
				table.write(0x0C);
				table.seek(position+10);
				table.write(0x04);
				table.seek(position+11);
				table.write(0x0C);
				table.seek(position+12);
				table.writeBytes(tableName);
				table.seek(position+32);
				table.writeBytes(columnsInformation.get(i).get(0));
				table.seek(position+52);
				table.writeBytes(columnsInformation.get(i).get(1).toUpperCase());
				table.seek(position+60);
				int ordinal_position=(i+1);
				table.write(ordinal_position);
				table.seek(position+61);
				String is_nullable="Yes";
				if(columnsInformation.get(i).size()>2){
					is_nullable="No";
				}
				table.writeBytes(is_nullable.toUpperCase());
				columnSize++;
				table.seek(startPage+1);
				table.write(columnSize);
			}
			table.close();
		}
		catch(Exception e){
			System.out.println("insertIntoDavisBaseColumns error");
		}
	}
	//done
	public static void insertIntoTable(String tableName,ArrayList<String> column_List,ArrayList<String> values_List,ArrayList<String> columnTypes){
		String path="data\\user_data\\"+tableName+".tbl";
		ArrayList<String> columnNames=getColumnsName(tableName);
		if(column_List.size()!=0){
			for(int a=0;a<columnNames.size();a++){
				if(!column_List.contains(columnNames.get(a))){
					if(checkNullable(columnNames.get(a),tableName)==false){
						System.out.println("Column "+columnNames.get(a)+" can't be null!");
						return;
					}
					switch(columnTypes.get(a).toLowerCase()){
					case "tinyint":
						values_List.add(a,"0");
						break;
					case "smallint":
						values_List.add(a,"0");
						break;
					case "int":
						values_List.add(a,"0");
						break;
					case "bigint":
						values_List.add(a,"0");
						break;
					case "float":
					case "real":
						values_List.add(a,"0");
						break;
					case "double":
						values_List.add(a,"0");
						break;
					case "datetime":
						values_List.add(a,"0");
						break;
					case "date":
						values_List.add(a,"0");
						break;
					case "text":
						values_List.add(a,null);
						break;
				}
			}
		}
		}
		int pk=0;
		try{
			pk=Integer.parseInt(values_List.get(0));
		}
		catch(NumberFormatException h){
			System.out.println("please input correct pk");
		}
		boolean duplicated=checkPrimaryKey(path,pk,columnTypes);
		if(duplicated==true){
			System.out.println("Primary Key Duplication!!!");
			return;
		}
		int[] sizes=getSize(columnTypes);
		int recordLength=columnTypes.size()+2;
		for(int i=0;i<sizes.length;i++){
			recordLength+=sizes[i];
		}
		try{
			RandomAccessFile table=new RandomAccessFile(path,"rw");
			int pageNumber=pageNumber(path);
			int pageStart=(pageNumber-1)*pageSize;
			table.seek(pageStart+1);
			int fileSize=table.read();
			int position;
			if(fileSize==0){
				position=pageSize*pageNumber-recordLength;
			}
			else{
				table.seek(pageStart+8+2*(fileSize-1));
				position=table.readShort()-recordLength;
			}
			table.seek(pageStart+8+2*fileSize);
			table.writeShort(position);
			table.seek(pageStart+1);
			table.write(fileSize+1);
			table.seek(pageStart+2);
			table.writeShort(position);
			table.seek(position);
			table.writeShort(recordLength-6);
			table.seek(position+2);
			table.writeInt(Integer.parseInt(values_List.get(0)));
			table.seek(position+6);
			table.write(columnTypes.size()-1);
			for(int i=1;i<columnTypes.size();i++){
				table.seek(position+6+i);
				table.write(getTypeCode(columnTypes.get(i),columnNames.get(i),tableName));
			}
			int pastSize=6+columnTypes.size();
			for(int j=1;j<columnTypes.size();j++){
				table.seek(position+pastSize);
				String type=columnTypes.get(j);
				switch(type.toLowerCase()){
				case "tinyint":
					table.write(Byte.parseByte(values_List.get(j)));
					pastSize+=1;
					break;
				case "smallint":
					table.writeShort(Short.parseShort(values_List.get(j)));
					pastSize+=2;
					break;
				case "int":
					table.writeInt(Integer.parseInt(values_List.get(j)));
					pastSize+=4;
					break;
				case "bigint":
					table.writeLong(Long.parseLong(values_List.get(j)));
					pastSize+=8;
					break;
				case "float":
				case "real":
					table.writeFloat(Float.parseFloat(values_List.get(j)));
					pastSize+=4;
					break;
				case "double":
					table.writeDouble(Double.parseDouble(values_List.get(j)));
					pastSize+=8;
					break;
				case "datetime":
					String dateTime=values_List.get(j);
					if(dateTime.equals("0")){
						table.writeLong(0);
						break;
					}
					int hour=Integer.parseInt(dateTime.substring(0,2));
					int minute=Integer.parseInt(dateTime.substring(3,5));
					int second=Integer.parseInt(dateTime.substring(6,8));
					int nanoSecond=Integer.parseInt(dateTime.substring(9));
					ZonedDateTime zdt1 = ZonedDateTime.of (0,0,0,hour,minute,second,nanoSecond, zoneId );
					long dateTimeToLong = zdt1.toInstant().toEpochMilli() / 1000;
					table.writeLong(dateTimeToLong);
					pastSize+=8;
					break;
				case "date":
					String date=values_List.get(j);
					if(date.equals("0")){
						table.writeLong(0);
						break;
					}
					int year=Integer.parseInt(date.substring(0,4));
					int month=Integer.parseInt(date.substring(5,7));
					int day=Integer.parseInt(date.substring(8));
					ZonedDateTime zdt2 = ZonedDateTime.of (year,month,day,0,0,0,0, zoneId );
					long dateToLong = zdt2.toInstant().toEpochMilli() / 1000;
					table.writeLong(dateToLong);
					pastSize+=8;
					break;
				case "text":
					table.writeBytes(values_List.get(j));
					pastSize+=20;
					break;
				default:
					break;
			}
		}
			table.close();
		}
		catch(Exception e){
			System.out.println("insertIntoTable error");
		}
	}
	//done
	public static boolean checkPrimaryKey(String tablePath,int primary_key,ArrayList<String> column_types){
		boolean duplication=false;
		int[] column_sizes=getSize(column_types);
		ArrayList<Object[]> tableContent=getColumnsContent(tablePath,column_sizes,column_types);
		ArrayList<Integer> pkList=new ArrayList<Integer>();
		for(int i=0;i<tableContent.size();i++){
			pkList.add((Integer)tableContent.get(i)[0]);
		}
		if(pkList.contains(primary_key)){
			duplication=true;
		}
		return duplication;
	}
	//done
	public static boolean checkNullable(String columnName,String tableName){
		String columnPath="data\\catalog\\davisbase_columns.tbl";
		boolean nullable=false;
		ArrayList<String> columnTypes=getColumnTypes("davisbase_columns");
		int[] column_sizes={4,20,20,8,1,4};
		ArrayList<Object[]> columnsContent=getColumnsContent(columnPath,column_sizes,columnTypes);
		for(int i=0;i<columnsContent.size();i++){
			if((columnsContent.get(i)[1].equals(tableName))&&(columnsContent.get(i)[2]).equals(columnName)){
				if(columnsContent.get(i)[5].equals("YES")){
					nullable=true;
				}
			}
		}
		return nullable;
		
	} 
	
	public static void returnUpdatedFile(String tableName,ArrayList<Object[]> tableContent){
		int[] column_sizes={};
		String tablePath="";
		ArrayList<String> columnTypes=getColumnTypes(tableName);
		if((tableName.equals("davisbase_tables"))||(tableName.equals("davisbase_columns"))){
			tablePath="data\\catalog\\"+tableName+".tbl";
			if(tableName.equals("davisbase_columns")){
				int[] size={4,20,20,8,1,4};
				column_sizes=size;
			}
			if(tableName.equals("davisbase_tables")){
				int[] size={4,20};
				column_sizes=size;
			}
		}
		else{
			tablePath="data\\user_data\\"+tableName+".tbl";
			column_sizes=getSize(columnTypes);
		}
		ArrayList<Integer> differentPosition=new ArrayList<Integer>();
		ArrayList<Object[]> fileContent=getColumnsContent(tablePath,column_sizes,columnTypes);
		if(fileContent.size()==tableContent.size()){
			for(int j=0;j<fileContent.size();j++){
				for(int k=0;k<fileContent.get(j).length;k++){
					if(!fileContent.get(j)[k].equals(tableContent.get(j)[k])){
						differentPosition.add(j);
					}
				}
			}
		}
		try{
			RandomAccessFile table=new RandomAccessFile(tablePath,"rw");
			for(int a=0;a<differentPosition.size();a++){
				int pageTotal=pageNumber(tablePath);
				for(int page=0;page<pageTotal;page++){
					int record_position=page*pageSize+8;
					table.seek(record_position);
					short position;
					position=table.readShort();
					while(position!=0){
						Object[] one_row_content=new Object[column_sizes.length];
						position+=2;
						for(int i=0;i<column_sizes.length;i++){
							table.seek(position);
							switch(columnTypes.get(i).toLowerCase()){
							case "tinyint":
								one_row_content[i]=table.readByte();
								break;
							case "smallint":
								one_row_content[i]=table.readShort();
								break;
							case "int":
								one_row_content[i]=table.readInt();
								break;
							case "bigint":
								one_row_content[i]=table.readLong();
								break;
							case "float":
							case "real":
								one_row_content[i]=table.readFloat();
								break;
							case "double":
								one_row_content[i]=table.readDouble();
								break;
							case "datetime":
								one_row_content[i]=table.readLong();//read datetime late;
								break;
							case "date":
								one_row_content[i]=table.readLong();//read datetime late;
								break;
							case "text":
								one_row_content[i]=getString(position,table,column_sizes[i]);
								break;
							default:
								break;
							}
							if(one_row_content[0].equals(tableContent.get(differentPosition.get(a))[0])){
								table.seek(position);
								switch(columnTypes.get(i).toLowerCase()){
								case "tinyint":
									table.write(Byte.parseByte(String.valueOf(tableContent.get(differentPosition.get(a))[i])));
									break;
								case "smallint":
									table.writeShort(Short.parseShort(String.valueOf(tableContent.get(differentPosition.get(a))[i])));
									break;
								case "int":
									table.writeInt(Integer.parseInt(String.valueOf(tableContent.get(differentPosition.get(a))[i])));
									break;
								case "bigint":
									table.writeLong(Long.parseLong(String.valueOf(tableContent.get(differentPosition.get(a))[i])));
									break;
								case "float":
								case "real":
									table.writeFloat(Float.parseFloat(String.valueOf(tableContent.get(differentPosition.get(a))[i])));
									break;
								case "double":
									table.writeDouble(Double.parseDouble(String.valueOf(tableContent.get(differentPosition.get(a))[i])));
									break;
								case "datetime":
									String dateTime=String.valueOf(tableContent.get(differentPosition.get(a))[i]);
									if(dateTime.equals("")){
										table.writeLong(0);
										break;
									}
									int hour=Integer.parseInt(dateTime.substring(0,2));
									int minute=Integer.parseInt(dateTime.substring(3,5));
									int second=Integer.parseInt(dateTime.substring(6,8));
									int nanoSecond=Integer.parseInt(dateTime.substring(9));
									ZonedDateTime zdt1 = ZonedDateTime.of (0,0,0,hour,minute,second,nanoSecond, zoneId );
									long dateTimeToLong = zdt1.toInstant().toEpochMilli() / 1000;
									table.writeLong(dateTimeToLong);
									break;
								case "date":
									String date=String.valueOf(tableContent.get(differentPosition.get(a))[i]);
									if(date.equals("")){
										table.writeLong(0);
										break;
									}
									int year=Integer.parseInt(date.substring(0,4));
									int month=Integer.parseInt(date.substring(5,7));
									int day=Integer.parseInt(date.substring(8));
									ZonedDateTime zdt2 = ZonedDateTime.of (year,month,day,0,0,0,0, zoneId );
									long dateToLong = zdt2.toInstant().toEpochMilli() / 1000;
									table.writeLong(dateToLong);
									break;
								case "text":
									for(int k=0;k<column_sizes[i];k++){
										table.write(0);
									}
									table.seek(position);
									table.writeBytes((String) tableContent.get(differentPosition.get(a))[i]);
									break;
								default:
									break;
								}
							}
							if(i==0){
								position+=column_sizes[i]+column_sizes.length;
							}
							else{
								position+=column_sizes[i];
							}
						}
							record_position+=2;
							table.seek(record_position);
							position=table.readShort();
				}
			}
			}
			table.close();
		}
		catch(Exception e){
			System.out.println("returnUpdatedFile error");
			System.out.println(e);
		}
	}
	
	public static void returnDeletedFile(String tableName,ArrayList<Object[]> tableContent){
		int[] column_sizes={};
		String tablePath="";
		ArrayList<String> columnTypes=getColumnTypes(tableName);
		if((tableName.equals("davisbase_tables"))||(tableName.equals("davisbase_columns"))){
			tablePath="data\\catalog\\"+tableName+".tbl";
			if(tableName.equals("davisbase_columns")){
				int[] size={4,20,20,8,1,4};
				column_sizes=size;
			}
			if(tableName.equals("davisbase_tables")){
				int[] size={4,20};
				column_sizes=size;
			}
		}
		else{
			tablePath="data\\user_data\\"+tableName+".tbl";
			column_sizes=getSize(columnTypes);
		}
		ArrayList<Integer> tableContentPK=new ArrayList<Integer>();
		for(int i=0;i<tableContent.size();i++){
			tableContentPK.add((Integer)tableContent.get(i)[0]);
		}
		try{
			RandomAccessFile table=new RandomAccessFile(tablePath,"rw");
			int pageTotal=pageNumber(tablePath);
			int removedIndex=-1;
			int pageNumber=0;
			for(int page=0;page<pageTotal;page++){
				int startPage=page*pageSize;
				table.seek(startPage+1);
				int recordSize=table.read();
				//table.seek(startPage+2);
				for(int i=0;i<recordSize;i++){
					table.seek(startPage+8+2*i);
					int position=table.readShort();
					table.seek(position+2);
					int pk=table.readInt();
					if(!tableContentPK.contains(pk)){
						removedIndex=i;
						pageNumber=page;
					}
				}
			}
			if(removedIndex==-1){
				System.out.println("0 row affected");
				table.close();
				return;
			}
			int startPage=pageNumber*pageSize;
			table.seek(startPage+1);
			int recordSize=table.read();
			for(int j=removedIndex;j<recordSize;j++){
				int previousPosition=0;
				if(j!=0){
					table.seek(startPage+8+2*(j-1));
					previousPosition=table.readShort();
				}
				table.seek(startPage+8+2*j);
				int curPosition=table.readShort();
				table.seek(startPage+8+2*(j+1));
				int nextPosition=table.readShort();
				if(j==(recordSize-1)){
					table.seek(startPage+8+2*j);
					table.writeShort(nextPosition);
					int recordLength=2+column_sizes.length;
					for(int m=0;m<column_sizes.length;m++){
						recordLength+=column_sizes[m];
					}
					table.seek(curPosition);
					for(int n=0;n<recordLength;n++){
						table.write(0);
					}
					table.seek(startPage+1);
					table.write(recordSize-1);
					table.seek(startPage+2);
					table.writeShort(previousPosition);
					break;
				}
				Object[] one_row_content=new Object[column_sizes.length];
				nextPosition+=2;
				curPosition+=2;
				for(int i=0;i<column_sizes.length;i++){
					table.seek(nextPosition);
					switch(columnTypes.get(i).toLowerCase()){
					case "tinyint":
						one_row_content[i]=table.readByte();
						table.seek(curPosition);
						table.write(Byte.parseByte(String.valueOf(one_row_content[i])));
						break;
					case "smallint":
						one_row_content[i]=table.readShort();
						table.seek(curPosition);
						table.writeShort(Short.parseShort(String.valueOf(one_row_content[i])));
						break;
					case "int":
						one_row_content[i]=table.readInt();
						table.seek(curPosition);
						table.writeInt((Integer)one_row_content[i]);
						break;
					case "bigint":
						one_row_content[i]=table.readLong();
						table.seek(curPosition);
						table.writeLong(Long.parseLong(String.valueOf(one_row_content[i])));
						break;
					case "float":
					case "real":
						one_row_content[i]=table.readFloat();
						table.seek(curPosition);
						table.writeFloat(Float.parseFloat(String.valueOf(one_row_content[i])));
						break;
					case "double":
						one_row_content[i]=table.readDouble();
						table.seek(curPosition);
						table.writeDouble(Double.parseDouble(String.valueOf(one_row_content[i])));
						break;
					case "datetime":
						one_row_content[i]=table.readLong();//read datetime late;
						table.seek(curPosition);
						table.writeLong(Long.parseLong(String.valueOf(one_row_content[i])));
						break;
					case "date":
						one_row_content[i]=table.readLong();//read datetime late;
						table.seek(curPosition);
						table.writeLong(Long.parseLong(String.valueOf(one_row_content[i])));
						break;
					case "text":
						one_row_content[i]=getString(nextPosition,table,column_sizes[i]);
						table.seek(curPosition);
						for(int k=0;k<column_sizes[i];k++){
							table.write(0);
						}
						table.seek(curPosition);
						table.writeBytes((String) one_row_content[i]);
						break;
					default:
						break;
				    }
					if(i==0){
						nextPosition+=column_sizes[i]+column_sizes.length;
						curPosition+=column_sizes[i]+column_sizes.length;
					}
					else{
					nextPosition+=column_sizes[i];
					curPosition+=column_sizes[i];
					}
				}
			}
			table.close();
		}
		catch(Exception e){
			System.out.println("returnDeletedFile error");
		}
	}
	
	public static int getTypeCode(String type,String columnName,String tableName){
		int typeCode=-1;
		switch(type.toLowerCase()){
		case "tinyint":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x00;
			}
			else{
				typeCode=0x04;
			}
			break;
		case "smallint":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x01;
			}
			else{
				typeCode=0x05;
			}
			break;
		case "int":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x02;
			}
			else{
				typeCode=0x06;
			}
			break;
		case "bigint":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x03;
			}
			else{
				typeCode=0x07;
			}
			break;
		case "float":
		case "real":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x02;
			}
			else{
				typeCode=0x08;
			}
			break;
		case "double":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x03;
			}
			else{
				typeCode=0x09;
			}
			break;
		case "datetime":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x03;
			}
			else{
				typeCode=0x0A;
			}
			break;
		case "date":
			if(checkNullable(columnName,tableName)==true){
				typeCode=0x03;
			}
			else{
				typeCode=0x0B;
			}
			break;
		case "text":
			typeCode=0x0C;
			break;
		default:
			break;
	    }
		return typeCode; 
	}
	//done
	public static int pageNumber(String path){
		int page=1;
		try{
			RandomAccessFile table=new RandomAccessFile(path,"rw");
			page=(int) (table.length()/pageSize);
			int startPage=(page-1)*pageSize;
			table.seek(startPage+1);
			int recordNumber=table.read();
			table.seek(startPage+2);
			int position=table.readShort();
			if(position==0){
				return page;
			}
			table.seek(position);
			int recordLength=table.readShort()+6;
			int leftSpace=pageSize-8-(2+recordLength)*recordNumber;
			if(leftSpace<(2+recordLength)){
				page+=1;
				table.setLength(pageSize*(page));
				table.seek(pageSize*(page-1));
				table.write(0x0D);
				table.seek(pageSize*(page-1)+4);
				table.writeInt(-1);
			}
			table.close();
		}
		catch(Exception e){
			System.out.println("pageNumber error");
		}
		return page;
	}
	
	public static int lastKeyForColumn(){
		int key=0;
		try{
			RandomAccessFile table=new RandomAccessFile("data\\catalog\\davisbase_columns.tbl","rw");
			int pageNumber=pageNumber("data\\catalog\\davisbase_columns.tbl");
			table.seek((pageNumber-1)*pageSize+1);
			int recordSize=table.read();
			if(recordSize==0){
				pageNumber-=1;
				table.seek((pageNumber-1)*pageSize+1);
				recordSize=table.read();
			}
			table.seek((pageNumber-1)*pageSize+8+2*(recordSize-1));
			int record=table.readShort();
			table.seek(record+2);
			key=table.readInt();
			table.close();
		}
		catch(Exception e){
			System.out.println("lastKeyColumn Error");
		}
		return key;
	}
	
	public static int lastKeyForTable(){
		int key=0;
		try{
			RandomAccessFile table=new RandomAccessFile("data\\catalog\\davisbase_tables.tbl","rw");
			int pageNumber=pageNumber("data\\catalog\\davisbase_tables.tbl");
			table.seek((pageNumber-1)*pageSize+1);
			int recordSize=table.read();
			if(recordSize==0){
				pageNumber-=1;
				table.seek((pageNumber-1)*pageSize+1);
				recordSize=table.read();
			}
			table.seek((pageNumber-1)*pageSize+8+2*(recordSize-1));
			int record=table.readShort();
			table.seek(record+2);
			key=table.readInt();
			table.close();
		}
		catch(Exception e){
			System.out.println("lastKeyTable Error");
		}
		return key;
	}
	
	public static void Btree(){
		
	}
}
